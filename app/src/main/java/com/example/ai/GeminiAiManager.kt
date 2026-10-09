package com.example.ai

import com.example.BuildConfig
import com.example.data.model.EmailItem
import com.example.data.model.Priority
import com.example.data.model.TaskCategory
import com.example.data.model.TaskItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class CommandExecutionResult(
    val spokenResponse: String,
    val visualResponse: String,
    val createdTask: TaskItem? = null,
    val actionType: String = "INFO" // "TASK_CREATED", "EMAILS_SUMMARIZED", "QUERY_SCHEDULE", "INFO"
)

class GeminiAiManager {

    private fun isApiKeyConfigured(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
    }

    suspend fun processVoiceOrTextCommand(
        rawCommand: String,
        originDevice: String = "Android Device",
        existingTasks: List<TaskItem> = emptyList(),
        existingEmails: List<EmailItem> = emptyList(),
        language: com.example.voice.AppLanguage = com.example.voice.AppLanguage.ENGLISH
    ): CommandExecutionResult = withContext(Dispatchers.IO) {
        val cleaned = LocalNlpParser.cleanWakeWord(rawCommand)
        val localIntent = LocalNlpParser.parseIntent(rawCommand, language)

        // If Gemini API Key is configured, attempt intelligent model query first
        if (isApiKeyConfigured()) {
            try {
                val geminiPrompt = buildPromptForCommand(cleaned, existingTasks, existingEmails, language)
                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiContentPart(text = geminiPrompt))
                        )
                    )
                )
                val response = GeminiClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!responseText.isNullOrBlank()) {
                    val parsedResult = tryParseGeminiResponse(responseText, cleaned, originDevice)
                    if (parsedResult != null) {
                        return@withContext parsedResult
                    }
                }
            } catch (e: Exception) {
                // Graceful fallback to deterministic local parser
            }
        }

        // Deterministic local processing fallback (guarantees offline & initial setup functionality)
        when (localIntent) {
            is ParsedVoiceIntent.TurnOn -> {
                val spoken = when (language) {
                    com.example.voice.AppLanguage.HINDI -> "नमस्ते! मैं पारुल हूँ। बताइए क्या शेड्यूल या ईमेल सारांश करना है?"
                    com.example.voice.AppLanguage.BENGALI -> "নমস্কার! আমি পারুল। বলুন কিভাবে সাহায্য করতে পারি?"
                    else -> "I'm here! How can I help you?"
                }
                CommandExecutionResult(
                    spokenResponse = spoken,
                    visualResponse = "Parul is ON and listening for commands (${language.displayName}).",
                    actionType = "ASSISTANT_TURNED_ON"
                )
            }

            is ParsedVoiceIntent.TurnOff -> {
                val spoken = when (language) {
                    com.example.voice.AppLanguage.HINDI -> "अलविदा! जब भी जरूरत हो, 'हे पारुल' बोलिएगा।"
                    com.example.voice.AppLanguage.BENGALI -> "বিদায়! প্রয়োজন হলে 'হে পারুল' বলবেন।"
                    else -> "Goodbye! Call 'Hey Parul' whenever you need me."
                }
                CommandExecutionResult(
                    spokenResponse = spoken,
                    visualResponse = "Parul is now OFF (Standby Mode). Say 'Hey Parul' to turn back on.",
                    actionType = "ASSISTANT_TURNED_OFF"
                )
            }

            is ParsedVoiceIntent.CreateTask -> {
                val res = localIntent.taskResult
                val newTask = TaskItem(
                    title = res.title,
                    description = res.description,
                    dueDate = res.dueDate,
                    dueTime = res.dueTime,
                    priority = res.priority,
                    category = res.category,
                    isCompleted = false,
                    source = "Voice Command ($originDevice)",
                    originDevice = originDevice,
                    rawCommand = rawCommand
                )
                CommandExecutionResult(
                    spokenResponse = res.spokenConfirmation,
                    visualResponse = "Created event: '${res.title}' on ${res.dueDate} at ${res.dueTime} [Priority: ${res.priority}]",
                    createdTask = newTask,
                    actionType = "TASK_CREATED"
                )
            }

            is ParsedVoiceIntent.SummarizeEmails -> {
                val count = existingEmails.size
                val unreadCount = existingEmails.count { !it.isRead }
                val urgentCount = existingEmails.count { it.urgencyScore >= 8 }
                val summaryText = if (existingEmails.isEmpty()) {
                    "Your inbox is all clear. No pending emails require attention."
                } else {
                    "You have $count emails, with $unreadCount unread and $urgentCount flagged as high priority. The most critical is from ${existingEmails.firstOrNull { it.urgencyScore >= 8 }?.senderName ?: existingEmails.first().senderName} regarding ${existingEmails.first().subject}."
                }
                CommandExecutionResult(
                    spokenResponse = summaryText,
                    visualResponse = "Daily Email Digest Generated: $count total emails analyzed ($unreadCount unread, $urgentCount urgent). Action items highlighted in Emails tab.",
                    actionType = "EMAILS_SUMMARIZED"
                )
            }

            is ParsedVoiceIntent.QuerySchedule -> {
                val pending = existingTasks.filter { !it.isCompleted }
                val text = if (pending.isEmpty()) {
                    "Your schedule is open! No tasks scheduled for today."
                } else {
                    val firstThree = pending.take(3).joinToString("; ") { "${it.title} at ${it.dueTime.ifBlank { "anytime" }}" }
                    "You have ${pending.size} upcoming tasks: $firstThree."
                }
                CommandExecutionResult(
                    spokenResponse = text,
                    visualResponse = text,
                    actionType = "QUERY_SCHEDULE"
                )
            }

            is ParsedVoiceIntent.DraftReply -> {
                val targetEmail = existingEmails.firstOrNull {
                    it.senderName.contains(localIntent.targetPerson, ignoreCase = true) ||
                            it.subject.contains(localIntent.targetPerson, ignoreCase = true)
                } ?: existingEmails.firstOrNull()

                val replyText = targetEmail?.generatedQuickReply?.ifBlank {
                    "Hi ${targetEmail.senderName}, thanks for reaching out. I've received your note and will review today."
                } ?: "Ready to draft your reply. Who would you like to email?"

                CommandExecutionResult(
                    spokenResponse = "Drafted reply for ${targetEmail?.senderName ?: "recipient"}.",
                    visualResponse = "Draft: \"$replyText\"",
                    actionType = "INFO"
                )
            }

            is ParsedVoiceIntent.DeviceSyncAction -> {
                val msg = "Cross-platform command relayed to ${localIntent.targetDevice}: \"${localIntent.action}\""
                CommandExecutionResult(
                    spokenResponse = "Synced command to ${localIntent.targetDevice}.",
                    visualResponse = msg,
                    actionType = "INFO"
                )
            }

            is ParsedVoiceIntent.PhoneAction -> {
                val spoken = when (language) {
                    com.example.voice.AppLanguage.HINDI -> "आपकी अनुमति से ${localIntent.description} खोला जा रहा है।"
                    com.example.voice.AppLanguage.BENGALI -> "আপনার অনুমতি নিয়ে ${localIntent.description} খোলা হচ্ছে।"
                    else -> "Opening ${localIntent.description.removePrefix("Opening ")} with your permission."
                }
                CommandExecutionResult(
                    spokenResponse = spoken,
                    visualResponse = "Phone Control Action: ${localIntent.description} (Executed with user permission).",
                    actionType = "PHONE_ACTION_${localIntent.actionType.name}"
                )
            }

            is ParsedVoiceIntent.GeneralAssistantQuery -> {
                CommandExecutionResult(
                    spokenResponse = "Hi! I'm Parul, your executive assistant. I can schedule tasks, summarize daily emails, and follow any voice command. Try saying: 'Schedule a meeting with John for next Tuesday at 10 AM'.",
                    visualResponse = "Parul is ready for your voice command. You can schedule meetings, check schedules, or generate daily email digests.",
                    actionType = "INFO"
                )
            }
        }
    }

    private fun buildPromptForCommand(
        command: String,
        tasks: List<TaskItem>,
        emails: List<EmailItem>,
        language: com.example.voice.AppLanguage
    ): String {
        return """
            You are Parul, an articulate female AI productivity assistant.
            Target Language: ${language.displayName} (${language.nativeName}).
            The user said: "$command"
            
            Current context:
            - Pending Tasks count: ${tasks.count { !it.isCompleted }}
            - Total Emails: ${emails.size} (Unread: ${emails.count { !it.isRead }})
            
            Determine if the user wants to:
            1. CREATE_TASK: Schedule a meeting, appointment, reminder, or task. Extract title, dueDate, dueTime, priority (HIGH, MEDIUM, LOW), category (MEETING, WORK, PERSONAL, URGENT).
            2. SUMMARIZE_EMAILS: Summarize daily emails or check messages.
            3. QUERY_SCHEDULE: Review agenda or today's tasks.
            4. OTHER: Answer helpfully in Parul's warm, professional female voice persona in ${language.displayName}.
            
            Respond strictly in valid JSON format with "spokenResponse" written naturally in ${language.displayName}:
            {
              "intent": "CREATE_TASK" | "SUMMARIZE_EMAILS" | "QUERY_SCHEDULE" | "OTHER",
              "spokenResponse": "concise spoken response for female Text-To-Speech in ${language.displayName}",
              "visualResponse": "detailed visual summary",
              "taskDetails": {
                "title": "string",
                "description": "string",
                "dueDate": "string (e.g. Next Tuesday, Tomorrow, Today)",
                "dueTime": "string (e.g. 10:00 AM)",
                "priority": "HIGH" | "MEDIUM" | "LOW",
                "category": "MEETING" | "WORK" | "PERSONAL" | "URGENT"
              }
            }
        """.trimIndent()
    }

    private fun tryParseGeminiResponse(rawJson: String, originalCommand: String, originDevice: String): CommandExecutionResult? {
        return try {
            val cleanJson = rawJson.substringAfter("{").substringBeforeLast("}")
            val json = JSONObject("{$cleanJson}")
            val intent = json.optString("intent", "OTHER")
            val spoken = json.optString("spokenResponse", "Understood.")
            val visual = json.optString("visualResponse", spoken)

            if (intent == "CREATE_TASK" && json.has("taskDetails")) {
                val td = json.getJSONObject("taskDetails")
                val title = td.optString("title", "New Task")
                val desc = td.optString("description", "")
                val dueDate = td.optString("dueDate", "Today")
                val dueTime = td.optString("dueTime", "10:00 AM")
                val prioStr = td.optString("priority", "MEDIUM")
                val catStr = td.optString("category", "WORK")

                val priority = try { Priority.valueOf(prioStr) } catch (e: Exception) { Priority.MEDIUM }
                val category = try { TaskCategory.valueOf(catStr) } catch (e: Exception) { TaskCategory.WORK }

                val task = TaskItem(
                    title = title,
                    description = desc,
                    dueDate = dueDate,
                    dueTime = dueTime,
                    priority = priority,
                    category = category,
                    isCompleted = false,
                    source = "Gemini AI Voice ($originDevice)",
                    originDevice = originDevice,
                    rawCommand = originalCommand
                )
                return CommandExecutionResult(
                    spokenResponse = spoken,
                    visualResponse = visual,
                    createdTask = task,
                    actionType = "TASK_CREATED"
                )
            }

            CommandExecutionResult(
                spokenResponse = spoken,
                visualResponse = visual,
                actionType = if (intent == "SUMMARIZE_EMAILS") "EMAILS_SUMMARIZED" else if (intent == "QUERY_SCHEDULE") "QUERY_SCHEDULE" else "INFO"
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun generateDailyBriefing(emails: List<EmailItem>): String = withContext(Dispatchers.IO) {
        if (emails.isEmpty()) return@withContext "No new emails. Your inbox is completely clear."

        if (isApiKeyConfigured()) {
            try {
                val emailContext = emails.joinToString("\n---\n") {
                    "From: ${it.senderName} (${it.senderEmail})\nSubject: ${it.subject}\nBody: ${it.body}\nUrgency: ${it.urgencyScore}/10"
                }
                val prompt = """
                    You are Parul, an executive assistant with a clear, warm female voice persona. Generate a crisp, high-impact Daily Email Digest for the user.
                    Emails:
                    $emailContext
                    
                    Format your response with:
                    1. Executive Summary (2 sentences)
                    2. Top Urgent Action Items (bullet points with immediate deadlines)
                    3. Recommended Schedule Tasks
                """.trimIndent()
                val request = GeminiRequest(
                    contents = listOf(GeminiContent(parts = listOf(GeminiContentPart(text = prompt))))
                )
                val response = GeminiClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) return@withContext text
            } catch (e: Exception) {
                // fallback
            }
        }

        // Smart synthesized fallback digest
        val urgent = emails.filter { it.urgencyScore >= 7 }
        val sb = StringBuilder()
        sb.append("📊 EXECUTIVE INBOX BRIEFING\n\n")
        sb.append("You have ${emails.size} emails analyzed today (${emails.count { !it.isRead }} unread). ")
        sb.append("Primary focus is required on ${urgent.size} urgent communications.\n\n")
        sb.append("⚡ TOP ACTION ITEMS:\n")
        for (u in urgent) {
            sb.append("• ${u.senderName}: ${u.suggestedAction.ifBlank { u.subject }}\n")
        }
        sb.append("\n📅 SUGGESTED SCHEDULE:\n")
        for (u in urgent) {
            if (u.suggestedTaskTitle.isNotBlank()) {
                sb.append("• ${u.suggestedTaskTitle} (${u.suggestedTaskTime})\n")
            }
        }
        sb.toString()
    }
}
