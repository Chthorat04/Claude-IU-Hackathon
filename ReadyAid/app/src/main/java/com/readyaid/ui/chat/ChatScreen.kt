package com.readyaid.ui.chat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.readyaid.core.theme.*
import com.readyaid.data.profile.ChatMessageDao
import com.readyaid.data.profile.ChatMessageEntity
import com.readyaid.data.profile.UserProfile
import com.readyaid.data.profile.UserProfileDao
import com.readyaid.data.rag.RagClient
import com.readyaid.data.rag.RagResponse
import com.readyaid.data.rag.ServerRagClient
import com.readyaid.data.voice.IntentMapper
import com.readyaid.data.voice.VoiceIntent
import com.readyaid.ui.components.ReadyAidLogo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import android.util.Log
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val ragResponse: RagResponse? = null,
    val isErrorState: Boolean = false
)


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val ragClient: RagClient,
    private val userDao: UserProfileDao,
    private val chatDao: ChatMessageDao
) : ViewModel() {

    private val _streamingMessage = MutableStateFlow<ChatMessage?>(null)
    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping

    private val _showCallSheet = MutableStateFlow(false)
    val showCallSheet: StateFlow<Boolean> = _showCallSheet

    private val _cachedProfile = MutableStateFlow(UserProfile.empty())
    val cachedProfile: StateFlow<UserProfile> = _cachedProfile

    // FIX: Combined messages ensure history + current stream stay in sync
    val messages: StateFlow<List<ChatMessage>> = combine(
        chatDao.getAllMessages().map { list -> list.map { it.toDomain() } },
        _streamingMessage
    ) { history, streaming ->
        if (streaming != null) history + streaming else history
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            userDao.getUserProfile().collect { it?.let { p -> _cachedProfile.value = p } }
        }
    }

    fun dismissCallSheet() { _showCallSheet.value = false }

    fun clearChat() {
        viewModelScope.launch {
            chatDao.clearAllMessages()
            _streamingMessage.value = null
        }
    }

    fun sendMessage(query: String) {
        if (query.isBlank()) return

        // Intercept CallEmergency intent
        val (intent, _) = IntentMapper.mapIntent(query)
        if (intent == VoiceIntent.CallEmergency) {
            _showCallSheet.value = true
            return
        }

        val userId = java.util.UUID.randomUUID().toString()
        val userMsg = ChatMessage(id = userId, text = query, isUser = true)
        
        _isTyping.value = true

        viewModelScope.launch {
            // 1. Save USER message to DB immediately
            chatDao.insertMessage(userMsg.toEntity())
            
            val profile = userDao.getUserProfileSync() ?: UserProfile.empty()
            val botId = java.util.UUID.randomUUID().toString()

            try {
                ragClient.ask(query, profile).collect { chunk ->
                    // Only hide "Thinking" when we actually have text to show
                    if (chunk.response.isNotEmpty()) {
                        _isTyping.value = false
                    }

                    val current = _streamingMessage.value
                    if (current == null) {
                        // First chunk: Initialize
                        _streamingMessage.value = ChatMessage(
                            id = botId,
                            text = chunk.response,
                            isUser = false,
                            ragResponse = chunk,
                            isErrorState = !chunk.isFirstAid
                        )
                    } else {
                        // Subsequent chunks
                        val newText = current.text + chunk.response
                        _streamingMessage.value = current.copy(
                            text = newText,
                            ragResponse = chunk.copy(response = newText)
                        )
                    }

                    if (chunk.isFinished) {
                        _isTyping.value = false
                        _streamingMessage.value?.let { finalMsg ->
                            chatDao.insertMessage(finalMsg.toEntity())
                        }
                        _streamingMessage.value = null
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Stream error: ${e.message}")
                _isTyping.value = false
                // Show error to user in a chat bubble
                val errorBotMsg = ChatMessage(
                    id = botId,
                    text = "⚠️ Connection error: ${e.localizedMessage ?: "Lost contact with ReadyAid AI."}",
                    isUser = false,
                    isErrorState = true
                )
                _streamingMessage.value = errorBotMsg
                chatDao.insertMessage(errorBotMsg.toEntity())
                _streamingMessage.value = null
            }
        }
    }
}

// Helpers
fun ChatMessage.toEntity() = ChatMessageEntity(
    id = id,
    text = text,
    isUser = isUser,
    responseText = ragResponse?.response,
    sources = ragResponse?.sources?.joinToString(","),
    isFirstAid = ragResponse?.isFirstAid ?: false,
    conditionDetected = ragResponse?.conditionDetected,
    isErrorState = isErrorState
)

fun ChatMessageEntity.toDomain() = ChatMessage(
    id = id,
    text = text,
    isUser = isUser,
    isErrorState = isErrorState,
    ragResponse = if (isUser) null else RagResponse(
        response = responseText ?: "",
        sources = sources?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
        isFirstAid = isFirstAid,
        conditionDetected = conditionDetected ?: ""
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateUp: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    val showCallSheet by viewModel.showCallSheet.collectAsState()
    val profile by viewModel.cachedProfile.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val context = LocalContext.current

    val callLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        val uri = Uri.parse("tel:${profile.emergencyContact1Phone}")
        context.startActivity(Intent(if (granted) Intent.ACTION_CALL else Intent.ACTION_DIAL, uri))
    }

    fun executeCall() {
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
        val uri = Uri.parse("tel:911")
        if (granted) context.startActivity(Intent(Intent.ACTION_CALL, uri))
        else callLauncher.launch(Manifest.permission.CALL_PHONE)
    }

    if (showCallSheet) {
        ConfirmCallBottomSheet(
            contactName = "Emergency Services (911)",
            contactPhone = "911",
            onConfirm = { viewModel.dismissCallSheet(); executeCall() },
            onDismiss = { viewModel.dismissCallSheet() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ReadyAidLogo(
                            iconSize = 20.dp,
                            textSize = 18.sp,
                            showIcon = false
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ReadyAidGreen)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearChat() }) {
                        Icon(Icons.Default.Delete, contentDescription = "New Chat", tint = ReadyAidRed)
                    }
                }
            )
        },
        bottomBar = {
            ChatInputBar(
                text = inputText,
                onTextChange = { inputText = it },
                onSend = {
                    viewModel.sendMessage(inputText)
                    inputText = ""
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundPrimary)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        EmptyChatState()
                    }
                }
                
                items(messages) { message ->
                    if (message.isUser) {
                        UserMessageBubble(message.text)
                    } else {
                        BotMessageBubble(message)
                    }
                }
                if (isTyping) {
                    item {
                        TypingIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyChatState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Mic, 
            contentDescription = null, 
            modifier = Modifier.size(48.dp),
            tint = TextSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Describe your emergency\ne.g., 'My child is choking'",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun UserMessageBubble(text: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp))
                .background(ReadyAidTeal)
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(text = text, color = Color.White, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun BotMessageBubble(message: ChatMessage) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        if (message.isErrorState) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp))
                    .background(Color(0xFFE5E5EA))
                    .padding(16.dp)
                    .widthIn(max = 300.dp)
            ) {
                Text(
                    text = message.text,
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                message.ragResponse?.let { rag ->
                    // For simplicity, we just print the raw response which is already 
                    // structured by the LLM system prompt. 
                    // Advanced: We could parse "CHECK FIRST:", "DO THIS NOW:" into beautiful colored cards.
                    StructuredRagCard(text = rag.response)
                    
                    if (rag.sources.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            rag.sources.forEach { source ->
                                SourceBadge(source)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun SourceBadge(source: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = source,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StructuredRagCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF2F2F7))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("ReadyAid is thinking...", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
}

@Composable
fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask for first-aid...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF2F2F7),
                    unfocusedContainerColor = Color(0xFFF2F2F7),
                    disabledContainerColor = Color(0xFFF2F2F7),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            FloatingActionButton(
                onClick = onSend,
                containerColor = ReadyAidTeal,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
fun ConfirmCallBottomSheet(
    contactName: String,
    contactPhone: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    // This could be a proper ModalBottomSheet in a real app
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Emergency Call") },
        text = { Text("Do you want to call $contactName ($contactPhone)?") },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = ReadyAidRed)) {
                Text("Call Now")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
