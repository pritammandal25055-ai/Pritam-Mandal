package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.Quickreply
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmailItem
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraDarkBorder
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkCardElevated
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraViolet
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityMedium

@Composable
fun EmailCard(
    email: EmailItem,
    onAddTask: () -> Unit,
    onToggleStar: () -> Unit,
    onToggleRead: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val urgencyColor = when {
        email.urgencyScore >= 8 -> PriorityHigh
        email.urgencyScore >= 5 -> PriorityMedium
        else -> AuraCyan
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!email.isRead) AuraDarkCardElevated else AuraDarkCard
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (email.urgencyScore >= 8) PriorityHigh.copy(alpha = 0.5f) else AuraDarkBorder
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("email_card_${email.id}")
            .clickable { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Avatar, Sender, Urgency pill, Star
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Sender Avatar
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(urgencyColor.copy(alpha = 0.2f))
                        .border(1.dp, urgencyColor, CircleShape)
                ) {
                    Text(
                        text = email.senderName.take(1).uppercase(),
                        color = urgencyColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = email.senderName,
                            color = AuraTextPrimary,
                            fontWeight = if (!email.isRead) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = email.receivedTime,
                            color = AuraTextMuted,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = email.senderEmail,
                        color = AuraTextMuted,
                        fontSize = 12.sp
                    )
                }

                // Urgency Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(urgencyColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (email.urgencyScore >= 8) "Urgent" else "Urgency ${email.urgencyScore}/10",
                        color = urgencyColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onToggleStar,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (email.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Star Email",
                        tint = if (email.isStarred) Color(0xFFFFD166) else AuraTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subject
            Text(
                text = email.subject,
                color = AuraTextPrimary,
                fontWeight = if (!email.isRead) FontWeight.Bold else FontWeight.SemiBold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Preview
            Text(
                text = email.preview,
                color = AuraTextSecondary,
                fontSize = 13.sp,
                maxLines = if (expanded) 8 else 2
            )

            // AI Summary Callout Box
            if (email.aiSummary.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AuraViolet.copy(alpha = 0.12f))
                        .border(1.dp, AuraViolet.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Summary",
                            tint = AuraCyan,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "AI Summary",
                                color = AuraCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = email.aiSummary,
                                color = AuraTextPrimary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Expanded detail: Full body, Quick Reply, 1-tap Task action
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    if (email.body.isNotBlank() && email.body != email.preview) {
                        Text(
                            text = "Full Message:",
                            color = AuraTextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = email.body,
                            color = AuraTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(AuraDarkCard.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (email.generatedQuickReply.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Quickreply,
                                contentDescription = null,
                                tint = AuraCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI Suggested Reply:",
                                color = AuraCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "\"${email.generatedQuickReply}\"",
                            color = AuraTextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mark read/unread toggle
                        IconButton(
                            onClick = onToggleRead,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (email.isRead) Icons.Default.MarkEmailUnread else Icons.Default.MarkEmailRead,
                                contentDescription = "Toggle Read",
                                tint = AuraTextMuted
                            )
                        }

                        // One-tap Add to Tasks Button
                        if (email.suggestedTaskTitle.isNotBlank()) {
                            Button(
                                onClick = onAddTask,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AuraCyan,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("add_email_task_button_${email.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddAlert,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Add to Tasks",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Expand collapse hint
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = AuraTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
