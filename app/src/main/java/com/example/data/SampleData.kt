package com.example.data

import com.example.data.model.EmailItem
import com.example.data.model.Priority
import com.example.data.model.TaskCategory
import com.example.data.model.TaskItem

object SampleData {
    fun getInitialTasks(): List<TaskItem> = listOf(
        TaskItem(
            title = "Finalize Q4 AI Assistant Roadmap",
            description = "Review cross-platform voice architecture and synchronization specs with leadership.",
            dueDate = "Today",
            dueTime = "2:30 PM",
            priority = Priority.HIGH,
            category = TaskCategory.WORK,
            isCompleted = false,
            source = "Voice Command (Pixel)",
            originDevice = "Android Phone"
        ),
        TaskItem(
            title = "Team Sprint Planning & Retrospective",
            description = "Sprint 42 demo on Gemini email summarization and voice wake-word sensitivity.",
            dueDate = "Tomorrow",
            dueTime = "10:00 AM",
            priority = Priority.MEDIUM,
            category = TaskCategory.MEETING,
            isCompleted = false,
            source = "Calendar Sync",
            originDevice = "MacBook Pro"
        ),
        TaskItem(
            title = "Review Security Protocol for Cloud Sync",
            description = "Verify zero-trust token exchange between iOS, Desktop, and Android nodes.",
            dueDate = "Friday, Oct 10",
            dueTime = "4:00 PM",
            priority = Priority.HIGH,
            category = TaskCategory.WORK,
            isCompleted = false,
            source = "Email Digest Action",
            originDevice = "Desktop Client"
        ),
        TaskItem(
            title = "Prepare Project Proposal Presentation for John",
            description = "Draft slide deck covering timeline, budget estimates, and architecture diagrams.",
            dueDate = "Next Tuesday",
            dueTime = "10:00 AM",
            priority = Priority.HIGH,
            category = TaskCategory.MEETING,
            isCompleted = false,
            source = "Voice Command (Android)",
            originDevice = "Android Phone"
        ),
        TaskItem(
            title = "Approve Cloud Server Quota Expansion",
            description = "DevOps token spike must be approved before 5 PM to prevent request throttling.",
            dueDate = "Today",
            dueTime = "4:30 PM",
            priority = Priority.HIGH,
            category = TaskCategory.URGENT,
            isCompleted = false,
            source = "Email Alert",
            originDevice = "Pixel 9 Pro"
        ),
        TaskItem(
            title = "Annual Health Checkup & Dental Cleaning",
            description = "Pick up prescription and confirm appointment time with Dr. Watson.",
            dueDate = "Saturday, Oct 11",
            dueTime = "11:00 AM",
            priority = Priority.MEDIUM,
            category = TaskCategory.PERSONAL,
            isCompleted = false,
            source = "Voice Command",
            originDevice = "Pixel 9 Pro"
        )
    )

    fun getInitialEmails(): List<EmailItem> = listOf(
        EmailItem(
            senderName = "John Miller",
            senderEmail = "john.miller@quantumtech.io",
            subject = "Project Proposal Review & Next Tuesday Meeting",
            preview = "Hi team, please review the revised proposal attached. Can we lock in Tuesday at 10 AM to discuss?",
            body = "Hi team,\n\nI have reviewed the preliminary architecture diagrams for the AI productivity engine. The natural language task extraction and cross-platform sync capabilities look very promising.\n\nCould we lock in next Tuesday at 10:00 AM to review the full proposal together? Please bring the updated budget timeline and voice latency benchmarks.\n\nBest regards,\nJohn Miller\nVP Engineering, QuantumTech",
            receivedTime = "8:30 AM",
            isRead = false,
            isStarred = true,
            category = "Client",
            urgencyScore = 9,
            aiSummary = "John reviewed the AI engine proposal and requests a meeting next Tuesday at 10:00 AM to finalize budget timeline and voice benchmarks.",
            suggestedAction = "Confirm meeting for next Tuesday at 10:00 AM and prepare budget timeline.",
            suggestedTaskTitle = "Prepare budget timeline & meeting with John",
            suggestedTaskTime = "Next Tuesday, 10:00 AM",
            generatedQuickReply = "Hi John, Tuesday at 10:00 AM works perfectly. I will prepare the updated budget timeline and voice latency benchmarks for our discussion."
        ),
        EmailItem(
            senderName = "Sarah Chen",
            senderEmail = "sarah.chen@innovate.co",
            subject = "URGENT: Cloud API Billing Quota Alert",
            preview = "Our development cluster API quota is nearing 85% utilization before the weekend.",
            body = "Hello everyone,\n\nOur Gemini API token consumption spiked by 40% yesterday due to increased voice command processing and email summarization tests. If we don't request a quota increase before 5:00 PM today, requests may be throttled.\n\nPlease approve the quota expansion ticket immediately.\n\nSarah Chen\nCloud DevOps Lead",
            receivedTime = "9:05 AM",
            isRead = false,
            isStarred = true,
            category = "Urgent",
            urgencyScore = 10,
            aiSummary = "URGENT: Gemini API token usage reached 85%. Quota increase ticket must be approved before 5:00 PM today to avoid throttling.",
            suggestedAction = "Approve API quota increase ticket before 5:00 PM today.",
            suggestedTaskTitle = "Approve Cloud API quota expansion before 5 PM",
            suggestedTaskTime = "Today, 4:30 PM",
            generatedQuickReply = "Thanks Sarah. I have submitted the approval for the quota increase immediately. Let me know once confirmed."
        ),
        EmailItem(
            senderName = "Elena Rostova",
            senderEmail = "elena.design@creativelab.org",
            subject = "New Design Assets for Cross-Platform Assistant",
            preview = "Attached are the exported iconography sets and dark mode color palettes for iOS and Desktop.",
            body = "Hi team!\n\nHere are the updated adaptive icons, dark-indigo UI theme tokens, and wake-word wave animation specs for our unified cross-platform assistant. Everything follows M3 guidelines and Apple HIG.\n\nLet me know if any vector assets need resizing.\n\nElena",
            receivedTime = "Yesterday",
            isRead = true,
            isStarred = false,
            category = "Team",
            urgencyScore = 4,
            aiSummary = "Design assets and theme tokens for cross-platform assistant have been delivered and ready for engineering integration.",
            suggestedAction = "Verify asset formats and test dark mode palette.",
            suggestedTaskTitle = "Verify new cross-platform UI tokens",
            suggestedTaskTime = "Tomorrow, 2:00 PM",
            generatedQuickReply = "Thanks Elena! The dark indigo theme and glowing wave specs look great. Integrating them into our build now."
        ),
        EmailItem(
            senderName = "Alex Rivera",
            senderEmail = "alex.r@venturepartners.com",
            subject = "Follow-up: Weekly Executive Sync Summary",
            preview = "Here are the minutes and key deliverables from yesterday's partnership debrief.",
            body = "Hi all,\n\nGreat discussion yesterday. The key takeaway is accelerating our multi-device voice assistant launch. Please send over your executive slide summary by Thursday noon.\n\nBest,\nAlex",
            receivedTime = "Oct 7",
            isRead = true,
            isStarred = false,
            category = "Work",
            urgencyScore = 6,
            aiSummary = "Summary of yesterday's executive sync: Team must submit executive slide deck by Thursday noon.",
            suggestedAction = "Submit executive slide deck by Thursday noon.",
            suggestedTaskTitle = "Send executive slide summary to Alex",
            suggestedTaskTime = "Thursday, 11:30 AM",
            generatedQuickReply = "Hi Alex, thanks for the recap. The executive slides are on track and will be delivered by Thursday noon."
        )
    )
}
