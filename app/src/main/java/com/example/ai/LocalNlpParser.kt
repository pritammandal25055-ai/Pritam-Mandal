package com.example.ai

import com.example.data.model.Priority
import com.example.data.model.TaskCategory
import com.example.data.model.TaskItem
import com.example.phone.PhoneActionType
import com.example.voice.AppLanguage
import java.util.Locale
import java.util.regex.Pattern

data class ParsedTaskResult(
    val title: String,
    val description: String,
    val dueDate: String,
    val dueTime: String,
    val priority: Priority,
    val category: TaskCategory,
    val spokenConfirmation: String
)

sealed class ParsedVoiceIntent {
    object TurnOn : ParsedVoiceIntent()
    object TurnOff : ParsedVoiceIntent()
    data class CreateTask(val taskResult: ParsedTaskResult) : ParsedVoiceIntent()
    data class SummarizeEmails(val filter: String = "ALL") : ParsedVoiceIntent()
    data class QuerySchedule(val dateFilter: String = "TODAY") : ParsedVoiceIntent()
    data class DraftReply(val targetPerson: String) : ParsedVoiceIntent()
    data class DeviceSyncAction(val targetDevice: String, val action: String) : ParsedVoiceIntent()
    data class PhoneAction(val actionType: PhoneActionType, val description: String) : ParsedVoiceIntent()
    data class GeneralAssistantQuery(val query: String) : ParsedVoiceIntent()
}

object LocalNlpParser {

    fun cleanWakeWord(input: String): String {
        var text = input.trim()
        val wakePrefixes = listOf(
            // English
            "hey parul,", "hey parul", "parul,", "parul",
            "ok parul,", "ok parul", "hey assistant,", "hey assistant",
            // Hindi
            "हे पारुल,", "हे पारुल", "सुनो पारुल,", "सुनो पारुल",
            // Bengali
            "হে পারুল,", "হে পারুল", "শুনছো পারুল,", "শুনছো পারুল"
        )
        for (prefix in wakePrefixes) {
            if (text.lowercase(Locale.ROOT).startsWith(prefix.lowercase(Locale.ROOT))) {
                text = text.substring(prefix.length).trim()
                if (text.startsWith(",") || text.startsWith(":") || text.startsWith("|")) {
                    text = text.substring(1).trim()
                }
                break
            }
        }
        return text
    }

