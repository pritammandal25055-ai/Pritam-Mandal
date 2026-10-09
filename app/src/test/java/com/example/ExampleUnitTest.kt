package com.example

import com.example.ai.LocalNlpParser
import com.example.ai.ParsedVoiceIntent
import com.example.data.model.Priority
import com.example.data.model.TaskCategory
import com.example.voice.AppLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testNaturalLanguageTaskCreation_ExtractsCorrectDetails() {
        val command = "Schedule a meeting with John for next Tuesday at 10 AM about the project proposal"
        val intent = LocalNlpParser.parseIntent(command)

        assertTrue(intent is ParsedVoiceIntent.CreateTask)
        val task = (intent as ParsedVoiceIntent.CreateTask).taskResult

        assertTrue(task.title.contains("John", ignoreCase = true))
        assertEquals("Next Tuesday", task.dueDate)
        assertEquals("10:00 AM", task.dueTime)
        assertEquals(TaskCategory.MEETING, task.category)
        assertTrue(task.description.contains("project proposal", ignoreCase = true))
    }

    @Test
    fun testWakeWordAndTurnOffIntents() {
        // Hey Parul turns on
        val wakeIntent = LocalNlpParser.parseIntent("hey parul")
        assertTrue(wakeIntent is ParsedVoiceIntent.TurnOn)

        // Bye turns off
        val byeIntent = LocalNlpParser.parseIntent("bye parul")
        assertTrue(byeIntent is ParsedVoiceIntent.TurnOff)

        val goodbyeIntent = LocalNlpParser.parseIntent("goodbye")
        assertTrue(goodbyeIntent is ParsedVoiceIntent.TurnOff)
    }

    @Test
    fun testHindiAndBengaliWakeAndTaskCommands() {
        // Hindi Turn On & Turn Off
        val hindiWake = LocalNlpParser.parseIntent("हे पारुल", AppLanguage.HINDI)
        assertTrue(hindiWake is ParsedVoiceIntent.TurnOn)

        val hindiBye = LocalNlpParser.parseIntent("अलविदा", AppLanguage.HINDI)
        assertTrue(hindiBye is ParsedVoiceIntent.TurnOff)

        // Bengali Turn On & Turn Off
        val bengaliWake = LocalNlpParser.parseIntent("হে পারুল", AppLanguage.BENGALI)
        assertTrue(bengaliWake is ParsedVoiceIntent.TurnOn)

        val bengaliBye = LocalNlpParser.parseIntent("বিদায়", AppLanguage.BENGALI)
        assertTrue(bengaliBye is ParsedVoiceIntent.TurnOff)
    }

    @Test
    fun testEmailSummarizationIntent() {
        val intent = LocalNlpParser.parseIntent("Summarize my emails")
        assertTrue(intent is ParsedVoiceIntent.SummarizeEmails)
    }

    @Test
    fun testCategoryExtraction_WorkPersonalUrgent() {
        val workIntent = LocalNlpParser.parseIntent("Schedule work task review Q4 budget next Friday")
        assertTrue(workIntent is ParsedVoiceIntent.CreateTask)
        assertEquals(TaskCategory.WORK, (workIntent as ParsedVoiceIntent.CreateTask).taskResult.category)

        val personalIntent = LocalNlpParser.parseIntent("Add personal task buy groceries tomorrow at 6 PM")
        assertTrue(personalIntent is ParsedVoiceIntent.CreateTask)
        assertEquals(TaskCategory.PERSONAL, (personalIntent as ParsedVoiceIntent.CreateTask).taskResult.category)

        val urgentIntent = LocalNlpParser.parseIntent("Create urgent task fix server outage asap")
        assertTrue(urgentIntent is ParsedVoiceIntent.CreateTask)
        assertEquals(TaskCategory.URGENT, (urgentIntent as ParsedVoiceIntent.CreateTask).taskResult.category)
        assertEquals(Priority.HIGH, (urgentIntent as ParsedVoiceIntent.CreateTask).taskResult.priority)
    }
}
