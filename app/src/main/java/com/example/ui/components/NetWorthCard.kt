package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RateProvider
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun NetWorthCard(
    totalVes: Double,
    totalUsd: Double,
    activeProvider: RateProvider,
    hideBalances: Boolean,
    onToggleHideBalances: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("net_worth_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = IndigoSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PATRIMONIO TOTAL",
                        color = IndigoSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(IndigoSecondary.copy(alpha = 0.12f))
                            .border(1.dp, IndigoSecondary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Tasa: ${activeProvider.label}",
                            color = IndigoSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(
                        onClick = onToggleHideBalances,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("toggle_privacy_button")
                    ) {
                        Icon(
                            imageVector = if (hideBalances) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Ocultar saldos",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Balance VES
            Text(
                text = if (hideBalances) "••••••••••" else String.format(Locale.US, "Bs. %,.2f", totalVes),
                color = TextWhite,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp
            )

            // Equivalent USD
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)) {
                Text(
                    text = if (hideBalances) "••••••" else String.format(Locale.US, "≈ $ %,.2f", totalUsd),
                    color = EmeraldPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "USD", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            // Health Meter Bar (Fintonic / PocketGuard method)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(DarkSurfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(EmeraldPrimary)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Disponible libre para el mes", color = TextMuted, fontSize = 11.sp)
                Text(text = "75% Saludable ✅", color = EmeraldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
