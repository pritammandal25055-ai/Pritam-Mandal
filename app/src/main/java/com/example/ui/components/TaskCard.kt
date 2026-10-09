package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Priority
import com.example.data.model.TaskCategory
import com.example.data.model.TaskItem
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraCyanLight
import com.example.ui.theme.AuraDarkBorder
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkCardElevated
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraViolet
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityMedium

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskCard(
    task: TaskItem,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val priorityColor = when (task.priority) {
        Priority.HIGH -> PriorityHigh
        Priority.MEDIUM -> PriorityMedium
        Priority.LOW -> PriorityLow
    }

    val cardBg by animateColorAsState(
        targetValue = if (task.isCompleted) AuraDarkCard.copy(alpha = 0.5f) else AuraDarkCard,
        label = "task_bg"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (task.priority == Priority.HIGH && !task.isCompleted) PriorityHigh.copy(alpha = 0.4f) else AuraDarkBorder
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("task_card_${task.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Custom check button
                IconButton(
                    onClick = onToggleComplete,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("complete_task_button_${task.id}")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(
                                if (task.isCompleted) AuraCyan else Color.Transparent
                            )
                            .border(
                                width = 2.dp,
                                color = if (task.isCompleted) AuraCyan else AuraTextMuted,
                                shape = CircleShape
                            )
                    ) {
                        if (task.isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Title
                Text(
                    text = task.title,
                    color = if (task.isCompleted) AuraTextMuted else AuraTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("delete_task_button_${task.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Task",
                        tint = AuraTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (task.description.isNotBlank()) {
                Text(
                    text = task.description,
                    color = AuraTextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 44.dp, bottom = 8.dp)
                )
            }

            // Metadata Chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 44.dp, top = 4.dp)
            ) {
                // Due Date chip
                MetadataPill(
                    icon = Icons.Default.CalendarToday,
                    text = task.dueDate,
                    color = AuraCyan
                )

                // Due Time chip
                if (task.dueTime.isNotBlank()) {
                    MetadataPill(
                        icon = Icons.Default.AccessTime,
                        text = task.dueTime,
                        color = AuraViolet
                    )
                }

                // Priority chip
                MetadataPill(
                    icon = Icons.Default.Flag,
                    text = task.priority.name,
                    color = priorityColor
                )

                // Category pill
                val (categoryName, categoryColor) = when (task.category) {
                    TaskCategory.WORK -> Pair("💼 Work", AuraCyan)
                    TaskCategory.PERSONAL -> Pair("👤 Personal", PriorityLow)
                    TaskCategory.URGENT -> Pair("🔥 Urgent", PriorityHigh)
                    TaskCategory.MEETING -> Pair("📅 Meeting", AuraViolet)
                    TaskCategory.EMAIL_FOLLOWUP -> Pair("✉️ Email Action", AuraCyanLight)
                }
                MetadataPill(
                    icon = Icons.Default.Event,
                    text = categoryName,
                    color = categoryColor
                )

                // Device / Source chip
                if (task.originDevice.isNotBlank()) {
                    MetadataPill(
                        icon = Icons.Default.Devices,
                        text = task.originDevice,
                        color = AuraCyan.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun MetadataPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
