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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Habit
import com.example.data.model.KanbanStatus
import com.example.data.model.KoreTask
import com.example.data.model.Note
import com.example.data.model.Priority
import com.example.ui.KoreLifeViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun TasksScreen(
    viewModel: KoreLifeViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val notes by viewModel.notes.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Tareas", "Hábitos & Rachas", "Kanban", "Pomodoro", "Notas")

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<KoreTask?>(null) }

    var showAddHabitDialog by remember { mutableStateOf(false) }
    var habitToEdit by remember { mutableStateOf<Habit?>(null) }
    var habitForDetails by remember { mutableStateOf<Habit?>(null) }
    var habitForHeatmap by remember { mutableStateOf<Habit?>(null) }

    var showAddNoteDialog by remember { mutableStateOf(false) }

    // Task Dialog
    if (showAddTaskDialog || taskToEdit != null) {
        AddEditTaskDialog(
            taskToEdit = taskToEdit,
            onDismiss = {
                showAddTaskDialog = false
                taskToEdit = null
            },
            onSave = { title, desc, prio, dueDate, category ->
                if (taskToEdit == null) {
                    viewModel.addTask(title, desc, prio, dueDate, category)
                } else {
                    viewModel.updateTask(
                        taskToEdit!!.copy(
                            title = title,
                            description = desc,
                            priority = prio,
                            dueDate = dueDate,
                            category = category
                        )
                    )
                }
            }
        )
    }

    // Habit Dialog
    if (showAddHabitDialog || habitToEdit != null) {
        AddEditHabitDialog(
            habitToEdit = habitToEdit,
            onDismiss = {
                showAddHabitDialog = false
                habitToEdit = null
            },
            onSave = { name, freq, emoji, category, targetDays, notesText ->
                if (habitToEdit == null) {
                    viewModel.addHabit(name, freq, emoji, category, targetDays, notesText)
                } else {
                    viewModel.updateHabit(
                        habitToEdit!!.copy(
                            name = name,
                            frequency = freq,
                            emoji = emoji,
                            category = category,
                            targetDaysPerWeek = targetDays,
                            notes = notesText
                        )
                    )
                }
            }
        )
    }

    // Habit Details Dialog
    habitForDetails?.let { detailHabit ->
        HabitDetailsDialog(
            habit = detailHabit,
            onDismiss = { habitForDetails = null },
            onEdit = {
                habitToEdit = detailHabit
                habitForDetails = null
            },
            onResetStreak = {
                viewModel.resetHabitStreak(detailHabit.id)
                habitForDetails = null
            },
            onDelete = {
                viewModel.deleteHabit(detailHabit.id)
                habitForDetails = null
            }
        )
    }

    // Habit Calendar Heatmap Dialog
    habitForHeatmap?.let { heatmapHabit ->
        HabitCalendarHeatmapDialog(
            habit = heatmapHabit,
            onDismiss = { habitForHeatmap = null },
            onToggleDay = { dayNum ->
                viewModel.toggleHabitDay(heatmapHabit.id, dayNum)
                val updated = viewModel.habits.value.find { it.id == heatmapHabit.id }
                if (updated != null) {
                    habitForHeatmap = updated
                }
            }
        )
    }

    // Note Dialog
    if (showAddNoteDialog) {
        AddNoteDialog(
            onDismiss = { showAddNoteDialog = false },
            onSave = { title, content ->
                viewModel.addNote(title, content)
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    when (selectedTab) {
                        0, 2 -> {
                            taskToEdit = null
                            showAddTaskDialog = true
                        }
                        1 -> {
                            habitToEdit = null
                            showAddHabitDialog = true
                        }
                        3 -> {}
                        4 -> showAddNoteDialog = true
                    }
                },
                containerColor = EmeraldPrimary,
                contentColor = EmeraldOnPrimary,
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .testTag("tasks_fab_add")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Productividad Life OS",
                        color = TextWhite,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Gestión de tareas, prioridades y rachas de hábitos",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            // Tab navigation
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

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> TasksListTab(
                        tasks = tasks,
                        viewModel = viewModel,
                        onEditTask = { taskToEdit = it },
                        onAddTask = {
                            taskToEdit = null
                            showAddTaskDialog = true
                        }
                    )
                    1 -> HabitsTab(
                        habits = habits,
                        viewModel = viewModel,
                        onViewDetails = { habitForDetails = it },
                        onOpenHeatmap = { habitForHeatmap = it },
                        onEditHabit = { habitToEdit = it },
                        onAddHabit = {
                            habitToEdit = null
                            showAddHabitDialog = true
                        }
                    )
                    2 -> KanbanTab(tasks = tasks, viewModel = viewModel)
                    3 -> PomodoroTab()
                    4 -> NotesTab(notes = notes, viewModel = viewModel, onAddNote = { showAddNoteDialog = true })
                }
            }
        }
    }
}

