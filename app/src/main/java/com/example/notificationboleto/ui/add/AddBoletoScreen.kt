package com.example.notificationboleto.ui.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBoletoScreen(
    initialValor: String = "",
    initialVencimento: String = "",
    initialDescricao: String = "",
    onSaveClick: (valor: String, vencimento: String, descricao: String) -> Unit = { _, _, _ -> },
    onImportClick: () -> Unit = {}
) {
    var valorTextFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = initialValor,
                selection = TextRange(initialValor.length)
            )
        )
    }
    var vencimentoTextFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = initialVencimento,
                selection = TextRange(initialVencimento.length)
            )
        )
    }
    var descricao by remember { mutableStateOf(initialDescricao) }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = if (initialValor.isEmpty()) "Cadastrar Boleto" else "Editar Boleto",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Campo Valor com formatação de moeda
        OutlinedTextField(
            value = valorTextFieldValue,
            onValueChange = { input ->
                val cleanString = input.text.replace(Regex("[^\\d]"), "")
                val newText = if (cleanString.isEmpty()) {
                    ""
                } else {
                    val parsed = cleanString.toDouble() / 100
                    currencyFormatter.format(parsed)
                }
                valorTextFieldValue = TextFieldValue(
                    text = newText,
                    selection = TextRange(newText.length)
                )
            },
            label = { Text("Valor do boleto") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Vencimento com máscara dd/mm/aaaa
        OutlinedTextField(
            value = vencimentoTextFieldValue,
            onValueChange = { input ->
                val clean = input.text.replace(Regex("[^\\d]"), "")
                if (clean.length <= 8) {
                    var formatted = ""
                    for (i in clean.indices) {
                        formatted += clean[i]
                        if ((i == 1 || i == 3) && i != clean.lastIndex) {
                            formatted += "/"
                        }
                    }
                    vencimentoTextFieldValue = TextFieldValue(
                        text = formatted,
                        selection = TextRange(formatted.length)
                    )
                }
            },
            label = { Text("Data de vencimento (dd/mm/aaaa)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
                onSaveClick(valorTextFieldValue.text, vencimentoTextFieldValue.text, descricao)
                if (initialValor.isEmpty()) {
                    valorTextFieldValue = TextFieldValue("")
                    vencimentoTextFieldValue = TextFieldValue("")
                    descricao = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = valorTextFieldValue.text.isNotBlank() && vencimentoTextFieldValue.text.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Icon(Icons.Default.CheckCircle, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (initialValor.isEmpty()) "Salvar boleto" else "Atualizar boleto")
        }


        if (initialValor.isEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { onImportClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Importar boleto (imagem/PDF)")
            }
        }
    }
}
