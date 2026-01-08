package com.example.notificationboleto.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notificationboleto.ui.viewmodel.BoletoUiModel

@Composable
fun HistoryScreen(
    boletos: List<BoletoUiModel>
) {
    if (boletos.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Nenhum boleto cadastrado")
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(boletos) { boleto ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("R$ ${boleto.valor}", style = MaterialTheme.typography.titleMedium)
                        Text("Vencimento: ${boleto.vencimento}")
                        if (boleto.descricao.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(boleto.descricao)
                        }
                    }
                }
            }
        }
    }
}
