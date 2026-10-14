package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Account
import com.example.data.model.CurrencyCode
import com.example.data.model.Goal
import com.example.ui.KoreLifeViewModel
import com.example.ui.components.AddEditGoalDialog
import com.example.ui.components.ContributeToGoalDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

enum class GoalFilterType(val label: String) {
    ALL("Todas"),
    IN_PROGRESS("En Progreso"),
    COMPLETED("Completadas 🏆"),
    EXPIRING_SOON("Próximas a Vencer")
}

@Composable
fun SavingsGoalsTab(
    goals: List<Goal>,
    accounts: List<Account>,
    viewModel: KoreLifeViewModel,
    onAddNewGoal: () -> Unit,
    onExportPdf: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(GoalFilterType.ALL) }
    var selectedCategory by remember { mutableStateOf<String?>("Todas") }

    // Dialog states
    var goalForContribution by remember { mutableStateOf<Goal?>(null) }
    var goalForEditing by remember { mutableStateOf<Goal?>(null) }
    var goalForDeletion by remember { mutableStateOf<Goal?>(null) }

    // Global calculations
    val totalTargetUsd = remember(goals) {
        goals.sumOf { g ->
            viewModel.repository.convert(g.targetAmount, g.currency, CurrencyCode.USD)
        }
    }
    val totalAccumulatedUsd = remember(goals) {
        goals.sumOf { g ->
            viewModel.repository.convert(g.currentAmount, g.currency, CurrencyCode.USD)
        }
    }
    val totalAccumulatedVes = remember(goals) {
        goals.sumOf { g ->
            viewModel.repository.convert(g.currentAmount, g.currency, CurrencyCode.VES)
        }
    }

    val globalProgress = remember(totalTargetUsd, totalAccumulatedUsd) {
        if (totalTargetUsd > 0.0) (totalAccumulatedUsd / totalTargetUsd).toFloat().coerceIn(0f, 1f) else 0f
    }
    val globalProgressPercent = (globalProgress * 100).toInt()

    val completedCount = goals.count { it.isCompleted }
    val inProgressCount = goals.count { !it.isCompleted }

    // Filtered goals
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val today = remember { Date() }

    val filteredGoals = remember(goals, selectedFilter, selectedCategory) {
        goals.filter { g ->
            val matchesCategory = selectedCategory == null || selectedCategory == "Todas" || g.category == selectedCategory
            val matchesStatus = when (selectedFilter) {
                GoalFilterType.ALL -> true
                GoalFilterType.IN_PROGRESS -> !g.isCompleted
                GoalFilterType.COMPLETED -> g.isCompleted
                GoalFilterType.EXPIRING_SOON -> {
                    try {
                        val d = dateFormat.parse(g.deadline)
                        if (d != null && !g.isCompleted) {
                            val diffDays = TimeUnit.MILLISECONDS.toDays(d.time - today.time)
                            diffDays in 0..60
                        } else false
                    } catch (e: Exception) {
                        false
                    }
                }
            }
            matchesCategory && matchesStatus
        }
    }

    // Contribution dialog
    goalForContribution?.let { targetGoal ->
        ContributeToGoalDialog(
            goal = targetGoal,
            accounts = accounts,
            formatCurrency = { amt, curr -> viewModel.repository.formatCurrency(amt, curr) },
            onDismiss = { goalForContribution = null },
            onContribute = { amount, accId ->
                viewModel.contributeToGoal(targetGoal.id, amount, accId)
                goalForContribution = null
            },
            onWithdraw = { amount, accId ->
                viewModel.withdrawFromGoal(targetGoal.id, amount, accId)
                goalForContribution = null
            }
        )
    }

    // Edit dialog
    goalForEditing?.let { editingGoal ->
        AddEditGoalDialog(
            goalToEdit = editingGoal,
            accounts = accounts,
            onDismiss = { goalForEditing = null },
            onSave = { title, emoji, target, curr, deadline, _, cat, accId, notes, color ->
                val updated = editingGoal.copy(
                    title = title,
                    emoji = emoji,
                    targetAmount = target,
                    currency = curr,
                    deadline = deadline,
                    category = cat,
                    accountId = accId,
                    notes = notes,
                    colorHex = color
                )
                viewModel.updateGoal(updated)
                goalForEditing = null
            }
        )
    }

    // Delete Confirmation Dialog
    goalForDeletion?.let { deletingGoal ->
        AlertDialog(
            onDismissRequest = { goalForDeletion = null },
            title = {
                Text(
                    text = "¿Eliminar meta de ahorro?",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "Estás a punto de eliminar '${deletingGoal.title}'. Esta acción no se puede deshacer.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteGoal(deletingGoal.id)
                        goalForDeletion = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { goalForDeletion = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Top Portfolio Overview Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("savings_goals_portfolio_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Savings,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Ahorros Acumulados",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = viewModel.repository.formatCurrency(totalAccumulatedUsd, CurrencyCode.USD),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Badge with counter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "≈ ${viewModel.repository.formatCurrency(totalAccumulatedVes, CurrencyCode.VES)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Global Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Progreso Global del Portafolio",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "$globalProgressPercent% de meta total",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Animated Visual Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(globalProgress)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(5.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primary,
                                            Color(0xFF34D399)
                                        )
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Metrics Strip: Goals active & completed
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "METAS ACTIVAS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "$inProgressCount en curso",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "CUMPLIDAS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "$completedCount logradas 🏆",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "META TOTAL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = viewModel.repository.formatCurrency(totalTargetUsd, CurrencyCode.USD),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Button to define new goal
                    Button(
                        onClick = onAddNewGoal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("define_new_goal_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Definir Nueva Meta de Ahorro", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    if (onExportPdf != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = onExportPdf,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_export_goals_pdf"),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Exportar Resumen Mensual en PDF",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Filter Pills Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(GoalFilterType.values()) { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = filter.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Empty state when filtered list is empty
        if (filteredGoals.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🎯", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (goals.isEmpty()) "Aún no tienes metas de ahorro" else "No hay metas para este filtro",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Define tus objetivos financieros para visualizarlos con su barra de progreso y alcanzar la libertad financiera.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onAddNewGoal,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Text("+ Crear Primera Meta", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // List of Goals
        items(filteredGoals, key = { it.id }) { goal ->
            SavingsGoalItemCard(
                goal = goal,
                formatCurrency = { amt, curr -> viewModel.repository.formatCurrency(amt, curr) },
                onContribute = { goalForContribution = goal },
                onQuickAdd = { quickAmount ->
                    viewModel.contributeToGoal(goal.id, quickAmount)
                },
                onEdit = { goalForEditing = goal },
                onDelete = { goalForDeletion = goal }
            )
        }
    }
}

@Composable
fun SavingsGoalItemCard(
    goal: Goal,
    formatCurrency: (Double, CurrencyCode) -> String,
    onContribute: () -> Unit,
    onQuickAdd: (Double) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    val progress = goal.progress
    val progressPercent = goal.progressPercent
    val isCompleted = goal.isCompleted
    val remaining = goal.remainingAmount

    // Days remaining calculation
    val daysRemaining = remember(goal.deadline) {
        try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val target = format.parse(goal.deadline)
            val today = Date()
            if (target != null) {
                TimeUnit.MILLISECONDS.toDays(target.time - today.time).toInt()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    // Suggested monthly pace calculation
    val suggestedMonthlyPace = remember(remaining, daysRemaining) {
        if (remaining > 0 && daysRemaining != null && daysRemaining > 0) {
            val months = (daysRemaining / 30.0).coerceAtLeast(0.5)
            remaining / months
        } else null
    }

    val goalAccentColor = Color(goal.colorHex)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("goal_card_${goal.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isCompleted) Color(0xFF10B981).copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Emoji, Title, Category, Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(goalAccentColor.copy(alpha = 0.15f))
                            .border(1.dp, goalAccentColor.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = goal.emoji, fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = goal.title,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = goal.category,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            if (isCompleted) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF10B981).copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "¡Alcanzada! 🏆",
                                        fontSize = 10.sp,
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else if (daysRemaining != null) {
                                val deadlineText = when {
                                    daysRemaining < 0 -> "Vencida hace ${-daysRemaining} d"
                                    daysRemaining == 0 -> "Vence hoy"
                                    daysRemaining == 1 -> "Vence mañana"
                                    daysRemaining <= 30 -> "Quedan $daysRemaining días"
                                    else -> "Plazo: ${goal.deadline}"
                                }
                                Text(
                                    text = deadlineText,
                                    fontSize = 11.sp,
                                    color = if (daysRemaining <= 15) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Overflow menu
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opciones",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Abonar / Retirar Ahorros") },
                            leadingIcon = { Icon(Icons.Default.Savings, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onContribute()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Editar Meta") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onEdit()
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Eliminar Meta", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Bar Visualizer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progreso acumulado",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                // Percentage Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(goalAccentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$progressPercent%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = goalAccentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // The main visual progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    goalAccentColor,
                                    if (isCompleted) Color(0xFF34D399) else goalAccentColor.copy(alpha = 0.85f)
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metrics row: Acumulado, Meta, Restante
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "ACUMULADO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatCurrency(goal.currentAmount, goal.currency),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "OBJETIVO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatCurrency(goal.targetAmount, goal.currency),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isCompleted) "ESTADO" else "FALTANTE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isCompleted) "¡Completa! 🎉" else formatCurrency(remaining, goal.currency),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Suggested Monthly Pace Banner
            if (suggestedMonthlyPace != null && !isCompleted) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Ritmo sugerido: ${formatCurrency(suggestedMonthlyPace, goal.currency)}/mes para cumplir el plazo",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Quick Contribution (+20, +50) and "+ Abonar" Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick shortcut buttons
                if (!isCompleted) {
                    val quickAmount = if (goal.currency == CurrencyCode.USD) 20.0 else 500.0
                    OutlinedButton(
                        onClick = { onQuickAdd(quickAmount) },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text(
                            text = "+${formatCurrency(quickAmount, goal.currency)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val quickAmount2 = if (goal.currency == CurrencyCode.USD) 50.0 else 1000.0
                    OutlinedButton(
                        onClick = { onQuickAdd(quickAmount2) },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text(
                            text = "+${formatCurrency(quickAmount2, goal.currency)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Main "+ Abonar Ahorro" Button
                Button(
                    onClick = onContribute,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("contribute_goal_button_${goal.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCompleted) MaterialTheme.colorScheme.surfaceVariant else goalAccentColor,
                        contentColor = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                    )
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCompleted) "Gestionar Fondos" else "Abonar Ahorro",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
