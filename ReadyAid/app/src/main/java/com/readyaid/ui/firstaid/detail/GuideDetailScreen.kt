package com.readyaid.ui.firstaid.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.readyaid.core.theme.*
import com.readyaid.data.FirstAidScenario
import com.readyaid.data.ScenarioProvider
import com.readyaid.data.voice.TtsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideDetailScreen(
    scenarioId: String,
    onNavigateUp: () -> Unit,
    onAskAI: (String) -> Unit
) {
    val context = LocalContext.current
    val scenario: FirstAidScenario? = remember { ScenarioProvider.loadById(context, scenarioId) }
    val ttsManager = remember { TtsManager(context) }
    var isSpeaking by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
        }
    }

    if (scenario == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Scenario not found.")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(scenario.title, fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Severity chip
                    Surface(
                        color = if (scenario.isEmergency) ReadyAidRed.copy(alpha = 0.12f) else ReadyAidGreen.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = scenario.severityLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (scenario.isEmergency) ReadyAidRed else ReadyAidGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
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
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // TTS Play/Stop bar
            Card(
                colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                elevation = CardDefaults.cardElevation(0.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = ReadyAidTeal,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isSpeaking) "Playing..." else "Play aloud",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ReadyAidTeal,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedButton(
                        onClick = {
                            if (isSpeaking) {
                                ttsManager.stop()
                                isSpeaking = false
                            } else {
                                val sections = buildList {
                                    add("${scenario.title}.")
                                    add("Check first. ${scenario.check_first.joinToString(". ")}")
                                    add("Do this now. ${scenario.do_now.joinToString(". ")}")
                                    add("Do not. ${scenario.do_not.joinToString(". ")}")
                                    add("When to call emergency services. ${scenario.when_to_call.joinToString(". ")}")
                                }
                                ttsManager.speak(sections)
                                isSpeaking = true
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ReadyAidTeal)
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isSpeaking) "Stop" else "Play")
                    }
                }
            }

            // CHECK FIRST
            GuideSection(
                label = "CHECK FIRST",
                backgroundColor = Color(0xFFFFFDE7),
                items = scenario.check_first,
                numbered = false
            )

            // DO THIS NOW
            GuideSection(
                label = "DO THIS NOW",
                backgroundColor = Color(0xFFE8F5E9),
                items = scenario.do_now,
                numbered = true
            )

            // DO NOT
            GuideSection(
                label = "DO NOT",
                backgroundColor = Color(0xFFFFEBEE),
                items = scenario.do_not,
                numbered = false
            )

            // WHEN TO CALL EMERGENCY
            GuideSection(
                label = "WHEN TO CALL EMERGENCY SERVICES",
                backgroundColor = Color(0xFFFFF3E0),
                items = scenario.when_to_call,
                numbered = false
            )

            // Sources
            Text(
                text = "Based on: ${scenario.sources.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            // Ask AI button
            Button(
                onClick = { onAskAI("Tell me more about treating ${scenario.title}") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ReadyAidTeal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ask ReadyAid AI about this")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun GuideSection(
    label: String,
    backgroundColor: Color,
    items: List<String>,
    numbered: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            items.forEachIndexed { index, item ->
                Row(modifier = Modifier.padding(vertical = 3.dp)) {
                    Text(
                        text = if (numbered) "${index + 1}.  " else "•  ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}
