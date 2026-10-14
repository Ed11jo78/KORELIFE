package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class KoreLifeRepository(private val coroutineScope: CoroutineScope) {

    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // --- Exchange Rates State ---
    private val defaultRates = mapOf(
        RateProvider.BCV_USD to ExchangeRate(
            provider = RateProvider.BCV_USD,
            name = "BCV Dólar",
            symbolPair = "USD/VES",
            rate = 36.54,
            change24h = 0.12,
            lastUpdated = "10:30 AM",
            icon = "🏛️"
        ),
        RateProvider.BCV_EUR to ExchangeRate(
            provider = RateProvider.BCV_EUR,
            name = "BCV Euro",
            symbolPair = "EUR/VES",
            rate = 39.85,
            change24h = -0.05,
            lastUpdated = "10:30 AM",
            icon = "💶"
        ),
        RateProvider.BINANCE to ExchangeRate(
            provider = RateProvider.BINANCE,
            name = "Binance P2P",
            symbolPair = "USDT/VES",
            rate = 41.20,
            change24h = 0.45,
            lastUpdated = "10:30 AM",
            icon = "🟡"
        ),
        RateProvider.AIRTM to ExchangeRate(
            provider = RateProvider.AIRTM,
            name = "Airtm",
            symbolPair = "AirUSD/VES",
            rate = 40.90,
            change24h = 0.20,
            lastUpdated = "10:30 AM",
            icon = "🔵"
        )
    )

    private val _rates = MutableStateFlow(defaultRates)
    val rates: StateFlow<Map<RateProvider, ExchangeRate>> = _rates.asStateFlow()

    private val _selectedProvider = MutableStateFlow(RateProvider.BCV_USD)
    val selectedProvider: StateFlow<RateProvider> = _selectedProvider.asStateFlow()

    private val _isRatesLoading = MutableStateFlow(false)
    val isRatesLoading: StateFlow<Boolean> = _isRatesLoading.asStateFlow()

    // --- Financial State ---
    private val _accounts = MutableStateFlow(
        listOf(
            Account("1", "Banesco (Bolívares)", AccountType.BANK, 145200.50, CurrencyCode.VES),
            Account("2", "Mercantil (Bolívares)", AccountType.BANK, 87500.00, CurrencyCode.VES),
            Account("3", "Efectivo USD ($)", AccountType.CASH, 450.00, CurrencyCode.USD),
            Account("4", "Binance USDT", AccountType.INVESTMENT, 320.00, CurrencyCode.USD)
        )
    )
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    private val _transactions = MutableStateFlow(
        listOf(
            Transaction("t1", "1", 3500.00, CurrencyCode.VES, TransactionType.EXPENSE, "Supermercado", "Compras de la semana", "2026-10-12"),
            Transaction("t2", "1", 85000.00, CurrencyCode.VES, TransactionType.INCOME, "Nómina", "Pago de quincena", "2026-10-10"),
            Transaction("t3", "3", 50.00, CurrencyCode.USD, TransactionType.EXPENSE, "Servicios", "Internet fibra óptica", "2026-10-09"),
            Transaction("t4", "4", 150.00, CurrencyCode.USD, TransactionType.INCOME, "Freelance", "Diseño UX/UI Proyecto", "2026-10-08")
        )
    )
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _budgets = MutableStateFlow(
        listOf(
            Budget("b1", "Alimentación", 25000.0, 14200.0, CurrencyCode.VES),
            Budget("b2", "Transporte & Movilidad", 8000.0, 3400.0, CurrencyCode.VES),
            Budget("b3", "Servicios del Hogar", 6000.0, 5800.0, CurrencyCode.VES),
            Budget("b4", "Entretenimiento", 4000.0, 1850.0, CurrencyCode.VES)
        )
    )
    val budgets: StateFlow<List<Budget>> = _budgets.asStateFlow()

    private val _recurringExpenses = MutableStateFlow(
        listOf(
            RecurringExpense(
                id = "rec_1",
                title = "Alquiler Departamento",
                amount = 250.0,
                currency = CurrencyCode.USD,
                category = "Hogar",
                dayOfMonth = 1,
                accountId = "3",
                autoDeduct = true,
                isActive = true,
                lastDeductedMonth = "2026-10",
                notes = "Pago directo mensual al propietario"
            ),
            RecurringExpense(
                id = "rec_2",
                title = "Internet Fibra Óptica 300MB",
                amount = 35.0,
                currency = CurrencyCode.USD,
                category = "Servicios del Hogar",
                dayOfMonth = 15,
                accountId = "3",
                autoDeduct = true,
                isActive = true,
                lastDeductedMonth = null,
                notes = "Débito del día 15 cada mes"
            ),
            RecurringExpense(
                id = "rec_3",
                title = "Condominio Residencial",
                amount = 1850.0,
                currency = CurrencyCode.VES,
                category = "Servicios del Hogar",
                dayOfMonth = 10,
                accountId = "1",
                autoDeduct = true,
                isActive = true,
                lastDeductedMonth = "2026-10",
                notes = "Abono a cuenta junta de condominio"
            ),
            RecurringExpense(
                id = "rec_4",
                title = "Gimnasio & Bienestar",
                amount = 25.0,
                currency = CurrencyCode.USD,
                category = "Salud",
                dayOfMonth = 5,
                accountId = "4",
                autoDeduct = true,
                isActive = true,
                lastDeductedMonth = "2026-10",
                notes = "Membresía mensual de entrenamiento"
            ),
            RecurringExpense(
                id = "rec_5",
                title = "Fondo de Alimentación Quincenal",
                amount = 5500.0,
                currency = CurrencyCode.VES,
                category = "Alimentación",
                dayOfMonth = 16,
                accountId = "1",
                autoDeduct = true,
                isActive = true,
                lastDeductedMonth = null,
                notes = "Asignación para víveres y supermercado"
            )
        )
    )
    val recurringExpenses: StateFlow<List<RecurringExpense>> = _recurringExpenses.asStateFlow()

    private val _goals = MutableStateFlow(
        listOf(
            Goal("g1", "Fondo de Emergencia", "🛡️", 5000.0, 2100.0, CurrencyCode.USD, "2026-12-31", 0xFF10B981),
            Goal("g2", "Nuevo Teléfono", "📱", 1200.0, 900.0, CurrencyCode.USD, "2026-11-30", 0xFFF59E0B),
            Goal("g3", "Vacaciones Fin de Año", "✈️", 2500.0, 1400.0, CurrencyCode.USD, "2026-12-15", 0xFF6366F1)
        )
    )
    val goals: StateFlow<List<Goal>> = _goals.asStateFlow()

    private val _subscriptions = MutableStateFlow(
        listOf(
            Subscription("s1", "Netflix Premium", "🍿", 15.99, CurrencyCode.USD, "Mensual", "28 Oct 2026", true),
            Subscription("s2", "Spotify Dúo", "🎵", 6.99, CurrencyCode.USD, "Mensual", "02 Nov 2026", true),
            Subscription("s3", "Fibra Óptica 300MB", "🌐", 35.00, CurrencyCode.USD, "Mensual", "15 Nov 2026", true),
            Subscription("s4", "Gimnasio Fitness", "💪", 25.00, CurrencyCode.USD, "Mensual", "01 Nov 2026", true)
        )
    )
    val subscriptions: StateFlow<List<Subscription>> = _subscriptions.asStateFlow()

    // --- Productivity State ---
    private val _tasks = MutableStateFlow(
        listOf(
            KoreTask("1", "Pagar factura de luz y fibra óptica", "Facturas del mes en curso", false, Priority.HIGH, "2026-10-15", KanbanStatus.TODO, "Finanzas"),
            KoreTask("2", "Conciliar cuentas en Banesco y Mercantil", "Revisión de comprobantes y saldos", true, Priority.MEDIUM, "2026-10-14", KanbanStatus.DONE, "Finanzas"),
            KoreTask("3", "Revisar asignación en presupuesto YNAB", "Ajustar sobre de alimentación y servicios", false, Priority.MEDIUM, "2026-10-16", KanbanStatus.IN_PROGRESS, "Presupuesto"),
            KoreTask("4", "Comprar víveres y despensa quincenal", "Verificar lista en supermercado", false, Priority.HIGH, "2026-10-17", KanbanStatus.TODO, "Personal"),
            KoreTask("5", "Respaldar informes en la nube", "Documentos personales y financieros", false, Priority.LOW, "2026-10-20", KanbanStatus.TODO, "Productividad"),
            KoreTask("6", "Declaración de ingresos trimestral", "Revisar facturación electrónica", false, Priority.HIGH, "2026-10-14", KanbanStatus.TODO, "Trabajo")
        )
    )
    val tasks: StateFlow<List<KoreTask>> = _tasks.asStateFlow()

    private val _habits = MutableStateFlow(
        listOf(
            Habit("h1", "Ejercicio matutino 30 min", 7, false, "Diario", "🏃", "Salud", 14, listOf(true, true, true, true, true, true, false)),
            Habit("h2", "Lectura de finanzas y productividad", 14, true, "Diario", "📚", "Aprendizaje", 21, listOf(true, true, true, true, true, true, true)),
            Habit("h3", "Registrar todos los gastos del día", 12, false, "Diario", "💰", "Finanzas", 18, listOf(true, true, true, true, true, false, false)),
            Habit("h4", "Tomar 2L de agua", 5, true, "Diario", "💧", "Salud", 9, listOf(true, true, false, true, true, true, true)),
            Habit("h5", "Meditación o respiración 10 min", 22, true, "Diario", "🧘", "Bienestar", 22, listOf(true, true, true, true, true, true, true))
        )
    )
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    private val _notes = MutableStateFlow(
        listOf(
            Note("n1", "Estrategia de Fondos 50/30/20", "50% Necesidades básicas, 30% Deseos y compras, 20% Ahorro e inversiones directas.", "2026-10-10"),
            Note("n2", "Reglas de Arbitraje y Cambio", "Verificar siempre tasa oficial BCV antes de fijar pagos de proveedores locales.", "2026-10-11")
        )
    )
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    // --- Social / Community State ---
    private val _posts = MutableStateFlow(
        listOf(
            CommunityPost(
                "1",
                "Alejandro Mendoza",
                "AM",
                "Ahorro cumplido",
                "Hace 2h",
                "🎯 ¡Completé mi meta de fondo de emergencia de 3 meses usando la tasa BCV! Muy emocionado con la disciplina financiera.",
                18,
                false,
                listOf(
                    PostComment("c1", "Carlos Pérez", "¡Excelente logro hermano! A seguir ahorrando.", "Hace 1h"),
                    PostComment("c2", "Valeria Gómez", "Felicidades, la constancia rinde frutos.", "Hace 45m")
                )
            ),
            CommunityPost(
                "2",
                "Valeria Gómez",
                "VG",
                "Productividad",
                "Hace 5h",
                "⚡ Racha de 14 días completando todos mis hábitos en KoreLife. ¡La constancia y el control hacen la diferencia!",
                24,
                true,
                listOf(
                    PostComment("c3", "Daniela Silva", "¿Qué hábitos tienes en tu rutina diaria?", "Hace 3h")
                )
            ),
            CommunityPost(
                "3",
                "Ricardo Torres",
                "RT",
                "Inversiones",
                "Hace 1d",
                "📊 Diversifiqué mis cuentas en Binance USDT y Banesco con las conversiones en tiempo real de KoreLife. Control total.",
                31,
                false,
                emptyList()
            )
        )
    )
    val posts: StateFlow<List<CommunityPost>> = _posts.asStateFlow()

    private val _groups = MutableStateFlow(
        listOf(
            CommunityGroup("1", "Inversionistas Venezuela 🇻🇪", 1242, "Estrategias de inversión, tasas BCV y arbitraje P2P en Binance.", "Finanzas", true),
            CommunityGroup("2", "Productividad GTD & Hábitos ⚡", 893, "Técnicas de enfoque, Pomodoro, gestión de tareas y rutinas diarias.", "Productividad", true),
            CommunityGroup("3", "Presupuestos & Ahorro Inteligente 💰", 1530, "Consejos para optimizar gastos hormiga y crear fondos de emergencia.", "Finanzas", false)
        )
    )
    val groups: StateFlow<List<CommunityGroup>> = _groups.asStateFlow()

    private val _tickets = MutableStateFlow(
        listOf(
            SupportTicket("tk1", "Consulta sobre cálculo de tasas BCV", "Finanzas", "Abierto", "2026-10-12"),
            SupportTicket("tk2", "Sugerencia: exportar reportes en CSV", "Mejora", "Resuelto", "2026-10-05")
        )
    )
    val tickets: StateFlow<List<SupportTicket>> = _tickets.asStateFlow()

    // --- Settings State ---
    private val _preferredCurrency = MutableStateFlow(CurrencyCode.VES)
    val preferredCurrency: StateFlow<CurrencyCode> = _preferredCurrency.asStateFlow()

    private val _hideBalances = MutableStateFlow(false)
    val hideBalances: StateFlow<Boolean> = _hideBalances.asStateFlow()

    private val _autoUpdateRates = MutableStateFlow(true)
    val autoUpdateRates: StateFlow<Boolean> = _autoUpdateRates.asStateFlow()

    private val _biometricsEnabled = MutableStateFlow(true)
    val biometricsEnabled: StateFlow<Boolean> = _biometricsEnabled.asStateFlow()

    init {
        fetchRates()
        checkAndProcessRecurringExpenses()
    }

    // --- Rates Functions ---
    fun setRateProvider(provider: RateProvider) {
        _selectedProvider.value = provider
    }

    fun fetchRates() {
        coroutineScope.launch(Dispatchers.IO) {
            _isRatesLoading.value = true
            val currentTime = timeFormat.format(Date())
            try {
                val url = URL("https://ve.dolarapi.com/v1/dolares")
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 4000
                conn.readTimeout = 4000
                conn.requestMethod = "GET"
                conn.setRequestProperty("Accept", "application/json")

                if (conn.responseCode == 200) {
                    val reader = BufferedReader(InputStreamReader(conn.inputStream))
                    val response = reader.readText()
                    reader.close()

                    val jsonArray = JSONArray(response)
                    var oficial = 36.54
                    var paralelo = 41.20

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val fuente = item.optString("fuente")
                        val promedio = item.optDouble("promedio", 0.0)
                        if (fuente == "oficial" && promedio > 0) oficial = promedio
                        if (fuente == "paralelo" && promedio > 0) paralelo = promedio
                    }

                    _rates.value = mapOf(
                        RateProvider.BCV_USD to ExchangeRate(RateProvider.BCV_USD, "BCV Dólar", "USD/VES", oficial, 0.12, currentTime, "🏛️"),
                        RateProvider.BCV_EUR to ExchangeRate(RateProvider.BCV_EUR, "BCV Euro", "EUR/VES", oficial * 1.09, -0.05, currentTime, "💶"),
                        RateProvider.BINANCE to ExchangeRate(RateProvider.BINANCE, "Binance P2P", "USDT/VES", paralelo, 0.45, currentTime, "🟡"),
                        RateProvider.AIRTM to ExchangeRate(RateProvider.AIRTM, "Airtm", "AirUSD/VES", paralelo * 0.99, 0.20, currentTime, "🔵")
                    )
                }
            } catch (e: Exception) {
                // Keep default updated with timestamp
                _rates.value = _rates.value.mapValues { (_, rate) ->
                    rate.copy(lastUpdated = currentTime)
                }
            } finally {
                _isRatesLoading.value = false
            }
        }
    }

    fun convert(amount: Double, from: CurrencyCode, to: CurrencyCode, provider: RateProvider? = null): Double {
        if (from == to || amount == 0.0) return amount
        val activeProvider = provider ?: _selectedProvider.value
        val currentUsdRate = _rates.value[activeProvider]?.rate ?: 36.54
        val currentEurRate = _rates.value[RateProvider.BCV_EUR]?.rate ?: 39.85

        // Normalize to VES
        val amountInVes = when (from) {
            CurrencyCode.VES -> amount
            CurrencyCode.USD -> amount * currentUsdRate
            CurrencyCode.EUR -> amount * currentEurRate
        }

        // Convert from VES to target
        return when (to) {
            CurrencyCode.VES -> amountInVes
            CurrencyCode.USD -> if (currentUsdRate > 0) amountInVes / currentUsdRate else 0.0
            CurrencyCode.EUR -> if (currentEurRate > 0) amountInVes / currentEurRate else 0.0
        }
    }

    fun formatCurrency(amount: Double, currency: CurrencyCode): String {
        val safe = if (amount.isNaN()) 0.0 else amount
        return when (currency) {
            CurrencyCode.VES -> String.format(Locale.US, "Bs. %,.2f", safe)
            CurrencyCode.USD -> String.format(Locale.US, "$ %,.2f", safe)
            CurrencyCode.EUR -> String.format(Locale.US, "€ %,.2f", safe)
        }
    }

    fun getTotalBalanceInVes(): Double {
        return _accounts.value.sumOf { acc ->
            convert(acc.balance, acc.currency, CurrencyCode.VES)
        }
    }

    fun getTotalBalanceInUsd(): Double {
        return _accounts.value.sumOf { acc ->
            convert(acc.balance, acc.currency, CurrencyCode.USD)
        }
    }

    // --- Action Methods ---
    fun addTransaction(
        accountId: String,
        amount: Double,
        currency: CurrencyCode,
        type: TransactionType,
        category: String,
        description: String
    ) {
        val newTx = Transaction(
            id = "tx_${System.currentTimeMillis()}",
            accountId = accountId,
            amount = amount,
            currency = currency,
            type = type,
            category = category,
            description = description,
            date = dateFormat.format(Date())
        )
        _transactions.value = listOf(newTx) + _transactions.value

        // Adjust account balance
        _accounts.value = _accounts.value.map { acc ->
            if (acc.id == accountId) {
                val delta = if (type == TransactionType.INCOME) amount else -amount
                val newBal = (acc.balance + delta).coerceAtLeast(0.0)
                acc.copy(balance = newBal)
            } else {
                acc
            }
        }

        // Adjust matching budgets if it's an expense
        if (type == TransactionType.EXPENSE) {
            _budgets.value = _budgets.value.map { b ->
                if (b.category.equals(category, ignoreCase = true) || 
                    category.contains(b.category, ignoreCase = true) || 
                    b.category.contains(category, ignoreCase = true)) {
                    val converted = convert(amount, currency, b.currency)
                    b.copy(spent = b.spent + converted)
                } else b
            }
        }
    }

    fun addAccount(name: String, type: AccountType, balance: Double, currency: CurrencyCode) {
        val newAcc = Account(
            id = "acc_${System.currentTimeMillis()}",
            name = name,
            type = type,
            balance = balance,
            currency = currency
        )
        _accounts.value = _accounts.value + newAcc
    }

    fun addBudget(category: String, amount: Double, currency: CurrencyCode, period: String = "Mensual") {
        // Calculate already recorded expenses for this category
        val currentCategorySpent = _transactions.value
            .filter { it.type == TransactionType.EXPENSE && (it.category.equals(category, ignoreCase = true) || category.contains(it.category, ignoreCase = true)) }
            .sumOf { convert(it.amount, it.currency, currency) }

        val newBudget = Budget(
            id = "b_${System.currentTimeMillis()}",
            category = category,
            amount = amount,
            spent = currentCategorySpent,
            currency = currency,
            period = period
        )
        _budgets.value = _budgets.value + newBudget
    }

    fun updateBudget(id: String, amount: Double, currency: CurrencyCode) {
        _budgets.value = _budgets.value.map { b ->
            if (b.id == id) b.copy(amount = amount, currency = currency) else b
        }
    }

    fun deleteBudget(id: String) {
        _budgets.value = _budgets.value.filter { it.id != id }
    }

    fun addExpenseToBudget(budgetId: String, expenseAmount: Double) {
        _budgets.value = _budgets.value.map { b ->
            if (b.id == budgetId) {
                b.copy(spent = b.spent + expenseAmount)
            } else b
        }
    }

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
    ) {
        val newGoal = Goal(
            id = "g_${System.currentTimeMillis()}",
            title = title,
            emoji = emoji.ifBlank { "🎯" },
            targetAmount = targetAmount,
            currentAmount = initialAmount.coerceAtLeast(0.0),
            currency = currency,
            deadline = deadline,
            colorHex = colorHex,
            category = category,
            accountId = accountId,
            notes = notes
        )

        // If an initial deposit was made from an account, deduct and register transaction
        if (initialAmount > 0.0 && accountId != null) {
            _accounts.value = _accounts.value.map { acc ->
                if (acc.id == accountId) {
                    acc.copy(balance = (acc.balance - initialAmount).coerceAtLeast(0.0))
                } else acc
            }
            val tx = Transaction(
                id = "tx_goal_${System.currentTimeMillis()}",
                accountId = accountId,
                amount = initialAmount,
                currency = currency,
                type = TransactionType.EXPENSE,
                category = "Ahorro e Inversión",
                description = "🎯 Depósito inicial meta: $title",
                date = dateFormat.format(Date())
            )
            _transactions.value = listOf(tx) + _transactions.value
        }

        _goals.value = _goals.value + newGoal
    }

    fun contributeToGoal(goalId: String, amount: Double, sourceAccountId: String? = null) {
        val goal = _goals.value.find { it.id == goalId } ?: return
        if (amount <= 0.0) return

        if (sourceAccountId != null) {
            _accounts.value = _accounts.value.map { acc ->
                if (acc.id == sourceAccountId) {
                    acc.copy(balance = (acc.balance - amount).coerceAtLeast(0.0))
                } else acc
            }
            val tx = Transaction(
                id = "tx_goal_${System.currentTimeMillis()}",
                accountId = sourceAccountId,
                amount = amount,
                currency = goal.currency,
                type = TransactionType.EXPENSE,
                category = "Ahorro e Inversión",
                description = "🎯 Abono a meta: ${goal.title}",
                date = dateFormat.format(Date())
            )
            _transactions.value = listOf(tx) + _transactions.value
        }

        _goals.value = _goals.value.map { g ->
            if (g.id == goalId) {
                g.copy(currentAmount = g.currentAmount + amount)
            } else g
        }
    }

    fun withdrawFromGoal(goalId: String, amount: Double, destinationAccountId: String? = null) {
        val goal = _goals.value.find { it.id == goalId } ?: return
        if (amount <= 0.0) return

        val withdrawAmount = amount.coerceAtMost(goal.currentAmount)

        if (destinationAccountId != null) {
            _accounts.value = _accounts.value.map { acc ->
                if (acc.id == destinationAccountId) {
                    acc.copy(balance = acc.balance + withdrawAmount)
                } else acc
            }
            val tx = Transaction(
                id = "tx_goal_with_${System.currentTimeMillis()}",
                accountId = destinationAccountId,
                amount = withdrawAmount,
                currency = goal.currency,
                type = TransactionType.INCOME,
                category = "Ahorro e Inversión",
                description = "🎯 Retiro de meta: ${goal.title}",
                date = dateFormat.format(Date())
            )
            _transactions.value = listOf(tx) + _transactions.value
        }

        _goals.value = _goals.value.map { g ->
            if (g.id == goalId) {
                g.copy(currentAmount = (g.currentAmount - withdrawAmount).coerceAtLeast(0.0))
            } else g
        }
    }

    fun updateGoal(updatedGoal: Goal) {
        _goals.value = _goals.value.map { g ->
            if (g.id == updatedGoal.id) updatedGoal else g
        }
    }

    fun deleteGoal(goalId: String) {
        _goals.value = _goals.value.filter { it.id != goalId }
    }

    fun toggleSubscription(subId: String) {
        _subscriptions.value = _subscriptions.value.map { s ->
            if (s.id == subId) s.copy(active = !s.active) else s
        }
    }

    // --- Recurring Expenses & Automatic Budget Deductions ---
    fun addRecurringExpense(
        title: String,
        amount: Double,
        currency: CurrencyCode,
        category: String,
        dayOfMonth: Int,
        accountId: String,
        autoDeduct: Boolean = true,
        notes: String = ""
    ) {
        val newExpense = RecurringExpense(
            id = "rec_${System.currentTimeMillis()}",
            title = title,
            amount = amount,
            currency = currency,
            category = category,
            dayOfMonth = dayOfMonth.coerceIn(1, 31),
            accountId = accountId,
            autoDeduct = autoDeduct,
            isActive = true,
            lastDeductedMonth = null,
            notes = notes
        )
        _recurringExpenses.value = _recurringExpenses.value + newExpense
        checkAndProcessRecurringExpenses()
    }

    fun updateRecurringExpense(expense: RecurringExpense) {
        _recurringExpenses.value = _recurringExpenses.value.map {
            if (it.id == expense.id) expense else it
        }
    }

    fun deleteRecurringExpense(id: String) {
        _recurringExpenses.value = _recurringExpenses.value.filter { it.id != id }
    }

    fun toggleRecurringExpenseActive(id: String) {
        _recurringExpenses.value = _recurringExpenses.value.map {
            if (it.id == id) it.copy(isActive = !it.isActive) else it
        }
    }

    /**
     * Executes deduction of a recurring expense:
     * 1. Deducts amount from the account balance
     * 2. Automatically updates matching budget's spent amount
     * 3. Creates an expense Transaction in transaction history
     * 4. Updates lastDeductedMonth
     */
    fun executeRecurringExpenseDeduction(id: String): Boolean {
        val rec = _recurringExpenses.value.find { it.id == id } ?: return false
        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        val todayStr = dateFormat.format(Date())

        // 1. Deduct from account balance
        _accounts.value = _accounts.value.map { acc ->
            if (acc.id == rec.accountId) {
                val newBal = (acc.balance - rec.amount).coerceAtLeast(0.0)
                acc.copy(balance = newBal)
            } else acc
        }

        // 2. Automatically deduct from budget (by increasing spent)
        _budgets.value = _budgets.value.map { b ->
            if (b.category.equals(rec.category, ignoreCase = true) ||
                rec.category.contains(b.category, ignoreCase = true) ||
                b.category.contains(rec.category, ignoreCase = true)) {
                val converted = convert(rec.amount, rec.currency, b.currency)
                b.copy(spent = b.spent + converted)
            } else b
        }

        // 3. Create expense transaction
        val newTx = Transaction(
            id = "tx_rec_${System.currentTimeMillis()}",
            accountId = rec.accountId,
            amount = rec.amount,
            currency = rec.currency,
            type = TransactionType.EXPENSE,
            category = rec.category,
            description = "[Recurrente] ${rec.title}",
            date = todayStr
        )
        _transactions.value = listOf(newTx) + _transactions.value

        // 4. Update lastDeductedMonth
        _recurringExpenses.value = _recurringExpenses.value.map {
            if (it.id == id) it.copy(lastDeductedMonth = currentMonth) else it
        }

        return true
    }

    /**
     * Checks all recurring expenses and automatically deducts those due up to today
     * that haven't been deducted for the current month yet.
     */
    fun checkAndProcessRecurringExpenses(forceAllDue: Boolean = false): Int {
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

        var processedCount = 0
        val expensesToProcess = _recurringExpenses.value.filter { rec ->
            rec.isActive &&
            (rec.autoDeduct || forceAllDue) &&
            rec.lastDeductedMonth != currentMonth &&
            (currentDay >= rec.dayOfMonth || forceAllDue)
        }

        for (rec in expensesToProcess) {
            executeRecurringExpenseDeduction(rec.id)
            processedCount++
        }

        return processedCount
    }

    fun addTask(
        title: String,
        description: String,
        priority: Priority,
        dueDate: String,
        category: String = "General"
    ) {
        val newTask = KoreTask(
            id = "task_${System.currentTimeMillis()}",
            title = title,
            description = description,
            completed = false,
            priority = priority,
            dueDate = dueDate.ifBlank { dateFormat.format(Date()) },
            status = KanbanStatus.TODO,
            category = category
        )
        _tasks.value = listOf(newTask) + _tasks.value
    }

    fun updateTask(task: KoreTask) {
        _tasks.value = _tasks.value.map { if (it.id == task.id) task else it }
    }

    fun toggleTask(taskId: String) {
        _tasks.value = _tasks.value.map { t ->
            if (t.id == taskId) {
                val newCompleted = !t.completed
                val newStatus = if (newCompleted) KanbanStatus.DONE else KanbanStatus.TODO
                val completedDate = if (newCompleted) dateFormat.format(Date()) else null
                t.copy(completed = newCompleted, status = newStatus, completedAt = completedDate)
            } else t
        }
    }

    fun updateTaskStatus(taskId: String, status: KanbanStatus) {
        _tasks.value = _tasks.value.map { t ->
            if (t.id == taskId) {
                val isDone = (status == KanbanStatus.DONE)
                t.copy(status = status, completed = isDone, completedAt = if (isDone) dateFormat.format(Date()) else null)
            } else t
        }
    }

    fun deleteTask(taskId: String) {
        _tasks.value = _tasks.value.filter { it.id != taskId }
    }

    fun addHabit(
        name: String,
        frequency: String,
        emoji: String = "🔥",
        category: String = "General",
        targetDaysPerWeek: Int = 7,
        notes: String = ""
    ) {
        val newHabit = Habit(
            id = "h_${System.currentTimeMillis()}",
            name = name,
            streak = 0,
            completedToday = false,
            frequency = frequency,
            emoji = emoji,
            category = category,
            bestStreak = 0,
            weeklyHistory = listOf(false, false, false, false, false, false, false),
            targetDaysPerWeek = targetDaysPerWeek,
            notes = notes
        )
        _habits.value = _habits.value + newHabit
    }

    fun updateHabit(habit: Habit) {
        _habits.value = _habits.value.map { if (it.id == habit.id) habit else it }
    }

    fun deleteHabit(habitId: String) {
        _habits.value = _habits.value.filter { it.id != habitId }
    }

    fun resetHabitStreak(habitId: String) {
        _habits.value = _habits.value.map { if (it.id == habitId) it.copy(streak = 0, completedToday = false) else it }
    }

    fun toggleHabit(habitId: String) {
        _habits.value = _habits.value.map { h ->
            if (h.id == habitId) {
                val newCompleted = !h.completedToday
                val newStreak = if (newCompleted) h.streak + 1 else (h.streak - 1).coerceAtLeast(0)
                val newBest = if (newStreak > h.bestStreak) newStreak else h.bestStreak
                val cal = java.util.Calendar.getInstance()
                val dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK)
                val idx = if (dayOfWeek == java.util.Calendar.SUNDAY) 6 else dayOfWeek - 2
                val updatedWeekly = h.weeklyHistory.toMutableList()
                if (idx in updatedWeekly.indices) {
                    updatedWeekly[idx] = newCompleted
                }
                val todayDayNum = cal.get(java.util.Calendar.DAY_OF_MONTH)
                val updatedMonthly = h.monthlyHistory.toMutableMap()
                updatedMonthly[todayDayNum] = newCompleted
                h.copy(
                    completedToday = newCompleted,
                    streak = newStreak,
                    bestStreak = newBest,
                    weeklyHistory = updatedWeekly,
                    monthlyHistory = updatedMonthly
                )
            } else h
        }
    }

    fun toggleHabitDay(habitId: String, day: Int) {
        _habits.value = _habits.value.map { h ->
            if (h.id == habitId) {
                val currentVal = h.monthlyHistory[day] ?: false
                val updatedMonthly = h.monthlyHistory.toMutableMap()
                updatedMonthly[day] = !currentVal
                h.copy(monthlyHistory = updatedMonthly)
            } else h
        }
    }

    fun addNote(title: String, content: String) {
        val newNote = Note(
            id = "n_${System.currentTimeMillis()}",
            title = title,
            content = content,
            date = dateFormat.format(Date())
        )
        _notes.value = listOf(newNote) + _notes.value
    }

    fun deleteNote(noteId: String) {
        _notes.value = _notes.value.filter { it.id != noteId }
    }

    fun addPost(content: String, category: String) {
        val newPost = CommunityPost(
            id = "post_${System.currentTimeMillis()}",
            author = "Usuario KoreLife",
            initials = "UK",
            category = category,
            timeAgo = "Hace un momento",
            content = content,
            likes = 0,
            likedByMe = false,
            comments = emptyList()
        )
        _posts.value = listOf(newPost) + _posts.value
    }

    fun toggleLikePost(postId: String) {
        _posts.value = _posts.value.map { p ->
            if (p.id == postId) {
                val liked = !p.likedByMe
                val likesCount = if (liked) p.likes + 1 else (p.likes - 1).coerceAtLeast(0)
                p.copy(likedByMe = liked, likes = likesCount)
            } else p
        }
    }

    fun addCommentToPost(postId: String, text: String) {
        val comment = PostComment(
            id = "c_${System.currentTimeMillis()}",
            author = "Tú",
            text = text,
            timeAgo = "Ahora"
        )
        _posts.value = _posts.value.map { p ->
            if (p.id == postId) {
                p.copy(comments = p.comments + comment)
            } else p
        }
    }

    fun toggleJoinGroup(groupId: String) {
        _groups.value = _groups.value.map { g ->
            if (g.id == groupId) {
                val joined = !g.isJoined
                val count = if (joined) g.membersCount + 1 else g.membersCount - 1
                g.copy(isJoined = joined, membersCount = count)
            } else g
        }
    }

    fun createSupportTicket(subject: String, category: String) {
        val ticket = SupportTicket(
            id = "tk_${System.currentTimeMillis()}",
            subject = subject,
            category = category,
            status = "Abierto",
            date = dateFormat.format(Date())
        )
        _tickets.value = listOf(ticket) + _tickets.value
    }

    fun setPreferredCurrency(currency: CurrencyCode) {
        _preferredCurrency.value = currency
    }

    fun toggleHideBalances() {
        _hideBalances.value = !_hideBalances.value
    }

    fun toggleAutoUpdateRates() {
        _autoUpdateRates.value = !_autoUpdateRates.value
    }

    fun toggleBiometrics() {
        _biometricsEnabled.value = !_biometricsEnabled.value
    }

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }
}
