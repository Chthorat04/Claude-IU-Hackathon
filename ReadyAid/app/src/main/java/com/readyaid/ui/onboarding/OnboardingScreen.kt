package com.readyaid.ui.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.readyaid.core.theme.*
import com.readyaid.ui.components.ReadyAidInlineLogo
import com.readyaid.data.profile.UserProfile
import com.readyaid.data.profile.UserProfileDao
import com.readyaid.ui.components.ReadyAidLogo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userDao: UserProfileDao
) : ViewModel() {

    private val _step = MutableStateFlow(1)
    val step: StateFlow<Int> = _step

    var profile = mutableStateOf(UserProfile.empty())
        private set

    fun updateProfile(newProfile: UserProfile) {
        profile.value = newProfile
    }

    fun nextStep() {
        if (_step.value < 3) _step.value += 1
    }

    fun previousStep() {
        if (_step.value > 1) _step.value -= 1
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            val completedProfile = profile.value.copy(profileCompleted = true)
            userDao.insertProfile(completedProfile)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val step by viewModel.step.collectAsState()
    val profile by viewModel.profile

    // Validation error state
    var showErrors by remember { mutableStateOf(false) }

    val progressFraction by animateFloatAsState(
        targetValue = step / 3f,
        animationSpec = tween(400),
        label = "Progress"
    )

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(BackgroundPrimary)) {
                // Top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ReadyAidLogo(iconSize = 24.dp, textSize = 18.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    // Step dots
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(3) { index ->
                            val isActive = index < step
                            Box(
                                modifier = Modifier
                                    .size(if (index + 1 == step) 10.dp else 8.dp)
                                    .clip(CircleShape)
                                    .background(if (isActive) ReadyAidTeal else DividerColor)
                            )
                        }
                    }
                }
                LinearProgressIndicator(
                    progress = progressFraction,
                    modifier = Modifier.fillMaxWidth(),
                    color = ReadyAidTeal,
                    trackColor = DividerColor
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundPrimary)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            when (step) {
                1 -> StepOne(profile, showErrors) { viewModel.updateProfile(it) }
                2 -> StepTwo(profile, showErrors) { viewModel.updateProfile(it) }
                3 -> StepThree(profile, showErrors) { viewModel.updateProfile(it) }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (step > 1) {
                    OutlinedButton(
                        onClick = { viewModel.previousStep(); showErrors = false },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Back")
                    }
                }

                Button(
                    onClick = {
                        showErrors = true
                        val step1Valid = profile.fullName.isNotBlank() && profile.age > 0
                        val step2Valid = profile.conditions.isNotBlank() && profile.allergies.isNotBlank() && profile.medications.isNotBlank()
                        val step3Valid = profile.emergencyContact1Name.isNotBlank() && profile.emergencyContact1Phone.isNotBlank()

                        when (step) {
                            1 -> if (step1Valid) { showErrors = false; viewModel.nextStep() }
                            2 -> if (step2Valid) { showErrors = false; viewModel.nextStep() }
                            3 -> if (step3Valid) { viewModel.completeOnboarding(); onComplete() }
                        }
                    },
                    modifier = Modifier.weight(if (step > 1) 1f else 1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ReadyAidTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (step == 3) "Complete Setup" else "Next →")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress label
            Text(
                text = "Step $step of 3",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepOne(profile: UserProfile, showErrors: Boolean, onUpdate: (UserProfile) -> Unit) {
    Text("Personal Information", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
    Spacer(modifier = Modifier.height(4.dp))
    Text("Tell us about yourself so we can personalize care.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    Spacer(modifier = Modifier.height(24.dp))

    OutlinedTextField(
        value = profile.fullName,
        onValueChange = { onUpdate(profile.copy(fullName = it)) },
        label = { Text("Full Name *") },
        isError = showErrors && profile.fullName.isBlank(),
        supportingText = { if (showErrors && profile.fullName.isBlank()) Text("Required") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = if (profile.age == 0) "" else profile.age.toString(),
        onValueChange = { onUpdate(profile.copy(age = it.toIntOrNull() ?: 0)) },
        label = { Text("Age *") },
        isError = showErrors && profile.age <= 0,
        supportingText = { if (showErrors && profile.age <= 0) Text("Required") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = profile.bloodType,
        onValueChange = { onUpdate(profile.copy(bloodType = it)) },
        label = { Text("Blood Type (Optional)") },
        placeholder = { Text("e.g. O+, AB-") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepTwo(profile: UserProfile, showErrors: Boolean, onUpdate: (UserProfile) -> Unit) {
    Text("Medical Information", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
    Spacer(modifier = Modifier.height(4.dp))
    Text("This helps ReadyAid give you personalized, safe guidance.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    Spacer(modifier = Modifier.height(24.dp))

    OutlinedTextField(
        value = profile.conditions,
        onValueChange = { onUpdate(profile.copy(conditions = it)) },
        label = { Text("Medical Conditions *") },
        placeholder = { Text("e.g. Asthma, Diabetes Type 2, Epilepsy") },
        isError = showErrors && profile.conditions.isBlank(),
        supportingText = { if (showErrors && profile.conditions.isBlank()) Text("Required — enter 'None' if not applicable") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = profile.allergies,
        onValueChange = { onUpdate(profile.copy(allergies = it)) },
        label = { Text("Allergies *") },
        placeholder = { Text("e.g. Penicillin, Latex, Bee stings") },
        isError = showErrors && profile.allergies.isBlank(),
        supportingText = { if (showErrors && profile.allergies.isBlank()) Text("Required — enter 'None' if not applicable") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = profile.medications,
        onValueChange = { onUpdate(profile.copy(medications = it)) },
        label = { Text("Current Medications *") },
        placeholder = { Text("e.g. Metformin 500mg, Salbutamol inhaler") },
        isError = showErrors && profile.medications.isBlank(),
        supportingText = { if (showErrors && profile.medications.isBlank()) Text("Required — enter 'None' if not applicable") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = profile.medicalHistory,
        onValueChange = { onUpdate(profile.copy(medicalHistory = it)) },
        label = { Text("Medical History (Optional)") },
        placeholder = { Text("e.g. Previous heart attack (2022), knee surgery") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        minLines = 3
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepThree(profile: UserProfile, showErrors: Boolean, onUpdate: (UserProfile) -> Unit) {
    Text("Emergency Contacts", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
    Spacer(modifier = Modifier.height(4.dp))
    Text("ReadyAid will call this person in an SOS situation.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    Spacer(modifier = Modifier.height(24.dp))

    OutlinedTextField(
        value = profile.emergencyContact1Name,
        onValueChange = { onUpdate(profile.copy(emergencyContact1Name = it)) },
        label = { Text("Primary Contact Name *") },
        isError = showErrors && profile.emergencyContact1Name.isBlank(),
        supportingText = { if (showErrors && profile.emergencyContact1Name.isBlank()) Text("Required") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = profile.emergencyContact1Phone,
        onValueChange = { onUpdate(profile.copy(emergencyContact1Phone = it)) },
        label = { Text("Primary Contact Phone *") },
        isError = showErrors && profile.emergencyContact1Phone.isBlank(),
        supportingText = { if (showErrors && profile.emergencyContact1Phone.isBlank()) Text("Required") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
    Spacer(modifier = Modifier.height(20.dp))
    Divider(color = DividerColor)
    Spacer(modifier = Modifier.height(20.dp))
    Text("Secondary Contact (Optional)", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = profile.emergencyContact2Name ?: "",
        onValueChange = { onUpdate(profile.copy(emergencyContact2Name = it.ifBlank { null })) },
        label = { Text("Secondary Contact Name") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = profile.emergencyContact2Phone ?: "",
        onValueChange = { onUpdate(profile.copy(emergencyContact2Phone = it.ifBlank { null })) },
        label = { Text("Secondary Contact Phone") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
    Spacer(modifier = Modifier.height(24.dp))

    // Summary preview card
    if (profile.fullName.isNotBlank()) {
        Surface(
            color = ReadyAidTeal.copy(alpha = 0.06f),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Profile Summary", style = MaterialTheme.typography.titleMedium, color = ReadyAidTeal)
                Spacer(modifier = Modifier.height(8.dp))
                Text("${profile.fullName}, age ${profile.age}", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                if (profile.allergies.isNotBlank()) Text("Allergies: ${profile.allergies}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                if (profile.conditions.isNotBlank()) Text("Conditions: ${profile.conditions}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                if (profile.emergencyContact1Name.isNotBlank()) Text("Emergency: ${profile.emergencyContact1Name} · ${profile.emergencyContact1Phone}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
    }
}
