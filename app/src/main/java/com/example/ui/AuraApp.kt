package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.CreateTaskDialog
import com.example.ui.components.DailyDigestDialog
import com.example.ui.components.PhonePermissionDialog
import com.example.ui.screens.AssistantScreen
import com.example.ui.screens.CrossPlatformScreen
import com.example.ui.screens.EmailsScreen
import com.example.ui.screens.ScheduleScreen
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraDarkBg
import com.example.ui.theme.AuraDarkBorder
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkSurface
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuraApp(
    viewModel: AuraViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Audio recording permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onMicrophoneClick()
        }
    }

    val onMicAction = {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.onMicrophoneClick()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AuraViolet)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AuraCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Parul AI",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = AuraTextPrimary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AuraCyan.copy(alpha = 0.15f))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Female Voice",
                                color = AuraCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    var showLangMenu by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

                    // Language Selector Dropdown
                    Box(modifier = Modifier.padding(end = 4.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(AuraViolet.copy(alpha = 0.25f))
                                .border(1.dp, AuraViolet.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .clickable { showLangMenu = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("language_selector_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = null,
                                    tint = AuraCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = uiState.currentLanguage.nativeName,
                                    color = AuraCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        androidx.compose.material3.DropdownMenu(
                            expanded = showLangMenu,
                            onDismissRequest = { showLangMenu = false }
                        ) {
                            com.example.voice.AppLanguage.values().forEach { lang ->
                                androidx.compose.material3.DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${lang.displayName} (${lang.nativeName})",
                                            fontWeight = if (uiState.currentLanguage == lang) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        viewModel.setLanguage(lang)
                                        showLangMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Mute / Unmute Voice Button
                    IconButton(
                        onClick = { viewModel.toggleMuteTts() },
                        modifier = Modifier.testTag("toggle_voice_mute")
                    ) {
                        Icon(
                            imageVector = if (uiState.isTtsMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Toggle Audio Feedback",
                            tint = if (uiState.isTtsMuted) AuraTextMuted else AuraCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AuraDarkSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = AuraDarkSurface,
                contentColor = AuraTextSecondary,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                val items = listOf(
                    Triple(0, "Parul", Icons.Default.Mic),
                    Triple(1, "Schedule", Icons.Default.CalendarMonth),
                    Triple(2, "Emails", Icons.Default.Email),
                    Triple(3, "Ecosystem", Icons.Default.Devices)
                )

                items.forEach { (index, label, icon) ->
                    val isSelected = uiState.selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedTab(index) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = AuraCyan,
                            indicatorColor = AuraCyan,
                            unselectedIconColor = AuraTextMuted,
                            unselectedTextColor = AuraTextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_$index")
                    )
                }
            }
        },
        containerColor = AuraDarkBg,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = uiState.selectedTab, label = "tab_crossfade") { tab ->
                when (tab) {
                    0 -> AssistantScreen(
                        uiState = uiState,
                        onOrbClick = onMicAction,
                        onSendCommand = { cmd -> viewModel.executeCommand(cmd) },
                        onReplayAudio = { text -> viewModel.voiceManager.speak(text) },
                        onToggleMute = { viewModel.toggleMuteTts() },
                        onToggleWakeWord = { viewModel.toggleWakeWordListening() }
                    )
                    1 -> ScheduleScreen(
                        uiState = uiState,
                        onToggleComplete = { task -> viewModel.toggleTaskCompleted(task) },
                        onDeleteTask = { task -> viewModel.deleteTask(task) },
                        onOpenCreateTask = { viewModel.setCreateTaskDialogOpen(true) },
                        onFilterChange = { filter -> viewModel.setTaskFilter(filter) },
                        onVoiceSchedulePrompt = onMicAction
                    )
                    2 -> EmailsScreen(
                        uiState = uiState,
                        onGenerateDigest = { viewModel.generateDailyEmailDigest() },
                        onAddTaskFromEmail = { email -> viewModel.addTaskFromEmail(email) },
                        onToggleStar = { email -> viewModel.toggleEmailStar(email) },
                        onToggleRead = { email -> viewModel.toggleEmailRead(email) },
                        onFilterChange = { filter -> viewModel.setEmailFilter(filter) }
                    )
                    3 -> CrossPlatformScreen(
                        uiState = uiState,
                        onToggleDeviceWakeWord = { id -> viewModel.toggleDeviceWakeWord(id) },
                        onToggleWakeWordGlobal = { viewModel.toggleWakeWordListening() },
                        onSetSensitivity = { s -> viewModel.setWakeWordSensitivity(s) },
                        onSetLanguage = { lang -> viewModel.setLanguage(lang) },
                        onTogglePhonePermission = { granted -> viewModel.setPhoneControlPermission(granted) },
                        onExecutePhoneActionDirect = { action -> viewModel.executePhoneActionDirect(action) },
                        onSimulateCommand = { dev, plat, cmd ->
                            viewModel.simulateCrossDeviceCommand(dev, plat, cmd)
                        }
                    )
                }
            }

            // Daily Digest Modal
            if (uiState.isDailyDigestOpen) {
                DailyDigestDialog(
                    digestContent = uiState.dailyDigestContent,
                    onDismiss = { viewModel.dismissDailyDigest() },
                    onSpeakBriefing = { text -> viewModel.voiceManager.speak(text) }
                )
            }

            // Create Task Dialog
            if (uiState.isCreateTaskDialogOpen) {
                CreateTaskDialog(
                    onDismiss = { viewModel.setCreateTaskDialogOpen(false) },
                    onCreateTask = { title, date, time, priority, category, desc ->
                        viewModel.createManualTask(title, date, time, priority, category, desc)
                    }
                )
            }

            // Phone Control User Permission Dialog
            if (uiState.phoneControlState.showPermissionConsentDialog) {
                PhonePermissionDialog(
                    actionDescription = uiState.phoneControlState.pendingActionDescription,
                    onAllow = { viewModel.confirmPendingPhoneAction() },
                    onDeny = { viewModel.dismissPhoneActionDialog() }
                )
            }
        }
    }
}
