package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.KoreTask
import com.example.data.model.Priority
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TaskMetricsOverview(
    tasks: List<KoreTask>,
    modifier: Modifier = Modifier
) {
    val totalCount = tasks.size
    val completedCount = tasks.count { it.completed }
    val pendingCount = totalCount - completedCount
    val highPriorityCount = tasks.count { !it.completed && it.priority == Priority.HIGH }
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
    val progressPercent = (progress * 100).toInt()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("task_metrics_overview_card"),
        shape = RoundedCornerShape(20.dp),
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
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmeraldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TaskAlt,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Rendimiento de Tareas",
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$completedCount de $totalCount completadas ($progressPercent%)",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                if (highPriorityCount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF7F1D1D).copy(alpha = 0.5f))
                            .border(1.dp, RedError, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "⚠️ $highPriorityCount urgentes",
                            color = RedError,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                color = EmeraldPrimary,
                trackColor = DarkSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quick 3-metric row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricChip(label = "Pendientes", value = "$pendingCount", color = AmberTertiary)
                MetricChip(label = "Completadas", value = "$completedCount", color = EmeraldPrimary)
                MetricChip(label = "Alta Prioridad", value = "$highPriorityCount", color = RedError)
                MetricChip(label = "Total", value = "$totalCount", color = TextWhite)
            }
        }
    }
}

@Composable
private fun MetricChip(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DarkSurfaceVariant)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = value, color = color, fontSize = 14.sp, fontWeight = FontWeight.Black)
        Text(text = label, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun TaskCard(
    task: KoreTask,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOverdue = task.isOverdue()
    val isDueToday = task.isDueToday()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("task_card_${task.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.completed) DarkSurface.copy(alpha = 0.6f) else DarkSurface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isOverdue && !task.completed) RedError.copy(alpha = 0.6f)
                else if (isDueToday && !task.completed) AmberTertiary.copy(alpha = 0.5f)
                else DarkBorder
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Checkbox + Title + Description
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onToggle() },
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (task.completed) EmeraldPrimary else DarkSurfaceVariant)
                            .border(
                                1.5.dp,
                                if (task.completed) EmeraldPrimary else TextSubtle,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (task.completed) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completada",
                                tint = DarkBackground,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            color = if (task.completed) TextSubtle else TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
                        )

                        if (task.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = task.description,
                                color = TextMuted,
                                fontSize = 12.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Action buttons (Edit & Delete)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar tarea",
                            tint = TextSubtle,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Eliminar tarea",
                            tint = TextSubtle,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata Row: Category, Due Date, Priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category chip
                    if (task.category.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkSurfaceVariant)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = task.category,
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Deadline / Due Date chip
                    if (task.dueDate.isNotBlank()) {
                        val (chipBg, chipBorder, chipText, icon) = when {
                            task.completed -> Quadruple(DarkSurfaceVariant, DarkBorder, TextSubtle, Icons.Default.EventAvailable)
                            isOverdue -> Quadruple(Color(0xFF7F1D1D).copy(alpha = 0.3f), RedError, RedError, Icons.Default.WarningAmber)
                            isDueToday -> Quadruple(Color(0xFF78350F).copy(alpha = 0.3f), AmberTertiary, AmberTertiary, Icons.Default.AccessTime)
                            else -> Quadruple(DarkSurfaceVariant, DarkBorder, TextWhite, Icons.Default.CalendarToday)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(chipBg)
                                .border(1.dp, chipBorder, RoundedCornerShape(6.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = chipText,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isOverdue && !task.completed) "Vencida: ${task.dueDate}"
                                    else if (isDueToday && !task.completed) "Vence hoy"
                                    else "Vence: ${task.dueDate}",
                                    color = chipText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Priority Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(task.priority.colorHex).copy(alpha = 0.2f))
                        .border(1.dp, Color(task.priority.colorHex), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${task.priority.iconEmoji} ${task.priority.title}",
                        color = Color(task.priority.colorHex),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun AddEditTaskDialog(
    taskToEdit: KoreTask? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, desc: String, priority: Priority, dueDate: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
    var desc by remember { mutableStateOf(taskToEdit?.description ?: "") }
    var selectedPriority by remember { mutableStateOf(taskToEdit?.priority ?: Priority.MEDIUM) }
    var dueDate by remember { mutableStateOf(taskToEdit?.dueDate ?: "") }
    var selectedCategory by remember { mutableStateOf(taskToEdit?.category ?: "General") }
    var customDateInput by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val todayStr = remember { dateFormat.format(Date()) }

    val categories = listOf("General", "Finanzas", "Trabajo", "Personal", "Presupuesto", "Salud", "Estudio")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("add_edit_task_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = if (taskToEdit == null) "Nueva Tarea con Fecha Límite" else "Editar Tarea",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Asigna prioridad y fecha de vencimiento para mantenerte al día.",
                    color = TextMuted,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título de la tarea *", color = TextMuted) },
                    placeholder = { Text("Ej. Pagar servicios, Entregar reporte", color = TextSubtle) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_input_title")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Description
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Descripción o notas (opcional)", color = TextMuted) },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Priority Selection
                Text(text = "Nivel de Prioridad", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Priority.values().forEach { priority ->
                        val isSelected = selectedPriority == priority
                        val prioColor = Color(priority.colorHex)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) prioColor.copy(alpha = 0.25f) else DarkSurfaceVariant)
                                .border(
                                    1.5.dp,
                                    if (isSelected) prioColor else DarkBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedPriority = priority }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${priority.iconEmoji} ${priority.title}",
                                color = if (isSelected) prioColor else TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Due Date Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Fecha Límite (Deadline)", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    if (dueDate.isNotBlank()) {
                        Text(
                            text = "Seleccionada: $dueDate",
                            color = EmeraldPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val quickPresets = listOf(
                        "Hoy" to todayStr,
                        "Mañana" to getOffsetDate(1),
                        "En 3 días" to getOffsetDate(3),
                        "Próx. semana" to getOffsetDate(7),
                        "Fin de mes" to getEndOfMonthDate(),
                        "Sin fecha" to ""
                    )

                    items(quickPresets) { (label, dateVal) ->
                        val isSelected = dueDate == dateVal
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) EmeraldContainer else DarkSurfaceVariant)
                                .border(1.dp, if (isSelected) EmeraldPrimary else DarkBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    dueDate = dateVal
                                    customDateInput = false
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) EmeraldOnContainer else TextWhite,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Optional manual date input
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Fecha límite exacta (AAAA-MM-DD)", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category selector
                Text(text = "Categoría", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                                .padding(horizontal = 9.dp, vertical = 5.dp)
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

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons
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
                            if (title.isNotBlank()) {
                                onSave(
                                    title.trim(),
                                    desc.trim(),
                                    selectedPriority,
                                    dueDate.ifBlank { todayStr },
                                    selectedCategory
                                )
                                onDismiss()
                            }
                        },
                        enabled = title.isNotBlank(),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("task_btn_save"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = EmeraldOnPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (taskToEdit == null) "Crear Tarea" else "Guardar",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun getOffsetDate(days: Int): String {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, days)
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return sdf.format(cal.time)
}

private fun getEndOfMonthDate(): String {
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return sdf.format(cal.time)
}
