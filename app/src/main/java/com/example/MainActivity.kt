package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KoreLifeViewModel
import com.example.ui.screens.*
import com.example.ui.theme.*

enum class KoreLifeScreen(val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    HOME("Inicio", Icons.Filled.Home, Icons.Outlined.Home),
    FINANCE("Finanzas", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
    TASKS("Tareas", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle),
    SOCIAL("Comunidad", Icons.Filled.People, Icons.Outlined.People),
    SETTINGS("Ajustes", Icons.Filled.Settings, Icons.Outlined.Settings)
}

class MainActivity : ComponentActivity() {
    private val viewModel: KoreLifeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val currentUser by viewModel.currentUser.collectAsState()
            val isDemoMode by viewModel.isDemoMode.collectAsState()

            KoreLifeTheme(themeMode = themeMode) {
                if (currentUser == null && !isDemoMode) {
                    AuthScreen(
                        viewModel = viewModel,
                        onAuthSuccess = {}
                    )
                } else {
                    KoreLifeMainApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun KoreLifeMainApp(viewModel: KoreLifeViewModel) {
    var currentScreen by remember { mutableStateOf(KoreLifeScreen.HOME) }

    // Predictive / system back handling
    BackHandler(enabled = currentScreen != KoreLifeScreen.HOME) {
        currentScreen = KoreLifeScreen.HOME
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .testTag("korelife_bottom_bar")
            ) {
                KoreLifeScreen.values().forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title,
                                tint = if (isSelected) EmeraldPrimary else TextMuted
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                color = if (isSelected) EmeraldPrimary else TextMuted,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else DarkSurfaceVariant
                        ),
                        modifier = Modifier.testTag("tab_${screen.name.lowercase()}")
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                KoreLifeScreen.HOME -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToFinance = { currentScreen = KoreLifeScreen.FINANCE },
                    onNavigateToTasks = { currentScreen = KoreLifeScreen.TASKS }
                )
                KoreLifeScreen.FINANCE -> FinanceScreen(
                    viewModel = viewModel
                )
                KoreLifeScreen.TASKS -> TasksScreen(
                    viewModel = viewModel
                )
                KoreLifeScreen.SOCIAL -> SocialScreen(
                    viewModel = viewModel
                )
                KoreLifeScreen.SETTINGS -> SettingsScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
