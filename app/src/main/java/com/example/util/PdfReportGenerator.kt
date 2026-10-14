package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.*
import com.example.data.repository.KoreLifeRepository
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

data class MonthlyReportData(
    val year: Int,
    val month: Int, // 1 - 12
    val monthName: String,
    val totalExpensesUsd: Double,
    val totalExpensesVes: Double,
    val totalIncomeUsd: Double,
    val totalIncomeVes: Double,
    val netBalanceUsd: Double,
    val netBalanceVes: Double,
    val categoryExpenses: List<CategoryExpenseSummary>,
    val transactions: List<Transaction>,
    val goals: List<Goal>,
    val recurringExpenses: List<RecurringExpense>,
    val accounts: List<Account>,
    val bcvRate: Double,
    val totalSavingsAccumulatedUsd: Double,
    val totalSavingsAccumulatedVes: Double,
    val totalSavingsTargetUsd: Double,
    val overallGoalsProgressPercent: Int
)

data class CategoryExpenseSummary(
    val category: String,
    val amountUsd: Double,
    val amountVes: Double,
    val count: Int,
    val percentageOfTotal: Float
)

data class ReportExportOptions(
    val includeExpenses: Boolean = true,
    val includeTransactions: Boolean = true,
    val includeGoals: Boolean = true,
    val includeRecurring: Boolean = true
)

object PdfReportGenerator {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN_X = 36f
    private const val USABLE_WIDTH = PAGE_WIDTH - (MARGIN_X * 2)

    private val COLOR_DARK_NAVY = Color.rgb(15, 23, 42) // #0F172A
    private val COLOR_HEADER_BG = Color.rgb(24, 34, 58)
    private val COLOR_EMERALD = Color.rgb(16, 185, 129) // #10B981
    private val COLOR_EMERALD_DARK = Color.rgb(5, 150, 105)
    private val COLOR_ROSE = Color.rgb(225, 29, 72) // #E11D48
    private val COLOR_BLUE = Color.rgb(37, 99, 235) // #2563EB
    private val COLOR_TEXT_PRIMARY = Color.rgb(30, 41, 59)
    private val COLOR_TEXT_MUTED = Color.rgb(100, 116, 139)
    private val COLOR_LIGHT_BG = Color.rgb(248, 250, 252)
    private val COLOR_ALT_ROW = Color.rgb(241, 245, 249)
    private val COLOR_BORDER = Color.rgb(226, 232, 240)

    fun prepareReportData(
        year: Int,
        month: Int,
        transactions: List<Transaction>,
        goals: List<Goal>,
        recurringExpenses: List<RecurringExpense>,
        accounts: List<Account>,
        repository: KoreLifeRepository
    ): MonthlyReportData {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
        val monthName = monthFormat.format(calendar.time).replaceFirstChar { it.uppercase() }

        val monthPrefix = String.format(Locale.US, "%04d-%02d", year, month)
        val monthTransactions = transactions.filter { it.date.startsWith(monthPrefix) }

        val expenseTransactions = monthTransactions.filter { it.type == TransactionType.EXPENSE }
        val incomeTransactions = monthTransactions.filter { it.type == TransactionType.INCOME }

        val totalExpensesUsd = expenseTransactions.sumOf {
            repository.convert(it.amount, it.currency, CurrencyCode.USD)
        }
        val totalExpensesVes = expenseTransactions.sumOf {
            repository.convert(it.amount, it.currency, CurrencyCode.VES)
        }

        val totalIncomeUsd = incomeTransactions.sumOf {
            repository.convert(it.amount, it.currency, CurrencyCode.USD)
        }
        val totalIncomeVes = incomeTransactions.sumOf {
            repository.convert(it.amount, it.currency, CurrencyCode.VES)
        }

        val netBalanceUsd = totalIncomeUsd - totalExpensesUsd
        val netBalanceVes = totalIncomeVes - totalExpensesVes

        // Group expenses by category
        val categoryGroups = expenseTransactions.groupBy { it.category }
        val categorySummaries = categoryGroups.map { (cat, list) ->
            val catUsd = list.sumOf { repository.convert(it.amount, it.currency, CurrencyCode.USD) }
            val catVes = list.sumOf { repository.convert(it.amount, it.currency, CurrencyCode.VES) }
            val pct = if (totalExpensesUsd > 0) (catUsd / totalExpensesUsd).toFloat() * 100f else 0f
            CategoryExpenseSummary(
                category = cat,
                amountUsd = catUsd,
                amountVes = catVes,
                count = list.size,
                percentageOfTotal = pct
            )
        }.sortedByDescending { it.amountUsd }

        val bcvRate = repository.rates.value[RateProvider.BCV_USD]?.rate ?: 36.54

        val totalSavingsAccumulatedUsd = goals.sumOf {
            repository.convert(it.currentAmount, it.currency, CurrencyCode.USD)
        }
        val totalSavingsAccumulatedVes = goals.sumOf {
            repository.convert(it.currentAmount, it.currency, CurrencyCode.VES)
        }
        val totalSavingsTargetUsd = goals.sumOf {
            repository.convert(it.targetAmount, it.currency, CurrencyCode.USD)
        }
        val overallGoalsProgress = if (totalSavingsTargetUsd > 0) {
            ((totalSavingsAccumulatedUsd / totalSavingsTargetUsd) * 100).toInt().coerceIn(0, 100)
        } else 0

        return MonthlyReportData(
            year = year,
            month = month,
            monthName = monthName,
            totalExpensesUsd = totalExpensesUsd,
            totalExpensesVes = totalExpensesVes,
            totalIncomeUsd = totalIncomeUsd,
            totalIncomeVes = totalIncomeVes,
            netBalanceUsd = netBalanceUsd,
            netBalanceVes = netBalanceVes,
            categoryExpenses = categorySummaries,
            transactions = monthTransactions.sortedByDescending { it.date },
            goals = goals,
            recurringExpenses = recurringExpenses.filter { it.isActive },
            accounts = accounts,
            bcvRate = bcvRate,
            totalSavingsAccumulatedUsd = totalSavingsAccumulatedUsd,
            totalSavingsAccumulatedVes = totalSavingsAccumulatedVes,
            totalSavingsTargetUsd = totalSavingsTargetUsd,
            overallGoalsProgressPercent = overallGoalsProgress
        )
    }