    fun parseIntent(rawInput: String, language: AppLanguage = AppLanguage.ENGLISH): ParsedVoiceIntent {
        val lowerRaw = rawInput.trim().lowercase(Locale.ROOT)
        val cleaned = cleanWakeWord(rawInput)
        val lower = cleaned.lowercase(Locale.ROOT)

        // Turn Off / Bye intents (English, Hindi, Bengali)
        val byeKeywords = listOf(
            "bye", "bye parul", "goodbye", "goodbye parul", "turn off", "sleep", "stop parul",
            // Hindi
            "अलविदा", "बाय", "बाय पारुल", "अलविदा पारुल", "बंद करो",
            // Bengali
            "বিদায়", "বাই", "বিদায় পারুল", "বাই পারুল", "বন্ধ করো"
        )
        if (byeKeywords.any { lowerRaw == it || lower.startsWith(it) }) {
            return ParsedVoiceIntent.TurnOff
        }

        // Turn On / Wake intents (English, Hindi, Bengali)
        val wakeKeywords = listOf(
            "hey parul", "parul", "ok parul", "turn on", "wake up",
            // Hindi
            "हे पारुल", "सुनो पारुल", "चालू करो",
            // Bengali
            "হে পারুল", "শুনছো পারুল", "চালু করো"
        )
        if (wakeKeywords.any { lowerRaw == it } || (cleaned.isBlank() && lowerRaw.contains("parul"))) {
            return ParsedVoiceIntent.TurnOn
        }

        // 1. Email summarization intents
        val emailKeywords = listOf(
            "summarize", "email", "inbox", "message", "daily email", "unread email",
            // Hindi
            "ईमेल", "सारांश", "इमेल", "संदेश",
            // Bengali
            "ইমেইল", "সংক্ষেপ", "ইমেল", "বার্তা"
        )
        val hasEmailIntent = (lower.contains("summarize") && (lower.contains("email") || lower.contains("inbox"))) ||
                (lower.contains("ईमेल") && lower.contains("सारांश")) ||
                (lower.contains("ইমেইল") && lower.contains("সংক্ষেপ")) ||
                lower.contains("daily email") || lower.contains("what are my emails")

        if (hasEmailIntent) {
            val filter = when {
                lower.contains("unread") || lower.contains("अपठित") || lower.contains("অপঠিত") -> "UNREAD"
                lower.contains("urgent") || lower.contains("जरूरी") || lower.contains("জরুরি") -> "URGENT"
                else -> "ALL"
            }
            return ParsedVoiceIntent.SummarizeEmails(filter)
        }

        // 2. Schedule query intents
        val isQuerySchedule = (lower.contains("what") && lower.contains("schedule")) ||
                (lower.contains("what") && lower.contains("tasks")) ||
                (lower.contains("show") && (lower.contains("task") || lower.contains("schedule"))) ||
                lower.contains("schedule") || lower.contains("कार्यसूची") || lower.contains("রুটিন") ||
                lower.contains("আজকের কাজ") || lower.contains("আজকে কি আছে") || lower.contains("आज क्या है")

        if (isQuerySchedule && !lower.startsWith("schedule ") && !lower.contains("schedule a") && !lower.contains("मीटिंग") && !lower.contains("শিডিউল")) {
            val dateFilter = when {
                lower.contains("tomorrow") || lower.contains("कल") || lower.contains("কাল") -> "TOMORROW"
                else -> "TODAY"
            }
            return ParsedVoiceIntent.QuerySchedule(dateFilter)
        }

        // 3. Draft reply intents
        if (lower.startsWith("draft reply") || lower.startsWith("reply to") || lower.contains("draft a reply") ||
            lower.contains("जवाब लिखो") || lower.contains("উত্তর লিখুন")
        ) {
            val target = cleaned.replace(Regex("(?i)draft (a )?reply to "), "").replace(Regex("(?i)reply to "), "").trim()
            return ParsedVoiceIntent.DraftReply(target.ifBlank { "sender" })
        }

        // 4. Cross device intents
        if (lower.contains("send to desktop") || lower.contains("send to mac") || lower.contains("sync to ios")) {
            val target = if (lower.contains("desktop") || lower.contains("mac")) "MacBook Pro" else "iPhone 16"
            return ParsedVoiceIntent.DeviceSyncAction(target, cleaned)
        }

        // 4b. Phone Control intents (Calendar, Settings, Camera, Alarm, Phone)
        if (lower.contains("open calendar") || lower.contains("show calendar") || lower.contains("कैलेंडर खोलो") || lower.contains("ক্যালেন্ডার খোলো")) {
            return ParsedVoiceIntent.PhoneAction(PhoneActionType.OPEN_CALENDAR, "Opening system Calendar")
        }
        if (lower.contains("open settings") || lower.contains("phone settings") || lower.contains("सेटिंग्स खोलो") || lower.contains("সেটিংস খোলো")) {
            return ParsedVoiceIntent.PhoneAction(PhoneActionType.OPEN_SETTINGS, "Opening phone Settings")
        }
        if (lower.contains("open camera") || lower.contains("launch camera") || lower.contains("कैमरा खोलो") || lower.contains("ক্যামেরা খোলো")) {
            return ParsedVoiceIntent.PhoneAction(PhoneActionType.OPEN_CAMERA, "Opening Camera")
        }
        if (lower.contains("open alarm") || lower.contains("show alarm") || lower.contains("अलार्म खोलो") || lower.contains("অ্যালার্ম খোলো")) {
            return ParsedVoiceIntent.PhoneAction(PhoneActionType.OPEN_ALARM, "Opening Alarms")
        }
        if (lower.contains("dial phone") || lower.contains("make a call") || lower.contains("open dialer") || lower.contains("कॉल करो") || lower.contains("কল করো")) {
            return ParsedVoiceIntent.PhoneAction(PhoneActionType.DIAL_PHONE, "Opening Phone Dialer")
        }

        // 5. Task creation intents
        val isTaskIntent = lower.startsWith("schedule") ||
                lower.startsWith("set a meeting") ||
                lower.startsWith("set meeting") ||
                lower.startsWith("add task") ||
                lower.startsWith("create task") ||
                lower.startsWith("personal task") ||
                lower.startsWith("work task") ||
                lower.startsWith("urgent task") ||
                lower.contains("personal task") ||
                lower.contains("work task") ||
                lower.contains("urgent task") ||
                lower.startsWith("remind me to") ||
                lower.contains("meeting with") ||
                lower.contains("schedule a") ||
                // Hindi task indicators
                lower.contains("मीटिंग") || lower.contains("शेड्यूल") || lower.contains("टास्क") || lower.contains("कार्य") ||
                // Bengali task indicators
                lower.contains("শিডিউল") || lower.contains("মিটিং") || lower.contains("টাস্ক") || lower.contains("কাজ যোগ")

        if (isTaskIntent) {
            val taskResult = extractTaskDetails(cleaned, language)
            return ParsedVoiceIntent.CreateTask(taskResult)
        }

        // 6. Default to general assistant query
        return ParsedVoiceIntent.GeneralAssistantQuery(cleaned)
    }

