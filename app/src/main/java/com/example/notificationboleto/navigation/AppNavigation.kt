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

@Composable
fun AppNavigation(navController: NavHostController) {

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
                onSaveClick = { valor, vencimento, descricao ->
                    boletoViewModel.addBoleto(valor, vencimento, descricao)
                    navController.popBackStack() // volta
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                boletos = boletoViewModel.boletos
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
