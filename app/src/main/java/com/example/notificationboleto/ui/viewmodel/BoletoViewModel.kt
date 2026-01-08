package com.example.notificationboleto.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class BoletoUiModel(
    val valor: String,
    val vencimento: String,
    val descricao: String
)

class BoletoViewModel : ViewModel() {

    private val _boletos = mutableStateListOf<BoletoUiModel>()
    val boletos: List<BoletoUiModel> = _boletos

    fun addBoleto(valor: String, vencimento: String, descricao: String) {
        _boletos.add(
            BoletoUiModel(
                valor = valor,
                vencimento = vencimento,
                descricao = descricao
            )
        )
    }
}
