package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Repeat
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
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun AddEditRecurringExpenseDialog(
    expenseToEdit: RecurringExpense? = null,
    accounts: List<Account>,
    budgets: List<Budget>,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        amount: Double,
        currency: CurrencyCode,
        category: String,
        dayOfMonth: Int,
        accountId: String,
        autoDeduct: Boolean,
        notes: String
    ) -> Unit
) {
    val presetCategories = listOf(
        Pair("Hogar", "🏡"),
        Pair("Servicios del Hogar", "💡"),
        Pair("Alimentación", "🛒"),
        Pair("Transporte", "🚌"),
        Pair("Salud", "💊"),
        Pair("Educación", "📚"),
        Pair("Entretenimiento", "🎬"),
        Pair("Tecnología", "💻"),
        Pair("Otros Gastos", "📌")
    )

    var title by remember { mutableStateOf(expenseToEdit?.title ?: "") }
    var selectedCategory by remember { mutableStateOf(expenseToEdit?.category ?: presetCategories[0].first) }
    var amountText by remember { mutableStateOf(expenseToEdit?.amount?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "") }
    var selectedCurrency by remember { mutableStateOf(expenseToEdit?.currency ?: CurrencyCode.USD) }
    var selectedAccountId by remember { mutableStateOf(expenseToEdit?.accountId ?: (accounts.firstOrNull()?.id ?: "1")) }
    var dayOfMonth by remember { mutableIntStateOf(expenseToEdit?.dayOfMonth ?: 1) }
    var autoDeduct by remember { mutableStateOf(expenseToEdit?.autoDeduct ?: true) }
    var notes by remember { mutableStateOf(expenseToEdit?.notes ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val matchingBudget = budgets.find {
        it.category.equals(selectedCategory, ignoreCase = true) ||
        selectedCategory.contains(it.category, ignoreCase = true) ||
        it.category.contains(selectedCategory, ignoreCase = true)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp)
                .testTag("dialog_recurring_expense")
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(EmeraldPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Repeat,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (expenseToEdit == null) "Programar Gasto Recurrente" else "Editar Gasto Recurrente",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Deducción automática mensual con impacto en presupuesto",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Title input
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        errorMessage = null
                    },
                    label = { Text("Concepto / Nombre del gasto", color = TextMuted, fontSize = 13.sp) },
                    placeholder = { Text("Ej. Alquiler, Fibra Óptica, Colegio", color = TextMuted.copy(alpha = 0.6f), fontSize = 13.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_recurring_title")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Category selection
                Text(
                    text = "Categoría vinculada al presupuesto:",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(presetCategories) { (cat, emoji) ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) EmeraldPrimary else DarkSurfaceVariant)
                                .border(1.dp, if (isSelected) EmeraldPrimary else DarkBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = emoji, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = cat,
                                    color = if (isSelected) EmeraldOnPrimary else TextWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Matching budget preview banner
                Spacer(modifier = Modifier.height(8.dp))
                if (matchingBudget != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmeraldContainer.copy(alpha = 0.35f))
                            .border(1.dp, EmeraldPrimary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🎯", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Presupuesto detectado: \"${matchingBudget.category}\" (${matchingBudget.currency.symbol} ${matchingBudget.amount})",
                                color = TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurfaceVariant.copy(alpha = 0.7f))
                            .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "ℹ️ No tienes un sobre de presupuesto para \"$selectedCategory\". Se registrará el gasto y puedes crear el presupuesto cuando gustes.",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Amount and Currency
                Text(
                    text = "Monto recurrente mensual:",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = {
                            amountText = it
                            errorMessage = null
                        },
                        placeholder = { Text("0.00", color = TextMuted.copy(alpha = 0.5f)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_recurring_amount")
                    )

                    // Currency selector pills
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        CurrencyCode.values().forEach { curr ->
                            val isSelected = selectedCurrency == curr
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) EmeraldPrimary else DarkSurfaceVariant)
                                    .border(1.dp, if (isSelected) EmeraldPrimary else DarkBorder, RoundedCornerShape(10.dp))
                                    .clickable { selectedCurrency = curr }
                                    .padding(horizontal = 10.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = curr.symbol,
                                    color = if (isSelected) EmeraldOnPrimary else TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Account Selection
                Text(
                    text = "Cuenta de débito automático:",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(accounts) { acc ->
                        val isSelected = selectedAccountId == acc.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) EmeraldPrimary.copy(alpha = 0.2f) else DarkSurfaceVariant)
                                .border(1.dp, if (isSelected) EmeraldPrimary else DarkBorder, RoundedCornerShape(12.dp))
                                .clickable { selectedAccountId = acc.id }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Column {
                                Text(
                                    text = acc.name,
                                    color = if (isSelected) EmeraldPrimary else TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${acc.currency.symbol} ${String.format(java.util.Locale.US, "%,.2f", acc.balance)}",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Day of Month Selection
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant.copy(alpha = 0.6f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Día del mes para deducir:",
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(EmeraldPrimary)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Día $dayOfMonth de cada mes",
                                    color = EmeraldOnPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Slider(
                            value = dayOfMonth.toFloat(),
                            onValueChange = { dayOfMonth = it.toInt() },
                            valueRange = 1f..31f,
                            steps = 29,
                            colors = SliderDefaults.colors(
                                thumbColor = EmeraldPrimary,
                                activeTrackColor = EmeraldPrimary,
                                inactiveTrackColor = DarkBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Día 1", color = TextMuted, fontSize = 10.sp)
                            Text("Día 15 (Quincena)", color = TextMuted, fontSize = 10.sp)
                            Text("Día 30/31 (Fin de mes)", color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Auto-deduct Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Deducción automática programada",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Descuenta del presupuesto y cuenta al iniciar la fecha",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                    Switch(
                        checked = autoDeduct,
                        onCheckedChange = { autoDeduct = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = EmeraldOnPrimary,
                            checkedTrackColor = EmeraldPrimary,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = DarkSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Optional Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas opcionales (Nro de contrato, referencia)", color = TextMuted, fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.4f),
                        unfocusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage!!, color = RedError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
                    ) {
                        Text("Cancelar", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            val cleanTitle = title.trim()
                            val cleanAmount = amountText.replace(',', '.').toDoubleOrNull()
                            if (cleanTitle.isBlank()) {
                                errorMessage = "Por favor ingresa un nombre para el gasto recurrente."
                                return@Button
                            }
                            if (cleanAmount == null || cleanAmount <= 0) {
                                errorMessage = "Ingresa un monto válido mayor a 0."
                                return@Button
                            }
                            onSave(
                                cleanTitle,
                                cleanAmount,
                                selectedCurrency,
                                selectedCategory,
                                dayOfMonth,
                                selectedAccountId,
                                autoDeduct,
                                notes.trim()
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_save_recurring"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = EmeraldOnPrimary
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (expenseToEdit == null) "Guardar" else "Actualizar",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
