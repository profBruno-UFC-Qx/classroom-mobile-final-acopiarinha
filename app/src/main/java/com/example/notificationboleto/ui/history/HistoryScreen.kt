package com.example.notificationboleto.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.notificationboleto.data.local.entity.BoletoEntity
import com.example.notificationboleto.ui.add.AddBoletoScreen
import com.example.notificationboleto.ui.viewmodel.BoletoViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(viewModel: BoletoViewModel) {
    val boletos by viewModel.boletos.collectAsState()

    var boletoParaEditar by remember { mutableStateOf<BoletoEntity?>(null) }
    var boletoParaExcluir by remember { mutableStateOf<BoletoEntity?>(null) }

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val hoje = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }

    if (boletoParaEditar != null) {
        Dialog(onDismissRequest = { boletoParaEditar = null }) {
            Surface(
                modifier = Modifier.padding(16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                AddBoletoScreen(
                    initialNome = boletoParaEditar!!.nome,
                    initialValor = boletoParaEditar!!.valor,
                    initialVencimento = boletoParaEditar!!.vencimento,
                    initialDescricao = boletoParaEditar!!.descricao,
                    onSaveClick = { n, v, ven, d ->
                        viewModel.updateBoleto(
                            boletoParaEditar!!.id, n, v, ven, d
                        )
                        boletoParaEditar = null
                    }
                )
            }
        }
    }

    if (boletoParaExcluir != null) {
        AlertDialog(
            onDismissRequest = { boletoParaExcluir = null },
            title = { Text("Excluir boleto") },
            text = { Text("Deseja realmente excluir este boleto?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteBoleto(boletoParaExcluir!!)
                    boletoParaExcluir = null
                }) {
                    Text("Excluir", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { boletoParaExcluir = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (boletos.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nenhum boleto cadastrado")
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(boletos) { boleto ->
            val dataVencimento = runCatching { dateFormat.parse(boleto.vencimento) }.getOrNull()
            val status = when {
                dataVencimento == null -> ""
                dataVencimento.before(hoje) -> "🔴"
                dataVencimento.after(hoje) -> "🟢"
                else -> "🟡"
            }

            Card {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(boleto.nome, style = MaterialTheme.typography.titleMedium)
                        Text("Valor: ${boleto.valor}")
                        Text("$status Vencimento: ${boleto.vencimento}")
                        if (boleto.descricao.isNotBlank()) {
                            Text(boleto.descricao, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    IconButton(onClick = { boletoParaEditar = boleto }) {
                        Icon(Icons.Default.Edit, null)
                    }
                    IconButton(onClick = { boletoParaExcluir = boleto }) {
                        Icon(Icons.Default.Delete, null, tint = Color.Red)
                    }
                }
            }
        }
    }
}
