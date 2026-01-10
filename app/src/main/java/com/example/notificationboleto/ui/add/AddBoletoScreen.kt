package com.example.notificationboleto.ui.add

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBoletoScreen(
    initialNome: String = "",
    initialValor: String = "",
    initialVencimento: String = "",
    initialDescricao: String = "",
    onSaveClick: (nome: String, valor: String, vencimento: String, descricao: String) -> Unit = { _, _, _, _ -> },
) {
    var nome by remember { mutableStateOf(initialNome) }
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
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    // Verifica se o boleto está vencido
    val isVencido = remember(vencimentoTextFieldValue.text) {
        try {
            val data = dateFormat.parse(vencimentoTextFieldValue.text)
            val hoje = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
            data != null && data.before(hoje)
        } catch (e: Exception) {
            false
        }
    }

    val recognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

    fun processText(text: String) {
        val cleanText = text.replace("\n", " ")
        
        val linhaDigitavelRegex = Regex("""\d{5}[\.\s]?\d{5}[\.\s]?\d{5}[\.\s]?\d{6}[\.\s]?\d{5}[\.\s]?\d{6}[\.\s]?\d[\.\s]?\d{14}""")
        val linhaMatch = linhaDigitavelRegex.find(cleanText)
        
        var valorFinal: String? = null
        
        if (linhaMatch != null) {
            val soNumeros = linhaMatch.value.replace(Regex("[^\\d]"), "")
            if (soNumeros.length >= 47) {
                val valorCentavos = soNumeros.takeLast(10).toDoubleOrNull()
                if (valorCentavos != null && valorCentavos > 0) {
                    valorFinal = currencyFormatter.format(valorCentavos / 100)
                }
            }
        }

        if (valorFinal == null) {
            val valorRegex = Regex("""(\d{1,3}(\.\d{3})*,\d{2})""")
            val matches = valorRegex.findAll(cleanText)
            
            val maiorValor = matches.mapNotNull { m ->
                val num = m.value.replace(".", "").replace(",", ".").toDoubleOrNull()
                num
            }.maxOrNull()

            if (maiorValor != null) {
                valorFinal = currencyFormatter.format(maiorValor)
            }
        }

        val dataRegex = Regex("""\d{2}/\d{2}/\d{4}""")
        val dataMatch = dataRegex.find(cleanText)

        valorFinal?.let {
            valorTextFieldValue = TextFieldValue(it, TextRange(it.length))
        }
        dataMatch?.let {
            vencimentoTextFieldValue = TextFieldValue(it.value, TextRange(it.value.length))
        }
        
        isLoading = false
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            isLoading = true
            try {
                val isPdf = context.contentResolver.getType(it)?.contains("pdf") == true || it.toString().lowercase().endsWith(".pdf")
                if (isPdf) {
                    val bitmap = pdfToBitmap(context, it)
                    bitmap?.let { b ->
                        recognizer.process(InputImage.fromBitmap(b, 0))
                            .addOnSuccessListener { visionText -> processText(visionText.text) }
                            .addOnFailureListener { isLoading = false }
                    } ?: run { isLoading = false }
                } else {
                    recognizer.process(InputImage.fromFilePath(context, it))
                        .addOnSuccessListener { visionText -> processText(visionText.text) }
                        .addOnFailureListener { isLoading = false }
                }
            } catch (e: Exception) {
                isLoading = false
            }
        }
    }

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

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome do boleto (Ex: Luz, Internet)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = valorTextFieldValue,
            onValueChange = { input ->
                val cleanString = input.text.replace(Regex("[^\\d]"), "")
                val newText = if (cleanString.isEmpty()) "" else currencyFormatter.format(cleanString.toDouble() / 100)
                valorTextFieldValue = TextFieldValue(newText, TextRange(newText.length))
            },
            label = { Text("Valor do boleto") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            OutlinedTextField(
                value = vencimentoTextFieldValue,
                onValueChange = { input ->
                    val clean = input.text.replace(Regex("[^\\d]"), "")
                    if (clean.length <= 8) {
                        var formatted = ""
                        for (i in clean.indices) {
                            formatted += clean[i]
                            if ((i == 1 || i == 3) && i != clean.lastIndex) formatted += "/"
                        }
                        vencimentoTextFieldValue = TextFieldValue(formatted, TextRange(formatted.length))
                    }
                },
                label = { Text("Data de vencimento (dd/mm/aaaa)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = isVencido
            )
            
            if (isVencido) {
                Text(
                    text = "Boleto vencido",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                onSaveClick(nome, valorTextFieldValue.text, vencimentoTextFieldValue.text, descricao)
                if (initialValor.isEmpty()) {
                    nome = ""
                    valorTextFieldValue = TextFieldValue("")
                    vencimentoTextFieldValue = TextFieldValue("")
                    descricao = ""
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = nome.isNotBlank() && valorTextFieldValue.text.isNotBlank() && vencimentoTextFieldValue.text.isNotBlank() && !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Icon(Icons.Default.CheckCircle, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (initialValor.isEmpty()) "Salvar boleto" else "Atualizar boleto")
        }

        if (initialValor.isEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { launcher.launch(arrayOf("image/*", "application/pdf")) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isLoading
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Importar boleto (imagem/PDF)")
            }
        }
    }
}

private fun pdfToBitmap(context: Context, pdfUri: Uri): Bitmap? {
    return try {
        context.contentResolver.openInputStream(pdfUri)?.use { inputStream ->
            val file = File(context.cacheDir, "temp_boleto.pdf")
            FileOutputStream(file).use { outputStream -> inputStream.copyTo(outputStream) }
            val fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(fd)
            val page = renderer.openPage(0)
            val bitmap = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            renderer.close()
            fd.close()
            bitmap
        }
    } catch (e: Exception) { null }
}
