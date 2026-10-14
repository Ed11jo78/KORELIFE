package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.KoreLifeRepository
import com.example.data.supabase.SupabaseClient
import com.example.data.supabase.SupabaseUser
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class KoreLifeViewModel : ViewModel() {

    val repository = KoreLifeRepository(viewModelScope)
    val supabaseClient = SupabaseClient()

    val currentUser: StateFlow<SupabaseUser?> = supabaseClient.currentUser
    val isAuthLoading: StateFlow<Boolean> = supabaseClient.isLoading

    val rates = repository.rates.stateIn(viewModelScope, SharingStarted.Lazily, repository.rates.value)
    val selectedProvider = repository.selectedProvider.stateIn(viewModelScope, SharingStarted.Lazily, repository.selectedProvider.value)
    val isRatesLoading = repository.isRatesLoading.stateIn(viewModelScope, SharingStarted.Lazily, false)

    val accounts = repository.accounts.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val transactions = repository.transactions.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val budgets = repository.budgets.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
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

    // --- Authentication Actions with Supabase ---
    fun signIn(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = supabaseClient.signInWithPassword(email, pass)
            res.fold(
                onSuccess = { onResult(true, null) },
                onFailure = { onResult(false, it.message ?: "Error al iniciar sesión") }
            )
        }
    }

    fun signUp(email: String, pass: String, fullName: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = supabaseClient.signUp(email, pass, fullName)
            res.fold(
                onSuccess = { onResult(true, null) },
                onFailure = { onResult(false, it.message ?: "Error al registrar cuenta") }
            )
        }
    }

    fun recoverPassword(email: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = supabaseClient.recoverPassword(email)
            res.fold(
                onSuccess = { onResult(true, null) },
                onFailure = { onResult(false, it.message ?: "No se pudo enviar el correo de recuperación") }
            )
        }
    }

    fun continueAsGuest() {
        supabaseClient.continueAsGuest()
    }

    fun signOut() {
        supabaseClient.signOut()
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