    fun generatePdf(
        context: Context,
        data: MonthlyReportData,
        options: ReportExportOptions = ReportExportOptions()
    ): File {
        val pdfDocument = PdfDocument()

        // Page 1: Resumen General, Flujo de Caja, Gastos por Categoría y Recurrentes
        val pageInfo1 = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page1 = pdfDocument.startPage(pageInfo1)
        renderPage1(page1.canvas, data, options)
        pdfDocument.finishPage(page1)

        // Page 2: Portafolio de Metas de Ahorro (con barras visuales) y Movimientos
        val pageInfo2 = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 2).create()
        val page2 = pdfDocument.startPage(pageInfo2)
        renderPage2(page2.canvas, data, options)
        pdfDocument.finishPage(page2)

        // Save to cache directory
        val reportsDir = File(context.cacheDir, "reports")
        if (!reportsDir.exists()) {
            reportsDir.mkdirs()
        }

        val fileName = String.format(
            Locale.US,
            "Resumen_Financiero_KoreLife_%04d_%02d.pdf",
            data.year,
            data.month
        )
        val outputFile = File(reportsDir, fileName)

        FileOutputStream(outputFile).use { fos ->
            pdfDocument.writeTo(fos)
        }
        pdfDocument.close()

        return outputFile
    }

    private fun renderPage1(
        canvas: Canvas,
        data: MonthlyReportData,
        options: ReportExportOptions
    ) {
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = COLOR_BORDER
            strokeWidth = 1f
        }

        // --- 1. Top Header Banner ---
        fillPaint.color = COLOR_HEADER_BG
        fillPaint.style = Paint.Style.FILL
        val headerRect = RectF(MARGIN_X, 28f, MARGIN_X + USABLE_WIDTH, 96f)
        canvas.drawRoundRect(headerRect, 12f, 12f, fillPaint)

        // Brand
        textPaint.color = COLOR_EMERALD
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 18f
        canvas.drawText("KORELIFE", MARGIN_X + 16f, 56f, textPaint)

        textPaint.color = Color.WHITE
        textPaint.textSize = 9f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("CENTRO FINANCIERO Y CONTROL DE METAS", MARGIN_X + 16f, 72f, textPaint)

        // Period Badge on Right
        val badgeWidth = 140f
        val badgeHeight = 36f
        val badgeRight = MARGIN_X + USABLE_WIDTH - 16f
        val badgeLeft = badgeRight - badgeWidth
        val badgeRect = RectF(badgeLeft, 44f, badgeRight, 44f + badgeHeight)
        fillPaint.color = Color.argb(45, 255, 255, 255)
        canvas.drawRoundRect(badgeRect, 8f, 8f, fillPaint)

        textPaint.color = Color.WHITE
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 11f
        textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText(data.monthName, badgeRect.centerX(), 60f, textPaint)

        textPaint.textSize = 7.5f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.color = Color.rgb(203, 213, 225)
        canvas.drawText("RESPALDO MENSUAL", badgeRect.centerX(), 72f, textPaint)
        textPaint.textAlign = Paint.Align.LEFT

        // --- 2. Title & Metadata ---
        var currentY = 118f
        textPaint.color = COLOR_DARK_NAVY
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 14f
        canvas.drawText("RESUMEN MENSUAL DE GASTOS Y FLUJO DE CAJA", MARGIN_X, currentY, textPaint)

        val currentDateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        textPaint.color = COLOR_TEXT_MUTED
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.textSize = 8.5f
        currentY += 14f
        canvas.drawText(
            "Fecha de emisión: $currentDateStr  •  Tasa de cambio referencial: Bs. ${String.format(Locale.US, "%.2f", data.bcvRate)} / USD",
            MARGIN_X,
            currentY,
            textPaint
        )

        currentY += 10f
        canvas.drawLine(MARGIN_X, currentY, MARGIN_X + USABLE_WIDTH, currentY, strokePaint)

        // --- 3. 3-KPI Overview Cards ---
        currentY += 14f
        val cardSpacing = 10f
        val cardWidth = (USABLE_WIDTH - (cardSpacing * 2)) / 3f
        val cardHeight = 62f

        // Card 1: Gastos Totales
        drawKpiCard(
            canvas = canvas,
            left = MARGIN_X,
            top = currentY,
            width = cardWidth,
            height = cardHeight,
            label = "GASTOS TOTALES",
            amountUsd = data.totalExpensesUsd,
            amountVes = data.totalExpensesVes,
            accentColor = COLOR_ROSE,
            textPaint = textPaint,
            fillPaint = fillPaint
        )

        // Card 2: Ingresos Totales
        drawKpiCard(
            canvas = canvas,
            left = MARGIN_X + cardWidth + cardSpacing,
            top = currentY,
            width = cardWidth,
            height = cardHeight,
            label = "INGRESOS TOTALES",
            amountUsd = data.totalIncomeUsd,
            amountVes = data.totalIncomeVes,
            accentColor = COLOR_EMERALD_DARK,
            textPaint = textPaint,
            fillPaint = fillPaint
        )

        // Card 3: Balance Neto
        drawKpiCard(
            canvas = canvas,
            left = MARGIN_X + (cardWidth * 2) + (cardSpacing * 2),
            top = currentY,
            width = cardWidth,
            height = cardHeight,
            label = if (data.netBalanceUsd >= 0) "SUPERÁVIT NETO" else "DÉFICIT NETO",
            amountUsd = data.netBalanceUsd,
            amountVes = data.netBalanceVes,
            accentColor = if (data.netBalanceUsd >= 0) COLOR_BLUE else COLOR_ROSE,
            textPaint = textPaint,
            fillPaint = fillPaint
        )

        currentY += cardHeight + 20f

        // --- 4. Desglose de Gastos por Categoría ---
        textPaint.color = COLOR_DARK_NAVY
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 11.5f
        canvas.drawText("1. Desglose de Gastos por Categoría", MARGIN_X, currentY, textPaint)

        currentY += 14f
        // Table Header
        val colCatW = 150f
        val colCountW = 65f
        val colUsdW = 100f
        val colVesW = 120f
        val colPctW = USABLE_WIDTH - (colCatW + colCountW + colUsdW + colVesW)

        fillPaint.color = COLOR_DARK_NAVY
        canvas.drawRoundRect(RectF(MARGIN_X, currentY, MARGIN_X + USABLE_WIDTH, currentY + 20f), 4f, 4f, fillPaint)

        textPaint.color = Color.WHITE
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 8.5f
        canvas.drawText("Categoría", MARGIN_X + 8f, currentY + 13f, textPaint)
        canvas.drawText("Movimientos", MARGIN_X + colCatW + 6f, currentY + 13f, textPaint)
        canvas.drawText("Total (USD)", MARGIN_X + colCatW + colCountW + 6f, currentY + 13f, textPaint)
        canvas.drawText("Total (VES)", MARGIN_X + colCatW + colCountW + colUsdW + 6f, currentY + 13f, textPaint)
        canvas.drawText("% Total", MARGIN_X + colCatW + colCountW + colUsdW + colVesW + 6f, currentY + 13f, textPaint)

        currentY += 22f

        if (data.categoryExpenses.isEmpty()) {
            fillPaint.color = COLOR_LIGHT_BG
            canvas.drawRect(RectF(MARGIN_X, currentY, MARGIN_X + USABLE_WIDTH, currentY + 26f), fillPaint)
            textPaint.color = COLOR_TEXT_MUTED
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            textPaint.textSize = 8.5f
            canvas.drawText("No se registraron gastos en este período.", MARGIN_X + 12f, currentY + 16f, textPaint)
            currentY += 30f
        } else {
            val topCategories = data.categoryExpenses.take(7)
            topCategories.forEachIndexed { index, cat ->
                val rowHeight = 20f
                fillPaint.color = if (index % 2 == 0) COLOR_LIGHT_BG else COLOR_ALT_ROW
                canvas.drawRect(RectF(MARGIN_X, currentY, MARGIN_X + USABLE_WIDTH, currentY + rowHeight), fillPaint)

                // Category name
                textPaint.color = COLOR_TEXT_PRIMARY
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textPaint.textSize = 8.5f
                val emoji = getCategoryEmoji(cat.category)
                canvas.drawText("$emoji ${cat.category}", MARGIN_X + 8f, currentY + 13f, textPaint)

                // Count
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textPaint.color = COLOR_TEXT_MUTED
                canvas.drawText("${cat.count} mov.", MARGIN_X + colCatW + 6f, currentY + 13f, textPaint)

                // USD
                textPaint.color = COLOR_DARK_NAVY
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(String.format(Locale.US, "$ %,.2f", cat.amountUsd), MARGIN_X + colCatW + colCountW + 6f, currentY + 13f, textPaint)

                // VES
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textPaint.color = COLOR_TEXT_MUTED
                canvas.drawText(String.format(Locale.US, "Bs. %,.2f", cat.amountVes), MARGIN_X + colCatW + colCountW + colUsdW + 6f, currentY + 13f, textPaint)

                // % and bar
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textPaint.color = COLOR_EMERALD_DARK
                canvas.drawText(String.format(Locale.US, "%.1f%%", cat.percentageOfTotal), MARGIN_X + colCatW + colCountW + colUsdW + colVesW + 6f, currentY + 13f, textPaint)

                currentY += rowHeight
            }

            // Total Category Row
            fillPaint.color = Color.rgb(226, 232, 240)
            canvas.drawRect(RectF(MARGIN_X, currentY, MARGIN_X + USABLE_WIDTH, currentY + 22f), fillPaint)
            textPaint.color = COLOR_DARK_NAVY
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textPaint.textSize = 8.5f
            canvas.drawText("TOTAL GASTOS DEL MES", MARGIN_X + 8f, currentY + 14f, textPaint)
            canvas.drawText("${data.categoryExpenses.sumOf { it.count }} mov.", MARGIN_X + colCatW + 6f, currentY + 14f, textPaint)
            canvas.drawText(String.format(Locale.US, "$ %,.2f", data.totalExpensesUsd), MARGIN_X + colCatW + colCountW + 6f, currentY + 14f, textPaint)
            canvas.drawText(String.format(Locale.US, "Bs. %,.2f", data.totalExpensesVes), MARGIN_X + colCatW + colCountW + colUsdW + 6f, currentY + 14f, textPaint)
            canvas.drawText("100.0%", MARGIN_X + colCatW + colCountW + colUsdW + colVesW + 6f, currentY + 14f, textPaint)
            currentY += 28f
        }

        // --- 5. Gastos Recurrentes / Compromisos Mensuales ---
        currentY += 10f
        textPaint.color = COLOR_DARK_NAVY
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 11.5f
        canvas.drawText("2. Compromisos Fijos y Gastos Recurrentes Programados", MARGIN_X, currentY, textPaint)

        currentY += 14f
        fillPaint.color = COLOR_HEADER_BG
        canvas.drawRoundRect(RectF(MARGIN_X, currentY, MARGIN_X + USABLE_WIDTH, currentY + 20f), 4f, 4f, fillPaint)

        val recCol1 = 160f
        val recCol2 = 60f
        val recCol3 = 120f
        val recCol4 = 90f

        textPaint.color = Color.WHITE
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 8.5f
        canvas.drawText("Servicio / Compromiso", MARGIN_X + 8f, currentY + 13f, textPaint)
        canvas.drawText("Día Cobro", MARGIN_X + recCol1 + 6f, currentY + 13f, textPaint)
        canvas.drawText("Categoría", MARGIN_X + recCol1 + recCol2 + 6f, currentY + 13f, textPaint)
        canvas.drawText("Modalidad", MARGIN_X + recCol1 + recCol2 + recCol3 + 6f, currentY + 13f, textPaint)
        canvas.drawText("Importe", MARGIN_X + recCol1 + recCol2 + recCol3 + recCol4 + 6f, currentY + 13f, textPaint)

        currentY += 22f

        if (data.recurringExpenses.isEmpty()) {
            fillPaint.color = COLOR_LIGHT_BG
            canvas.drawRect(RectF(MARGIN_X, currentY, MARGIN_X + USABLE_WIDTH, currentY + 24f), fillPaint)
            textPaint.color = COLOR_TEXT_MUTED
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            textPaint.textSize = 8.5f
            canvas.drawText("No hay gastos recurrentes registrados activos.", MARGIN_X + 12f, currentY + 15f, textPaint)
            currentY += 28f
        } else {
            val visibleRecurring = data.recurringExpenses.take(5)
            visibleRecurring.forEachIndexed { index, item ->
                val rowHeight = 20f
                fillPaint.color = if (index % 2 == 0) COLOR_LIGHT_BG else COLOR_ALT_ROW
                canvas.drawRect(RectF(MARGIN_X, currentY, MARGIN_X + USABLE_WIDTH, currentY + rowHeight), fillPaint)

                textPaint.color = COLOR_TEXT_PRIMARY
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textPaint.textSize = 8.5f
                canvas.drawText(item.title, MARGIN_X + 8f, currentY + 13f, textPaint)

                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textPaint.color = COLOR_TEXT_MUTED
                canvas.drawText("Día ${item.dayOfMonth}", MARGIN_X + recCol1 + 6f, currentY + 13f, textPaint)
                canvas.drawText(item.category, MARGIN_X + recCol1 + recCol2 + 6f, currentY + 13f, textPaint)
                canvas.drawText(if (item.autoDeduct) "Auto-débito" else "Recordatorio", MARGIN_X + recCol1 + recCol2 + recCol3 + 6f, currentY + 13f, textPaint)

                textPaint.color = COLOR_ROSE
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                val currSym = if (item.currency == CurrencyCode.USD) "$" else "Bs."
                canvas.drawText(String.format(Locale.US, "$currSym %,.2f", item.amount), MARGIN_X + recCol1 + recCol2 + recCol3 + recCol4 + 6f, currentY + 13f, textPaint)

                currentY += rowHeight
            }
        }

        // --- 6. Resumen de Cuentas / Posición de Liquidez ---
        currentY += 14f
        textPaint.color = COLOR_DARK_NAVY
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 11.5f
        canvas.drawText("3. Cuentas y Liquidez al Cierre", MARGIN_X, currentY, textPaint)

        currentY += 12f
        val accBoxHeight = 38f
        val accRect = RectF(MARGIN_X, currentY, MARGIN_X + USABLE_WIDTH, currentY + accBoxHeight)
        fillPaint.color = COLOR_LIGHT_BG
        canvas.drawRoundRect(accRect, 6f, 6f, fillPaint)
        strokePaint.color = COLOR_BORDER
        canvas.drawRoundRect(accRect, 6f, 6f, strokePaint)

        textPaint.textSize = 8.5f
        textPaint.color = COLOR_TEXT_PRIMARY
        var accX = MARGIN_X + 10f
        data.accounts.take(4).forEach { acc ->
            val sym = if (acc.currency == CurrencyCode.USD) "$" else "Bs."
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(acc.name, accX, currentY + 15f, textPaint)
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textPaint.color = COLOR_TEXT_MUTED
            canvas.drawText(String.format(Locale.US, "$sym %,.2f", acc.balance), accX, currentY + 28f, textPaint)
            textPaint.color = COLOR_TEXT_PRIMARY
            accX += 130f
        }

        // --- Page 1 Footer ---
        drawPageFooter(canvas, 1, 2, textPaint, strokePaint)
    }

    private fun renderPage2(
        canvas: Canvas,
        data: MonthlyReportData,
        options: ReportExportOptions
    ) {
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = COLOR_BORDER
            strokeWidth = 1f
        }

        // Mini Header
        fillPaint.color = COLOR_HEADER_BG
        fillPaint.style = Paint.Style.FILL
        val miniHeaderRect = RectF(MARGIN_X, 28f, MARGIN_X + USABLE_WIDTH, 62f)
        canvas.drawRoundRect(miniHeaderRect, 8f, 8f, fillPaint)

        textPaint.color = COLOR_EMERALD
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 12f
        canvas.drawText("KORELIFE", MARGIN_X + 14f, 48f, textPaint)

        textPaint.color = Color.WHITE
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.textSize = 9.5f
        canvas.drawText("•  Metas de Ahorro y Detalle de Movimientos  •  ${data.monthName}", MARGIN_X + 75f, 48f, textPaint)

        var currentY = 82f

        // --- SECTION 4: METAS DE AHORRO ---
        textPaint.color = COLOR_DARK_NAVY
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 12f
        canvas.drawText("4. Portafolio de Metas de Ahorro y Progreso Visual", MARGIN_X, currentY, textPaint)

        currentY += 12f

        // Portfolio metrics summary card
        val summaryCardH = 46f
        val summaryRect = RectF(MARGIN_X, currentY, MARGIN_X + USABLE_WIDTH, currentY + summaryCardH)
        fillPaint.color = Color.rgb(236, 253, 245) // Mint tint
        canvas.drawRoundRect(summaryRect, 8f, 8f, fillPaint)
        strokePaint.color = Color.rgb(167, 243, 208)
        canvas.drawRoundRect(summaryRect, 8f, 8f, strokePaint)

        textPaint.color = COLOR_EMERALD_DARK
        textPaint.textSize = 8f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("TOTAL AHORRADO EN METAS", MARGIN_X + 14f, currentY + 16f, textPaint)
        textPaint.textSize = 13f
        canvas.drawText(String.format(Locale.US, "$ %,.2f", data.totalSavingsAccumulatedUsd), MARGIN_X + 14f, currentY + 34f, textPaint)

        val col2X = MARGIN_X + 170f
        textPaint.textSize = 8f
        textPaint.color = COLOR_TEXT_MUTED
        canvas.drawText("OBJETIVO PROYECTADO", col2X, currentY + 16f, textPaint)
        textPaint.textSize = 13f
        textPaint.color = COLOR_DARK_NAVY
        canvas.drawText(String.format(Locale.US, "$ %,.2f", data.totalSavingsTargetUsd), col2X, currentY + 34f, textPaint)

        val col3X = MARGIN_X + 340f
        textPaint.textSize = 8f
        textPaint.color = COLOR_TEXT_MUTED
        canvas.drawText("AVANCE GLOBAL", col3X, currentY + 16f, textPaint)
        textPaint.textSize = 13f
        textPaint.color = COLOR_EMERALD_DARK
        canvas.drawText("${data.overallGoalsProgressPercent}% Alcanzado", col3X, currentY + 34f, textPaint)

        currentY += summaryCardH + 14f

        if (data.goals.isEmpty()) {
            fillPaint.color = COLOR_LIGHT_BG
            canvas.drawRect(RectF(MARGIN_X, currentY, MARGIN_X + USABLE_WIDTH, currentY + 30f), fillPaint)
            textPaint.color = COLOR_TEXT_MUTED
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            textPaint.textSize = 9f
            canvas.drawText("No se han configurado metas de ahorro activas aún.", MARGIN_X + 12f, currentY + 18f, textPaint)
            currentY += 40f
        } else {
            data.goals.forEach { goal ->
                val goalCardHeight = 48f
                val goalRect = RectF(MARGIN_X, currentY, MARGIN_X + USABLE_WIDTH, currentY + goalCardHeight)

                fillPaint.color = COLOR_LIGHT_BG
                canvas.drawRoundRect(goalRect, 6f, 6f, fillPaint)
                strokePaint.color = COLOR_BORDER
                canvas.drawRoundRect(goalRect, 6f, 6f, strokePaint)

                // Goal Header Line
                textPaint.color = COLOR_DARK_NAVY
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textPaint.textSize = 9.5f
                val goalTitle = "${goal.emoji} ${goal.title} (${goal.category})"
                canvas.drawText(goalTitle, MARGIN_X + 10f, currentY + 16f, textPaint)

                // Target vs Current and remaining
                val currSym = if (goal.currency == CurrencyCode.USD) "$" else "Bs."
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textPaint.textSize = 8.5f
                textPaint.color = COLOR_TEXT_MUTED
                val statsText = "Ahorrado: $currSym ${String.format(Locale.US, "%,.2f", goal.currentAmount)} de $currSym ${String.format(Locale.US, "%,.2f", goal.targetAmount)}  •  Faltan: $currSym ${String.format(Locale.US, "%,.2f", goal.remainingAmount)}"
                canvas.drawText(statsText, MARGIN_X + 10f, currentY + 28f, textPaint)

                // Status Badge on Right
                val statusText = if (goal.isCompleted) "¡COMPLETADA! 🏆" else "${goal.progressPercent}%"
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textPaint.color = if (goal.isCompleted) COLOR_EMERALD_DARK else Color.rgb(37, 99, 235)
                canvas.drawText(statusText, MARGIN_X + USABLE_WIDTH - 75f, currentY + 20f, textPaint)

                // VISUAL PROGRESS BAR (MANDATORY REQUIREMENT)
                val barLeft = MARGIN_X + 10f
                val barTop = currentY + 34f
                val barWidth = USABLE_WIDTH - 90f
                val barHeight = 6f

                // Background track
                fillPaint.color = Color.rgb(226, 232, 240)
                canvas.drawRoundRect(RectF(barLeft, barTop, barLeft + barWidth, barTop + barHeight), 3f, 3f, fillPaint)

                // Progress fill
                val fillWidth = (barWidth * goal.progress).coerceIn(0f, barWidth)
                fillPaint.color = if (goal.isCompleted) COLOR_EMERALD else goal.colorHex.toInt()
                if (fillWidth > 0f) {
                    canvas.drawRoundRect(RectF(barLeft, barTop, barLeft + fillWidth, barTop + barHeight), 3f, 3f, fillPaint)
                }

                // Deadline date
                textPaint.color = COLOR_TEXT_MUTED
                textPaint.textSize = 7.5f
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                canvas.drawText("Límite: ${goal.deadline}", MARGIN_X + USABLE_WIDTH - 75f, currentY + 39f, textPaint)

                currentY += goalCardHeight + 8f
            }
        }

        // --- SECTION 5: HISTORIAL DETALLADO DE TRANSACCIONES ---
        currentY += 10f
        textPaint.color = COLOR_DARK_NAVY
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 12f
        canvas.drawText("5. Movimientos Registrados en el Período", MARGIN_X, currentY, textPaint)

        currentY += 12f
        fillPaint.color = COLOR_DARK_NAVY
        canvas.drawRoundRect(RectF(MARGIN_X, currentY, MARGIN_X + USABLE_WIDTH, currentY + 18f), 4f, 4f, fillPaint)

        val txCol1 = 65f   // Fecha
        val txCol2 = 180f  // Descripción
        val txCol3 = 110f  // Categoría
        val txCol4 = 85f   // Tipo / Cuenta

        textPaint.color = Color.WHITE
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 8f
        canvas.drawText("Fecha", MARGIN_X + 8f, currentY + 12f, textPaint)
        canvas.drawText("Descripción", MARGIN_X + txCol1 + 6f, currentY + 12f, textPaint)
        canvas.drawText("Categoría", MARGIN_X + txCol1 + txCol2 + 6f, currentY + 12f, textPaint)
        canvas.drawText("Tipo", MARGIN_X + txCol1 + txCol2 + txCol3 + 6f, currentY + 12f, textPaint)
        canvas.drawText("Monto", MARGIN_X + txCol1 + txCol2 + txCol3 + txCol4 + 6f, currentY + 12f, textPaint)

        currentY += 20f

        val visibleTransactions = data.transactions.take(13)
        if (visibleTransactions.isEmpty()) {
            fillPaint.color = COLOR_LIGHT_BG
            canvas.drawRect(RectF(MARGIN_X, currentY, MARGIN_X + USABLE_WIDTH, currentY + 22f), fillPaint)
            textPaint.color = COLOR_TEXT_MUTED
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            textPaint.textSize = 8f
            canvas.drawText("No se encontraron transacciones registradas en este período.", MARGIN_X + 12f, currentY + 14f, textPaint)
        } else {
            visibleTransactions.forEachIndexed { index, tx ->
                val rowHeight = 17f
                fillPaint.color = if (index % 2 == 0) COLOR_LIGHT_BG else COLOR_ALT_ROW
                canvas.drawRect(RectF(MARGIN_X, currentY, MARGIN_X + USABLE_WIDTH, currentY + rowHeight), fillPaint)

                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textPaint.textSize = 7.5f
                textPaint.color = COLOR_TEXT_MUTED
                canvas.drawText(tx.date, MARGIN_X + 8f, currentY + 11.5f, textPaint)

                textPaint.color = COLOR_TEXT_PRIMARY
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                val safeDesc = if (tx.description.length > 28) tx.description.take(26) + "…" else tx.description
                canvas.drawText(safeDesc, MARGIN_X + txCol1 + 6f, currentY + 11.5f, textPaint)

                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textPaint.color = COLOR_TEXT_MUTED
                canvas.drawText(tx.category, MARGIN_X + txCol1 + txCol2 + 6f, currentY + 11.5f, textPaint)

                canvas.drawText(tx.type.title, MARGIN_X + txCol1 + txCol2 + txCol3 + 6f, currentY + 11.5f, textPaint)

                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                val sym = if (tx.currency == CurrencyCode.USD) "$" else "Bs."
                textPaint.color = when (tx.type) {
                    TransactionType.EXPENSE -> COLOR_ROSE
                    TransactionType.INCOME -> COLOR_EMERALD_DARK
                    TransactionType.TRANSFER -> COLOR_BLUE
                }
                val prefix = if (tx.type == TransactionType.INCOME) "+" else "-"
                canvas.drawText("$prefix $sym ${String.format(Locale.US, "%,.2f", tx.amount)}", MARGIN_X + txCol1 + txCol2 + txCol3 + txCol4 + 6f, currentY + 11.5f, textPaint)

                currentY += rowHeight
            }
        }

        // Page 2 Footer
        drawPageFooter(canvas, 2, 2, textPaint, strokePaint)
    }

    private fun drawKpiCard(
        canvas: Canvas,
        left: Float,
        top: Float,
        width: Float,
        height: Float,
        label: String,
        amountUsd: Double,
        amountVes: Double,
        accentColor: Int,
        textPaint: Paint,
        fillPaint: Paint
    ) {
        val rect = RectF(left, top, left + width, top + height)
        fillPaint.color = COLOR_LIGHT_BG
        canvas.drawRoundRect(rect, 8f, 8f, fillPaint)

        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = COLOR_BORDER
            strokeWidth = 1f
        }
        canvas.drawRoundRect(rect, 8f, 8f, strokePaint)

        // Accent indicator bar on left
        val accentRect = RectF(left, top, left + 4f, top + height)
        fillPaint.color = accentColor
        canvas.drawRoundRect(accentRect, 4f, 4f, fillPaint)

        // Label
        textPaint.color = COLOR_TEXT_MUTED
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 7.5f
        canvas.drawText(label, left + 10f, top + 15f, textPaint)

        // USD Amount
        textPaint.color = COLOR_DARK_NAVY
        textPaint.textSize = 12f
        canvas.drawText(String.format(Locale.US, "$ %,.2f", amountUsd), left + 10f, top + 34f, textPaint)

        // VES Amount
        textPaint.color = COLOR_TEXT_MUTED
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.textSize = 8.5f
        canvas.drawText(String.format(Locale.US, "Bs. %,.2f", amountVes), left + 10f, top + 49f, textPaint)
    }

    private fun drawPageFooter(
        canvas: Canvas,
        currentPage: Int,
        totalPages: Int,
        textPaint: Paint,
        strokePaint: Paint
    ) {
        val footerY = PAGE_HEIGHT - 32f
        canvas.drawLine(MARGIN_X, footerY - 8f, MARGIN_X + USABLE_WIDTH, footerY - 8f, strokePaint)

        textPaint.color = COLOR_TEXT_MUTED
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.textSize = 8f
        canvas.drawText("KoreLife Life OS • Respaldo financiero y metas de ahorro personal confidencial", MARGIN_X, footerY + 6f, textPaint)

        textPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Página $currentPage de $totalPages", MARGIN_X + USABLE_WIDTH, footerY + 6f, textPaint)
        textPaint.textAlign = Paint.Align.LEFT
    }

    fun sharePdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Resumen Financiero KoreLife - ${file.name}")
            putExtra(Intent.EXTRA_TEXT, "Adjunto el resumen mensual de gastos y metas de ahorro generado desde KoreLife.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(sendIntent, "Exportar y Compartir Resumen PDF")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    fun viewPdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            val chooser = Intent.createChooser(viewIntent, "Abrir Resumen Financiero PDF")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            sharePdf(context, file)
        }
    }

    fun copyFileToUri(context: Context, file: File, targetUri: Uri): Boolean {
        return try {
            context.contentResolver.openOutputStream(targetUri)?.use { output ->
                file.inputStream().use { input ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
