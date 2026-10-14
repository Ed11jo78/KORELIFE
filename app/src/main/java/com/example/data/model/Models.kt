package com.example.data.model

import com.google.firebase.auth.FirebaseUser


enum class CurrencyCode(val symbol: String, val displayName: String) {
    VES("Bs.", "Bolívares (VES)"),
    USD("$", "Dólares (USD)"),
    EUR("€", "Euros (EUR)")
}

enum class RateProvider(val idName: String, val label: String, val symbolPair: String, val icon: String) {
    BCV_USD("bcv_usd", "BCV Dólar", "USD/VES", "🏛️"),
    BCV_EUR("bcv_eur", "BCV Euro", "EUR/VES", "💶"),
    BINANCE("binance", "Binance P2P", "USDT/VES", "🟡"),
    AIRTM("airtm", "Airtm", "AirUSD/VES", "🔵")
}

data class ExchangeRate(
    val provider: RateProvider,
    val name: String,
    val symbolPair: String,
    val rate: Double,
    val change24h: Double,
    val lastUpdated: String,
    val icon: String
)

enum class AccountType(val title: String) {
    BANK("Banco"),
    CASH("Efectivo"),
    INVESTMENT("Inversión / Crypto"),
    CREDIT("Tarjeta de Crédito")
}

data class Account(
    val id: String,
    val name: String,
    val type: AccountType,
    val balance: Double,
    val currency: CurrencyCode
)

enum class TransactionType(val title: String) {
    EXPENSE("Gasto"),
    INCOME("Ingreso"),
    TRANSFER("Transferencia")
}

data class Transaction(
    val id: String,
    val accountId: String,
    val amount: Double,
    val currency: CurrencyCode,
    val type: TransactionType,
    val category: String,
    val description: String,
    val date: String
)

enum class ThemeMode(val title: String, val subtitle: String, val icon: String) {
    SYSTEM("Sistema", "Sigue la configuración del dispositivo", "📱"),
    LIGHT("Claro", "Modo diurno de alta visibilidad", "☀️"),
    DARK("Oscuro", "Modo nocturno de bajo consumo", "🌙")
}

data class Budget(
    val id: String,
    val category: String,
    val amount: Double,
    val spent: Double,
    val currency: CurrencyCode,
    val period: String = "Mensual"
)

enum class BudgetAlertSeverity {
    WARNING,
    CRITICAL
}

data class BudgetAlert(
    val budgetId: String,
    val category: String,
    val spent: Double,
    val amount: Double,
    val percentage: Int,
    val severity: BudgetAlertSeverity,
    val message: String
)


enum class TransactionSortOrder(val title: String, val icon: String) {
    DATE_DESC("Fecha: Más reciente", "🕒↓"),
    DATE_ASC("Fecha: Más antigua", "🕒↑"),
    AMOUNT_DESC("Monto: Mayor a menor", "💰↓"),
    AMOUNT_ASC("Monto: Menor a mayor", "💰↑")
}

enum class DateRangeFilter(val title: String) {
    ALL("Todos los días"),
    TODAY("Hoy"),
    THIS_WEEK("Esta semana"),
    THIS_MONTH("Este mes"),
    LAST_30_DAYS("Últimos 30 días")
}

data class RecurringExpense(
    val id: String,
    val title: String,
    val amount: Double,
    val currency: CurrencyCode,
    val category: String,
    val dayOfMonth: Int,
    val accountId: String,
    val autoDeduct: Boolean = true,
    val isActive: Boolean = true,
    val lastDeductedMonth: String? = null,
    val notes: String = ""
)

fun getCategoryEmoji(category: String): String {
    return when {
        category.contains("Supermercado", ignoreCase = true) || category.contains("Aliment", ignoreCase = true) -> "🛒"
        category.contains("Comida", ignoreCase = true) || category.contains("Restaurante", ignoreCase = true) || category.contains("Café", ignoreCase = true) -> "🍔"
        category.contains("Transporte", ignoreCase = true) || category.contains("Movilidad", ignoreCase = true) || category.contains("Gasolina", ignoreCase = true) -> "🚌"
        category.contains("Servicio", ignoreCase = true) || category.contains("Luz", ignoreCase = true) || category.contains("Agua", ignoreCase = true) || category.contains("Internet", ignoreCase = true) -> "💡"
        category.contains("Salud", ignoreCase = true) || category.contains("Farmacia", ignoreCase = true) || category.contains("Medicin", ignoreCase = true) -> "💊"
        category.contains("Entretenimiento", ignoreCase = true) || category.contains("Streaming", ignoreCase = true) || category.contains("Cine", ignoreCase = true) -> "🎬"
        category.contains("Compra", ignoreCase = true) || category.contains("Ropa", ignoreCase = true) -> "🛍️"
        category.contains("Educación", ignoreCase = true) || category.contains("Curso", ignoreCase = true) -> "📚"
        category.contains("Tecnología", ignoreCase = true) || category.contains("Software", ignoreCase = true) -> "💻"
        category.contains("Hogar", ignoreCase = true) || category.contains("Alquiler", ignoreCase = true) -> "🏡"
        else -> "🏷️"
    }
}

