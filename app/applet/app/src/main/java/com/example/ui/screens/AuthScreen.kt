package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.supabase.SupabaseConfig
import com.example.ui.KoreLifeViewModel
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    viewModel: KoreLifeViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableStateOf(0) } // 0 = Login, 1 = Register
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var showConfigDialog by remember { mutableStateOf(false) }

    val isAuthLoading by viewModel.isAuthLoading.collectAsState()
    val scrollState = rememberScrollState()

    val isRegisterMode = selectedTabIndex == 1

    if (showForgotPasswordDialog) {
        ForgotPasswordM3Dialog(
            viewModel = viewModel,
            onDismiss = { showForgotPasswordDialog = false }
        )
    }

    if (showConfigDialog) {
        SupabaseConfigDialog(onDismiss = { showConfigDialog = false })
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(top = 36.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 1. Brand Logo & Header ---
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(DarkSurface)
                    .border(2.dp, EmeraldPrimary, RoundedCornerShape(26.dp))
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_korelife_logo),
                    contentDescription = "Logo KoreLife",
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp))
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "KORELIFE",
                color = TextWhite,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Text(
                text = "Life OS • Finanzas Personales y Productividad",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- 2. Supabase Cloud Status Pill (Clickable to view config) ---
            val isConfigured = SupabaseConfig.isConfigured
            Surface(
                onClick = { showConfigDialog = true },
                shape = RoundedCornerShape(12.dp),
                color = if (isConfigured) Color(0xFF064E3B) else DarkSurfaceVariant,
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(if (isConfigured) EmeraldPrimary else DarkBorder)),
                modifier = Modifier.testTag("supabase_status_badge")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isConfigured) EmeraldPrimary else AmberTertiary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isConfigured) "Supabase Cloud: Conectado ✓" else "Supabase: Modo Local Seguro (Click para ver)",
                        color = if (isConfigured) EmeraldOnContainer else Color(0xFFFDE68A),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configurar",
                        tint = if (isConfigured) EmeraldPrimary else TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // --- 3. Main Material 3 Authentication Card ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    // M3 Segmented Navigation Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurfaceVariant)
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (!isRegisterMode) EmeraldPrimary else Color.Transparent)
                                .clickable {
                                    selectedTabIndex = 0
                                    errorMessage = null
                                    successMessage = null
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Iniciar Sesión",
                                color = if (!isRegisterMode) EmeraldOnPrimary else TextMuted,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isRegisterMode) EmeraldPrimary else Color.Transparent)
                                .clickable {
                                    selectedTabIndex = 1
                                    errorMessage = null
                                    successMessage = null
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Crear Cuenta",
                                color = if (isRegisterMode) EmeraldOnPrimary else TextMuted,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Error Message Banner
                    AnimatedVisibility(visible = errorMessage != null) {
                        if (errorMessage != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF450A0A))
                                    .border(1.dp, RedError, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = RedError, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = errorMessage!!, color = Color(0xFFFCA5A5), fontSize = 12.sp, lineHeight = 16.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    // Success Message Banner
                    AnimatedVisibility(visible = successMessage != null) {
                        if (successMessage != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF064E3B))
                                    .border(1.dp, EmeraldPrimary, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = successMessage!!, color = EmeraldOnContainer, fontSize = 12.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    // Form Field: Full Name (Solo Registro)
                    AnimatedVisibility(visible = isRegisterMode) {
                        Column {
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("Nombre Completo", color = TextMuted) },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldPrimary)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_fullname_input"),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = EmeraldPrimary,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedContainerColor = DarkSurfaceVariant,
                                    unfocusedContainerColor = DarkSurfaceVariant
                                )
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    // Form Field: Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        label = { Text("Correo Electrónico (Supabase Auth)", color = TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = EmeraldPrimary)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Form Field: Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("Contraseña", color = TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldPrimary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Ver contraseña",
                                    tint = TextMuted
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant
                        )
                    )

                    // Password Strength Indicator (in Registration mode)
                    if (isRegisterMode && password.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        val strength = when {
                            password.length < 6 -> "Muy corta (mínimo 6)"
                            password.length < 8 -> "Moderada"
                            else -> "Segura ✓"
                        }
                        val strengthColor = when {
                            password.length < 6 -> RedError
                            password.length < 8 -> AmberTertiary
                            else -> EmeraldPrimary
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Seguridad: ", color = TextMuted, fontSize = 11.sp)
                            Text(text = strength, color = strengthColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Form Field: Confirm Password (Solo Registro)
                    AnimatedVisibility(visible = isRegisterMode) {
                        Column {
                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = {
                                    confirmPassword = it
                                    errorMessage = null
                                },
                                label = { Text("Confirmar Contraseña", color = TextMuted) },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldPrimary)
                                },
                                trailingIcon = {
                                    if (confirmPassword.isNotBlank()) {
                                        val match = confirmPassword == password
                                        Icon(
                                            imageVector = if (match) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                            contentDescription = null,
                                            tint = if (match) EmeraldPrimary else RedError
                                        )
                                    }
                                },
                                singleLine = true,
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_confirm_password_input"),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = EmeraldPrimary,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedContainerColor = DarkSurfaceVariant,
                                    unfocusedContainerColor = DarkSurfaceVariant
                                )
                            )
                        }
                    }

                    // Options in Login mode: Remember Me & Forgot Password
                    if (!isRegisterMode) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = EmeraldPrimary,
                                        checkmarkColor = EmeraldOnPrimary,
                                        uncheckedColor = TextMuted
                                    )
                                )
                                Text("Recordarme", color = TextWhite, fontSize = 12.sp)
                            }

                            Text(
                                text = "¿Olvidaste tu contraseña?",
                                color = EmeraldPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clickable { showForgotPasswordDialog = true }
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Primary Submit Button
                    Button(
                        onClick = {
                            if (email.isBlank()) {
                                errorMessage = "Por favor ingresa tu correo electrónico."
                                return@Button
                            }
                            if (!email.contains("@") || !email.contains(".")) {
                                errorMessage = "Por favor ingresa un formato de correo válido."
                                return@Button
                            }
                            if (password.length < 6) {
                                errorMessage = "La contraseña debe tener un mínimo de 6 caracteres."
                                return@Button
                            }
                            if (isRegisterMode && password != confirmPassword) {
                                errorMessage = "Las contraseñas ingresadas no coinciden."
                                return@Button
                            }

                            if (isRegisterMode) {
                                viewModel.signUp(email, password, fullName) { success, err ->
                                    if (success) {
                                        successMessage = "¡Cuenta creada con éxito! Entrando a KoreLife..."
                                        onAuthSuccess()
                                    } else {
                                        errorMessage = err
                                    }
                                }
                            } else {
                                viewModel.signIn(email, password) { success, err ->
                                    if (success) {
                                        successMessage = "¡Bienvenido de vuelta!"
                                        onAuthSuccess()
                                    } else {
                                        errorMessage = err
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("auth_submit_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = EmeraldOnPrimary
                        ),
                        enabled = !isAuthLoading
                    ) {
                        if (isAuthLoading) {
                            CircularProgressIndicator(color = EmeraldOnPrimary, modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
                        } else {
                            Text(
                                text = if (isRegisterMode) "Crear Mi Cuenta en Supabase" else "Iniciar Sesión con Supabase",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Demo Credentials Fill Button
                    OutlinedButton(
                        onClick = {
                            email = "demo@korelife.com"
                            password = "password123"
                            errorMessage = null
                            viewModel.signIn(email, password) { success, _ ->
                                if (success) onAuthSuccess()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("auth_demo_fill_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFA5B4FC)),
                        border = ButtonDefaults.outlinedButtonBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(IndigoSecondary.copy(alpha = 0.5f)))
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, tint = IndigoSecondary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Probar con Cuenta Demo (demo@korelife.com)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Guest Mode / Direct Access
            TextButton(
                onClick = {
                    viewModel.continueAsGuest()
                    onAuthSuccess()
                },
                modifier = Modifier.testTag("auth_guest_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Explorar todas las funciones como Invitado",
                        color = TextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun ForgotPasswordM3Dialog(
    viewModel: KoreLifeViewModel,
    onDismiss: () -> Unit
) {
    var resetEmail by remember { mutableStateOf("") }
    var sentMessage by remember { mutableStateOf(false) }
    var isSending by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(26.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LockReset, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Recuperar Contraseña", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "Te enviaremos un correo con un enlace seguro de Supabase para restablecer tu clave.",
                    color = TextMuted,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                if (sentMessage) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF064E3B))
                            .border(1.dp, EmeraldPrimary, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "✓ Correo enviado con éxito a $resetEmail. Revisa tu bandeja de entrada o spam.",
                            color = EmeraldOnContainer,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    if (errorText != null) {
                        Text(errorText!!, color = RedError, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = {
                            resetEmail = it
                            errorText = null
                        },
                        label = { Text("Correo Electrónico Registrado", color = TextMuted) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = EmeraldPrimary) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant
                        )
                    )
                }
            }
        },
        confirmButton = {
            if (sentMessage) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = EmeraldOnPrimary)
                ) {
                    Text("Listo")
                }
            } else {
                Button(
                    onClick = {
                        if (resetEmail.isBlank() || !resetEmail.contains("@")) {
                            errorText = "Ingresa un correo electrónico válido."
                            return@Button
                        }
                        isSending = true
                        viewModel.recoverPassword(resetEmail) { success, err ->
                            isSending = false
                            if (success) sentMessage = true else errorText = err
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = EmeraldOnPrimary),
                    enabled = !isSending
                ) {
                    if (isSending) {
                        CircularProgressIndicator(color = EmeraldOnPrimary, modifier = Modifier.size(18.dp))
                    } else {
                        Text("Enviar Enlace", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        dismissButton = {
            if (!sentMessage) {
                OutlinedButton(onClick = onDismiss) {
                    Text("Cancelar", color = TextWhite)
                }
            }
        }
    )
}

@Composable
private fun SupabaseConfigDialog(onDismiss: () -> Unit) {
    var url by remember { mutableStateOf(SupabaseConfig.supabaseUrl) }
    var key by remember { mutableStateOf(SupabaseConfig.supabaseAnonKey) }
    var saved by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(26.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CloudSync, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Configuración de Supabase", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "Credenciales cargadas desde .env / .env.example. Puedes modificarlas para conectar tu propio proyecto en Supabase Cloud.",
                    color = TextMuted,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("SUPABASE_URL", color = TextMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    label = { Text("SUPABASE_ANON_KEY", color = TextMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (saved) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("✓ Configuración actualizada", color = EmeraldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    SupabaseConfig.updateCredentials(url, key)
                    saved = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = EmeraldOnPrimary)
            ) {
                Text("Guardar Cambios", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cerrar", color = TextWhite)
            }
        }
    )
}
