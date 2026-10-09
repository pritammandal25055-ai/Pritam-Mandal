package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraDarkBorder
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkCardElevated
import com.example.ui.theme.AuraMagenta
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraViolet
import kotlin.math.abs
import kotlin.math.sin

@Composable
fun VoiceWaveformVisualizer(
    isListening: Boolean,
    micAudioLevel: Float, // 0.0f to 1.0f from SpeechRecognizer onRmsChanged
    modifier: Modifier = Modifier
) {
    // Smoothly animate the target mic audio level
    val animatedLevel by animateFloatAsState(
        targetValue = if (isListening) (micAudioLevel.coerceIn(0.05f, 1.0f)) else 0.05f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "animated_mic_level"
    )

    // Infinite phase runner for continuous fluid organic wave movement
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_phase")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase_anim"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AuraDarkCardElevated)
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        AuraCyan.copy(alpha = if (isListening) 0.6f else 0.2f),
                        AuraViolet.copy(alpha = if (isListening) 0.8f else 0.3f),
                        AuraMagenta.copy(alpha = if (isListening) 0.6f else 0.2f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("voice_waveform_visualizer")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Status bar above waveform
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isListening) AuraCyan else AuraTextMuted)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isListening) "Parul is actively listening to your voice" else "Microphone ready (Tap orb to speak)",
                        color = if (isListening) AuraCyan else AuraTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (isListening) {
                    val percent = (animatedLevel * 100).toInt().coerceIn(5, 100)
                    Text(
                        text = "Mic Input: $percent%",
                        color = AuraTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Canvas with Multi-Bar Equalizer and Sinusoidal Audio Wave
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .testTag("waveform_canvas")
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val midY = canvasHeight / 2f

                val barCount = 32
                val barSpacing = canvasWidth / barCount
                val barWidth = barSpacing * 0.62f

                // Draw 32 dancing equalizer bars
                for (i in 0 until barCount) {
                    val normalizedIndex = i.toFloat() / barCount
                    val distanceCenter = abs(normalizedIndex - 0.5f) * 2f // 0 at center, 1 at edges
                    val centerMultiplier = (1f - (distanceCenter * 0.45f)) // Center bars taller

                    // Combine microphone audio level with sinusoidal harmonic variation
                    val waveVariance = (sin(phase + (i * 0.35f)) * 0.4f + 0.6f).toFloat()
                    val rawHeightFactor = if (isListening) {
                        (animatedLevel * 0.75f + 0.25f) * waveVariance * centerMultiplier
                    } else {
                        0.08f * (sin(phase * 0.5f + i * 0.2f).toFloat() * 0.3f + 0.7f)
                    }

                    val barHeight = (rawHeightFactor * (canvasHeight - 12.dp.toPx())).coerceIn(4.dp.toPx(), canvasHeight)
                    val barLeft = (i * barSpacing) + ((barSpacing - barWidth) / 2f)
                    val barTop = midY - (barHeight / 2f)

                    val barBrush = Brush.verticalGradient(
                        colors = listOf(
                            AuraCyan,
                            AuraViolet,
                            AuraMagenta
                        ),
                        startY = barTop,
                        endY = barTop + barHeight
                    )

                    drawRoundRect(
                        brush = barBrush,
                        topLeft = Offset(barLeft, barTop),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }

                // Overlay glowing continuous sinusoidal waveform path when listening
                if (isListening) {
                    val path = Path()
                    val points = 60
                    for (j in 0..points) {
                        val px = (j.toFloat() / points) * canvasWidth
                        val angle = phase * 1.8f + (j.toFloat() / points) * (4 * Math.PI).toFloat()
                        val amplitude = (animatedLevel * 22.dp.toPx())
                        val py = midY + (sin(angle) * amplitude).toFloat()

                        if (j == 0) {
                            path.moveTo(px, py)
                        } else {
                            path.lineTo(px, py)
                        }
                    }

                    drawPath(
                        path = path,
                        color = Color.White.copy(alpha = 0.85f),
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}
