package com.example.app_contactos

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioContactoScreen(
    db: DatabaseHelper,
    contactoId: Int,
    onGuardar: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val esEdicion = contactoId != -1

    var nombre      by remember { mutableStateOf("") }
    var telefono    by remember { mutableStateOf("") }
    var email       by remember { mutableStateOf("") }
    var fotoUri     by remember { mutableStateOf("") }

    var errorNombre   by remember { mutableStateOf("") }
    var errorTelefono by remember { mutableStateOf("") }
    var errorEmail    by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(contactoId) {
        if (esEdicion) {
            db.obtenerPorId(contactoId)?.let {
                nombre   = it.nombre
                telefono = it.telefono
                email    = it.email
                fotoUri  = it.fotoUri
            }
        }
    }

    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) { }
            fotoUri = it.toString()
        }
    }

    fun validar(): Boolean {
        var valido = true

        if (nombre.isBlank()) {
            errorNombre = "⚠ El nombre es obligatorio"
            valido = false
        } else {
            errorNombre = ""
        }

        if (telefono.isBlank()) {
            errorTelefono = "⚠ El teléfono es obligatorio"
            valido = false
        } else if (telefono.length != 10) {
            errorTelefono = "⚠ Debe tener exactamente 10 dígitos"
            valido = false
        } else {
            errorTelefono = ""
        }

        if (email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorEmail = "⚠ Correo inválido, debe contener @"
            valido = false
        } else {
            errorEmail = ""
        }

        return valido
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = FondoGris
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(AzulPrimario)
            ) {
                IconButton(
                    onClick = onVolver,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }

                Text(
                    text = if (esEdicion) "Editar Contacto" else "Agregar Contacto",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 20.dp)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                        .clickable { galeriaLauncher.launch("image/*") }
                ) {
                    val contactoPreview = Contacto(nombre = nombre, fotoUri = fotoUri)
                    AvatarContacto(contacto = contactoPreview, size = 90)

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(26.dp)
                            .background(AzulOscuro, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📷", fontSize = 12.sp)
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it; errorNombre = "" },
                        label = { Text("Nombre *") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = errorNombre.isNotEmpty(),
                        supportingText = {
                            if (errorNombre.isNotEmpty())
                                Text(errorNombre, color = Color.Red, fontSize = 12.sp)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = telefono,
                        onValueChange = {
                            if (it.all { c -> c.isDigit() } && it.length <= 10) {
                                telefono = it
                                errorTelefono = ""
                            }
                        },
                        label = { Text("Móvil * (10 dígitos)") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = errorTelefono.isNotEmpty(),
                        supportingText = {
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = errorTelefono,
                                    color = Color.Red,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${telefono.length}/10",
                                    color = if (telefono.length == 10) Color(0xFF4CAF50) else Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorEmail = "" },
                        label = { Text("Correo (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = errorEmail.isNotEmpty(),
                        supportingText = {
                            if (errorEmail.isNotEmpty())
                                Text(errorEmail, color = Color.Red, fontSize = 12.sp)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Button(
                onClick = {
                    if (!validar()) return@Button
                    val contacto = Contacto(
                        id       = if (esEdicion) contactoId else 0,
                        nombre   = nombre.trim(),
                        telefono = telefono.trim(),
                        email    = email.trim(),
                        fotoUri  = fotoUri
                    )
                    if (esEdicion) db.actualizarContacto(contacto)
                    else db.insertarContacto(contacto)
                    onGuardar()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (esEdicion) "Actualizar" else "Guardar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}