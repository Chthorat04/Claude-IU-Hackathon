package com.readyaid.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.readyaid.core.theme.*
import com.readyaid.ui.components.ReadyAidInlineLogo
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun DisclaimerDialog(onAccept: () -> Unit) {
    AlertDialog(
        onDismissRequest = { /* Not dismissible by outside click */ },
        containerColor = BackgroundCard,
        shape = RoundedCornerShape(20.dp),
        icon = {
            Icon(
                Icons.Default.HealthAndSafety,
                contentDescription = null,
                tint = ReadyAidRed,
                modifier = Modifier.size(40.dp)
            )
        },
        title = {
            Text(
                text = "Important Notice",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ReadyAid provides educational first-aid guidance based on vetted medical sources (ERC 2025, ICRC, American Red Cross, St John Ambulance).",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = ReadyAidRed.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "⚠️  It is NOT a substitute for professional medical care or emergency services. Always call 911 / 999 / 112 when in doubt.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ReadyAidRed,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onAccept,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ReadyAidTeal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("I Understand — Open ", style = MaterialTheme.typography.bodyLarge)
                    ReadyAidInlineLogo(color = Color.White, fontWeight = FontWeight.Black)
                }
            }
        }
    )
}
