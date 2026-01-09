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
import com.example.notificationboleto.ui.add.AddBoletoScreen
import com.example.notificationboleto.ui.viewmodel.BoletoUiModel

@Composable
fun HistoryScreen(
    boletos: List<BoletoUiModel>,
    onDeleteClick: (String) -> Unit = {},
    onUpdateClick: (id: String, valor: String, vencimento: String, descricao: String) -> Unit = { _, _, _, _ -> }
) {
    var boletoParaEditar by remember { mutableStateOf<BoletoUiModel?>(null) }
    var boletoParaExcluir by remember { mutableStateOf<BoletoUiModel?>(null) }

    // Diálogo de Edição
    if (boletoParaEditar != null) {
        Dialog(onDismissRequest = { boletoParaEditar = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                AddBoletoScreen(
                    initialValor = boletoParaEditar!!.valor,
                    initialVencimento = boletoParaEditar!!.vencimento,
                    initialDescricao = boletoParaEditar!!.descricao,
                    onSaveClick = { v, ven, d ->
                        onUpdateClick(boletoParaEditar!!.id, v, ven, d)
                        boletoParaEditar = null
                    }
                )
            }
        }
    }

    // Diálogo de Confirmação de Exclusão
    if (boletoParaExcluir != null) {
        AlertDialog(
            onDismissRequest = { boletoParaExcluir = null },
            title = { Text("Excluir Boleto") },
            text = { Text("Tem certeza que deseja excluir este boleto? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick(boletoParaExcluir!!.id)
                        boletoParaExcluir = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Excluir")
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
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(boleto.valor, style = MaterialTheme.typography.titleMedium)
                            Text("Vencimento: ${boleto.vencimento}")
                            if (boleto.descricao.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(boleto.descricao, style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Row {
                            IconButton(onClick = { boletoParaEditar = boleto }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.Blue)
                            }
                            IconButton(onClick = { boletoParaExcluir = boleto }) {
                                Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}
