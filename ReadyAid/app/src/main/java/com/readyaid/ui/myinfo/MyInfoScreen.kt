package com.readyaid.ui.myinfo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.readyaid.core.theme.*
import com.readyaid.data.profile.UserProfile
import com.readyaid.data.profile.UserProfileDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyInfoViewModel @Inject constructor(
    private val userDao: UserProfileDao
) : ViewModel() {

    private val _profile = MutableStateFlow(UserProfile.empty())
    val profile: StateFlow<UserProfile> = _profile

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    init {
        viewModelScope.launch {
            userDao.getUserProfile().collect { it?.let { p -> _profile.value = p } }
        }
    }

    fun startEditing() { _isEditing.value = true }

    fun updateProfile(profile: UserProfile) { _profile.value = profile }

    fun saveChanges() {
        viewModelScope.launch {
            userDao.insertProfile(_profile.value.copy(profileCompleted = true))
            _isEditing.value = false
            _saveSuccess.value = true
        }
    }

    fun cancelEditing() { _isEditing.value = false }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyInfoScreen(
    onNavigateUp: () -> Unit,
    viewModel: MyInfoViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    val context = LocalContext.current
    val callLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        // This is generic, we'll use it for both 1 and 2
    }

    fun makeCall(phone: String) {
        val intent = Intent(if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) Intent.ACTION_CALL else Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        context.startActivity(intent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Info", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isEditing) {
                        IconButton(onClick = { viewModel.startEditing() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = ReadyAidTeal)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundPrimary)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (isEditing) {
                EditableProfileContent(
                    profile = profile,
                    onUpdate = viewModel::updateProfile,
                    onSave = viewModel::saveChanges,
                    onCancel = viewModel::cancelEditing
                )
            } else {
                ReadableProfileContent(profile = profile, onCallClick = { makeCall(it) })
            }
        }
    }
}

@Composable
fun ReadableProfileContent(profile: UserProfile, onCallClick: (String) -> Unit) {
    // Identity Card
    Card(
        colors = CardDefaults.cardColors(containerColor = BackgroundCard),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(ReadyAidTeal.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = ReadyAidTeal, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(profile.fullName, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                    Text("Age ${profile.age}  ·  ${profile.bloodType.ifBlank { "Blood type not set" }}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = ReadyAidRed.copy(alpha = 0.08f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🏥  Show this card to a first responder",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReadyAidRed,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }

    InfoRow(label = "Allergies", value = profile.allergies, accentColor = ReadyAidRed)
    InfoRow(label = "Conditions", value = profile.conditions, accentColor = ReadyAidAmber)
    InfoRow(label = "Medications", value = profile.medications, accentColor = ReadyAidTeal)
    if (profile.medicalHistory.isNotBlank()) {
        InfoRow(label = "Medical History", value = profile.medicalHistory, accentColor = ReadyAidPurple)
    }

    // Emergency Contact
    Card(
        colors = CardDefaults.cardColors(containerColor = BackgroundCard),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Emergency Contacts", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Spacer(modifier = Modifier.height(10.dp))
            ContactRow(
                name = profile.emergencyContact1Name, 
                phone = profile.emergencyContact1Phone, 
                label = "Primary",
                onCall = { onCallClick(profile.emergencyContact1Phone) }
            )
            if (!profile.emergencyContact2Name.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                ContactRow(
                    name = profile.emergencyContact2Name, 
                    phone = profile.emergencyContact2Phone ?: "", 
                    label = "Secondary",
                    onCall = { onCallClick(profile.emergencyContact2Phone ?: "") }
                )
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, accentColor: androidx.compose.ui.graphics.Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BackgroundCard),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(accentColor))
            Column(modifier = Modifier.padding(16.dp)) {
                Text(label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium), color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(value.trimStart('[').trimEnd(']').replace("\"", "").ifBlank { "None specified" }, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
            }
        }
    }
}

@Composable
fun ContactRow(name: String, phone: String, label: String, onCall: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ReadyAidAmber.copy(alpha = 0.1f))
                .clickable { onCall() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Phone, contentDescription = "Call Contact", tint = ReadyAidAmber, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text("$label: $name", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
            Text(phone, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableProfileContent(
    profile: UserProfile,
    onUpdate: (UserProfile) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Text("Edit Profile", style = MaterialTheme.typography.titleLarge, color = TextPrimary)

    OutlinedTextField(value = profile.fullName, onValueChange = { onUpdate(profile.copy(fullName = it)) }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(value = if (profile.age == 0) "" else profile.age.toString(), onValueChange = { onUpdate(profile.copy(age = it.toIntOrNull() ?: 0)) }, label = { Text("Age") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
    OutlinedTextField(value = profile.bloodType, onValueChange = { onUpdate(profile.copy(bloodType = it)) }, label = { Text("Blood Type") }, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(value = profile.conditions, onValueChange = { onUpdate(profile.copy(conditions = it)) }, label = { Text("Conditions") }, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(value = profile.allergies, onValueChange = { onUpdate(profile.copy(allergies = it)) }, label = { Text("Allergies") }, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(value = profile.medications, onValueChange = { onUpdate(profile.copy(medications = it)) }, label = { Text("Medications") }, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(value = profile.medicalHistory, onValueChange = { onUpdate(profile.copy(medicalHistory = it)) }, label = { Text("Medical History") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
    OutlinedTextField(value = profile.emergencyContact1Name, onValueChange = { onUpdate(profile.copy(emergencyContact1Name = it)) }, label = { Text("Primary Contact Name") }, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(value = profile.emergencyContact1Phone, onValueChange = { onUpdate(profile.copy(emergencyContact1Phone = it)) }, label = { Text("Primary Contact Phone") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth())

    Spacer(modifier = Modifier.height(8.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancel") }
        Button(onClick = onSave, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = ReadyAidTeal)) { Text("Save") }
    }
}
