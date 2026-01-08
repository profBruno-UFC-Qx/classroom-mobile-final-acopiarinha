package com.example.notificationboleto.ui.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBoletoScreen(
    onSaveClick: (valor: String, vencimento: String, descricao: String) -> Unit = { _, _, _ -> },
    onImportClick: () -> Unit = {}
) {
    var valor by remember { mutableStateOf("") }
    var vencimento by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Cadastrar Boleto",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Valor do boleto") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = vencimento,
            onValueChange = { vencimento = it },
            label = { Text("Data de vencimento (dd/mm/aaaa)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 🔘 Botão Salvar
        Button(
            onClick = {
                onSaveClick(valor, vencimento, descricao)

                // limpa campos
                valor = ""
                vencimento = ""
                descricao = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = valor.isNotBlank() && vencimento.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Icon(Icons.Default.CheckCircle, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Salvar boleto")
        }


        Spacer(modifier = Modifier.height(16.dp))

        // (sem funcionalidade)
        OutlinedButton(
            onClick = { onImportClick() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Importar boleto (imagem/PDF)")
        }
    }
}
