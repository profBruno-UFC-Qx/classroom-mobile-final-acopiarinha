package com.example.notificationboleto

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.notificationboleto.navigation.AppNavigation
import com.example.notificationboleto.navigation.Screen
import com.example.notificationboleto.ui.theme.NotificationBoletoTheme
import com.example.notificationboleto.ui.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkMode by themeViewModel.isDarkMode

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { }
                )
                LaunchedEffect(Unit) {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            NotificationBoletoTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("Boleto Track") },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = if (isDarkMode) Color.DarkGray else Color.Red,
                                titleContentColor = Color.White
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = backStackEntry?.destination?.route == Screen.Home.route,
                                onClick = {
                                    navController.navigate(Screen.Home.route)
                                },
                                icon = { Icon(Icons.Default.Home, null) },
                                label = { Text("Início") }
                            )

                            NavigationBarItem(
                                selected = backStackEntry?.destination?.route == Screen.AddBoleto.route,
                                onClick = {
                                    navController.navigate(Screen.AddBoleto.route)
                                },
                                icon = { Icon(Icons.Default.AddCircle, null) },
                                label = { Text("Adicionar") }
                            )

                            NavigationBarItem(
                                selected = backStackEntry?.destination?.route == Screen.History.route,
                                onClick = {
                                    navController.navigate(Screen.History.route)
                                },
                                icon = { Icon(Icons.Default.List, null) },
                                label = { Text("Boletos") }
                            )

                            NavigationBarItem(
                                selected = backStackEntry?.destination?.route == Screen.Settings.route,
                                onClick = {
                                    navController.navigate(Screen.Settings.route)
                                },
                                icon = { Icon(Icons.Default.Settings, null) },
                                label = { Text("Configurações") }
                            )
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        AppNavigation(
                            navController = navController,
                            themeViewModel = themeViewModel
                        )
                    }
                }

            }
        }
    }
}
