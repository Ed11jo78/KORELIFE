package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Habit
import com.example.ui.theme.*
import java.util.Calendar

@Composable
fun HabitStreakBanner(
    habits: List<Habit>,
    modifier: Modifier = Modifier
) {
    val totalHabits = habits.size
    val completedToday = habits.count { it.completedToday }
    val progress = if (totalHabits > 0) completedToday.toFloat() / totalHabits else 0f
    val progressPercent = (progress * 100).toInt()
    val maxStreak = habits.maxOfOrNull { it.streak } ?: 0
    val totalAccumulatedStreaks = habits.sumOf { it.streak }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("habit_streak_banner"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF78350F).copy(alpha = 0.4f))
                            .border(1.dp, AmberTertiary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔥", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Rachas de Hábitos Diarios",
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (completedToday == totalHabits && totalHabits > 0)
                                "¡Objetivo diario cumplido al 100%!"
                            else
                                "$completedToday de $totalHabits hábitos cumplidos hoy ($progressPercent%)",
                            color = if (completedToday == totalHabits && totalHabits > 0) EmeraldPrimary else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF064E3B).copy(alpha = 0.5f))
                        .border(1.dp, EmeraldPrimary, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "⭐ $totalAccumulatedStreaks d acumulados",
                        color = EmeraldPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (progress >= 1f) EmeraldPrimary else AmberTertiary,
                trackColor = DarkSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Motivation chip
            val motivationalTip = when {
                completedToday == totalHabits && totalHabits > 0 ->
                    "🎉 ¡Extraordinario! Has ganado el día manteniendo todos tus hábitos al día."
                completedToday > 0 ->
                    "💪 ¡Vas muy bien! Cada hábito suma para consolidar tu identidad y consistencia."
                else ->
                    "⚡ ¡Comienza hoy marcando tu primer hábito para encender tu racha de disciplina!"
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💡 ", fontSize = 13.sp)
                    Text(
                        text = motivationalTip,
                        color = TextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun HabitCard(
    habit: Habit,
    onToggleToday: () -> Unit,
    onViewDetails: () -> Unit,
    onOpenHeatmap: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dayLabels = listOf("L", "M", "M", "J", "V", "S", "D")
    val cal = Calendar.getInstance()
    val rawDay = cal.get(Calendar.DAY_OF_WEEK)
    val todayIdx = if (rawDay == Calendar.SUNDAY) 6 else rawDay - 2

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("habit_card_${habit.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (habit.completedToday) DarkSurface else DarkSurface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (habit.completedToday) AmberTertiary.copy(alpha = 0.5f) else DarkBorder
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Emoji + Name + Menu Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Emoji avatar
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                            .border(1.dp, if (habit.completedToday) AmberTertiary else DarkBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = habit.emoji, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = habit.name,
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = habit.category,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                            Text(text = " • ", color = TextSubtle, fontSize = 11.sp)
                            Text(
                                text = habit.frequency,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Options buttons (calendar heatmap & details)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenHeatmap,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Calendario Mensual",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onViewDetails,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Detalles del hábito",
                            tint = TextSubtle,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Streak Counter Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurfaceVariant)
                    .border(
                        1.dp,
                        if (habit.completedToday) AmberTertiary.copy(alpha = 0.3f) else DarkBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Streak Counter
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🔥 ${habit.streak}",
                        color = AmberTertiary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "días seguidos",
                            color = TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = habit.streakTierTitle,
                            color = AmberTertiary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Badge & Record
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = habit.streakBadgeEmoji, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Récord: ${habit.bestStreak}d",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 7-Day Consistency Tracker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Consistencia semanal",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${habit.targetDaysPerWeek} días/semana meta",
                    color = TextSubtle,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dayLabels.forEachIndexed { index, label ->
                    val isDone = habit.weeklyHistory.getOrElse(index) { false }
                    val isToday = index == todayIdx

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isDone -> EmeraldContainer
                                        isToday -> DarkSurfaceVariant
                                        else -> DarkBackground
                                    }
                                )
                                .border(
                                    width = if (isToday) 2.dp else 1.dp,
                                    color = when {
                                        isToday && isDone -> EmeraldPrimary
                                        isToday -> AmberTertiary
                                        isDone -> EmeraldPrimary
                                        else -> DarkBorder
                                    },
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = label,
                                    color = if (isToday) AmberTertiary else TextSubtle,
                                    fontSize = 10.sp,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = label,
                            color = if (isToday) AmberTertiary else TextSubtle,
                            fontSize = 9.sp,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Complete Today Action Button
            Button(
                onClick = onToggleToday,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("habit_toggle_${habit.id}"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (habit.completedToday) Color(0xFF78350F) else EmeraldPrimary,
                    contentColor = if (habit.completedToday) AmberTertiary else EmeraldOnPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (habit.completedToday) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (habit.completedToday) "¡Completado Hoy! 🔥 (Toca para deshacer)" else "Marcar como hecho hoy",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun AddEditHabitDialog(
    habitToEdit: Habit? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, frequency: String, emoji: String, category: String, targetDays: Int, notes: String) -> Unit
) {
    var name by remember { mutableStateOf(habitToEdit?.name ?: "") }
    var selectedEmoji by remember { mutableStateOf(habitToEdit?.emoji ?: "🔥") }
    var selectedCategory by remember { mutableStateOf(habitToEdit?.category ?: "Salud") }
    var frequency by remember { mutableStateOf(habitToEdit?.frequency ?: "Diario") }
    var targetDays by remember { mutableStateOf(habitToEdit?.targetDaysPerWeek ?: 7) }
    var notes by remember { mutableStateOf(habitToEdit?.notes ?: "") }

    val emojis = listOf("🔥", "🏃", "📚", "💧", "💰", "🧘", "🥦", "💤", "⚡", "🎯", "✍️", "🥑")
    val categories = listOf("Salud", "Finanzas", "Aprendizaje", "Bienestar", "Productividad", "General")
    val frequencies = listOf("Diario", "Lunes a Viernes", "Fines de semana", "3 veces por semana")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("add_edit_habit_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = if (habitToEdit == null) "Nuevo Hábito con Racha" else "Editar Hábito",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Construye hábitos diarios consistentes y acumula días de racha.",
                    color = TextMuted,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del hábito *", color = TextMuted) },
                    placeholder = { Text("Ej. Leer 20 min, Tomar agua, Ejercicio", color = TextSubtle) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("habit_input_name")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Emoji picker
                Text(text = "Icono / Emoji", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(emojis) { emoji ->
                        val isSelected = selectedEmoji == emoji
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) AmberTertiary.copy(alpha = 0.3f) else DarkSurfaceVariant)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) AmberTertiary else DarkBorder,
                                    shape = CircleShape
                                )
                                .clickable { selectedEmoji = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category chips
                Text(text = "Área / Categoría", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) DarkBorder else DarkSurfaceVariant)
                                .border(1.dp, if (isSelected) EmeraldPrimary else DarkBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) TextWhite else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Frequency options
                Text(text = "Frecuencia", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(frequencies) { freq ->
                        val isSelected = frequency == freq
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) EmeraldContainer else DarkSurfaceVariant)
                                .border(1.dp, if (isSelected) EmeraldPrimary else DarkBorder, RoundedCornerShape(8.dp))
                                .clickable { frequency = freq }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = freq,
                                color = if (isSelected) EmeraldOnContainer else TextWhite,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Notes / Motivation
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Nota motivacional o recordatorio (opcional)", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(
                                    name.trim(),
                                    frequency,
                                    selectedEmoji,
                                    selectedCategory,
                                    targetDays,
                                    notes.trim()
                                )
                                onDismiss()
                            }
                        },
                        enabled = name.isNotBlank(),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("habit_btn_save"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = EmeraldOnPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (habitToEdit == null) "Crear Hábito" else "Guardar",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HabitDetailsDialog(
    habit: Habit,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onResetStreak: () -> Unit,
    onDelete: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("habit_details_dialog")
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = habit.emoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = habit.name,
                                color = TextWhite,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${habit.category} • ${habit.frequency}",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextSubtle)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Streak Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Racha Actual",
                        value = "🔥 ${habit.streak} d",
                        subtitle = habit.streakTierTitle,
                        color = AmberTertiary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Mejor Racha",
                        value = "🏆 ${habit.bestStreak} d",
                        subtitle = "Récord histórico",
                        color = EmeraldPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Milestone progress tracker
                Text(
                    text = "Hitos de Consistencia",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                val milestones = listOf(
                    Triple(3, "Iniciador (3 d)", "🥉"),
                    Triple(7, "Semana Imparable (7 d)", "🥈"),
                    Triple(14, "Hábito Fuerte (14 d)", "🥇"),
                    Triple(21, "Rutina de Acero (21 d)", "💎"),
                    Triple(30, "Leyenda (30 d)", "👑")
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    milestones.forEach { (days, title, badge) ->
                        val reached = habit.streak >= days
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (reached) DarkSurfaceVariant else DarkBackground)
                                .border(1.dp, if (reached) EmeraldPrimary.copy(alpha = 0.5f) else DarkBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = badge, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = title,
                                    color = if (reached) TextWhite else TextSubtle,
                                    fontSize = 12.sp,
                                    fontWeight = if (reached) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            if (reached) {
                                Text(text = "¡Alcanzado!", color = EmeraldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Text(text = "Faltan ${days - habit.streak} d", color = TextSubtle, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Motivation quote
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceVariant)
                        .padding(12.dp)
                ) {
                    Text(
                        text = habit.motivationMessage,
                        color = AmberTertiary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action buttons: Edit, Reset, Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onEdit()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Editar", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            onResetStreak()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberTertiary)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reiniciar", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            onDelete()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7F1D1D),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Borrar", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Text(text = title, color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = subtitle, color = TextSubtle, fontSize = 10.sp)
    }
}


@Composable
fun HabitCalendarHeatmapDialog(
    habit: Habit,
    onDismiss: () -> Unit,
    onToggleDay: (Int) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("habit_calendar_heatmap_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Emoji and Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = habit.emoji, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Historial Mensual",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = habit.name,
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Month header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceVariant)
                        .padding(vertical = 10.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📅 Octubre 2026", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = "${habit.monthlyCompletedCount} de 31 días (${habit.monthlyConsistencyPercent}%)",
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Weekday headers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom").forEach { dayName ->
                        Text(
                            text = dayName,
                            color = TextSubtle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Calendar Grid (October 2026 starts on Thursday -> 3 blank spaces before day 1)
                val startOffset = 3
                val totalCells = 31 + startOffset

                Column(modifier = Modifier.fillMaxWidth()) {
                    for (row in 0 until (totalCells + 6) / 7) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (col in 0 until 7) {
                                val cellIndex = row * 7 + col
                                val dayNum = cellIndex - startOffset + 1
                                if (dayNum in 1..31) {
                                    val isCompleted = habit.monthlyHistory[dayNum] == true
                                    val isToday = dayNum == 14
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when {
                                                    isCompleted -> EmeraldPrimary
                                                    isToday -> DarkSurfaceVariant
                                                    else -> DarkSurfaceVariant.copy(alpha = 0.5f)
                                                }
                                            )
                                            .border(
                                                width = if (isToday) 2.dp else 0.dp,
                                                color = if (isToday) AmberTertiary else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { onToggleDay(dayNum) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = dayNum.toString(),
                                            color = if (isCompleted) Color.Black else TextWhite,
                                            fontSize = 12.sp,
                                            fontWeight = if (isToday || isCompleted) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.size(36.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Heatmap Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(EmeraldPrimary))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Completado", color = TextMuted, fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(DarkSurfaceVariant))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Pendiente", color = TextMuted, fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).border(1.dp, AmberTertiary, RoundedCornerShape(3.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Hoy", color = TextMuted, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = EmeraldOnPrimary)
                ) {
                    Text("Cerrar Calendario", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
