package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.KoreLifeViewModel
import com.example.ui.components.AddTransactionDialog
import com.example.ui.components.AddEditRecurringExpenseDialog
import com.example.ui.components.AddEditGoalDialog
import com.example.ui.components.ExportPdfSummaryDialog
import com.example.ui.components.DailyReminderCard
import com.example.ui.components.ExchangeRatesCard
import com.example.ui.components.ExpensesPieChart
import com.example.ui.components.TransactionsHistoryView
import com.example.ui.screens.RecurringExpensesTab
import com.example.ui.screens.SavingsGoalsTab
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun FinanceScreen(
    viewModel: KoreLifeViewModel,
    modifier: Modifier = Modifier
) {
    val accounts by viewModel.accounts.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val budgets by viewModel.budgets.collectAsState()
    val recurringExpenses by viewModel.recurringExpenses.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val subscriptions by viewModel.subscriptions.collectAsState()
    val hideBalances by viewModel.hideBalances.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Principal", "Cuentas", "Transacciones", "Presupuestos", "Recurrentes", "Metas de Ahorro", "Suscripciones", "Conversor")

    var showAddTxDialog by remember { mutableStateOf(false) }
    var showAddAccountDialog by remember { mutableStateOf(false) }
    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var showAddRecurringDialog by remember { mutableStateOf(false) }
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var showExportPdfDialog by remember { mutableStateOf(false) }

    if (showAddTxDialog) {
        AddTransactionDialog(
            accounts = accounts,
            onDismiss = { showAddTxDialog = false },
            onSave = { accId, amt, curr, type, cat, desc ->
                viewModel.addTransaction(accId, amt, curr, type, cat, desc)
            }
        )
    }

    if (showAddAccountDialog) {
        AddAccountDialog(
            onDismiss = { showAddAccountDialog = false },
            onSave = { name, type, balance, currency ->
                viewModel.addAccount(name, type, balance, currency)
            }
        )
    }

    if (showAddBudgetDialog) {
        AddBudgetDialog(
            onDismiss = { showAddBudgetDialog = false },
            onSave = { cat, amt, curr ->
                viewModel.addBudget(cat, amt, curr)
            }
        )
    }

    if (showAddRecurringDialog) {
        AddEditRecurringExpenseDialog(
            accounts = accounts,
            budgets = budgets,
            onDismiss = { showAddRecurringDialog = false },
            onSave = { title, amt, curr, cat, day, accId, autoDeduct, notes ->
                viewModel.addRecurringExpense(title, amt, curr, cat, day, accId, autoDeduct, notes)
                showAddRecurringDialog = false
            }
        )
    }

    if (showAddGoalDialog) {
        AddEditGoalDialog(
            accounts = accounts,
            onDismiss = { showAddGoalDialog = false },
            onSave = { title, emoji, target, curr, deadline, initial, cat, accId, notes, color ->
                viewModel.addGoal(
                    title = title,
                    emoji = emoji,
                    targetAmount = target,
                    currency = curr,
                    deadline = deadline,
                    initialAmount = initial,
                    category = cat,
                    accountId = accId,
                    notes = notes,
                    colorHex = color
                )
                showAddGoalDialog = false
            }
        )
    }

    if (showExportPdfDialog) {
        ExportPdfSummaryDialog(
            transactions = transactions,
            goals = goals,
            recurringExpenses = recurringExpenses,
            accounts = accounts,
            repository = viewModel.repository,
            onDismiss = { showExportPdfDialog = false }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTxDialog = true },
                containerColor = EmeraldPrimary,
                contentColor = EmeraldOnPrimary,
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .testTag("finance_fab_add_transaction")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Centro Financiero", color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.Black)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledTonalButton(
                        onClick = { showExportPdfDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = DarkSurfaceVariant,
                            contentColor = EmeraldPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("finance_btn_export_pdf")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Exportar PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(onClick = { viewModel.toggleHideBalances() }) {
                        Icon(
                            imageVector = if (hideBalances) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Ocultar saldos",
                            tint = TextMuted
                        )
                    }
                }
            }

            // Scrollable Sub-Tabs
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tabTitles.indices.toList()) { index ->
                    val isSelected = selectedTab == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) EmeraldPrimary else DarkSurfaceVariant)
                            .border(1.dp, if (isSelected) EmeraldPrimary else DarkBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedTab = index }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = tabTitles[index],
                            color = if (isSelected) EmeraldOnPrimary else TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-Tab Content
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> MainFinanceTab(
                        viewModel = viewModel,
                        accounts = accounts,
                        transactions = transactions,
                        hideBalances = hideBalances,
                        onExportPdf = { showExportPdfDialog = true }
                    )
                    1 -> AccountsTab(
                        accounts = accounts,
                        hideBalances = hideBalances,
                        viewModel = viewModel,
                        onAddAccount = { showAddAccountDialog = true }
                    )
                    2 -> TransactionsTab(
                        transactions = transactions,
                        viewModel = viewModel
                    )
                    3 -> BudgetsTab(
                        budgets = budgets,
                        accounts = accounts,
                        viewModel = viewModel,
                        onAddBudget = { showAddBudgetDialog = true },
                        onGoToRecurring = { selectedTab = 4 }
                    )
                    4 -> RecurringExpensesTab(
                        recurringExpenses = recurringExpenses,
                        budgets = budgets,
                        accounts = accounts,
                        viewModel = viewModel,
                        onAddNew = { showAddRecurringDialog = true }
                    )
                    5 -> SavingsGoalsTab(
                        goals = goals,
                        accounts = accounts,
                        viewModel = viewModel,
                        onAddNewGoal = { showAddGoalDialog = true },
                        onExportPdf = { showExportPdfDialog = true }
                    )
                    6 -> SubscriptionsTab(
                        subscriptions = subscriptions,
                        viewModel = viewModel
                    )
                    7 -> CurrencyConverterTab(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun MainFinanceTab(
    viewModel: KoreLifeViewModel,
    accounts: List<Account>,
    transactions: List<Transaction>,
    hideBalances: Boolean,
    onExportPdf: () -> Unit
) {
    val rates by viewModel.rates.collectAsState()
    val selectedProvider by viewModel.selectedProvider.collectAsState()
    val isRatesLoading by viewModel.isRatesLoading.collectAsState()

    var expenseAmount by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf(CurrencyCode.VES) }
    var selectedCategory by remember { mutableStateOf("Supermercado") }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: "1") }
    var expenseDescription by remember { mutableStateOf("") }
    var showSuccessBanner by remember { mutableStateOf(false) }

    val categories = listOf(
        Pair("Supermercado", "🛒"),
        Pair("Comida", "🍔"),
        Pair("Transporte", "🚌"),
        Pair("Servicios", "💡"),
        Pair("Salud", "💊"),
        Pair("Café", "☕"),
        Pair("Compras", "🛍️"),
        Pair("Otros", "📌")
    )

    val todayStr = remember {
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
    }

    val todayExpenses = transactions.filter {
        it.type == TransactionType.EXPENSE && it.date == todayStr
    }

    val todaySpentInVes = todayExpenses.sumOf {
        viewModel.repository.convert(it.amount, it.currency, CurrencyCode.VES)
    }
    val todaySpentInUsd = viewModel.repository.convert(todaySpentInVes, CurrencyCode.VES, CurrencyCode.USD)

    val totalVes = viewModel.repository.getTotalBalanceInVes()
    val totalUsd = viewModel.repository.getTotalBalanceInUsd()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp)
    ) {
        // 1. Monitor de Tasas de Cambio en Vivo
        item {
            ExchangeRatesCard(
                rates = rates,
                selectedProvider = selectedProvider,
                isLoading = isRatesLoading,
                onSelectProvider = { viewModel.setRateProvider(it) },
                onRefresh = { viewModel.refreshRates() }
            )
        }

        // 2. Resumen de Patrimonio y Gasto de Hoy
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
                        Column {
                            Text("SALDO TOTAL DISPONIBLE", color = IndigoSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                            Text(
                                text = if (hideBalances) "••••••••••" else String.format(Locale.US, "Bs. %,.2f", totalVes),
                                color = TextWhite,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = if (hideBalances) "••••••" else String.format(Locale.US, "≈ $ %,.2f USD", totalUsd),
                                color = EmeraldPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Gasto de hoy badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(RedError.copy(alpha = 0.12f))
                                .border(1.dp, RedError.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text("GASTADO HOY", color = RedError, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                                Text(
                                    text = if (hideBalances) "••••" else String.format(Locale.US, "- Bs. %,.0f", todaySpentInVes),
                                    color = RedError,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (hideBalances) "••" else String.format(Locale.US, "≈ $ %,.2f", todaySpentInUsd),
                                    color = RedError.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Respaldo Mensual: Exportar Resumen PDF
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onExportPdf() }
                    .testTag("card_export_monthly_pdf"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EmeraldPrimary.copy(alpha = 0.35f)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF831843).copy(alpha = 0.35f))
                                .border(1.dp, Color(0xFFF43F5E).copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = Color(0xFFF43F5E),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Exportar Resumen Mensual (PDF)",
                                    color = TextWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF064E3B))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("RESPALDO", color = EmeraldPrimary, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Descarga o comparte gastos, flujo de caja y metas de ahorro",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Exportar",
                        tint = EmeraldPrimary
                    )
                }
            }
        }

        // 3. Gráfico Circular Interactivo de Gastos Mensuales (Recharts alternative in Compose)
        item {
            ExpensesPieChart(
                transactions = transactions,
                repository = viewModel.repository
            )
        }

        // 4. Sistema de Recordatorios Push Locales Diarios
        item {
            DailyReminderCard()
        }

        // 5. Formulario de Registro Rápido de Gastos Diarios
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_expense_entry_card"),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EmeraldPrimary.copy(alpha = 0.4f)))
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
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF064E3B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Registrar Gasto Diario Rápido", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text("Captura en 1 toque sin fricción", color = TextMuted, fontSize = 11.sp)
                            }
                        }

                        // Currency Switcher
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceVariant)
                                .clickable {
                                    selectedCurrency = if (selectedCurrency == CurrencyCode.VES) CurrencyCode.USD else CurrencyCode.VES
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = selectedCurrency.name,
                                color = EmeraldPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Monto Input
                    OutlinedTextField(
                        value = expenseAmount,
                        onValueChange = { expenseAmount = it },
                        label = { Text("Monto del gasto (${selectedCurrency.symbol})", color = TextMuted) },
                        leadingIcon = {
                            Text(
                                text = selectedCurrency.symbol,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quick_expense_amount_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Chips de montos predeterminados
                    val presets = if (selectedCurrency == CurrencyCode.VES) {
                        listOf(500.0, 1000.0, 2500.0, 5000.0)
                    } else {
                        listOf(5.0, 10.0, 20.0, 50.0)
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(presets) { preset ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DarkSurfaceVariant)
                                    .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                                    .clickable {
                                        val current = expenseAmount.toDoubleOrNull() ?: 0.0
                                        expenseAmount = String.format(Locale.US, "%.0f", current + preset)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (selectedCurrency == CurrencyCode.VES) "+Bs. ${preset.toInt()}" else "+$${preset.toInt()}",
                                    color = Color(0xFFA5B4FC),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Categorías
                    Text("Categoría del Gasto", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { (catName, emoji) ->
                            val isSelected = selectedCategory == catName
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) EmeraldPrimary else DarkSurfaceVariant)
                                    .border(1.dp, if (isSelected) EmeraldPrimary else DarkBorder, RoundedCornerShape(12.dp))
                                    .clickable { selectedCategory = catName }
                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(emoji, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = catName,
                                        color = if (isSelected) EmeraldOnPrimary else TextWhite,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Selector de Cuenta de Pago
                    Text("Cuenta a Debitar", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(accounts) { acc ->
                            val isSelected = selectedAccountId == acc.id
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0xFF1E1B4B) else DarkSurfaceVariant)
                                    .border(1.dp, if (isSelected) IndigoSecondary else DarkBorder, RoundedCornerShape(12.dp))
                                    .clickable { selectedAccountId = acc.id }
                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                            ) {
                                Column {
                                    Text(
                                        text = acc.name,
                                        color = if (isSelected) Color(0xFFA5B4FC) else TextWhite,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (hideBalances) "••••" else viewModel.repository.formatCurrency(acc.balance, acc.currency),
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Detalle / Comercio opcional
                    OutlinedTextField(
                        value = expenseDescription,
                        onValueChange = { expenseDescription = it },
                        label = { Text("Detalle o Comercio (ej. Farmatodo, Panadería)", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón Guardar
                    Button(
                        onClick = {
                            val amt = expenseAmount.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                viewModel.addTransaction(
                                    accountId = selectedAccountId,
                                    amount = amt,
                                    currency = selectedCurrency,
                                    type = TransactionType.EXPENSE,
                                    category = selectedCategory,
                                    description = expenseDescription.ifBlank { selectedCategory }
                                )
                                expenseAmount = ""
                                expenseDescription = ""
                                showSuccessBanner = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_quick_expense_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = EmeraldOnPrimary
                        )
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Registrar Gasto de Hoy", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    if (showSuccessBanner) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF064E3B))
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✓ ¡Gasto registrado y saldo actualizado!", color = EmeraldOnContainer, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 4. Lista de Gastos Recientes
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Gastos Registrados Recientes", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "${transactions.count { it.type == TransactionType.EXPENSE }} movimientos",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        val allExpenses = transactions.filter { it.type == TransactionType.EXPENSE }
        if (allExpenses.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Box(modifier = Modifier.padding(20.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No hay gastos registrados hoy. ¡Usa el formulario arriba!", color = TextMuted, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(allExpenses.take(6)) { exp ->
                val convertedUsd = viewModel.repository.convert(exp.amount, exp.currency, CurrencyCode.USD)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF450A0A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("💸", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(exp.description, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text("${exp.category} • ${exp.date}", color = TextMuted, fontSize = 11.sp)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "- ${viewModel.repository.formatCurrency(exp.amount, exp.currency)}",
                                color = RedError,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (exp.currency != CurrencyCode.USD) {
                                Text(
                                    text = String.format(Locale.US, "≈ $ %,.2f", convertedUsd),
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountsTab(
    accounts: List<Account>,
    hideBalances: Boolean,
    viewModel: KoreLifeViewModel,
    onAddAccount: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            Button(
                onClick = onAddAccount,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_account_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = EmeraldPrimary)
            ) {
                Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Vincular / Agregar Nueva Cuenta", fontWeight = FontWeight.Bold)
            }
        }

        items(accounts) { acc ->
            val convertedUsd = viewModel.repository.convert(acc.balance, acc.currency, CurrencyCode.USD)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = acc.name, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DarkSurfaceVariant)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = acc.type.title, color = TextMuted, fontSize = 10.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (hideBalances) "••••••" else viewModel.repository.formatCurrency(acc.balance, acc.currency),
                            color = EmeraldPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    if (acc.currency != CurrencyCode.USD) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Equivalente", color = TextMuted, fontSize = 10.sp)
                            Text(
                                text = if (hideBalances) "••••" else String.format(Locale.US, "≈ $ %,.2f", convertedUsd),
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionsTab(
    transactions: List<Transaction>,
    viewModel: KoreLifeViewModel
) {
    TransactionsHistoryView(
        transactions = transactions,
        viewModel = viewModel
    )
}

@Composable
private fun BudgetsTab(
    budgets: List<Budget>,
    accounts: List<Account>,
    viewModel: KoreLifeViewModel,
    onAddBudget: () -> Unit,
    onGoToRecurring: () -> Unit = {}
) {
    val budgetAlerts by viewModel.budgetAlerts.collectAsState()
    var budgetToEdit by remember { mutableStateOf<Budget?>(null) }
    var budgetForQuickExpense by remember { mutableStateOf<Budget?>(null) }
    var budgetToDelete by remember { mutableStateOf<Budget?>(null) }

    if (budgetToEdit != null) {
        EditBudgetDialog(
            budget = budgetToEdit!!,
            onDismiss = { budgetToEdit = null },
            onSave = { newAmount, newCurr ->
                viewModel.updateBudget(budgetToEdit!!.id, newAmount, newCurr)
                budgetToEdit = null
            }
        )
    }

    if (budgetForQuickExpense != null) {
        QuickBudgetExpenseDialog(
            budget = budgetForQuickExpense!!,
            accounts = accounts,
            onDismiss = { budgetForQuickExpense = null },
            onSave = { accountId, amount, desc ->
                viewModel.addTransaction(
                    accountId = accountId,
                    amount = amount,
                    currency = budgetForQuickExpense!!.currency,
                    type = TransactionType.EXPENSE,
                    category = budgetForQuickExpense!!.category,
                    description = desc
                )
                budgetForQuickExpense = null
            }
        )
    }

    if (budgetToDelete != null) {
        AlertDialog(
            onDismissRequest = { budgetToDelete = null },
            title = { Text("Eliminar Presupuesto", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = { Text("¿Deseas eliminar el sobre presupuestario de \"${budgetToDelete!!.category}\"?", color = TextMuted) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteBudget(budgetToDelete!!.id)
                    budgetToDelete = null
                }) {
                    Text("Eliminar", color = RedError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { budgetToDelete = null }) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = DarkSurface
        )
    }

    val totalBudgetedVes = budgets.sumOf {
        viewModel.repository.convert(it.amount, it.currency, CurrencyCode.VES)
    }
    val totalSpentVes = budgets.sumOf {
        viewModel.repository.convert(it.spent, it.currency, CurrencyCode.VES)
    }
    val totalRemainingVes = totalBudgetedVes - totalSpentVes
    val globalProgress = if (totalBudgetedVes > 0) (totalSpentVes / totalBudgetedVes).toFloat() else 0f
    val animatedGlobalProgress by androidx.compose.animation.core.animateFloatAsState(
        targetValue = globalProgress.coerceIn(0f, 1f),
        animationSpec = androidx.compose.animation.core.tween(700),
        label = "global_budget_progress"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp)
    ) {
        if (budgetAlerts.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("smart_budget_alerts_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (budgetAlerts.any { it.severity == BudgetAlertSeverity.CRITICAL }) RedError.copy(alpha = 0.6f) else AmberTertiary.copy(alpha = 0.5f)
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🔔", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sistema de Alertas Inteligentes",
                                    color = TextWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (budgetAlerts.any { it.severity == BudgetAlertSeverity.CRITICAL }) RedError.copy(alpha = 0.2f) else AmberTertiary.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${budgetAlerts.size} avisos",
                                    color = if (budgetAlerts.any { it.severity == BudgetAlertSeverity.CRITICAL }) RedError else AmberTertiary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        budgetAlerts.forEach { alert ->
                            val isCritical = alert.severity == BudgetAlertSeverity.CRITICAL
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isCritical) RedError.copy(alpha = 0.15f) else AmberTertiary.copy(alpha = 0.15f))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isCritical) "🚨" else "⚠️",
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = alert.message,
                                        color = TextWhite,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 1. Resumen Global de Presupuesto Mensual
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("budget_overview_summary_card"),
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
                        Column {
                            Text(
                                text = "Presupuestos Mensuales (YNAB)",
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Asignación inteligente de sobres por categoría",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        // Porcentaje Global Badge
                        val pctGlobal = if (totalBudgetedVes > 0) (totalSpentVes / totalBudgetedVes * 100) else 0.0
                        val badgeColor = when {
                            pctGlobal >= 100 -> RedError
                            pctGlobal >= 85 -> Color(0xFFF97316)
                            pctGlobal >= 70 -> AmberTertiary
                            else -> EmeraldPrimary
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(badgeColor.copy(alpha = 0.15f))
                                .border(1.dp, badgeColor, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = String.format(Locale.US, "%.1f%% Utilizado", pctGlobal),
                                color = badgeColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Global Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkSurfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedGlobalProgress)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when {
                                        globalProgress >= 1f -> RedError
                                        globalProgress >= 0.85f -> Color(0xFFF97316)
                                        globalProgress >= 0.70f -> AmberTertiary
                                        else -> EmeraldPrimary
                                    }
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Metrics row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("GASTADO TOTAL", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = String.format(Locale.US, "Bs. %,.2f", totalSpentVes),
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("LÍMITE TOTAL", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = String.format(Locale.US, "Bs. %,.2f", totalBudgetedVes),
                                color = Color(0xFFA5B4FC),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("DISPONIBLE", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (totalRemainingVes >= 0) String.format(Locale.US, "Bs. %,.2f", totalRemainingVes) else String.format(Locale.US, "-Bs. %,.2f", -totalRemainingVes),
                                color = if (totalRemainingVes >= 0) EmeraldPrimary else RedError,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        // 2. Action Button to Define Budget
        item {
            Button(
                onClick = onAddBudget,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("add_budget_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPrimary,
                    contentColor = EmeraldOnPrimary
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Definir Nuevo Presupuesto Mensual", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        // 2b. Card banner for Recurring Expenses that deduct from budget
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onGoToRecurring)
                    .testTag("banner_recurring_expenses_in_budget"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant.copy(alpha = 0.7f)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EmeraldPrimary.copy(alpha = 0.4f)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmeraldPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Repeat, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Gastos Recurrentes Programados", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Deducción mensual automática de presupuestos y cuentas", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = "Ver", tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                }
            }
        }

        // 3. Budgets by Category List
        items(budgets) { budget ->
            val ratio = if (budget.amount > 0) (budget.spent / budget.amount).toFloat() else 0f
            val isExceeded = budget.spent > budget.amount
            val percentageUsed = if (budget.amount > 0) (budget.spent / budget.amount * 100.0) else 0.0

            val animatedProgress by androidx.compose.animation.core.animateFloatAsState(
                targetValue = ratio.coerceIn(0f, 1f),
                animationSpec = androidx.compose.animation.core.tween(600),
                label = "progress_${budget.id}"
            )

            val statusColor = when {
                isExceeded -> RedError
                ratio >= 0.85f -> Color(0xFFF97316)
                ratio >= 0.70f -> AmberTertiary
                else -> EmeraldPrimary
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("budget_item_${budget.id}"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (isExceeded) RedError.copy(alpha = 0.6f) else DarkBorder
                    )
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Category Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(statusColor.copy(alpha = 0.15f))
                                    .border(1.dp, statusColor.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = com.example.data.model.getCategoryEmoji(budget.category),
                                    fontSize = 20.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = budget.category,
                                    color = TextWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Presupuesto Mensual • ${budget.currency.name}",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Percentage Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(statusColor.copy(alpha = 0.15f))
                                .border(1.dp, statusColor, RoundedCornerShape(12.dp))
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = String.format(Locale.US, "%.1f%%", percentageUsed),
                                color = statusColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Numerical Amounts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "Gastado",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = viewModel.repository.formatCurrency(budget.spent, budget.currency),
                                color = if (isExceeded) RedError else TextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Límite mensual",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = viewModel.repository.formatCurrency(budget.amount, budget.currency),
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Linear Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(DarkSurfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(5.dp))
                                .background(statusColor)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Remaining text & Actions
                    val remaining = budget.amount - budget.spent
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (remaining >= 0)
                                "Restante: ${viewModel.repository.formatCurrency(remaining, budget.currency)}"
                            else
                                "¡Sobrepasado por ${viewModel.repository.formatCurrency(-remaining, budget.currency)}!",
                            color = if (remaining >= 0) EmeraldPrimary else RedError,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Quick Add Expense button
                            IconButton(
                                onClick = { budgetForQuickExpense = budget },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.AddShoppingCart,
                                    contentDescription = "Gasto rápido",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Edit Budget button
                            IconButton(
                                onClick = { budgetToEdit = budget },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Editar límite",
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Delete Budget button
                            IconButton(
                                onClick = { budgetToDelete = budget },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.DeleteOutline,
                                    contentDescription = "Eliminar presupuesto",
                                    tint = RedError.copy(alpha = 0.8f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun SubscriptionsTab(
    subscriptions: List<Subscription>,
    viewModel: KoreLifeViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        val totalMonthly = subscriptions.filter { it.active }.sumOf { it.amount }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Gasto Mensual Recurrente (Rocket Money)", color = TextMuted, fontSize = 11.sp)
                        Text(
                            text = String.format(Locale.US, "$ %,.2f USD / mes", totalMonthly),
                            color = EmeraldPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        items(subscriptions) { sub ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(text = sub.emoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = sub.name, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Próximo cobro: ${sub.nextBilling}", color = TextMuted, fontSize = 11.sp)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = viewModel.repository.formatCurrency(sub.amount, sub.currency),
                            color = if (sub.active) TextWhite else TextSubtle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = sub.active,
                            onCheckedChange = { viewModel.toggleSubscription(sub.id) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = EmeraldPrimary,
                                checkedTrackColor = EmeraldContainer,
                                uncheckedTrackColor = DarkSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrencyConverterTab(viewModel: KoreLifeViewModel) {
    var amountInput by remember { mutableStateOf("100") }
    var fromCurrency by remember { mutableStateOf(CurrencyCode.USD) }
    var toCurrency by remember { mutableStateOf(CurrencyCode.VES) }

    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val converted = viewModel.repository.convert(amount, fromCurrency, toCurrency)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Calculadora Multimoneda en Tiempo Real",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("Monto a convertir", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Desde", color = TextMuted, fontSize = 11.sp)
                            Row {
                                CurrencyCode.values().forEach { c ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (fromCurrency == c) EmeraldPrimary else DarkSurfaceVariant)
                                            .clickable { fromCurrency = c }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(text = c.name, color = if (fromCurrency == c) EmeraldOnPrimary else TextWhite, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }
                        }

                        Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(24.dp))

                        Column {
                            Text("Hacia", color = TextMuted, fontSize = 11.sp)
                            Row {
                                CurrencyCode.values().forEach { c ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (toCurrency == c) EmeraldPrimary else DarkSurfaceVariant)
                                            .clickable { toCurrency = c }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(text = c.name, color = if (toCurrency == c) EmeraldOnPrimary else TextWhite, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurfaceVariant)
                            .border(1.dp, EmeraldPrimary, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Resultado Calculado", color = TextMuted, fontSize = 12.sp)
                            Text(
                                text = viewModel.repository.formatCurrency(converted, toCurrency),
                                color = EmeraldPrimary,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

// Dialogs for Adding Account, Budget, Goal
@Composable
private fun AddAccountDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, type: AccountType, balance: Double, currency: CurrencyCode) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var balanceText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AccountType.BANK) }
    var selectedCurrency by remember { mutableStateOf(CurrencyCode.VES) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth().testTag("add_account_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Nueva Cuenta Financiera", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre (ej. Banesco, Binance)", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { balanceText = it },
                    label = { Text("Saldo inicial", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                    Button(
                        onClick = {
                            val bal = balanceText.toDoubleOrNull() ?: 0.0
                            if (name.isNotBlank()) {
                                onSave(name, selectedType, bal, selectedCurrency)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = EmeraldOnPrimary)
                    ) {
                        Text("Crear", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AddBudgetDialog(
    onDismiss: () -> Unit,
    onSave: (category: String, amount: Double, currency: CurrencyCode) -> Unit
) {
    val presetCategories = listOf(
        Pair("Alimentación & Despensa", "🛒"),
        Pair("Comida & Restaurantes", "🍔"),
        Pair("Transporte & Movilidad", "🚌"),
        Pair("Servicios Públicos", "💡"),
        Pair("Salud & Farmacia", "💊"),
        Pair("Entretenimiento & Ocio", "🎬"),
        Pair("Ropa & Calzado", "🛍️"),
        Pair("Educación & Cursos", "📚"),
        Pair("Tecnología & Servicios", "💻"),
        Pair("Hogar & Mantenimiento", "🏡"),
        Pair("Otra Categoría", "📌")
    )

    var selectedPreset by remember { mutableStateOf(presetCategories[0].first) }
    var customCategory by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf(CurrencyCode.VES) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)),
            modifier = Modifier.fillMaxWidth().testTag("add_budget_dialog")
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmeraldPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Savings, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Definir Presupuesto Mensual", color = TextWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Text("Asigna un tope de gasto mensual por categoría", color = TextMuted, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Categoría:", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                // Scrollable category chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(presetCategories) { (cat, emoji) ->
                        val isSelected = selectedPreset == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) EmeraldPrimary else DarkSurfaceVariant)
                                .border(1.dp, if (isSelected) EmeraldPrimary else DarkBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedPreset = cat }
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

                if (selectedPreset == "Otra Categoría") {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customCategory,
                        onValueChange = { customCategory = it },
                        label = { Text("Nombre de Categoría Personalizada", color = TextMuted) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Currency selector
                Text("Moneda del Presupuesto:", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CurrencyCode.values().forEach { curr ->
                        val isSel = selectedCurrency == curr
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) EmeraldPrimary else DarkSurfaceVariant)
                                .border(1.dp, if (isSel) EmeraldPrimary else DarkBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedCurrency = curr }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${curr.symbol} ${curr.name}",
                                color = if (isSel) EmeraldOnPrimary else TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Amount text field
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Límite mensual a asignar (${selectedCurrency.symbol})", color = TextMuted) },
                    placeholder = { Text("ej. 25000", color = TextSubtle) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("budget_amount_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                        border = ButtonDefaults.outlinedButtonBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            val finalCat = if (selectedPreset == "Otra Categoría") customCategory.trim() else selectedPreset
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            if (finalCat.isNotBlank() && amt > 0) {
                                onSave(finalCat, amt, selectedCurrency)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp).testTag("save_budget_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = EmeraldOnPrimary
                        )
                    ) {
                        Text("Guardar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun EditBudgetDialog(
    budget: Budget,
    onDismiss: () -> Unit,
    onSave: (Double, CurrencyCode) -> Unit
) {
    var amountText by remember { mutableStateOf(budget.amount.toString()) }
    var selectedCurrency by remember { mutableStateOf(budget.currency) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)),
            modifier = Modifier.fillMaxWidth().testTag("edit_budget_dialog")
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Text("Editar Límite de Presupuesto", color = TextWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text(text = "Categoría: ${budget.category}", color = EmeraldPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(16.dp))

                Text("Moneda:", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CurrencyCode.values().forEach { curr ->
                        val isSel = selectedCurrency == curr
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) EmeraldPrimary else DarkSurfaceVariant)
                                .border(1.dp, if (isSel) EmeraldPrimary else DarkBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedCurrency = curr }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${curr.symbol} ${curr.name}",
                                color = if (isSel) EmeraldOnPrimary else TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Nuevo límite mensual", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Cancelar", color = TextMuted)
                    }

                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                onSave(amt, selectedCurrency)
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = EmeraldOnPrimary
                        )
                    ) {
                        Text("Actualizar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickBudgetExpenseDialog(
    budget: Budget,
    accounts: List<Account>,
    onDismiss: () -> Unit,
    onSave: (accountId: String, amount: Double, desc: String) -> Unit
) {
    var expenseAmount by remember { mutableStateOf("") }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: "1") }
    var description by remember { mutableStateOf("Gasto en ${budget.category}") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)),
            modifier = Modifier.fillMaxWidth().testTag("quick_budget_expense_dialog")
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Text(
                    text = "Registrar Gasto Rápido",
                    color = TextWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${com.example.data.model.getCategoryEmoji(budget.category)} ${budget.category} • Se descontará del sobre",
                    color = EmeraldPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Account selector
                Text("Cuenta de origen:", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(accounts) { acc ->
                        val isSel = selectedAccountId == acc.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) EmeraldPrimary else DarkSurfaceVariant)
                                .border(1.dp, if (isSel) EmeraldPrimary else DarkBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedAccountId = acc.id }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = acc.name,
                                color = if (isSel) EmeraldOnPrimary else TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = expenseAmount,
                    onValueChange = { expenseAmount = it },
                    label = { Text("Monto del gasto (${budget.currency.symbol})", color = TextMuted) },
                    placeholder = { Text("ej. 450", color = TextSubtle) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Nota o descripción", color = TextMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Cancelar", color = TextMuted)
                    }

                    Button(
                        onClick = {
                            val amt = expenseAmount.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                onSave(selectedAccountId, amt, description)
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = EmeraldOnPrimary
                        )
                    ) {
                        Text("Registrar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
