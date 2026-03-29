package com.readyaid.ui.sos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.readyaid.data.profile.UserProfile
import com.readyaid.data.profile.UserProfileDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SosViewModel @Inject constructor(
    private val userDao: UserProfileDao
) : ViewModel() {

    private val _profile = MutableStateFlow(UserProfile.empty())
    val profile: StateFlow<UserProfile> = _profile

    private val _countdown = MutableStateFlow(5)
    val countdown: StateFlow<Int> = _countdown

    private val _isCountingDown = MutableStateFlow(false)
    val isCountingDown: StateFlow<Boolean> = _isCountingDown

    init {
        viewModelScope.launch {
            _profile.value = userDao.getUserProfileSync() ?: UserProfile.empty()
        }
    }

    fun startCountdown(onComplete: () -> Unit) {
        if (_isCountingDown.value) return
        _isCountingDown.value = true
        _countdown.value = 5
        
        viewModelScope.launch {
            while (_countdown.value > 0 && _isCountingDown.value) {
                delay(1000)
                if (_isCountingDown.value) {
                    _countdown.value -= 1
                }
            }
            if (_countdown.value == 0 && _isCountingDown.value) {
                _isCountingDown.value = false
                onComplete()
            }
        }
    }

    fun cancelCountdown() {
        _isCountingDown.value = false
        _countdown.value = 5
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SosScreen(
    onNavigateUp: () -> Unit,
    viewModel: SosViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val countdown by viewModel.countdown.collectAsState()
    val isCountingDown by viewModel.isCountingDown.collectAsState()
    
    val context = LocalContext.current

    val dialerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Default to 911 for the main SOS logic
        val uri = Uri.parse("tel:911")
        if (isGranted) {
            val intent = Intent(Intent.ACTION_CALL, uri)
            context.startActivity(intent)
        } else {
            val intent = Intent(Intent.ACTION_DIAL, uri)
            context.startActivity(intent)
        }
    }

    val contactDialerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        val uri = Uri.parse("tel:${profile.emergencyContact1Phone}")
        if (isGranted) {
            val intent = Intent(Intent.ACTION_CALL, uri)
            context.startActivity(intent)
        } else {
            val intent = Intent(Intent.ACTION_DIAL, uri)
            context.startActivity(intent)
        }
    }

    fun executeCall(phoneNum: String) {
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
        val uri = Uri.parse("tel:$phoneNum")
        
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_CALL, uri)
            context.startActivity(intent)
        } else {
            // For simplicity in the launcher, if we get here we'll use the profile contact 
            // but the main SOS button uses 911 which should have been granted or handled by dialerLauncher
            if (phoneNum == "911") dialerLauncher.launch(Manifest.permission.CALL_PHONE)
            else contactDialerLauncher.launch(Manifest.permission.CALL_PHONE)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency SOS", fontWeight = FontWeight.Medium, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(ReadyAidRed, ReadyAidAmber.copy(alpha=0.5f), BackgroundPrimary)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Spacer(modifier = Modifier.weight(1f))

                // SOS Button
                SosButton(
                    isCountingDown = isCountingDown,
                    countdown = countdown,
                    onTap = {
                        if (!isCountingDown) viewModel.startCountdown { executeCall("911") }
                        else viewModel.cancelCountdown()
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                if (isCountingDown) {
                    TextButton(onClick = { viewModel.cancelCountdown() }) {
                        Text("Cancel", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    }
                } else {
                    Text("Tap or say 'send SOS'", color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.weight(1f))

                // Contact Card (Clickable to call personal contact)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { executeCall(profile.emergencyContact1Phone) },
                    colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(ReadyAidTeal.copy(alpha=0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = ReadyAidTeal)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Call Primary Emergency Contact", style = MaterialTheme.typography.labelSmall, color = ReadyAidTeal)
                            Text(profile.emergencyContact1Name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text(profile.emergencyContact1Phone, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Mic button
                IconButton(
                    onClick = { /* Handle pure voice later */ },
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.White.copy(alpha=0.2f), CircleShape)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = "Voice", tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

@Composable
fun SosButton(isCountingDown: Boolean, countdown: Int, onTap: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isCountingDown) 1f else 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(220.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha=0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size((200 * scale).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha=0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { onTap() },
                contentAlignment = Alignment.Center
            ) {
                if (isCountingDown) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Calling 911 in", color = ReadyAidRed, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text("$countdown", color = ReadyAidRed, fontSize = 48.sp, fontWeight = FontWeight.Medium)
                    }
                } else {
                    Text("SOS", color = ReadyAidRed, fontSize = 48.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
