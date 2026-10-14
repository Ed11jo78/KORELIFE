package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.KoreLifeRepository
import com.example.data.supabase.SupabaseClient
import com.example.data.supabase.SupabaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class KoreLifeViewModel : ViewModel() {

    val repository = KoreLifeRepository(viewModelScope)

    private val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    private val _currentUser = kotlinx.coroutines.flow.MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<com.google.firebase.auth.FirebaseUser?> = _currentUser.asStateFlow()

    private val _isAuthLoading = kotlinx.coroutines.flow.MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    val rates = repository.rates.stateIn(viewModelScope, SharingStarted.Lazily, repository.rates.value)
    val selectedProvider = repository.selectedProvider.stateIn(viewModelScope, SharingStarted.Lazily, repository.selectedProvider.value)
    val isRatesLoading = repository.isRatesLoading.stateIn(viewModelScope, SharingStarted.Lazily, false)

    val accounts = repository.accounts.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val transactions = repository.transactions.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val budgets = repository.budgets.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val budgetAlerts: StateFlow<List<BudgetAlert>> = budgets.map { budgetList ->
        budgetList.mapNotNull { b ->
            if (b.amount > 0) {
                val ratio = b.spent / b.amount
                val pct = (ratio * 100).toInt()
                when {
                    ratio >= 0.90 -> BudgetAlert(
                        budgetId = b.id,
                        category = b.category,
                        spent = b.spent,
                        amount = b.amount,
                        percentage = pct,
                        severity = BudgetAlertSeverity.CRITICAL,
                        message = "🚨 ¡Alerta de Presupuesto Crítico! Has gastado el $pct% del presupuesto de '${b.category}' (${b.spent} / ${b.amount} ${b.currency.symbol})."
                    )
                    ratio >= 0.80 -> BudgetAlert(
                        budgetId = b.id,
                        category = b.category,
                        spent = b.spent,
                        amount = b.amount,
                        percentage = pct,
                        severity = BudgetAlertSeverity.WARNING,
                        message = "⚠️ ¡Aviso Preventivo! El gasto en '${b.category}' ha alcanzado el $pct% de tu límite mensual."
                    )
                    else -> null
                }
            } else null
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val goals = repository.goals.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val subscriptions = repository.subscriptions.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val recurringExpenses = repository.recurringExpenses.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val tasks = repository.tasks.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val habits = repository.habits.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val notes = repository.notes.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val posts = repository.posts.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val groups = repository.groups.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val tickets = repository.tickets.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val preferredCurrency = repository.preferredCurrency.stateIn(viewModelScope, SharingStarted.Lazily, CurrencyCode.VES)
    val hideBalances = repository.hideBalances.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val autoUpdateRates = repository.autoUpdateRates.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val biometricsEnabled = repository.biometricsEnabled.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val themeMode = repository.themeMode.stateIn(viewModelScope, SharingStarted.Lazily, repository.themeMode.value)

    // --- Authentication Actions with Firebase ---
    fun signIn(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            try {
                auth.signInWithEmailAndPassword(email, pass).await()
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Error al iniciar sesión")
            } finally {
                _isAuthLoading.value = false
            }
        }
    }

    fun signUp(email: String, pass: String, fullName: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            try {
                auth.createUserWithEmailAndPassword(email, pass).await()
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Error al registrar cuenta")
            } finally {
                _isAuthLoading.value = false
            }
        }
    }

    fun recoverPassword(email: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            try {
                auth.sendPasswordResetEmail(email).await()
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "No se pudo enviar el correo de recuperación")
            } finally {
                _isAuthLoading.value = false
            }
        }
    }

    fun continueAsGuest() {
        viewModelScope.launch {
            try {
                auth.signInAnonymously().await()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }

    // --- Rates Functions ---
    fun refreshRates() = repository.fetchRates()
    fun setRateProvider(provider: RateProvider) = repository.setRateProvider(provider)

    // --- Transactions, Accounts, Budgets, Goals, Subscriptions ---
    fun addTransaction(
        accountId: String,
        amount: Double,
        currency: CurrencyCode,
        type: TransactionType,
        category: String,
        description: String
    ) = repository.addTransaction(accountId, amount, currency, type, category, description)

    fun addAccount(name: String, type: AccountType, balance: Double, currency: CurrencyCode) =
        repository.addAccount(name, type, balance, currency)

    fun addBudget(category: String, amount: Double, currency: CurrencyCode, period: String = "Mensual") =
        repository.addBudget(category, amount, currency, period)

    fun updateBudget(id: String, amount: Double, currency: CurrencyCode) =
        repository.updateBudget(id, amount, currency)

    fun deleteBudget(id: String) =
        repository.deleteBudget(id)

    fun addExpenseToBudget(budgetId: String, amount: Double) =
        repository.addExpenseToBudget(budgetId, amount)

    fun addGoal(
        title: String,
        emoji: String,
        targetAmount: Double,
        currency: CurrencyCode = CurrencyCode.USD,
        deadline: String,
        initialAmount: Double = 0.0,
        category: String = "Ahorro General",
        accountId: String? = null,
        notes: String = "",
        colorHex: Long = 0xFF10B981
    ) = repository.addGoal(title, emoji, targetAmount, currency, deadline, initialAmount, category, accountId, notes, colorHex)

    fun contributeToGoal(goalId: String, amount: Double, sourceAccountId: String? = null) =
        repository.contributeToGoal(goalId, amount, sourceAccountId)

    fun withdrawFromGoal(goalId: String, amount: Double, destinationAccountId: String? = null) =
        repository.withdrawFromGoal(goalId, amount, destinationAccountId)

    fun updateGoal(goal: Goal) = repository.updateGoal(goal)

    fun deleteGoal(goalId: String) = repository.deleteGoal(goalId)

    fun toggleSubscription(subId: String) = repository.toggleSubscription(subId)

    // --- Recurring Expenses ---
    fun addRecurringExpense(
        title: String,
        amount: Double,
        currency: CurrencyCode,
        category: String,
        dayOfMonth: Int,
        accountId: String,
        autoDeduct: Boolean = true,
        notes: String = ""
    ) = repository.addRecurringExpense(title, amount, currency, category, dayOfMonth, accountId, autoDeduct, notes)

    fun updateRecurringExpense(expense: RecurringExpense) = repository.updateRecurringExpense(expense)
    fun deleteRecurringExpense(id: String) = repository.deleteRecurringExpense(id)
    fun toggleRecurringExpenseActive(id: String) = repository.toggleRecurringExpenseActive(id)
    fun executeRecurringExpenseDeduction(id: String) = repository.executeRecurringExpenseDeduction(id)
    fun checkAndProcessRecurringExpenses(forceAllDue: Boolean = false) = repository.checkAndProcessRecurringExpenses(forceAllDue)

    // --- Tasks, Habits, Notes ---
    fun addTask(
        title: String,
        description: String,
        priority: Priority,
        dueDate: String,
        category: String = "General"
    ) = repository.addTask(title, description, priority, dueDate, category)

    fun updateTask(task: KoreTask) = repository.updateTask(task)
    fun toggleTask(taskId: String) = repository.toggleTask(taskId)
    fun updateTaskStatus(taskId: String, status: KanbanStatus) = repository.updateTaskStatus(taskId, status)
    fun deleteTask(taskId: String) = repository.deleteTask(taskId)

    fun addHabit(
        name: String,
        frequency: String,
        emoji: String = "🔥",
        category: String = "General",
        targetDaysPerWeek: Int = 7,
        notes: String = ""
    ) = repository.addHabit(name, frequency, emoji, category, targetDaysPerWeek, notes)

    fun updateHabit(habit: Habit) = repository.updateHabit(habit)
    fun deleteHabit(habitId: String) = repository.deleteHabit(habitId)
    fun resetHabitStreak(habitId: String) = repository.resetHabitStreak(habitId)
    fun toggleHabit(habitId: String) = repository.toggleHabit(habitId)
    fun toggleHabitDay(habitId: String, day: Int) = repository.toggleHabitDay(habitId, day)

    fun addNote(title: String, content: String) = repository.addNote(title, content)
    fun deleteNote(noteId: String) = repository.deleteNote(noteId)

    // --- Social & Support ---
    fun addPost(content: String, category: String) = repository.addPost(content, category)
    fun toggleLikePost(postId: String) = repository.toggleLikePost(postId)
    fun addCommentToPost(postId: String, text: String) = repository.addCommentToPost(postId, text)

    fun toggleJoinGroup(groupId: String) = repository.toggleJoinGroup(groupId)
    fun createSupportTicket(subject: String, category: String) = repository.createSupportTicket(subject, category)

    // --- Settings ---
    fun setPreferredCurrency(currency: CurrencyCode) = repository.setPreferredCurrency(currency)
    fun toggleHideBalances() = repository.toggleHideBalances()
    fun toggleAutoUpdateRates() = repository.toggleAutoUpdateRates()
    fun toggleBiometrics() = repository.toggleBiometrics()
    fun setThemeMode(mode: ThemeMode) = repository.setThemeMode(mode)
}
