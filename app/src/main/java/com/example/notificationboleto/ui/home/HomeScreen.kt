package com.example.notificationboleto.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun HomeScreen(
    onAddClick: () -> Unit,
    onListClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(35.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Seja bem-vindo ao Boleto Track",
            fontWeight = FontWeight.Bold,
            fontSize = 23.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sua ferramenta para gerenciar boletos e te avisar quando o prazo de pagamento " +
                    "está perto de expirar!",
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        AsyncImage( model = "https://facil123.com.br/wp-content/uploads/boleto-banc%C3%A1rio-campos.jpg",
            contentDescription = "Imagem de boas-vindas",
            modifier = Modifier .height(220.dp).fillMaxWidth() )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onAddClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Icon(Icons.Default.AddCircle, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Inserir boleto")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onListClick,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Default.List, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Meus boletos")
        }
    }
}
