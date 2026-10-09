package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.AuraRepository
import com.example.ui.AuraApp
import com.example.ui.AuraViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.voice.VoiceInteractionManager

class MainActivity : ComponentActivity() {

    private lateinit var voiceInteractionManager: VoiceInteractionManager
    private lateinit var viewModel: AuraViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = AuraRepository(database)

        voiceInteractionManager = VoiceInteractionManager(this) { command, isWakeWord ->
            viewModel.executeCommand(
                commandText = command,
                isWakeWord = isWakeWord,
                originDevice = "Pixel 9 Pro",
                platform = "Android"
            )
        }

        val phoneControlManager = com.example.phone.PhoneControlManager(this)

        viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AuraViewModel(
                        repository = repository,
                        voiceManager = voiceInteractionManager,
                        phoneControlManager = phoneControlManager
                    ) as T
                }
            }
        )[AuraViewModel::class.java]

        setContent {
            MyApplicationTheme(darkTheme = true) {
                AuraApp(viewModel = viewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::voiceInteractionManager.isInitialized) {
            voiceInteractionManager.cleanup()
        }
    }
}
