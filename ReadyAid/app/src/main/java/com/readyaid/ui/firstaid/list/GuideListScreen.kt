package com.readyaid.ui.firstaid.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.readyaid.core.theme.*
import com.readyaid.data.FirstAidScenario
import com.readyaid.data.ScenarioProvider

private val CategoryColors = mapOf(
    "burns"       to Color(0xFFFFF3E0L),
    "bleeding"    to Color(0xFFFFEBEEL),
    "choking"     to Color(0xFFE8F5E9L),
    "fracture"    to Color(0xFFE3F2FDL),
    "seizure"     to Color(0xFFF3E5F5L),
    "asthma"      to Color(0xFFE0F7FAL),
    "cardiac"     to Color(0xFFFFEBEEL),
    "stroke"      to Color(0xFFE8EAF6L),
    "anaphylaxis" to Color(0xFFFCE4ECL),
    "unconscious" to Color(0xFFF9FBE7L),
    "poisoning"   to Color(0xFFE8F5E9L),
    "general"     to Color(0xFFF5F5F5L)
)

private val CategoryAccentColors = mapOf(
    "burns"       to ReadyAidAmber,
    "bleeding"    to ReadyAidRed,
    "choking"     to ReadyAidGreen,
    "fracture"    to Color(0xFF1565C0L),
    "seizure"     to ReadyAidPurple,
    "asthma"      to ReadyAidTeal,
    "cardiac"     to ReadyAidRed,
    "stroke"      to Color(0xFF3949ABL),
    "anaphylaxis" to Color(0xFFAD1457L),
    "unconscious" to Color(0xFF689F38L),
    "poisoning"   to ReadyAidGreen,
    "general"     to TextSecondary
)

private val CategoryEmoji = mapOf(
    "burns"       to "🔥",
    "bleeding"    to "🩸",
    "choking"     to "😮",
    "fracture"    to "🦴",
    "seizure"     to "⚡",
    "asthma"      to "💨",
    "cardiac"     to "❤️",
    "stroke"      to "🧠",
    "anaphylaxis" to "🐝",
    "unconscious" to "😴",
    "poisoning"   to "☠️",
    "general"     to "🏥"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideListScreen(
    onNavigateUp: () -> Unit,
    onScenarioSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val scenarios = remember { ScenarioProvider.loadAll(context) }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = if (searchQuery.isBlank()) scenarios
    else scenarios.filter { it.title.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("First-Aid Guide", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Describe what happened...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = DividerColor,
                    focusedBorderColor = ReadyAidTeal,
                    containerColor = BackgroundCard
                ),
                singleLine = true
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered) { scenario ->
                    ScenarioCategoryCard(
                        scenario = scenario,
                        onClick = { onScenarioSelected(scenario.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ScenarioCategoryCard(scenario: FirstAidScenario, onClick: () -> Unit) {
    val bg = CategoryColors[scenario.category] ?: Color(0xFFF5F5F5)
    val accent = CategoryAccentColors[scenario.category] ?: TextSecondary
    val emoji = CategoryEmoji[scenario.category] ?: "🏥"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Accent left strip
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accent)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = emoji, style = MaterialTheme.typography.titleLarge)
                Column {
                    Text(
                        text = scenario.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                        maxLines = 2
                    )
                    if (scenario.isEmergency) {
                        Text(
                            text = "⚠ Emergency",
                            style = MaterialTheme.typography.labelSmall,
                            color = ReadyAidRed
                        )
                    }
                }
            }
        }
    }
}
