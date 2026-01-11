package com.example.notificationboleto.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.notificationboleto.ui.add.AddBoletoScreen
import com.example.notificationboleto.ui.history.HistoryScreen
import com.example.notificationboleto.ui.home.HomeScreen
import com.example.notificationboleto.ui.settings.SettingsScreen
import com.example.notificationboleto.ui.viewmodel.BoletoViewModel
import com.example.notificationboleto.ui.viewmodel.ThemeViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    themeViewModel: ThemeViewModel
) {
    val boletoViewModel: BoletoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {

        composable(Screen.Home.route) {
            HomeScreen(
                onAddClick = {
                    navController.navigate(Screen.AddBoleto.route)
                },
                onListClick = {
                    navController.navigate(Screen.History.route)
                }
            )
        }

        composable(Screen.AddBoleto.route) {
            AddBoletoScreen(
                onSaveClick = { nome, valor, vencimento, descricao ->
                    boletoViewModel.addBoleto(nome, valor, vencimento, descricao)
                    navController.navigate(Screen.History.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(viewModel = boletoViewModel)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(themeViewModel = themeViewModel)
        }
    }
}
