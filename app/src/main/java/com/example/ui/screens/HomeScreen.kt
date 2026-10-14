package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
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
import com.example.data.model.TransactionType
import com.example.ui.KoreLifeViewModel
import com.example.ui.components.AddTransactionDialog
import com.example.ui.components.ExchangeRatesCard
import com.example.ui.components.NetWorthCard
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: KoreLifeViewModel,
    onNavigateToFinance: () -> Unit,
    onNavigateToTasks: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rates by viewModel.rates.collectAsState()
    val selectedProvider by viewModel.selectedProvider.collectAsState()
    val isRatesLoading by viewModel.isRatesLoading.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val hideBalances by viewModel.hideBalances.collectAsState()

    var showAddTxDialog by remember { mutableStateOf(false) }

    val totalVes = viewModel.repository.getTotalBalanceInVes()
    val totalUsd = viewModel.repository.getTotalBalanceInUsd()

    if (showAddTxDialog) {
        AddTransactionDialog(
            accounts = accounts,
            onDismiss = { showAddTxDialog = false },
            onSave = { accId, amt, curr, type, cat, desc ->
                viewModel.addTransaction(accId, amt, curr, type, cat, desc)
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        // Welcome Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "BIENVENIDO DE NUEVO",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${currentUser?.fullName ?: "Usuario KoreLife"} 👋",
                        color = TextWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(1.dp, EmeraldPrimary, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        // Live Exchange Rates
        item {
            ExchangeRatesCard(
                rates = rates,
                selectedProvider = selectedProvider,
                isLoading = isRatesLoading,
                onSelectProvider = { viewModel.setRateProvider(it) },
                onRefresh = { viewModel.refreshRates() }
            )
        }

        // Net Worth Card
        item {
            NetWorthCard(
                totalVes = totalVes,
                totalUsd = totalUsd,
                activeProvider = selectedProvider,
                hideBalances = hideBalances,
                onToggleHideBalances = { viewModel.toggleHideBalances() }
            )
        }

        // Quick Action Button
        item {
            Button(
                onClick = { showAddTxDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("quick_add_transaction_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPrimary,
                    contentColor = EmeraldOnPrimary
                )
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Registrar Gasto / Ingreso", fontSize = 15.sp, fontWeight = FontWeight.Black)
            }
        }

        // Today's Tasks Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Tareas & Prioridades", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            val completedCount = tasks.count { it.completed }
                            Text(
                                text = "$completedCount de ${tasks.size} listas para hoy",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        TextButton(
                            onClick = onNavigateToTasks,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(text = "Ver todas →", color = EmeraldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (tasks.isEmpty()) {
                        Text(text = "No tienes tareas pendientes para hoy.", color = TextMuted, fontSize = 12.sp)
                    } else {
                        tasks.take(3).forEach { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkSurfaceVariant)
                                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                                    .clickable { viewModel.toggleTask(task.id) }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (task.completed) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (task.completed) EmeraldPrimary else TextSubtle,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = task.title,
                                            color = if (task.completed) TextSubtle else TextWhite,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        if (task.dueDate.isNotBlank()) {
                                            Text(
                                                text = if (task.isDueToday()) "⏳ Vence hoy" else "📅 ${task.dueDate}",
                                                color = if (task.isDueToday()) AmberTertiary else TextMuted,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(task.priority.colorHex).copy(alpha = 0.2f))
                                        .border(1.dp, Color(task.priority.colorHex), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${task.priority.iconEmoji} ${task.priority.title}",
                                        color = Color(task.priority.colorHex),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }

        // Active Habits Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Rachas de Hábitos Diarios", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            val doneCount = habits.count { it.completedToday }
                            Text(text = "$doneCount de ${habits.size} completados hoy", color = TextMuted, fontSize = 11.sp)
                        }

                        TextButton(
                            onClick = onNavigateToTasks,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(text = "Ver rachas →", color = AmberTertiary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    habits.take(3).forEach { habit ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceVariant)
                                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                                .clickable { viewModel.toggleHabit(habit.id) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(text = habit.emoji, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = if (habit.completedToday) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (habit.completedToday) AmberTertiary else TextSubtle,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = habit.name, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text(text = habit.streakTierTitle, color = TextMuted, fontSize = 10.sp)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF78350F).copy(alpha = 0.5f))
                                    .border(1.dp, AmberTertiary, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "🔥 ${habit.streak} d",
                                    color = AmberTertiary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        // Savings Goals Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_savings_goals_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Savings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Metas de Ahorro",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Gestionar",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateToFinance() }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (goals.isEmpty()) {
                        Text(
                            text = "No has definido metas de ahorro aún.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    } else {
                        goals.take(2).forEach { goal ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                                    .clickable { onNavigateToFinance() }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = goal.emoji, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = goal.title,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = "${goal.progressPercent}%",
                                        color = Color(goal.colorHex),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Progress bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(goal.progress)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(goal.colorHex))
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${viewModel.repository.formatCurrency(goal.currentAmount, goal.currency)} acumulado",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Meta: ${viewModel.repository.formatCurrency(goal.targetAmount, goal.currency)}",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        // Recent Transactions Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Transacciones Recientes", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "Ver Todo",
                    color = EmeraldPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToFinance() }
                )
            }
        }

        // Recent Transactions List
        items(transactions.take(4)) { tx ->
            val isIncome = tx.type == TransactionType.INCOME
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
                                .background(if (isIncome) Color(0xFF064E3B) else Color(0xFF450A0A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isIncome) "↓" else "↑",
                                color = if (isIncome) EmeraldPrimary else RedError,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = tx.description, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "${tx.category} • ${tx.date}", color = TextMuted, fontSize = 11.sp)
                        }
                    }

                    Text(
                        text = "${if (isIncome) "+ " else "- "}${viewModel.repository.formatCurrency(tx.amount, tx.currency)}",
                        color = if (isIncome) EmeraldPrimary else RedError,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