@Composable
private fun TasksListTab(
    tasks: List<KoreTask>,
    viewModel: KoreLifeViewModel,
    onEditTask: (KoreTask) -> Unit,
    onAddTask: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf(0) } // 0: Todas, 1: Pendientes, 2: Completadas
    var selectedPriorityFilter by remember { mutableStateOf<Priority?>(null) } // null: Todas, HIGH, MEDIUM, LOW
    var sortOption by remember { mutableStateOf(0) } // 0: Urgencia/Fecha, 1: Prioridad, 2: Título

    // Filter logic
    val filteredTasks = tasks.filter { task ->
        val matchesSearch = searchQuery.isBlank() ||
                task.title.contains(searchQuery, ignoreCase = true) ||
                task.description.contains(searchQuery, ignoreCase = true) ||
                task.category.contains(searchQuery, ignoreCase = true)

        val matchesStatus = when (selectedStatusFilter) {
            1 -> !task.completed
            2 -> task.completed
            else -> true
        }

        val matchesPriority = selectedPriorityFilter == null || task.priority == selectedPriorityFilter

        matchesSearch && matchesStatus && matchesPriority
    }.sortedWith { a, b ->
        when (sortOption) {
            0 -> {
                // Incompleted first, then by dueDate ascending
                if (a.completed != b.completed) {
                    if (a.completed) 1 else -1
                } else {
                    a.dueDate.compareTo(b.dueDate)
                }
            }
            1 -> {
                // By priority (HIGH first, then MEDIUM, then LOW)
                val orderA = when (a.priority) { Priority.HIGH -> 0; Priority.MEDIUM -> 1; Priority.LOW -> 2 }
                val orderB = when (b.priority) { Priority.HIGH -> 0; Priority.MEDIUM -> 1; Priority.LOW -> 2 }
                orderA.compareTo(orderB)
            }
            else -> a.title.compareTo(b.title, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Metrics Overview
        item {
            TaskMetricsOverview(tasks = tasks)
        }

        // Search bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar tareas por título, categoría...", color = TextSubtle, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = TextSubtle, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("task_search_field")
            )
        }

        // Status & Priority Filters
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Status Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val statusChips = listOf("Todas", "Pendientes", "Completadas")
                    statusChips.forEachIndexed { index, label ->
                        val isSelected = selectedStatusFilter == index
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) EmeraldContainer else DarkSurfaceVariant)
                                .border(1.dp, if (isSelected) EmeraldPrimary else DarkBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedStatusFilter = index }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) EmeraldOnContainer else TextWhite,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Sort button toggle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurfaceVariant)
                            .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                sortOption = (sortOption + 1) % 3
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Sort, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (sortOption) {
                                    0 -> "Fecha"
                                    1 -> "Prioridad"
                                    else -> "Título"
                                },
                                color = TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Priority Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Prioridad:", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)

                    val priorities = listOf<Priority?>(null, Priority.HIGH, Priority.MEDIUM, Priority.LOW)
                    priorities.forEach { prio ->
                        val isSelected = selectedPriorityFilter == prio
                        val label = when (prio) {
                            null -> "Todas"
                            Priority.HIGH -> "🔴 Alta"
                            Priority.MEDIUM -> "🟡 Media"
                            Priority.LOW -> "⚪ Baja"
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) DarkBorder else DarkSurfaceVariant)
                                .border(1.dp, if (isSelected) EmeraldPrimary else DarkBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedPriorityFilter = prio }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) TextWhite else TextMuted,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Task Items Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredTasks.size} tareas encontradas",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                TextButton(
                    onClick = onAddTask,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Nueva tarea", color = EmeraldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (filteredTasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
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
                        Text(text = "📋", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No se encontraron tareas con '$searchQuery'" else "No tienes tareas en este filtro",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Presiona 'Nueva tarea' para agregar un elemento con fecha límite.",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        } else {
            items(filteredTasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    onToggle = { viewModel.toggleTask(task.id) },
                    onEdit = { onEditTask(task) },
                    onDelete = { viewModel.deleteTask(task.id) }
                )
            }
        }
    }
}

