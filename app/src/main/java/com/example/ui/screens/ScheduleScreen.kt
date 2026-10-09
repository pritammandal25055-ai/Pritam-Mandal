package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Priority
import com.example.data.model.TaskCategory
import com.example.data.model.TaskItem
import com.example.ui.AuraUiState
import com.example.ui.components.TaskCard
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraDarkBorder
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraViolet

@Composable
fun ScheduleScreen(
    uiState: AuraUiState,
    onToggleComplete: (TaskItem) -> Unit,
    onDeleteTask: (TaskItem) -> Unit,
    onOpenCreateTask: () -> Unit,
    onFilterChange: (String) -> Unit,
    onVoiceSchedulePrompt: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filterTabs = listOf("ALL", "WORK", "PERSONAL", "URGENT", "MEETING", "COMPLETED")

    val filteredTasks = uiState.tasks.filter { task ->
        when (uiState.taskFilter) {
            "WORK" -> task.category == TaskCategory.WORK
            "PERSONAL" -> task.category == TaskCategory.PERSONAL
            "URGENT" -> task.category == TaskCategory.URGENT || task.priority == Priority.HIGH
            "MEETING" -> task.category == TaskCategory.MEETING
            "COMPLETED" -> task.isCompleted
            else -> true
        }
    }

    val pendingCount = uiState.tasks.count { !it.isCompleted }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Schedule & Tasks",
                            color = AuraTextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$pendingCount pending • Voice & Email Sync",
                            color = AuraTextSecondary,
                            fontSize = 13.sp
                        )
                    }

                    // Schedule by voice shortcut
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(AuraViolet.copy(alpha = 0.2f))
                            .clickable { onVoiceSchedulePrompt() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = AuraCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Speak to Add",
                                color = AuraCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Weekly Completed vs Pending Tasks Bar Chart
            item {
                com.example.ui.components.WeeklyTaskBarChart(
                    tasks = uiState.tasks
                )
            }

            // Filter Tabs
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filterTabs) { tab ->
                        val selected = uiState.taskFilter == tab
                        val label = when (tab) {
                            "ALL" -> "All (${uiState.tasks.size})"
                            "WORK" -> "💼 Work (${uiState.tasks.count { it.category == TaskCategory.WORK }})"
                            "PERSONAL" -> "👤 Personal (${uiState.tasks.count { it.category == TaskCategory.PERSONAL }})"
                            "URGENT" -> "🔥 Urgent (${uiState.tasks.count { it.category == TaskCategory.URGENT || it.priority == Priority.HIGH }})"
                            "MEETING" -> "📅 Meetings (${uiState.tasks.count { it.category == TaskCategory.MEETING }})"
                            "COMPLETED" -> "✓ Done (${uiState.tasks.count { it.isCompleted }})"
                            else -> tab
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) AuraCyan else AuraDarkCard)
                                .clickable { onFilterChange(tab) }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (selected) Color.Black else AuraTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Task list
            if (filteredTasks.isEmpty()) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = null,
                            tint = AuraTextMuted,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No tasks found for this filter",
                            color = AuraTextSecondary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Say \"Schedule a meeting...\" or tap + to create one.",
                            color = AuraTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                items(filteredTasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onToggleComplete = { onToggleComplete(task) },
                        onDelete = { onDeleteTask(task) }
                    )
                }
            }

            // Padding at bottom for FAB
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onOpenCreateTask,
            containerColor = AuraCyan,
            contentColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_task_fab")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Task",
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
