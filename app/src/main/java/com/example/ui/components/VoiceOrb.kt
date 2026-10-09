package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraMagenta
import com.example.ui.theme.AuraViolet
import com.example.voice.VoiceState

@Composable
fun VoiceOrb(
    voiceState: VoiceState,
    isTurnedOn: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isListening = isTurnedOn && voiceState is VoiceState.ListeningCommand
    val isSpeaking = voiceState is VoiceState.Speaking
    val isProcessing = voiceState is VoiceState.Processing

    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.25f else if (isSpeaking) 1.15f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 800 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val waveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 1000 else 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_alpha"
    )

    val waveRadiusScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = if (isListening) 1.5f else 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 1000 else 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_radius"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(170.dp)
            .testTag("voice_orb_container")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        // Outer animated radar pulse rings
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = center
            val baseRadius = size.minDimension / 2.7f

            if (isListening || isSpeaking || isProcessing) {
                drawCircle(
                    color = AuraCyan.copy(alpha = waveAlpha * 0.4f),
                    radius = baseRadius * waveRadiusScale,
                    center = centerOffset,
                    style = Stroke(width = 3.dp.toPx())
                )
                drawCircle(
                    color = AuraViolet.copy(alpha = waveAlpha * 0.6f),
                    radius = baseRadius * (waveRadiusScale * 0.85f),
                    center = centerOffset,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        // Glowing backdrop sphere
        Box(
            modifier = Modifier
                .size(115.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = when {
                            isListening -> listOf(AuraCyan, AuraViolet, Color(0xFF0F172A))
                            isSpeaking -> listOf(AuraMagenta, AuraViolet, Color(0xFF0F172A))
                            isProcessing -> listOf(AuraViolet, AuraCyan, Color(0xFF0F172A))
                            else -> listOf(AuraViolet, Color(0xFF2E1065), Color(0xFF0F172A))
                        }
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Center icon
            Icon(
                imageVector = when {
                    isSpeaking -> Icons.Default.VolumeUp
                    isProcessing -> Icons.Default.GraphicEq
                    else -> Icons.Default.Mic
                },
                contentDescription = "Voice Assistant Orb",
                tint = Color.White,
                modifier = Modifier
                    .size(46.dp)
                    .testTag("voice_orb_icon")
            )
        }
    }
}