    fun extractTaskDetails(text: String, language: AppLanguage = AppLanguage.ENGLISH): ParsedTaskResult {
        var working = text.trim()

        val prefixes = listOf(
            "schedule a personal task to", "schedule personal task to", "schedule a personal task", "schedule personal task",
            "schedule an urgent task to", "schedule urgent task to", "schedule an urgent task", "schedule urgent task",
            "schedule a work task to", "schedule work task to", "schedule a work task", "schedule work task",
            "add a personal task to", "add personal task to", "add a personal task", "add personal task",
            "add an urgent task to", "add urgent task to", "add an urgent task", "add urgent task",
            "add a work task to", "add work task to", "add a work task", "add work task",
            "schedule a meeting with", "schedule meeting with", "schedule a meeting", "schedule an appointment with",
            "schedule an appointment", "schedule a", "schedule", "remind me to", "add a task to", "add task to",
            "add task", "create a task to", "create task to", "create a task", "create task", "set a meeting with",
            "set a meeting", "set meeting with", "set meeting",
            "personal task:", "personal task", "urgent task:", "urgent task", "work task:", "work task",
            // Hindi
            "व्यक्तिगत कार्य जोड़ो", "व्यक्तिगत कार्य", "जरूरी कार्य जोड़ो", "जरूरी कार्य", "काम का कार्य",
            "मीटिंग शेड्यूल करो", "शेड्यूल करो", "टास्क जोड़ो",
            // Bengali
            "ব্যক্তিগত কাজ যোগ করো", "ব্যক্তিগত কাজ", "জরুরি কাজ যোগ করো", "জরুরি কাজ",
            "মিটিং শিডিউল করো", "শিডিউল করো", "টাস্ক তৈরি করো"
        )

        var matchedPrefix = ""
        for (p in prefixes) {
            if (working.lowercase(Locale.ROOT).startsWith(p.lowercase(Locale.ROOT))) {
                matchedPrefix = p
                working = working.substring(p.length).trim()
                break
            }
        }

        val priority = when {
            working.lowercase(Locale.ROOT).contains("urgent") ||
                    working.contains("जरूरी") || working.contains("জরুরি") ||
                    matchedPrefix.lowercase(Locale.ROOT).contains("urgent") ||
                    working.lowercase(Locale.ROOT).contains("high priority") -> Priority.HIGH
            working.lowercase(Locale.ROOT).contains("low priority") -> Priority.LOW
            else -> Priority.MEDIUM
        }

        val lowerWorkingForCategory = working.lowercase(Locale.ROOT)
        val matchedPrefixLower = matchedPrefix.lowercase(Locale.ROOT)
        val category = when {
            matchedPrefixLower.contains("personal") || lowerWorkingForCategory.contains("personal") ||
                    lowerWorkingForCategory.contains("व्यक्तिगत") || lowerWorkingForCategory.contains("ব্যক্তিগত") -> TaskCategory.PERSONAL
            matchedPrefixLower.contains("urgent") || lowerWorkingForCategory.contains("urgent") ||
                    lowerWorkingForCategory.contains("जरूरी") || lowerWorkingForCategory.contains("জরুরি") || priority == Priority.HIGH -> TaskCategory.URGENT
            matchedPrefixLower.contains("meeting") || lowerWorkingForCategory.contains("मीटिंग") || lowerWorkingForCategory.contains("মিটিং") ||
                    lowerWorkingForCategory.contains("meeting with") || lowerWorkingForCategory.contains("call with") -> TaskCategory.MEETING
            matchedPrefixLower.contains("work") || lowerWorkingForCategory.contains("work") ||
                    lowerWorkingForCategory.contains("काम") || lowerWorkingForCategory.contains("কাজ") -> TaskCategory.WORK
            lowerWorkingForCategory.contains("email") || lowerWorkingForCategory.contains("ईमेल") || lowerWorkingForCategory.contains("ইমেইল") -> TaskCategory.EMAIL_FOLLOWUP
            else -> TaskCategory.WORK
        }

        // Clean redundant category keywords from the working text
        working = working.replace(Regex("(?i)^personal task:?\\s*"), "")
            .replace(Regex("(?i)^work task:?\\s*"), "")
            .replace(Regex("(?i)^urgent task:?\\s*"), "")
            .replace(Regex("(?i)^urgent:?\\s*"), "")
            .replace(Regex("^(व्यक्तिगत कार्य|काम का कार्य|जरूरी कार्य|ব্যক্তিগত কাজ|জরুরি কাজ)[:\\s]*"), "")
            .trim()

        // Extract Time
        var extractedTime = ""
        val timePattern = Pattern.compile("(?i)\\b(?:at|बजे|টায়)?\\s*(\\d{1,2}(?::\\d{2})?\\s*(?:am|pm)?)\\b")
        val timeMatcher = timePattern.matcher(working)
        if (timeMatcher.find()) {
            val matchedGroup = timeMatcher.group(1)
            extractedTime = matchedGroup?.trim()?.uppercase(Locale.ROOT) ?: ""
            if (!extractedTime.contains("AM") && !extractedTime.contains("PM")) {
                val hour = extractedTime.toIntOrNull() ?: 10
                extractedTime = if (hour in 8..11) "$hour:00 AM" else if (hour in 1..7) "$hour:00 PM" else "$hour:00"
            } else if (!extractedTime.contains(":")) {
                extractedTime = extractedTime.replace(Regex("\\s*AM"), ":00 AM").replace(Regex("\\s*PM"), ":00 PM")
            }
            working = timeMatcher.replaceFirst(" ").trim()
        }

        // Extract Date
        var extractedDate = ""
        val dateKeywords = listOf(
            "for next tuesday", "next tuesday", "for this tuesday", "this tuesday",
            "for next monday", "next monday", "for tomorrow", "tomorrow", "for today", "today",
            // Hindi
            "अगले मंगलवार", "कल सुबह", "कल शाम", "कल", "आज",
            // Bengali
            "আগামী মঙ্গলবার", "কাল সকাল", "কাল বিকাল", "আগামীকাল", "আজকে", "আজ"
        )

        val workingLower = working.lowercase(Locale.ROOT)
        for (dk in dateKeywords) {
            val idx = workingLower.indexOf(dk.lowercase(Locale.ROOT))
            if (idx != -1) {
                val rawDate = dk.removePrefix("for ").removePrefix("on ").trim()
                extractedDate = rawDate.split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                val before = working.substring(0, idx)
                val after = working.substring(idx + dk.length)
                working = "$before $after".replace("\\s+".toRegex(), " ").trim()
                break
            }
        }

        if (extractedDate.isBlank()) {
            extractedDate = when (language) {
                AppLanguage.HINDI -> "आज"
                AppLanguage.BENGALI -> "আজকে"
                else -> "Today"
            }
        }
        if (extractedTime.isBlank()) {
            extractedTime = "10:00 AM"
        }

        // Extract Description
        var description = ""
        val aboutIndex = working.lowercase(Locale.ROOT).indexOf("about ")
        if (aboutIndex != -1) {
            description = working.substring(aboutIndex + 6).trim()
            working = working.substring(0, aboutIndex).trim()
        }

        working = working.replace(Regex("(?i)\\b(with|for|on|at|by|के साथ|সাথে)\\b$"), "").trim()
        working = working.replace(Regex("^(?i)(with|for|on|at|by)\\b"), "").trim()

        var title = working.ifBlank {
            when (language) {
                AppLanguage.HINDI -> "नया कार्य"
                AppLanguage.BENGALI -> "নতুন টাস্ক"
                else -> "New Task"
            }
        }

        if (matchedPrefix.contains("meeting with") || matchedPrefix.contains("appointment with")) {
            val person = title.replace(Regex("(?i)^meeting with "), "").trim()
            title = if (description.isNotBlank()) "Meeting with $person - $description" else "Meeting with $person"
        } else if (description.isNotBlank() && !title.contains(description)) {
            title = "$title ($description)"
        }

        title = title.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

        val spokenConfirmation = when (language) {
            AppLanguage.HINDI -> "पारुल ने '$title' को $extractedDate $extractedTime के लिए शेड्यूल कर दिया है।"
            AppLanguage.BENGALI -> "পারুল '$title' $extractedDate সকাল $extractedTime-এর জন্য শিডিউল করেছে।"
            else -> "Scheduled '$title' for $extractedDate at $extractedTime."
        }

        return ParsedTaskResult(
            title = title,
            description = description.ifBlank { "Scheduled via Parul AI." },
            dueDate = extractedDate,
            dueTime = extractedTime,
            priority = priority,
            category = category,
            spokenConfirmation = spokenConfirmation
        )
    }
}
