package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.KoreLifeViewModel
import com.example.ui.components.AddEditRecurringExpenseDialog
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun RecurringExpensesTab(
    recurringExpenses: List<RecurringExpense>,
    budgets: List<Budget>,
    accounts: List<Account>,
    viewModel: KoreLifeViewModel,
    onAddNew: () -> Unit
) {
    var expenseToEdit by remember { mutableStateOf<RecurringExpense?>(null) }
    var expenseToDelete by remember { mutableStateOf<RecurringExpense?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val currentMonth = remember { SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date()) }
    val currentDay = remember { Calendar.getInstance().get(Calendar.DAY_OF_MONTH) }

    // Dialog for editing
    if (expenseToEdit != null) {
        AddEditRecurringExpenseDialog(
            expenseToEdit = expenseToEdit,
            accounts = accounts,
            budgets = budgets,
            onDismiss = { expenseToEdit = null },
            onSave = { title, amt, curr, cat, day, accId, autoDeduct, notes ->
                viewModel.updateRecurringExpense(
                    expenseToEdit!!.copy(
                        title = title,
                        amount = amt,
                        currency = curr,
                        category = cat,
                        dayOfMonth = day,
                        accountId = accId,
                        autoDeduct = autoDeduct,
                        notes = notes
                    )
                )
                expenseToEdit = null
                statusMessage = "Gasto recurrente \"$title\" actualizado con éxito."
            }
        )
    }

    // Dialog for delete confirmation
    if (expenseToDelete != null) {
        AlertDialog(
            onDismissRequest = { expenseToDelete = null },
            title = { Text("Eliminar Gasto Recurrente", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = { Text("¿Deseas eliminar \"${expenseToDelete!!.title}\"? Los gastos ya deducidos permanecerán en tu historial y presupuesto.", color = TextMuted) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteRecurringExpense(expenseToDelete!!.id)
                    expenseToDelete = null
                    statusMessage = "Gasto recurrente eliminado."
                }) {
                    Text("Eliminar", color = RedError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { expenseToDelete = null }) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = DarkSurface
        )
    }

    // Totals calculation
    val totalRecurringVes = recurringExpenses.filter { it.isActive }.sumOf {
        viewModel.repository.convert(it.amount, it.currency, CurrencyCode.VES)
    }
    val totalRecurringUsd = viewModel.repository.convert(totalRecurringVes, CurrencyCode.VES, CurrencyCode.USD)

    val deductedThisMonthCount = recurringExpenses.count { it.lastDeductedMonth == currentMonth }
    val pendingThisMonthCount = recurringExpenses.count { it.isActive && it.lastDeductedMonth != currentMonth }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp)
    ) {
        // Status Banner if an action was executed
        if (statusMessage != null) {
            item {
                AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(EmeraldContainer.copy(alpha = 0.5f))
                            .border(1.dp, EmeraldPrimary, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(text = "⚡", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = statusMessage!!, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                            IconButton(
                                onClick = { statusMessage = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextWhite, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // Header Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
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
                                    .background(EmeraldPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Repeat, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Gastos Fijos Recurrentes",
                                    color = TextWhite,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Deducción automática contra presupuesto",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Day of current month indicator
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceVariant)
                                .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Hoy: Día $currentDay",
                                color = TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Monthly Total Commitment
                    Text(text = "Total mensual recurrente activo:", color = TextMuted, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = viewModel.repository.formatCurrency(totalRecurringVes, CurrencyCode.VES),
                            color = TextWhite,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "≈ ${viewModel.repository.formatCurrency(totalRecurringUsd, CurrencyCode.USD)}",
                            color = EmeraldPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats row: Deducted vs Pending
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Deducted this month badge
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(EmeraldContainer.copy(alpha = 0.4f))
                                .border(1.dp, EmeraldPrimary.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Deducidos este mes", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$deductedThisMonthCount gastos",
                                    color = EmeraldPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        // Pending this month badge
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(DarkSurfaceVariant)
                                .border(1.dp, if (pendingThisMonthCount > 0) AmberWarning.copy(alpha = 0.5f) else DarkBorder, RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Schedule, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pendientes mes", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$pendingThisMonthCount gastos",
                                    color = if (pendingThisMonthCount > 0) AmberWarning else TextMuted,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val processed = viewModel.checkAndProcessRecurringExpenses(forceAllDue = false)
                                statusMessage = if (processed > 0) {
                                    "¡Listo! Se procesaron y dedujeron $processed gastos programados para hoy de su cuenta y presupuesto."
                                } else {
                                    "Todos los gastos recurrentes hasta el día $currentDay ya fueron procesados o no están vencidos."
                                }
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("btn_check_recurring_deductions"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldPrimary,
                                contentColor = EmeraldOnPrimary
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Verificar y Deducir", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = onAddNew,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_add_recurring_expense"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(EmeraldPrimary))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = EmeraldPrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Nuevo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gastos Programados (${recurringExpenses.size})",
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Deducción automática activa",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // List of Recurring Expenses
        if (recurringExpenses.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🗓️", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No tienes gastos recurrentes configurados",
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Agrega alquileres, servicios, colegiaturas o cuotas fijas para que se descuenten automáticamente de tu presupuesto en la fecha elegida.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onAddNew,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = EmeraldOnPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Agregar mi primer gasto recurrente", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(recurringExpenses) { rec ->
                val isDeductedThisMonth = rec.lastDeductedMonth == currentMonth
                val account = accounts.find { it.id == rec.accountId }
                val linkedBudget = budgets.find {
                    it.category.equals(rec.category, ignoreCase = true) ||
                    rec.category.contains(it.category, ignoreCase = true) ||
                    it.category.contains(rec.category, ignoreCase = true)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("recurring_card_${rec.id}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isDeductedThisMonth) EmeraldPrimary.copy(alpha = 0.4f) else DarkBorder
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Top row: Emoji, Title, Category, Amount
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (isDeductedThisMonth) EmeraldContainer.copy(alpha = 0.5f) else DarkSurfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = getCategoryEmoji(rec.category), fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = rec.title,
                                        color = TextWhite,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${rec.category} • Cuenta: ${account?.name ?: "Banesco"}",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Amount badge
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = viewModel.repository.formatCurrency(rec.amount, rec.currency),
                                    color = if (isDeductedThisMonth) EmeraldPrimary else TextWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "/ mes",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Middle row: Day of month, Status chip, Active Switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceVariant.copy(alpha = 0.5f))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Date badge
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Event, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Día ${rec.dayOfMonth} de cada mes",
                                    color = TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Status badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when {
                                            !rec.isActive -> DarkSurfaceVariant
                                            isDeductedThisMonth -> EmeraldContainer
                                            else -> AmberWarning.copy(alpha = 0.2f)
                                        }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = when {
                                        !rec.isActive -> "Pausado"
                                        isDeductedThisMonth -> "✅ Deducido este mes"
                                        currentDay >= rec.dayOfMonth -> "⚠️ Pendiente hoy"
                                        else -> "⏳ Próximo: Día ${rec.dayOfMonth}"
                                    },
                                    color = when {
                                        !rec.isActive -> TextMuted
                                        isDeductedThisMonth -> EmeraldOnContainer
                                        else -> AmberWarning
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Linked Budget impact row
                        Spacer(modifier = Modifier.height(8.dp))
                        if (linkedBudget != null) {
                            val budgetUsagePercent = ((linkedBudget.spent / linkedBudget.amount) * 100).toInt()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(EmeraldPrimary.copy(alpha = 0.08f))
                                    .border(1.dp, EmeraldPrimary.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Impacta: \"${linkedBudget.category}\" (Uso actual: $budgetUsagePercent%)",
                                        color = TextWhite,
                                        fontSize = 11.sp
                                    )
                                }
                                Text(
                                    text = "Tope: ${linkedBudget.currency.symbol} ${linkedBudget.amount.toInt()}",
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Notes if any
                        if (rec.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "📝 ${rec.notes}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Bottom Actions: Manual Deduct now, Toggle Active, Edit, Delete
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (!isDeductedThisMonth && rec.isActive) {
                                    Button(
                                        onClick = {
                                            viewModel.executeRecurringExpenseDeduction(rec.id)
                                            statusMessage = "¡Se dedujo \"${rec.title}\" exitosamente de tu cuenta y presupuesto!"
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = EmeraldPrimary,
                                            contentColor = EmeraldOnPrimary
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Deducir ahora", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                IconButton(
                                    onClick = { expenseToEdit = rec },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = TextMuted, modifier = Modifier.size(18.dp))
                                }

                                IconButton(
                                    onClick = { expenseToDelete = rec },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = RedError.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                                }
                            }

                            // Active toggle switch
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (rec.isActive) "Activo" else "Pausa",
                                    color = if (rec.isActive) EmeraldPrimary else TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = rec.isActive,
                                    onCheckedChange = { viewModel.toggleRecurringExpenseActive(rec.id) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = EmeraldOnPrimary,
                                        checkedTrackColor = EmeraldPrimary,
                                        uncheckedThumbColor = TextMuted,
                                        uncheckedTrackColor = DarkSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
