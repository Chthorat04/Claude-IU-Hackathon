package com.readyaid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.readyaid.core.theme.ReadyAidTeal
import com.readyaid.core.theme.TextPrimary

@Composable
fun ReadyAidLogo(
    modifier: Modifier = Modifier,
    iconSize: Dp = 32.dp,
    textSize: androidx.compose.ui.unit.TextUnit = 22.sp,
    showIcon: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showIcon) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = ReadyAidTeal.copy(alpha = 0.12f),
                modifier = Modifier.size(iconSize + 4.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.HealthAndSafety,
                        contentDescription = null,
                        tint = ReadyAidTeal,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
        
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = TextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = textSize
                    )
                ) {
                    append("Ready")
                }
                withStyle(
                    style = SpanStyle(
                        color = ReadyAidTeal,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = textSize
                    )
                ) {
                    append("Aid")
                }
            },
            letterSpacing = (-0.5).sp
        )
    }
}

@Composable
fun ReadyAidInlineLogo(
    color: Color = TextPrimary,
    fontWeight: FontWeight = FontWeight.Bold
) {
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = fontWeight)) {
                append("Ready")
            }
            withStyle(style = SpanStyle(color = ReadyAidTeal, fontWeight = FontWeight.Black)) {
                append("Aid")
            }
        }
    )
}
