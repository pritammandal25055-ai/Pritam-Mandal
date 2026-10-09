package com.example.phone

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class PhoneActionType {
    OPEN_CALENDAR,
    OPEN_SETTINGS,
    OPEN_EMAIL_APP,
    OPEN_ALARM,
    OPEN_CAMERA,
    DRAFT_SMS,
    DIAL_PHONE,
    NONE
}

data class PhoneControlState(
    val isPermissionGranted: Boolean = false,
    val requireConfirmationPerAction: Boolean = true,
    val lastActionExecuted: String = "",
    val pendingAction: PhoneActionType = PhoneActionType.NONE,
    val pendingActionDescription: String = "",
    val showPermissionConsentDialog: Boolean = false
)

class PhoneControlManager(private val context: Context) {

    private val _controlState = MutableStateFlow(PhoneControlState())
    val controlState: StateFlow<PhoneControlState> = _controlState.asStateFlow()

    fun setPermissionGranted(granted: Boolean) {
        _controlState.value = _controlState.value.copy(
            isPermissionGranted = granted,
            showPermissionConsentDialog = false
        )
    }

    fun setRequireConfirmation(require: Boolean) {
        _controlState.value = _controlState.value.copy(requireConfirmationPerAction = require)
    }

    fun requestActionExecution(action: PhoneActionType, description: String, onExecute: () -> Unit) {
        if (!_controlState.value.isPermissionGranted) {
            // Need user consent first
            _controlState.value = _controlState.value.copy(
                pendingAction = action,
                pendingActionDescription = description,
                showPermissionConsentDialog = true
            )
            return
        }

        // Permission already granted: execute
        executeAction(action)
        onExecute()
    }

    fun confirmPendingAction() {
        val action = _controlState.value.pendingAction
        if (action != PhoneActionType.NONE) {
            _controlState.value = _controlState.value.copy(isPermissionGranted = true)
            executeAction(action)
        }
        _controlState.value = _controlState.value.copy(
            showPermissionConsentDialog = false,
            pendingAction = PhoneActionType.NONE
        )
    }

    fun dismissConsentDialog() {
        _controlState.value = _controlState.value.copy(
            showPermissionConsentDialog = false,
            pendingAction = PhoneActionType.NONE
        )
    }

    fun executeAction(action: PhoneActionType): Boolean {
        return try {
            val intent = when (action) {
                PhoneActionType.OPEN_CALENDAR -> {
                    Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("content://com.android.calendar/time")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                }
                PhoneActionType.OPEN_SETTINGS -> {
                    Intent(Settings.ACTION_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                }
                PhoneActionType.OPEN_EMAIL_APP -> {
                    Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_APP_EMAIL)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                }
                PhoneActionType.OPEN_ALARM -> {
                    Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                }
                PhoneActionType.OPEN_CAMERA -> {
                    Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                }
                PhoneActionType.DRAFT_SMS -> {
                    Intent(Intent.ACTION_VIEW, Uri.parse("sms:")).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                }
                PhoneActionType.DIAL_PHONE -> {
                    Intent(Intent.ACTION_DIAL).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                }
                PhoneActionType.NONE -> null
            }

            if (intent != null && intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                _controlState.value = _controlState.value.copy(
                    lastActionExecuted = action.name
                )
                true
            } else if (intent != null) {
                // Try starting anyway with NEW_TASK
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}
