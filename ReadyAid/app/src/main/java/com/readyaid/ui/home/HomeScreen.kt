package com.readyaid.ui.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import android.content.Intent
import android.net.Uri
import com.readyaid.core.theme.*
import com.readyaid.data.voice.IntentMapper
import com.readyaid.data.voice.VoiceController
import com.readyaid.data.voice.VoiceIntent
import com.readyaid.data.voice.VoiceState
import com.readyaid.ui.components.ReadyAidLogo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToChat: () -> Unit = {},
    onNavigateToSos: () -> Unit = {},
    onNavigateToGuide: () -> Unit = {},
    onNavigateToMyInfo: () -> Unit = {}
) {
    val context = LocalContext.current
    var voiceState by remember { mutableStateOf(VoiceState.Idle) }
    
    val voiceController = remember { VoiceController(context) }
    
    DisposableEffect(Unit) {
        onDispose { voiceController.destroy() }
    }

    val startVoice = {
        voiceController.startListening(
            onResult = { text ->
                val (intent, _) = IntentMapper.mapIntent(text)
                when (intent) {
                    VoiceIntent.SOS -> onNavigateToSos()
                    VoiceIntent.CallEmergency -> onNavigateToSos() // or dialer
                    VoiceIntent.FirstAidScenario, VoiceIntent.Unknown -> {
                        // Normally pass 'text' to ChatScreen, but for hackathon:
                        onNavigateToChat()
                    }
                }
            },
            onError = { },
            onStateChange = { newState -> voiceState = newState }
        )
    }

    val recordLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) startVoice()
    }

    fun handleMicClick() {
        if (voiceState == VoiceState.Listening) {
            voiceController.stopListening()
            return
        }
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            startVoice()
        } else {
            recordLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    ReadyAidLogo(iconSize = 24.dp, textSize = 20.sp)
                },
                actions = {
                    IconButton(onClick = { /* TODO settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        bottomBar = {
            VoiceAssistBar(
                state = voiceState,
                onMicClick = { handleMicClick() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundPrimary)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Hero Action
            EmergencyActionButton(
                onClick = onNavigateToSos
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Grid of Actions
            Row(modifier = Modifier.fillMaxWidth()) {
                ActionCard(
                    title = "Start First-Aid Chat",
                    subtitle = "Get step-by-step guidance",
                    icon = Icons.Default.Chat,
                    color = ReadyAidTeal,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToChat
                )
                Spacer(modifier = Modifier.width(16.dp))
                ActionCard(
                    title = "Medical Profile",
                    subtitle = "Manage your health info",
                    icon = Icons.Default.Person,
                    color = Color(0xFF5856D6),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToMyInfo
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                ActionCard(
                    title = "Emergency Guide",
                    subtitle = "Offline instructions",
                    icon = Icons.Default.MenuBook,
                    color = Color(0xFFFF9500),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToGuide
                )
                Spacer(modifier = Modifier.width(16.dp))
                ActionCard(
                    title = "Local Services",
                    subtitle = "Find nearest hospitals",
                    icon = Icons.Default.LocalHospital,
                    color = ReadyAidGreen,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val gmmIntentUri = Uri.parse("geo:0,0?q=hospitals")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        context.startActivity(mapIntent)
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Recent Alert/Tip
            TipCard()
        }
    }
}

@Composable
fun EmergencyActionButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = ReadyAidRed,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Emergency,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "EMERGENCY SOS",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF2F2F7))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun TipCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = ReadyAidTeal.copy(alpha = 0.05f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = ReadyAidTeal)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "Daily Safety Tip",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ReadyAidTeal
                )
                Text(
                    "Check your first-aid kit's expiration dates every 6 months.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun VoiceAssistBar(
    state: VoiceState,
    onMicClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        tonalElevation = 8.dp,
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = when(state) {
                    VoiceState.Idle -> "Tap mic to speak..."
                    VoiceState.Listening -> "Listening..."
                    VoiceState.Processing -> "Processing..."
                    VoiceState.Error -> "Error, try again."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (state == VoiceState.Listening) ReadyAidTeal else TextSecondary
            )
            
            FloatingActionButton(
                onClick = onMicClick,
                containerColor = if (state == VoiceState.Listening) ReadyAidRed else ReadyAidTeal,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    if (state == VoiceState.Listening) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = "Voice Assistant"
                )
            }
        }
    }
}
