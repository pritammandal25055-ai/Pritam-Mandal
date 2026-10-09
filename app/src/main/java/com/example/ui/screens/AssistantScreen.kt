package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.AuraUiState
import com.example.ui.components.VoiceOrb
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraDarkBorder
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkCardElevated
import com.example.ui.theme.AuraDarkSurface
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraViolet
import com.example.voice.VoiceState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AssistantScreen(
    uiState: AuraUiState,
    onOrbClick: () -> Unit,
    onSendCommand: (String) -> Unit,
    onReplayAudio: (String) -> Unit,
    onToggleMute: () -> Unit,
    onToggleWakeWord: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quickPrompts = when (uiState.currentLanguage) {
        com.example.voice.AppLanguage.HINDI -> listOf(
            "हे पारुल",
            "बाय पारुल",
            "कल सुबह 10 बजे जॉन के साथ मीटिंग शेड्यूल करो",
            "आज के सभी ईमेल का सारांश बताओ",
            "आज की कार्यसूची क्या है?",
            "जरूरी कार्य दिखाओ"
        )
        com.example.voice.AppLanguage.BENGALI -> listOf(
            "হে পারুল",
            "বিদায় পারুল",
            "আগামী মঙ্গলবার সকাল ১০টায় জনের সাথে মিটিং শিডিউল করো",
            "আজকের সব ইমেইল সংক্ষেপ করো",
            "আজকের শিডিউল কি?",
            "জরুরি কাজগুলি দেখাও"
        )
        else -> listOf(
            "Hey Parul",
            "Bye Parul",
            "Schedule a meeting with John for next Tuesday at 10 AM about the project proposal",
            "Summarize my emails",
            "What's on my schedule today?",
            "Approve Cloud API quota before 5 PM"
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Graphic Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, AuraViolet.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ai_hero_banner_1791526376328),
                    contentDescription = "Aura AI Assistant Hero",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC0D0F1D))
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "PARUL INTELLIGENCE",
                            color = AuraCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Voice Assistant • Female Voice",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Wake-word active chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (uiState.isWakeWordActive) AuraCyan.copy(alpha = 0.2f) else AuraDarkCard)
                            .clickable { onToggleWakeWord() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (uiState.isWakeWordActive && uiState.isAssistantActive) AuraCyan else AuraTextMuted)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.isAssistantActive) "\"Hey Parul\" Active" else "Parul in Standby",
                                color = if (uiState.isAssistantActive) AuraCyan else AuraTextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Voice Orb Interactive Center
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                VoiceOrb(
                    voiceState = uiState.voiceState,
                    isTurnedOn = uiState.isAssistantActive,
                    onClick = onOrbClick
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Voice status label
                val statusText = when {
                    !uiState.isAssistantActive -> "Standby (Off) • Say \"Hey Parul\" to turn on"
                    uiState.voiceState is VoiceState.ListeningCommand -> "Listening... Speak naturally"
                    uiState.voiceState is VoiceState.Speaking -> "Parul is speaking..."
                    uiState.voiceState is VoiceState.Processing -> "Parul is thinking..."
                    uiState.voiceState is VoiceState.Error -> (uiState.voiceState as VoiceState.Error).message
                    else -> "Parul is ON • Say \"Bye\" to turn off"
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(AuraDarkCard)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    if (uiState.isProcessingCommand || uiState.voiceState is VoiceState.Processing) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            color = AuraCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    } else {
                        Icon(
                            imageVector = if (uiState.voiceState is VoiceState.Speaking) Icons.Default.VolumeUp else Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = if (uiState.voiceState is VoiceState.ListeningCommand) AuraCyan else AuraViolet,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = statusText,
                        color = AuraTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Partial transcription preview if listening
                if (uiState.voiceState is VoiceState.ListeningCommand) {
                    val partial = (uiState.voiceState as VoiceState.ListeningCommand).partialText
                    if (partial.isNotBlank()) {
                        Text(
                            text = "\"$partial\"",
                            color = AuraCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        // Real-Time Reactive Microphone Voice Waveform Visualizer
        item {
            val isListening = uiState.isAssistantActive && uiState.voiceState is VoiceState.ListeningCommand
            com.example.ui.components.VoiceWaveformVisualizer(
                isListening = isListening,
                micAudioLevel = uiState.soundWaveLevel
            )
        }

        // Last Response Card
        if (uiState.lastVisualResponse.isNotBlank()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AuraDarkCardElevated),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(AuraCyan.copy(alpha = 0.5f))
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_response_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AuraCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Aura Assistant Response",
                                    color = AuraCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = onToggleMute,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (uiState.isTtsMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                        contentDescription = "Mute TTS",
                                        tint = if (uiState.isTtsMuted) AuraTextMuted else AuraCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onReplayAudio(uiState.lastSpokenResponse) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Replay",
                                        tint = AuraTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = uiState.lastVisualResponse,
                            color = AuraTextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )

                        if (uiState.lastActionType == "TASK_CREATED") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AuraCyan.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AuraCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Added to Schedule (View in Tasks tab)",
                                    color = AuraCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Natural Language Input Field
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.textInput,
                    onValueChange = { /* handled in parent */ },
                    placeholder = {
                        Text(
                            text = "Type or speak command...",
                            color = AuraTextMuted,
                            fontSize = 13.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AuraCyan,
                        unfocusedBorderColor = AuraDarkBorder,
                        focusedTextColor = AuraTextPrimary,
                        unfocusedTextColor = AuraTextPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("assistant_command_input")
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (uiState.textInput.isNotBlank()) {
                            onSendCommand(uiState.textInput)
                        } else {
                            onOrbClick()
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (uiState.textInput.isNotBlank()) AuraCyan else AuraViolet)
                        .testTag("send_command_button")
                ) {
                    Icon(
                        imageVector = if (uiState.textInput.isNotBlank()) Icons.Default.Send else Icons.Default.Mic,
                        contentDescription = "Send or Speak",
                        tint = if (uiState.textInput.isNotBlank()) Color.Black else Color.White
                    )
                }
            }
        }

        // Quick Suggestion Chips
        item {
            Column {
                Text(
                    text = "Quick Voice Commands:",
                    color = AuraTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    quickPrompts.forEach { prompt ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(AuraDarkCard)
                                .border(1.dp, AuraDarkBorder, RoundedCornerShape(12.dp))
                                .clickable { onSendCommand(prompt) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = prompt,
                                color = AuraTextPrimary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Recent Voice History
        if (uiState.voiceLogs.isNotEmpty()) {
            item {
                Text(
                    text = "Recent Commands & Actions:",
                    color = AuraTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(uiState.voiceLogs.take(5)) { log ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AuraViolet.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Devices,
                                contentDescription = null,
                                tint = AuraCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = log.commandText,
                                color = AuraTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${log.originDevice} • ${log.intentDetected}",
                                color = AuraTextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