@Composable
private fun HabitsTab(
    habits: List<Habit>,
    viewModel: KoreLifeViewModel,
    onViewDetails: (Habit) -> Unit,
    onOpenHeatmap: (Habit) -> Unit,
    onEditHabit: (Habit) -> Unit,
    onAddHabit: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Todos") }
    val categories = listOf("Todos", "Salud", "Finanzas", "Aprendizaje", "Bienestar", "Productividad", "General")

    val filteredHabits = habits.filter { habit ->
        selectedCategory == "Todos" || habit.category.equals(selectedCategory, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Streak Banner
        item {
            HabitStreakBanner(habits = habits)
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Categorías de Hábitos",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                TextButton(
                    onClick = onAddHabit,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = AmberTertiary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Nuevo hábito", color = AmberTertiary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFF78350F).copy(alpha = 0.5f) else DarkSurfaceVariant)
                            .border(1.dp, if (isSelected) AmberTertiary else DarkBorder, RoundedCornerShape(10.dp))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) AmberTertiary else TextWhite,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        if (filteredHabits.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
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
                        Text(text = "🔥", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No tienes hábitos en la categoría '$selectedCategory'",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Comienza creando un hábito diario con contador de rachas.",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        } else {
            items(filteredHabits, key = { it.id }) { habit ->
                HabitCard(
                    habit = habit,
                    onToggleToday = { viewModel.toggleHabit(habit.id) },
                    onViewDetails = { onViewDetails(habit) },
                    onOpenHeatmap = { onOpenHeatmap(habit) },
                    onEdit = { onEditHabit(habit) },
                    onDelete = { viewModel.deleteHabit(habit.id) }
                )
            }
        }
    }
}

@Composable
private fun KanbanTab(
    tasks: List<KoreTask>,
    viewModel: KoreLifeViewModel
) {
    val statuses = KanbanStatus.values()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        items(statuses) { status ->
            val columnTasks = tasks.filter { it.status == status }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = status.title, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(text = "${columnTasks.size}", color = EmeraldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (columnTasks.isEmpty()) {
                        Text(text = "Sin tareas en esta etapa", color = TextSubtle, fontSize = 11.sp, modifier = Modifier.padding(vertical = 8.dp))
                    } else {
                        columnTasks.forEach { task ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkSurfaceVariant)
                                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = task.title,
                                            color = TextWhite,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(task.priority.colorHex).copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = task.priority.title,
                                                color = Color(task.priority.colorHex),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    if (task.dueDate.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "📅 ${task.dueDate}", color = TextMuted, fontSize = 10.sp)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        if (status != KanbanStatus.TODO) {
                                            Text(
                                                text = "← Mover",
                                                color = TextMuted,
                                                fontSize = 11.sp,
                                                modifier = Modifier.clickable {
                                                    val prev = if (status == KanbanStatus.DONE) KanbanStatus.IN_PROGRESS else KanbanStatus.TODO
                                                    viewModel.updateTaskStatus(task.id, prev)
                                                }
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        if (status != KanbanStatus.DONE) {
                                            Text(
                                                text = "Avanzar →",
                                                color = EmeraldPrimary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.clickable {
                                                    val next = if (status == KanbanStatus.TODO) KanbanStatus.IN_PROGRESS else KanbanStatus.DONE
                                                    viewModel.updateTaskStatus(task.id, next)
                                                }
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
    }
}

@Composable
private fun PomodoroTab() {
    var secondsLeft by remember { mutableStateOf(25 * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var completedSessions by remember { mutableStateOf(3) }

    LaunchedEffect(isRunning) {
        while (isRunning && secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
        if (secondsLeft == 0 && isRunning) {
            isRunning = false
            completedSessions++
            secondsLeft = 25 * 60
        }
    }

    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    val timeFormatted = String.format(Locale.US, "%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Temporizador Pomodoro (TickTick)", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = "Bloques de enfoque de 25 minutos con 5 min de descanso", color = TextMuted, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(30.dp))

        // Timer Circle
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .background(DarkSurface)
                .border(4.dp, if (isRunning) EmeraldPrimary else DarkBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = timeFormatted,
                    color = if (isRunning) EmeraldPrimary else TextWhite,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black
                )
                Text(text = if (isRunning) "Enfocado..." else "En pausa", color = TextMuted, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { isRunning = !isRunning },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) AmberTertiary else EmeraldPrimary,
                    contentColor = if (isRunning) DarkBackground else EmeraldOnPrimary
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isRunning) "Pausar" else "Comenzar", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    isRunning = false
                    secondsLeft = 25 * 60
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reiniciar")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Sesiones completadas hoy: $completedSessions 🍅", color = AmberTertiary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun NotesTab(
    notes: List<Note>,
    viewModel: KoreLifeViewModel,
    onAddNote: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        items(notes) { note ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = note.title, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { viewModel.deleteNote(note.id) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = TextSubtle, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = note.content, color = TextMuted, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = note.date, color = TextSubtle, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun AddNoteDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, content: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Nueva Nota Rápida", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Contenido de la nota", color = TextMuted) },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(title, content)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = EmeraldOnPrimary)
                    ) {
                        Text("Guardar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
