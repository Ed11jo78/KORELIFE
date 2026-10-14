package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrencyCode
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import com.example.data.repository.KoreLifeRepository
import com.example.ui.theme.*
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class CategoryExpense(
    val category: String,
    val totalVes: Double,
    val totalUsd: Double,
    val percentage: Float,
    val color: Color,
    val emoji: String
)

private val ChartColors = listOf(
    Color(0xFF10B981), // Emerald
    Color(0xFF6366F1), // Indigo
    Color(0xFFF59E0B), // Amber
    Color(0xFF06B6D4), // Cyan
    Color(0xFFEC4899), // Pink
    Color(0xFF8B5CF6), // Purple
    Color(0xFFF97316), // Orange
    Color(0xFF14B8A6)  // Teal
)

@Composable
fun ExpensesPieChart(
    transactions: List<Transaction>,
    repository: KoreLifeRepository,
    modifier: Modifier = Modifier
) {
    // 1. Filter expenses
    val expenses = transactions.filter { it.type == TransactionType.EXPENSE }

    // 2. Aggregate by category
    val totalAllVes = expenses.sumOf {
        repository.convert(it.amount, it.currency, CurrencyCode.VES)
    }.coerceAtLeast(1.0)

    val categoryData = remember(transactions) {
        val grouped = expenses.groupBy { it.category }
        val categoryEmojis = mapOf(
            "Supermercado" to "🛒",
            "Comida" to "🍔",
            "Transporte" to "🚌",
            "Servicios" to "💡",
            "Salud" to "💊",
            "Café" to "☕",
            "Compras" to "🛍️",
            "Freelance" to "💻",
            "Nómina" to "💼",
            "Otros" to "📌"
        )

        grouped.entries.mapIndexed { index, entry ->
            val sumVes = entry.value.sumOf {
                repository.convert(it.amount, it.currency, CurrencyCode.VES)
            }
            val sumUsd = repository.convert(sumVes, CurrencyCode.VES, CurrencyCode.USD)
            val pct = (sumVes / totalAllVes).toFloat()
            val color = ChartColors[index % ChartColors.size]
            val emoji = categoryEmojis[entry.key] ?: "🏷️"
            CategoryExpense(entry.key, sumVes, sumUsd, pct, color, emoji)
        }.sortedByDescending { it.totalVes }
    }

    var selectedCategoryIndex by remember { mutableStateOf<Int?>(null) }

    // Animation progress
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(transactions) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("expenses_pie_chart_card"),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Distribución Mensual de Gastos",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Gráfico Circular Interactivo (Recharts Style)",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                if (selectedCategoryIndex != null) {
                    TextButton(
                        onClick = { selectedCategoryIndex = null },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Ver Todos", color = EmeraldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (categoryData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay gastos registrados en el periodo.", color = TextMuted, fontSize = 13.sp)
                }
            } else {
                // Interactive Donut Canvas
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(categoryData) {
                                detectTapGestures { offset ->
                                    val center = Offset(size.width / 2f, size.height / 2f)
                                    val dx = offset.x - center.x
                                    val dy = offset.y - center.y
                                    var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                    if (angle < 0) angle += 360f

                                    // Rotate so 0 starts at -90 (top)
                                    val normalizedAngle = (angle + 90f) % 360f

                                    var currentAngle = 0f
                                    for ((idx, item) in categoryData.withIndex()) {
                                        val sweep = item.percentage * 360f
                                        if (normalizedAngle >= currentAngle && normalizedAngle < currentAngle + sweep) {
                                            selectedCategoryIndex = if (selectedCategoryIndex == idx) null else idx
                                            break
                                        }
                                        currentAngle += sweep
                                    }
                                }
                            }
                    ) {
                        val strokeWidthNormal = 28.dp.toPx()
                        val strokeWidthSelected = 38.dp.toPx()
                        val diameter = size.minDimension - strokeWidthSelected
                        val radius = diameter / 2f
                        val arcSize = Size(diameter, diameter)
                        val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)

                        var startAngle = -90f

                        categoryData.forEachIndexed { index, item ->
                            val isSelected = selectedCategoryIndex == index
                            val strokeWidth = if (isSelected) strokeWidthSelected else strokeWidthNormal
                            val sweepAngle = item.percentage * 360f * animationProgress.value

                            drawArc(
                                color = item.color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle - 2f, // subtle gap
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )

                            startAngle += item.percentage * 360f
                        }
                    }

                    // Center Information Label
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        if (selectedCategoryIndex != null && selectedCategoryIndex!! < categoryData.size) {
                            val selected = categoryData[selectedCategoryIndex!!]
                            Text(
                                text = "${selected.emoji} ${selected.category}",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = String.format(Locale.US, "%.1f%%", selected.percentage * 100),
                                color = selected.color,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = String.format(Locale.US, "Bs. %,.0f", selected.totalVes),
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Text(
                                text = "TOTAL GASTOS",
                                color = TextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = String.format(Locale.US, "Bs. %,.0f", totalAllVes),
                                color = TextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            val totalUsd = repository.convert(totalAllVes, CurrencyCode.VES, CurrencyCode.USD)
                            Text(
                                text = String.format(Locale.US, "≈ $ %,.2f", totalUsd),
                                color = EmeraldPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Legend Chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categoryData.indices.toList()) { idx ->
                        val item = categoryData[idx]
                        val isSelected = selectedCategoryIndex == idx

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) item.color.copy(alpha = 0.25f) else DarkSurfaceVariant)
                                .border(1.dp, if (isSelected) item.color else DarkBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedCategoryIndex = if (selectedCategoryIndex == idx) null else idx
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(item.color)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${item.emoji} ${item.category}",
                                    color = if (isSelected) TextWhite else TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = String.format(Locale.US, "%.0f%%", item.percentage * 100),
                                    color = item.color,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
