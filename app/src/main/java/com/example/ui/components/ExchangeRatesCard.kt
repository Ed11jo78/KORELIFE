package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExchangeRate
import com.example.data.model.RateProvider
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun ExchangeRatesCard(
    rates: Map<RateProvider, ExchangeRate>,
    selectedProvider: RateProvider,
    isLoading: Boolean,
    onSelectProvider: (RateProvider) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by rememberInfiniteTransition(label = "spin").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("exchange_rates_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Tasas de Cambio en Vivo", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF064E3B))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "LIVE", color = EmeraldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.testTag("refresh_rates_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualizar tasas",
                        tint = EmeraldPrimary,
                        modifier = if (isLoading) Modifier.rotate(rotation) else Modifier
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2x2 Grid of Rate Chips
            val providers = listOf(RateProvider.BCV_USD, RateProvider.BINANCE, RateProvider.BCV_EUR, RateProvider.AIRTM)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (row in providers.chunked(2)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (provider in row) {
                            val rate = rates[provider]
                            val isSelected = provider == selectedProvider
                            RateItemChip(
                                rate = rate,
                                isSelected = isSelected,
                                onClick = { onSelectProvider(provider) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RateItemChip(
    rate: ExchangeRate?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) EmeraldPrimary else DarkBorder
    val bgColor = if (isSelected) Color(0xFF064E3B).copy(alpha = 0.4f) else DarkSurfaceVariant

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${rate?.icon ?: "💵"} ${rate?.name ?: ""}",
                    color = if (isSelected) EmeraldPrimary else TextWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                if (rate != null) {
                    val isPositive = rate.change24h >= 0
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (isPositive) EmeraldPrimary else RedError,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = String.format(Locale.US, "%.2f%%", rate.change24h),
                            color = if (isPositive) EmeraldPrimary else RedError,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = String.format(Locale.US, "Bs. %,.2f", rate?.rate ?: 0.0),
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "${rate?.symbolPair ?: ""} • ${rate?.lastUpdated ?: ""}",
                color = TextMuted,
                fontSize = 10.sp
            )
        }
    }
}
