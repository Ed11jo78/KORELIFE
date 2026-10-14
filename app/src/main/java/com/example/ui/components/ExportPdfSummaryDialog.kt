package com.example.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Account
import com.example.data.model.Goal
import com.example.data.model.RecurringExpense
import com.example.data.model.Transaction
import com.example.data.repository.KoreLifeRepository
import com.example.ui.theme.*
import com.example.util.PdfReportGenerator
import com.example.util.ReportExportOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

@Composable
fun ExportPdfSummaryDialog(
    transactions: List<Transaction>,
    goals: List<Goal>,
    recurringExpenses: List<RecurringExpense>,
    accounts: List<Account>,
    repository: KoreLifeRepository,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val calendar = remember { Calendar.getInstance() }
    var selectedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH) + 1) } // 1 - 12

    val monthsList = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    var includeExpenses by remember { mutableStateOf(true) }
    var includeGoals by remember { mutableStateOf(true) }
    var includeTransactions by remember { mutableStateOf(true) }
    var includeRecurring by remember { mutableStateOf(true) }

    var isGenerating by remember { mutableStateOf(false) }
    var generatedFile by remember { mutableStateOf<File?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    // Live preview report data for the selected period
    val previewData = remember(selectedYear, selectedMonth, transactions, goals, recurringExpenses, accounts) {
        PdfReportGenerator.prepareReportData(
            year = selectedYear,
            month = selectedMonth,
            transactions = transactions,
            goals = goals,
            recurringExpenses = recurringExpenses,
            accounts = accounts,
            repository = repository
        )
    }

    // Document creation launcher to save to device
    val createDocLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        if (uri != null && generatedFile != null) {
            coroutineScope.launch {
                val success = withContext(Dispatchers.IO) {
                    PdfReportGenerator.copyFileToUri(context, generatedFile!!, uri)
                }
                if (success) {
                    Toast.makeText(context, "Resumen PDF guardado en el dispositivo correctamente", Toast.LENGTH_LONG).show()
                    statusMessage = "Archivo guardado exitosamente en tus documentos."
                } else {
                    Toast.makeText(context, "Error al guardar el archivo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("export_pdf_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF831843).copy(alpha = 0.35f))
                                .border(1.dp, Color(0xFFF43F5E), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = Color(0xFFF43F5E),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Exportar Resumen PDF",
                                color = TextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Respaldo externo mensual de finanzas y metas",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("export_pdf_btn_close")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextMuted)
                    }
                }

                HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 12.dp))

                // Scrollable Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Year and Month Selector
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "1. Selecciona el Período Mensual",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Year selector chips
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(2025, 2026, 2027).forEach { year ->
                                    val isYearSelected = selectedYear == year
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isYearSelected) EmeraldPrimary else DarkSurfaceVariant)
                                            .clickable {
                                                selectedYear = year
                                                generatedFile = null
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "$year",
                                            color = if (isYearSelected) EmeraldOnPrimary else TextWhite,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Months LazyRow
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(monthsList) { index, name ->
                                val monthNum = index + 1
                                val isSelected = selectedMonth == monthNum
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) EmeraldPrimary else DarkSurfaceVariant)
                                        .border(1.dp, if (isSelected) EmeraldPrimary else DarkBorder, RoundedCornerShape(10.dp))
                                        .clickable {
                                            selectedMonth = monthNum
                                            generatedFile = null
                                        }
                                        .padding(horizontal = 12.dp, vertical = 7.dp)
                                        .testTag("month_chip_${monthNum}")
                                ) {
                                    Text(
                                        text = name,
                                        color = if (isSelected) EmeraldOnPrimary else TextWhite,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    // Live Preview of Data to be Exported
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Vista Previa • ${previewData.monthName}",
                                    color = EmeraldPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "2 Páginas A4",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Gastos del Mes", color = TextMuted, fontSize = 10.sp)
                                    Text(
                                        text = String.format(Locale.US, "$ %,.2f", previewData.totalExpensesUsd),
                                        color = RedError,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = String.format(Locale.US, "Bs. %,.2f", previewData.totalExpensesVes),
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }

                                Column {
                                    Text("Metas de Ahorro", color = TextMuted, fontSize = 10.sp)
                                    Text(
                                        text = String.format(Locale.US, "$ %,.2f", previewData.totalSavingsAccumulatedUsd),
                                        color = EmeraldPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${previewData.overallGoalsProgressPercent}% completado",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }

                                Column {
                                    Text("Movimientos", color = TextMuted, fontSize = 10.sp)
                                    Text(
                                        text = "${previewData.transactions.size} reg.",
                                        color = TextWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${previewData.goals.size} metas activas",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    // Report Content Checkboxes
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurfaceVariant)
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "2. Secciones a Incluir en el PDF",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        ExportSectionToggle(
                            title = "Resumen de Flujo de Caja y Gastos por Categoría",
                            subtitle = "Métricas de balance, desglose de categorías y porcentajes",
                            checked = includeExpenses,
                            onCheckedChange = { includeExpenses = it }
                        )

                        ExportSectionToggle(
                            title = "Portafolio de Metas de Ahorro con Barras de Progreso",
                            subtitle = "Muestra barras visuales, montos acumulados y metas fijadas",
                            checked = includeGoals,
                            onCheckedChange = { includeGoals = it }
                        )

                        ExportSectionToggle(
                            title = "Compromisos y Gastos Recurrentes Programados",
                            subtitle = "Listado de débitos y pagos fijos configurados del mes",
                            checked = includeRecurring,
                            onCheckedChange = { includeRecurring = it }
                        )

                        ExportSectionToggle(
                            title = "Historial Detallado de Transacciones del Mes",
                            subtitle = "Registro de fecha, categoría, descripción y cuenta",
                            checked = includeTransactions,
                            onCheckedChange = { includeTransactions = it }
                        )
                    }

                    // Status Banner when generated
                    AnimatedVisibility(visible = generatedFile != null || statusMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF064E3B).copy(alpha = 0.6f))
                                .border(1.dp, EmeraldPrimary, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = statusMessage ?: "¡Documento PDF listo! Puedes visualizarlo, compartirlo o guardarlo externamente.",
                                    color = TextWhite,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 12.dp))

                // Action Buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Primary Action: Generar y Compartir PDF
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isGenerating = true
                                statusMessage = null
                                try {
                                    val file = withContext(Dispatchers.IO) {
                                        PdfReportGenerator.generatePdf(
                                            context = context,
                                            data = previewData,
                                            options = ReportExportOptions(
                                                includeExpenses = includeExpenses,
                                                includeTransactions = includeTransactions,
                                                includeGoals = includeGoals,
                                                includeRecurring = includeRecurring
                                            )
                                        )
                                    }
                                    generatedFile = file
                                    PdfReportGenerator.sharePdf(context, file)
                                    statusMessage = "PDF generado y preparado para compartir."
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Toast.makeText(context, "Error al generar PDF: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isGenerating = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_generate_share_pdf"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = EmeraldOnPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isGenerating
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = EmeraldOnPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Generando PDF...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generar y Compartir PDF", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Secondary Action Row: Guardar en Dispositivo y Ver PDF
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Guardar en Dispositivo
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    isGenerating = true
                                    try {
                                        val file = withContext(Dispatchers.IO) {
                                            PdfReportGenerator.generatePdf(
                                                context = context,
                                                data = previewData,
                                                options = ReportExportOptions(
                                                    includeExpenses = includeExpenses,
                                                    includeTransactions = includeTransactions,
                                                    includeGoals = includeGoals,
                                                    includeRecurring = includeRecurring
                                                )
                                            )
                                        }
                                        generatedFile = file
                                        val defaultFileName = String.format(
                                            Locale.US,
                                            "Resumen_Financiero_KoreLife_%04d_%02d.pdf",
                                            selectedYear,
                                            selectedMonth
                                        )
                                        createDocLauncher.launch(defaultFileName)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isGenerating = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_save_pdf_device"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary),
                            enabled = !isGenerating
                        ) {
                            Icon(Icons.Default.SaveAlt, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Guardar Archivo", color = EmeraldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // Ver PDF
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    isGenerating = true
                                    try {
                                        val file = generatedFile ?: withContext(Dispatchers.IO) {
                                            PdfReportGenerator.generatePdf(
                                                context = context,
                                                data = previewData,
                                                options = ReportExportOptions(
                                                    includeExpenses = includeExpenses,
                                                    includeTransactions = includeTransactions,
                                                    includeGoals = includeGoals,
                                                    includeRecurring = includeRecurring
                                                )
                                            )
                                        }
                                        generatedFile = file
                                        PdfReportGenerator.viewPdf(context, file)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error al abrir: ${e.message}", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isGenerating = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_view_pdf"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                            enabled = !isGenerating
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Visualizar", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExportSectionToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, color = TextMuted, fontSize = 10.sp)
        }
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = EmeraldPrimary,
                checkmarkColor = EmeraldOnPrimary,
                uncheckedColor = TextMuted
            )
        )
    }
}
