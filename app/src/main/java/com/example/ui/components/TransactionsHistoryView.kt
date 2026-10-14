package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.KoreLifeViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TransactionsHistoryView(
    transactions: List<Transaction>,
    viewModel: KoreLifeViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<TransactionType?>(null) }
    var selectedSort by remember { mutableStateOf(TransactionSortOrder.DATE_DESC) }
    var selectedDateRange by remember { mutableStateOf(DateRangeFilter.ALL) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    var showFilterModal by remember { mutableStateOf(false) }

    // Dynamic unique categories from transactions + presets
    val allCategories = remember(transactions) {
        val fromTx = transactions.map { it.category }.distinct()
        val standard = listOf("Alimentación", "Supermercado", "Comida", "Transporte", "Servicios", "Hogar", "Salud", "Entretenimiento", "Nómina", "Freelance")
        (standard + fromTx).distinct()
    }

    // Helper dates for filtering
    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val currentMonthStr = remember { SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date()) }

    // Filter computation
    val filteredTransactions = remember(transactions, searchQuery, selectedType, selectedSort, selectedDateRange, selectedCategory) {
        transactions.filter { tx ->
            // 1. Type filter
            val matchesType = selectedType == null || tx.type == selectedType

            // 2. Category filter
            val matchesCategory = selectedCategory == null || tx.category.equals(selectedCategory, ignoreCase = true) ||
                    tx.category.contains(selectedCategory!!, ignoreCase = true)

            // 3. Search query filter
            val matchesSearch = searchQuery.isBlank() ||
                    tx.description.contains(searchQuery, ignoreCase = true) ||
                    tx.category.contains(searchQuery, ignoreCase = true)

            // 4. Date range filter
            val matchesDate = when (selectedDateRange) {
                DateRangeFilter.ALL -> true
                DateRangeFilter.TODAY -> tx.date == todayStr
                DateRangeFilter.THIS_MONTH -> tx.date.startsWith(currentMonthStr)
                DateRangeFilter.THIS_WEEK -> {
                    try {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val txDate = sdf.parse(tx.date)
                        val now = Date()
                        val diffDays = (now.time - (txDate?.time ?: 0)) / (1000 * 60 * 60 * 24)
                        diffDays in 0..7
                    } catch (e: Exception) {
                        true
                    }
                }
                DateRangeFilter.LAST_30_DAYS -> {
                    try {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val txDate = sdf.parse(tx.date)
                        val now = Date()
                        val diffDays = (now.time - (txDate?.time ?: 0)) / (1000 * 60 * 60 * 24)
                        diffDays in 0..30
                    } catch (e: Exception) {
                        true
                    }
                }
            }

            matchesType && matchesCategory && matchesSearch && matchesDate
        }.let { list ->
            // Sort list
            when (selectedSort) {
                TransactionSortOrder.DATE_DESC -> list.sortedByDescending { it.date }
                TransactionSortOrder.DATE_ASC -> list.sortedBy { it.date }
                TransactionSortOrder.AMOUNT_DESC -> list.sortedByDescending {
                    viewModel.repository.convert(it.amount, it.currency, CurrencyCode.VES)
                }
                TransactionSortOrder.AMOUNT_ASC -> list.sortedBy {
                    viewModel.repository.convert(it.amount, it.currency, CurrencyCode.VES)
                }
            }
        }
    }

    // Active filters count
    val activeFiltersCount = (if (selectedType != null) 1 else 0) +
            (if (selectedDateRange != DateRangeFilter.ALL) 1 else 0) +
            (if (selectedCategory != null) 1 else 0) +
            (if (selectedSort != TransactionSortOrder.DATE_DESC) 1 else 0) +
            (if (searchQuery.isNotBlank()) 1 else 0)

    val hasActiveFilters = activeFiltersCount > 0

    // Filtered Financial Totals
    val filteredInflowVes = filteredTransactions.filter { it.type == TransactionType.INCOME }.sumOf {
        viewModel.repository.convert(it.amount, it.currency, CurrencyCode.VES)
    }
    val filteredOutflowVes = filteredTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf {
        viewModel.repository.convert(it.amount, it.currency, CurrencyCode.VES)
    }
    val netBalanceVes = filteredInflowVes - filteredOutflowVes

    // Filter Dialog
    if (showFilterModal) {
        FilterAndSortModalDialog(
            currentSort = selectedSort,
            currentDateRange = selectedDateRange,
            currentCategory = selectedCategory,
            currentType = selectedType,
            categories = allCategories,
            onApply = { sort, dateRange, category, type ->
                selectedSort = sort
                selectedDateRange = dateRange
                selectedCategory = category
                selectedType = type
                showFilterModal = false
            },
            onReset = {
                selectedSort = TransactionSortOrder.DATE_DESC
                selectedDateRange = DateRangeFilter.ALL
                selectedCategory = null
                selectedType = null
                searchQuery = ""
                showFilterModal = false
            },
            onDismiss = { showFilterModal = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Buscar por descripción, concepto o categoría...", color = TextMuted, fontSize = 13.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Limpiar búsqueda", tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = EmeraldPrimary,
                unfocusedBorderColor = DarkBorder,
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .testTag("input_search_transactions")
        )

        // Filter and Sort Chips Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Main Filter Button with active count badge
            item {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (hasActiveFilters) EmeraldPrimary else DarkSurfaceVariant)
                        .border(1.dp, if (hasActiveFilters) EmeraldPrimary else DarkBorder, RoundedCornerShape(12.dp))
                        .clickable { showFilterModal = true }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                        .testTag("btn_open_filters_modal")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = null,
                            tint = if (hasActiveFilters) EmeraldOnPrimary else TextWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (hasActiveFilters) "Filtros ($activeFiltersCount)" else "Filtrar y Ordenar",
                            color = if (hasActiveFilters) EmeraldOnPrimary else TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Quick Type: Todos
            item {
                QuickFilterPill(
                    title = "Todos",
                    isSelected = selectedType == null,
                    onClick = { selectedType = null }
                )
            }

            // Quick Type: Ingresos
            item {
                QuickFilterPill(
                    title = "Ingresos (↑)",
                    isSelected = selectedType == TransactionType.INCOME,
                    onClick = {
                        selectedType = if (selectedType == TransactionType.INCOME) null else TransactionType.INCOME
                    }
                )
            }

            // Quick Type: Gastos
            item {
                QuickFilterPill(
                    title = "Gastos (↓)",
                    isSelected = selectedType == TransactionType.EXPENSE,
                    onClick = {
                        selectedType = if (selectedType == TransactionType.EXPENSE) null else TransactionType.EXPENSE
                    }
                )
            }

            // Quick Date Range Pills: Hoy, Este mes
            item {
                QuickFilterPill(
                    title = "📅 Hoy",
                    isSelected = selectedDateRange == DateRangeFilter.TODAY,
                    onClick = {
                        selectedDateRange = if (selectedDateRange == DateRangeFilter.TODAY) DateRangeFilter.ALL else DateRangeFilter.TODAY
                    }
                )
            }

            item {
                QuickFilterPill(
                    title = "🗓️ Este mes",
                    isSelected = selectedDateRange == DateRangeFilter.THIS_MONTH,
                    onClick = {
                        selectedDateRange = if (selectedDateRange == DateRangeFilter.THIS_MONTH) DateRangeFilter.ALL else DateRangeFilter.THIS_MONTH
                    }
                )
            }

            // Reset Button if active
            if (hasActiveFilters) {
                item {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(RedError.copy(alpha = 0.15f))
                            .border(1.dp, RedError.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .clickable {
                                selectedSort = TransactionSortOrder.DATE_DESC
                                selectedDateRange = DateRangeFilter.ALL
                                selectedCategory = null
                                selectedType = null
                                searchQuery = ""
                            }
                            .padding(horizontal = 10.dp, vertical = 7.dp)
                            .testTag("btn_reset_filters")
                    ) {
                        Text(
                            text = "Limpiar ✕",
                            color = RedError,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Active Criteria Summary Banner
        if (hasActiveFilters) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant.copy(alpha = 0.7f)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${filteredTransactions.size} resultados encontrados",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Orden: ${selectedSort.title} • Rango: ${selectedDateRange.title}${if (selectedCategory != null) " • Cat: $selectedCategory" else ""}",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }

                    // Net balance for filtered items
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Neto: ${if (netBalanceVes >= 0) "+" else ""}${viewModel.repository.formatCurrency(netBalanceVes, CurrencyCode.VES)}",
                            color = if (netBalanceVes >= 0) EmeraldPrimary else RedError,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Gastos: -${viewModel.repository.formatCurrency(filteredOutflowVes, CurrencyCode.VES)}",
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Transactions List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp)
        ) {
            if (filteredTransactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🔍", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Sin transacciones con estos filtros",
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Intenta cambiar el rango de fecha, la categoría o borrar el texto de búsqueda.",
                                color = TextMuted,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            if (hasActiveFilters) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = {
                                        selectedSort = TransactionSortOrder.DATE_DESC
                                        selectedDateRange = DateRangeFilter.ALL
                                        selectedCategory = null
                                        selectedType = null
                                        searchQuery = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = EmeraldOnPrimary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Restablecer todos los filtros", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                items(filteredTransactions) { tx ->
                    val isIncome = tx.type == TransactionType.INCOME
                    val isRecurring = tx.description.startsWith("[Recurrente]")

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tx_item_${tx.id}"),
                        shape = RoundedCornerShape(16.dp),
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
                                        .background(if (isIncome) Color(0xFF064E3B) else Color(0xFF450A0A)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isIncome) "↓" else "↑",
                                        color = if (isIncome) EmeraldPrimary else RedError,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = tx.description,
                                            color = TextWhite,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        if (isRecurring) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(EmeraldContainer)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("Recurrente", color = EmeraldOnContainer, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "${getCategoryEmoji(tx.category)} ${tx.category}", color = TextMuted, fontSize = 11.sp)
                                        Text(text = " • ", color = TextMuted, fontSize = 11.sp)
                                        Text(text = tx.date, color = TextMuted, fontSize = 11.sp)
                                    }
                                }
                            }

                            // Amount column
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${if (isIncome) "+" else "-"} ${viewModel.repository.formatCurrency(tx.amount, tx.currency)}",
                                    color = if (isIncome) EmeraldPrimary else RedError,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                // If not VES, show converted VES
                                if (tx.currency != CurrencyCode.VES) {
                                    val inVes = viewModel.repository.convert(tx.amount, tx.currency, CurrencyCode.VES)
                                    Text(
                                        text = "≈ ${viewModel.repository.formatCurrency(inVes, CurrencyCode.VES)}",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickFilterPill(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) EmeraldPrimary else DarkSurfaceVariant)
            .border(1.dp, if (isSelected) EmeraldPrimary else DarkBorder, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = title,
            color = if (isSelected) EmeraldOnPrimary else TextWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun FilterAndSortModalDialog(
    currentSort: TransactionSortOrder,
    currentDateRange: DateRangeFilter,
    currentCategory: String?,
    currentType: TransactionType?,
    categories: List<String>,
    onApply: (TransactionSortOrder, DateRangeFilter, String?, TransactionType?) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    var tempSort by remember { mutableStateOf(currentSort) }
    var tempDateRange by remember { mutableStateOf(currentDateRange) }
    var tempCategory by remember { mutableStateOf(currentCategory) }
    var tempType by remember { mutableStateOf(currentType) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("dialog_filter_sort_modal")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Filtros y Ordenación", color = TextWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 1. Sort Order
                Text("Ordenar por:", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(TransactionSortOrder.values()) { sortOpt ->
                        val isSelected = tempSort == sortOpt
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) EmeraldPrimary else DarkSurfaceVariant)
                                .border(1.dp, if (isSelected) EmeraldPrimary else DarkBorder, RoundedCornerShape(8.dp))
                                .clickable { tempSort = sortOpt }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${sortOpt.icon} ${sortOpt.title}",
                                color = if (isSelected) EmeraldOnPrimary else TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Date Range
                Text("Rango de Fecha:", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(DateRangeFilter.values()) { rangeOpt ->
                        val isSelected = tempDateRange == rangeOpt
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) EmeraldPrimary else DarkSurfaceVariant)
                                .border(1.dp, if (isSelected) EmeraldPrimary else DarkBorder, RoundedCornerShape(8.dp))
                                .clickable { tempDateRange = rangeOpt }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = rangeOpt.title,
                                color = if (isSelected) EmeraldOnPrimary else TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Category Filter
                Text("Categoría:", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        val isSelected = tempCategory == null
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) EmeraldPrimary else DarkSurfaceVariant)
                                .border(1.dp, if (isSelected) EmeraldPrimary else DarkBorder, RoundedCornerShape(8.dp))
                                .clickable { tempCategory = null }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Todas",
                                color = if (isSelected) EmeraldOnPrimary else TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    items(categories) { cat ->
                        val isSelected = tempCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) EmeraldPrimary else DarkSurfaceVariant)
                                .border(1.dp, if (isSelected) EmeraldPrimary else DarkBorder, RoundedCornerShape(8.dp))
                                .clickable { tempCategory = if (isSelected) null else cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${getCategoryEmoji(cat)} $cat",
                                color = if (isSelected) EmeraldOnPrimary else TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Transaction Type
                Text("Tipo de Movimiento:", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (tempType == null) EmeraldPrimary else DarkSurfaceVariant)
                            .clickable { tempType = null }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Todos", color = if (tempType == null) EmeraldOnPrimary else TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (tempType == TransactionType.INCOME) EmeraldPrimary else DarkSurfaceVariant)
                            .clickable { tempType = TransactionType.INCOME }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Ingresos", color = if (tempType == TransactionType.INCOME) EmeraldOnPrimary else TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (tempType == TransactionType.EXPENSE) EmeraldPrimary else DarkSurfaceVariant)
                            .clickable { tempType = TransactionType.EXPENSE }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Gastos", color = if (tempType == TransactionType.EXPENSE) EmeraldOnPrimary else TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Buttons: Reset & Apply
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onReset,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
                    ) {
                        Text("Restablecer", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            onApply(tempSort, tempDateRange, tempCategory, tempType)
                        },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = EmeraldOnPrimary)
                    ) {
                        Text("Aplicar Filtros", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
