package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Account
import com.example.data.model.CurrencyCode
import com.example.data.model.Goal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditGoalDialog(
    goalToEdit: Goal? = null,
    accounts: List<Account>,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        emoji: String,
        targetAmount: Double,
        currency: CurrencyCode,
        deadline: String,
        initialAmount: Double,
        category: String,
        accountId: String?,
        notes: String,
        colorHex: Long
    ) -> Unit
) {
    val isEditing = goalToEdit != null

    var title by remember { mutableStateOf(goalToEdit?.title ?: "") }
    var selectedEmoji by remember { mutableStateOf(goalToEdit?.emoji ?: "🎯") }
    var targetText by remember {
        mutableStateOf(if (goalToEdit != null && goalToEdit.targetAmount > 0.0) goalToEdit.targetAmount.toString() else "")
    }
    var initialAmountText by remember {
        mutableStateOf(if (isEditing && goalToEdit != null && goalToEdit.currentAmount > 0.0) goalToEdit.currentAmount.toString() else "")
    }
    var currency by remember { mutableStateOf(goalToEdit?.currency ?: CurrencyCode.USD) }
    var category by remember { mutableStateOf(goalToEdit?.category ?: "Fondo de Emergencia") }
    var selectedAccountId by remember { mutableStateOf<String?>(goalToEdit?.accountId ?: accounts.firstOrNull()?.id) }
    var notes by remember { mutableStateOf(goalToEdit?.notes ?: "") }
    var colorHex by remember { mutableStateOf(goalToEdit?.colorHex ?: 0xFF10B981) }

    // Precalculate default deadline (+6 months from today)
    val defaultDeadline = remember {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, 6)
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }
    var deadline by remember { mutableStateOf(goalToEdit?.deadline ?: defaultDeadline) }

    val presetEmojis = listOf(
        "🎯", "🛡️", "🚗", "🏠", "✈️", "💻", "📱", "🎓", "💼", "💍", "🏖️", "🏥", "👶", "💰"
    )

    val presetCategories = listOf(
        "Fondo de Emergencia",
        "Ahorro General",
        "Bienes y Hogar",
        "Vehículo y Transporte",
        "Viajes y Ocio",
        "Educación y Crecimiento",
        "Tecnología y Trabajo",
        "Inversión Futura"
    )

    val presetColors = listOf(
        0xFF10B981 to "Verde Éxito",
        0xFF6366F1 to "Índigo",
        0xFF3B82F6 to "Azul",
        0xFFF59E0B to "Ámbar",
        0xFFEC4899 to "Rosa",
        0xFF8B5CF6 to "Púrpura",
        0xFF06B6D4 to "Cian"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(colorHex).copy(alpha = 0.15f))
                                .border(1.dp, Color(colorHex), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = selectedEmoji, fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isEditing) "Editar Meta de Ahorro" else "Nueva Meta de Ahorro",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Define tu objetivo financiero específico",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nombre de la meta") },
                    placeholder = { Text("Ej: Fondo de 3 meses, Viaje familiar...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("goal_title_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Emoji Selection Row
                Text(
                    text = "Ícono / Emoji representativo",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(presetEmojis) { emoji ->
                        val isSelected = selectedEmoji == emoji
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedEmoji = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Target Amount & Currency
                Text(
                    text = "Monto Objetivo a Ahorrar",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = targetText,
                        onValueChange = { targetText = it },
                        label = { Text("Monto") },
                        placeholder = { Text("0.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("goal_target_amount_input"),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    // Currency Switcher
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            .padding(4.dp)
                    ) {
                        listOf(CurrencyCode.USD, CurrencyCode.VES).forEach { c ->
                            val isSelected = currency == c
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { currency = c }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = c.name,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Initial amount if creating new goal
                if (!isEditing) {
                    OutlinedTextField(
                        value = initialAmountText,
                        onValueChange = { initialAmountText = it },
                        label = { Text("Aporte inicial acumulado (Opcional)") },
                        placeholder = { Text("0.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("goal_initial_amount_input"),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    val initAmt = initialAmountText.toDoubleOrNull() ?: 0.0
                    if (initAmt > 0 && accounts.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Debitar depósito inicial de la cuenta:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(accounts) { acc ->
                                val isAccSelected = selectedAccountId == acc.id
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isAccSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                        .border(
                                            1.dp,
                                            if (isAccSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { selectedAccountId = acc.id }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "${acc.name} (${acc.currency.name})",
                                        fontSize = 11.sp,
                                        color = if (isAccSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Category selector
                Text(
                    text = "Categoría del objetivo",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(presetCategories) { cat ->
                        val isSelected = category == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { category = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Deadline
                OutlinedTextField(
                    value = deadline,
                    onValueChange = { deadline = it },
                    label = { Text("Fecha límite / Plazo (AAAA-MM-DD)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("goal_deadline_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))
                // Quick deadline shortcut chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(
                        "3 meses" to 3,
                        "6 meses" to 6,
                        "1 año" to 12,
                        "2 años" to 24
                    ).forEach { (label, months) ->
                        val chipDate = remember(months) {
                            val cal = Calendar.getInstance()
                            cal.add(Calendar.MONTH, months)
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (deadline == chipDate) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { deadline = chipDate }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "+$label",
                                fontSize = 10.sp,
                                color = if (deadline == chipDate) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Color Accent Row
                Text(
                    text = "Color distintivo de la meta",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    presetColors.forEach { (colorValue, _) ->
                        val isSelected = colorHex == colorValue
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(colorValue))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { colorHex = colorValue }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas o motivación (Opcional)") },
                    placeholder = { Text("Ej: Aportar $50 semanales de ingresos freelance...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 2
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
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            val target = targetText.toDoubleOrNull() ?: 0.0
                            val initial = initialAmountText.toDoubleOrNull() ?: 0.0
                            if (title.isNotBlank() && target > 0) {
                                onSave(
                                    title.trim(),
                                    selectedEmoji,
                                    target,
                                    currency,
                                    deadline.ifBlank { defaultDeadline },
                                    initial,
                                    category,
                                    if (initial > 0) selectedAccountId else null,
                                    notes.trim(),
                                    colorHex
                                )
                                onDismiss()
                            }
                        },
                        enabled = title.isNotBlank() && (targetText.toDoubleOrNull() ?: 0.0) > 0,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_goal_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Savings, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEditing) "Actualizar" else "Crear Meta",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
