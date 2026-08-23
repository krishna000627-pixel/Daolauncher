package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.CultivationTask
import com.example.data.model.DaoCategory
import com.example.data.model.TaskGrade
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidBorder
import com.example.ui.theme.VoidCard
import com.example.ui.theme.VoidDark

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InscribeQuestDialog(
    onDismiss: () -> Unit,
    onSaveTask: (CultivationTask) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedGrade by remember { mutableStateOf(TaskGrade.EARTH) }
    var selectedCategory by remember { mutableStateOf(DaoCategory.SECT_DUTY) }
    var isDailyRecurring by remember { mutableStateOf(false) }
    var priority by remember { mutableStateOf(1) } // 1: Normal, 2: High, 3: Heavenly Mandate

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, VoidBorder, RoundedCornerShape(20.dp))
                .testTag("inscribe_quest_dialog"),
            colors = CardDefaults.cardColors(containerColor = VoidDark),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Inscribe Cultivation Quest",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CelestialGold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Quest Objective / Mission Title") },
                    placeholder = { Text("e.g. Inscribe 500 lines of sacred code") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quest_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CelestialGold,
                        unfocusedBorderColor = VoidBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Description Input
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Dao Intent / Quest Details (Optional)") },
                    placeholder = { Text("Key milestones, focus constraints...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quest_desc_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CelestialGold,
                        unfocusedBorderColor = VoidBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Quest Grade Selection
                Text(
                    text = "Spiritual Grade & Rewards",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TaskGrade.entries.forEach { grade ->
                        val isSelected = grade == selectedGrade
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) grade.color.copy(alpha = 0.25f) else VoidCard)
                                .border(
                                    1.dp,
                                    if (isSelected) grade.color else VoidBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedGrade = grade }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = grade.name.take(4),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) grade.color else TextSecondary
                                )
                                Text(
                                    text = "+${grade.qiReward} Qi",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Dao Category
                Text(
                    text = "Dao Path / Category",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DaoCategory.entries.forEach { category ->
                        val isSelected = category == selectedCategory
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) category.color.copy(alpha = 0.25f) else VoidCard)
                                .border(
                                    1.dp,
                                    if (isSelected) category.color else VoidBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (category) {
                                    DaoCategory.DAO_STUDY -> Icons.Default.MenuBook
                                    DaoCategory.SWORD_BODY -> Icons.Default.FitnessCenter
                                    DaoCategory.MIND_MEDITATION -> Icons.Default.SelfImprovement
                                    DaoCategory.SECT_DUTY -> Icons.Default.Assignment
                                    DaoCategory.ALCHEMY -> Icons.Default.Science
                                    DaoCategory.MUNDANE -> Icons.Default.Home
                                },
                                contentDescription = null,
                                tint = if (isSelected) category.color else TextMuted,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = category.displayName.substringBefore(" &"),
                                fontSize = 11.sp,
                                color = if (isSelected) category.color else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Recurring Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isDailyRecurring = !isDailyRecurring }
                ) {
                    Checkbox(
                        checked = isDailyRecurring,
                        onCheckedChange = { isDailyRecurring = it },
                        colors = CheckboxDefaults.colors(checkedColor = CelestialGold)
                    )
                    Text(
                        text = "Daily Recurring Heavenly Mandate (Repeats daily)",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save button
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onSaveTask(
                                CultivationTask(
                                    title = title.trim(),
                                    description = description.trim(),
                                    grade = selectedGrade,
                                    category = selectedCategory,
                                    isDailyRecurring = isDailyRecurring,
                                    priority = priority
                                )
                            )
                            onDismiss()
                        }
                    },
                    enabled = title.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = CelestialGold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_quest_button")
                ) {
                    Text(
                        text = "Inscribe on Quest Scroll",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
