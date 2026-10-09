package com.example.voice

import com.example.data.model.ConnectedDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CrossPlatformVoiceEngine {

    private val _devices = MutableStateFlow<List<ConnectedDevice>>(
        listOf(
            ConnectedDevice(
                id = "dev_android_01",
                name = "Google Pixel 9 Pro",
                platform = "Android",
                iconName = "android",
                isCurrentDevice = true,
                isOnline = true,
                wakeWordActive = true,
                lastSyncText = "Active (Host Node)"
            ),
            ConnectedDevice(
                id = "dev_ios_01",
                name = "Apple iPhone 16 Pro",
                platform = "iOS",
                iconName = "phone_iphone",
                isCurrentDevice = false,
                isOnline = true,
                wakeWordActive = true,
                lastSyncText = "Synced 2m ago"
            ),
            ConnectedDevice(
                id = "dev_mac_01",
                name = "MacBook Pro 16\" (M3 Max)",
                platform = "macOS",
                iconName = "laptop_mac",
                isCurrentDevice = false,
                isOnline = true,
                wakeWordActive = true,
                lastSyncText = "Synced 1m ago"
            ),
            ConnectedDevice(
                id = "dev_ipad_01",
                name = "iPad Pro 13\" (M4)",
                platform = "iOS",
                iconName = "tablet_mac",
                isCurrentDevice = false,
                isOnline = true,
                wakeWordActive = false,
                lastSyncText = "Standby"
            ),
            ConnectedDevice(
                id = "dev_win_01",
                name = "Windows 11 Workstation",
                platform = "Windows",
                iconName = "desktop_windows",
                isCurrentDevice = false,
                isOnline = false,
                wakeWordActive = false,
                lastSyncText = "Offline (Last seen 2h ago)"
            )
        )
    )
    val devices: StateFlow<List<ConnectedDevice>> = _devices.asStateFlow()

    private val _arbitrationActive = MutableStateFlow(true)
    val arbitrationActive: StateFlow<Boolean> = _arbitrationActive.asStateFlow()

    fun toggleDeviceWakeWord(deviceId: String) {
        _devices.value = _devices.value.map { dev ->
            if (dev.id == deviceId) dev.copy(wakeWordActive = !dev.wakeWordActive) else dev
        }
    }

    fun toggleArbitration() {
        _arbitrationActive.value = !_arbitrationActive.value
    }
}