data class Goal(
    val id: String,
    val title: String,
    val emoji: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val currency: CurrencyCode,
    val deadline: String,
    val colorHex: Long = 0xFF10B981,
    val category: String = "Ahorro General",
    val accountId: String? = null,
    val notes: String = ""
) {
    val progress: Float
        get() = if (targetAmount > 0) (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f) else 0f

    val progressPercent: Int
        get() = (progress * 100).toInt()

    val isCompleted: Boolean
        get() = currentAmount >= targetAmount && targetAmount > 0

    val remainingAmount: Double
        get() = (targetAmount - currentAmount).coerceAtLeast(0.0)
}

data class Subscription(
    val id: String,
    val name: String,
    val emoji: String,
    val amount: Double,
    val currency: CurrencyCode,
    val period: String,
    val nextBilling: String,
    val active: Boolean
)

enum class Priority(val title: String, val colorHex: Long) {
    LOW("Baja", 0xFF64748B),
    MEDIUM("Media", 0xFF3B82F6),
    HIGH("Alta", 0xFFEF4444);

    val iconEmoji: String
        get() = when (this) {
            HIGH -> "🔴"
            MEDIUM -> "🟡"
            LOW -> "⚪"
        }
}

enum class KanbanStatus(val title: String) {
    TODO("Por Hacer"),
    IN_PROGRESS("En Progreso"),
    DONE("Completado")
}

data class KoreTask(
    val id: String,
    val title: String,
    val description: String = "",
    val completed: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val dueDate: String = "",
    val status: KanbanStatus = KanbanStatus.TODO,
    val category: String = "General",
    val completedAt: String? = null
) {
    fun isOverdue(todayStr: String = "2026-10-14"): Boolean {
        if (completed || dueDate.isBlank()) return false
        return dueDate < todayStr && !dueDate.equals("Hoy", ignoreCase = true) && !dueDate.equals("Mañana", ignoreCase = true)
    }

    fun isDueToday(todayStr: String = "2026-10-14"): Boolean {
        if (completed || dueDate.isBlank()) return false
        return dueDate == todayStr || dueDate.equals("Hoy", ignoreCase = true)
    }

    val deadlineBadgeText: String
        get() = when {
            dueDate.isBlank() -> "Sin fecha"
            dueDate.equals("Hoy", ignoreCase = true) -> "Hoy"
            dueDate.equals("Mañana", ignoreCase = true) -> "Mañana"
            else -> dueDate
        }
}

data class Habit(
    val id: String,
    val name: String,
    val streak: Int,
    val completedToday: Boolean,
    val frequency: String = "Diario",
    val emoji: String = "🔥",
    val category: String = "General",
    val bestStreak: Int = streak,
    val weeklyHistory: List<Boolean> = listOf(true, true, true, false, true, true, false),
    val monthlyHistory: Map<Int, Boolean> = mapOf(
        1 to true, 2 to true, 3 to true, 4 to false, 5 to true, 6 to true, 7 to true,
        8 to true, 9 to false, 10 to true, 11 to true, 12 to true, 13 to true, 14 to true
    ),
    val targetDaysPerWeek: Int = 7,
    val notes: String = ""
) {
    val monthlyCompletedCount: Int
        get() = monthlyHistory.values.count { it }

    val monthlyConsistencyPercent: Int
        get() {
            val totalSoFar = 14
            return if (totalSoFar > 0) (monthlyCompletedCount * 100).coerceAtMost(100) else 0
        }

    val streakTierTitle: String
        get() = when {
            streak >= 30 -> "Leyenda Imparable"
            streak >= 21 -> "Rutina de Acero"
            streak >= 14 -> "Hábito Consolidado"
            streak >= 7  -> "Semana Imparable"
            streak >= 3  -> "Racha Encendida"
            streak > 0   -> "Iniciando"
            else         -> "Por empezar"
        }

    val streakBadgeEmoji: String
        get() = when {
            streak >= 30 -> "👑"
            streak >= 21 -> "💎"
            streak >= 14 -> "🥇"
            streak >= 7  -> "🥈"
            streak >= 3  -> "🥉"
            streak > 0   -> "🔥"
            else         -> "🌱"
        }

    val motivationMessage: String
        get() = when {
            streak >= 30 -> "¡Eres una máquina de constancia! Más de 30 días seguidos."
            streak >= 21 -> "¡Hábito totalmente arraigado en tu cerebro!"
            streak >= 14 -> "¡Dos semanas completas sin fallar! Gran disciplina."
            streak >= 7  -> "¡Una semana sólida consecutiva! Mantén el fuego."
            streak >= 3  -> "¡El impulso ya comenzó! No rompas la cadena."
            streak > 0   -> "Buen comienzo, cada día suma para tu mejor versión."
            else         -> "¡Hoy es el mejor día para comenzar tu racha!"
        }
}

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val date: String
)

data class PostComment(
    val id: String,
    val author: String,
    val text: String,
    val timeAgo: String
)

data class CommunityPost(
    val id: String,
    val author: String,
    val initials: String,
    val category: String,
    val timeAgo: String,
    val content: String,
    val likes: Int,
    val likedByMe: Boolean,
    val comments: List<PostComment>
)

data class CommunityGroup(
    val id: String,
    val name: String,
    val membersCount: Int,
    val description: String,
    val category: String,
    val isJoined: Boolean
)

data class SupportTicket(
    val id: String,
    val subject: String,
    val category: String,
    val status: String,
    val date: String
)

val FirebaseUser?.fullName: String
    get() = this?.displayName ?: this?.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "Usuario KoreLife"

val FirebaseUser?.isDemo: Boolean
    get() = this?.isAnonymous == true
