package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TabletMac
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConnectedDevice
import com.example.ui.AuraUiState
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraDarkBorder
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkCardElevated
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraViolet
import com.example.voice.WakeWordSensitivity

@Composable
fun CrossPlatformScreen(
    uiState: AuraUiState,
    onToggleDeviceWakeWord: (String) -> Unit,
    onToggleWakeWordGlobal: () -> Unit,
    onSetSensitivity: (WakeWordSensitivity) -> Unit,
    onSetLanguage: (com.example.voice.AppLanguage) -> Unit,
    onTogglePhonePermission: (Boolean) -> Unit,
    onExecutePhoneActionDirect: (com.example.phone.PhoneActionType) -> Unit,
    onSimulateCommand: (deviceName: String, platform: String, command: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showProtocolSchema by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = "Cross-Platform Ecosystem",
                    color = AuraTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Unified Voice & Wake-Word Sync across Android, iOS & Desktop",
                    color = AuraTextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        // Parul Voice Configuration Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AuraDarkCardElevated),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(AuraViolet.copy(alpha = 0.5f))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voice_config_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AuraViolet.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = null,
                                    tint = AuraCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Parul Voice Engine",
                                    color = AuraTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Female Voice • English, हिन्दी, বাংলা",
                                    color = AuraCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Switch(
                            checked = uiState.isWakeWordActive,
                            onCheckedChange = { onToggleWakeWordGlobal() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = AuraCyan
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Parul Voice Language:",
                        color = AuraTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        com.example.voice.AppLanguage.values().forEach { lang ->
                            val isSelected = uiState.currentLanguage == lang
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) AuraCyan else AuraDarkCard)
                                    .clickable { onSetLanguage(lang) }
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = "${lang.displayName}\n(${lang.nativeName})",
                                    color = if (isSelected) Color.Black else AuraTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Wake-Word Detection Sensitivity (\"Hey Parul\"):",
                        color = AuraTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        WakeWordSensitivity.values().forEach { sens ->
                            val isSelected = uiState.wakeWordSensitivity == sens
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) AuraCyan else AuraDarkCard)
                                    .clickable { onSetSensitivity(sens) }
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = sens.name,
                                    color = if (isSelected) Color.Black else AuraTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Phone Control & User Permission Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AuraDarkCardElevated),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (uiState.phoneControlState.isPermissionGranted) AuraCyan.copy(alpha = 0.5f) else AuraDarkBorder
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("phone_control_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AuraCyan.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhoneAndroid,
                                    contentDescription = null,
                                    tint = AuraCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "AI Phone Control Permission",
                                    color = AuraTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = if (uiState.phoneControlState.isPermissionGranted) "Status: Permitted by User" else "Status: Permission Required",
                                    color = if (uiState.phoneControlState.isPermissionGranted) AuraCyan else AuraTextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Switch(
                            checked = uiState.phoneControlState.isPermissionGranted,
                            onCheckedChange = { onTogglePhonePermission(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = AuraCyan
                            ),
                            modifier = Modifier.testTag("toggle_phone_control_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "When permitted, Parul can execute system actions upon your voice command (e.g. \"Open calendar\", \"Open settings\", \"Open camera\", \"Show alarms\"). Parul will always respect your consent.",
                        color = AuraTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Quick Test System Phone Actions:",
                        color = AuraCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AuraDarkCard)
                                .border(1.dp, AuraDarkBorder, RoundedCornerShape(8.dp))
                                .clickable { onExecutePhoneActionDirect(com.example.phone.PhoneActionType.OPEN_CALENDAR) }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(text = "Calendar", color = AuraTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AuraDarkCard)
                                .border(1.dp, AuraDarkBorder, RoundedCornerShape(8.dp))
                                .clickable { onExecutePhoneActionDirect(com.example.phone.PhoneActionType.OPEN_SETTINGS) }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(text = "Settings", color = AuraTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AuraDarkCard)
                                .border(1.dp, AuraDarkBorder, RoundedCornerShape(8.dp))
                                .clickable { onExecutePhoneActionDirect(com.example.phone.PhoneActionType.OPEN_CAMERA) }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(text = "Camera", color = AuraTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AuraDarkCard)
                                .border(1.dp, AuraDarkBorder, RoundedCornerShape(8.dp))
                                .clickable { onExecutePhoneActionDirect(com.example.phone.PhoneActionType.OPEN_ALARM) }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(text = "Alarms", color = AuraTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Connected Devices Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Connected Ecosystem Nodes",
                    color = AuraTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = AuraCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Real-time Sync",
                        color = AuraCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // List of Devices
        items(uiState.devices) { device ->
            DeviceItemRow(
                device = device,
                onToggleWake = { onToggleDeviceWakeWord(device.id) }
            )
        }

        // Cross-Platform Voice Command Simulator
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(AuraDarkBorder)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("simulate_relay_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Devices,
                            contentDescription = null,
                            tint = AuraCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Test Cross-Device Command Relay",
                            color = AuraTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }

                    Text(
                        text = "Simulate voice commands captured on iOS or Desktop clients to verify seamless multi-device intent processing and schedule updates:",
                        color = AuraTextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SimulateCommandButton(
                        platformLabel = "iPhone 16 Pro (iOS)",
                        command = "Schedule a meeting with John for next Tuesday at 10 AM about the project proposal",
                        onClick = {
                            onSimulateCommand(
                                "iPhone 16 Pro",
                                "iOS",
                                "Schedule a meeting with John for next Tuesday at 10 AM about the project proposal"
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SimulateCommandButton(
                        platformLabel = "MacBook Pro (macOS)",
                        command = "Summarize today's emails",
                        onClick = {
                            onSimulateCommand(
                                "MacBook Pro 16\"",
                                "macOS",
                                "Summarize today's emails"
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SimulateCommandButton(
                        platformLabel = "Windows 11 Workstation",
                        command = "What's on my schedule today?",
                        onClick = {
                            onSimulateCommand(
                                "Windows 11 PC",
                                "Windows",
                                "What's on my schedule today?"
                            )
                        }
                    )
                }
            }
        }

        // Unified Protocol Schema
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showProtocolSchema = !showProtocolSchema }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Unified Cross-Platform Protocol Schema",
                            color = AuraTextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (showProtocolSchema) "Hide" else "Show JSON",
                            color = AuraCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    AnimatedVisibility(visible = showProtocolSchema) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Text(
                                text = """
{
  "protocolVersion": "2.4",
  "assistantIdentity": "Parul",
  "voiceProfile": {
    "gender": "female",
    "pitch": 1.22,
    "speed": 1.02
  },
  "supportedClients": ["Android", "iOS", "macOS", "Windows", "Web"],
  "wakeWord": {
    "primary": "Hey Parul",
    "aliases": ["Parul", "Ok Parul"],
    "arbitrationStrategy": "nearest_device_first"
  },
  "intentProcessing": {
    "engine": "gemini-3.5-flash",
    "offlineFallback": "deterministic_nlp_rulebase"
  }
}
                                """.trimIndent(),
                                fontFamily = FontFamily.Monospace,
                                color = AuraCyan,
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF090B16), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun DeviceItemRow(
    device: ConnectedDevice,
    onToggleWake: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (device.isCurrentDevice) AuraDarkCardElevated else AuraDarkCard
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (device.isCurrentDevice) AuraCyan.copy(alpha = 0.4f) else AuraDarkBorder
            )
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = when (device.platform) {
                "Android" -> Icons.Default.PhoneAndroid
                "iOS" -> if (device.name.contains("iPad")) Icons.Default.TabletMac else Icons.Default.PhoneIphone
                "macOS" -> Icons.Default.Computer
                else -> Icons.Default.Computer
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (device.isOnline) AuraCyan.copy(alpha = 0.15f) else AuraDarkBorder.copy(alpha = 0.3f))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (device.isOnline) AuraCyan else AuraTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = device.name,
                        color = AuraTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    if (device.isCurrentDevice) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AuraCyan.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "THIS DEVICE",
                                color = AuraCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Text(
                    text = "${device.platform} • ${device.lastSyncText}",
                    color = AuraTextMuted,
                    fontSize = 11.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (device.wakeWordActive) "\"Hey Parul\" On" else "Wake Off",
                    color = if (device.wakeWordActive) AuraCyan else AuraTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
                Switch(
                    checked = device.wakeWordActive,
                    onCheckedChange = { onToggleWake() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = AuraCyan
                    ),
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

@Composable
fun SimulateCommandButton(
    platformLabel: String,
    command: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0F1226))
            .border(1.dp, AuraDarkBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(AuraViolet)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = platformLabel,
                    color = AuraCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "\"$command\"",
                    color = AuraTextPrimary,
                    fontSize = 12.sp
                )
            }
        }
    }
}
