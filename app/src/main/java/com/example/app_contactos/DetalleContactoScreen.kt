package com.example.app_contactos

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DetalleContactoScreen(
    db: DatabaseHelper,
    contactoId: Int,
    onVolver: () -> Unit,
    onEditar: () -> Unit
) {
    val context = LocalContext.current
    var contacto by remember { mutableStateOf<Contacto?>(null) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    LaunchedEffect(contactoId) {
        contacto = db.obtenerPorId(contactoId)
    }

    contacto?.let { c ->

        if (mostrarDialogo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                title = { Text("Eliminar contacto") },
                text = { Text("¿Seguro que deseas eliminar a ${c.nombre}?") },
                confirmButton = {
                    TextButton(onClick = {
                        db.eliminarContacto(contactoId)
                        mostrarDialogo = false
                        onVolver()
                    }) { Text("Eliminar", color = Color.Red) }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(FondoGris)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(AzulPrimario)
            ) {
                // Botón volver con fondo circular
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 48.dp, start = 16.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { onVolver() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }

                // Botones editar / eliminar con fondo circular
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 48.dp, end = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { onEditar() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { mostrarDialogo = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White)
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AvatarContacto(contacto = c, size = 100)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = c.nombre,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .width(220.dp)
                        .offset(y = 60.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    BotonAccionDetalle(
                        icono = Icons.Default.Call,
                        label = "Llamar",
                        habilitado = c.telefono.isNotEmpty(),
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${c.telefono}"))
                            context.startActivity(intent)
                        }
                    )
                    BotonAccionDetalle(
                        icono = Icons.Default.Email,
                        label = "Correo",
                        habilitado = c.email.isNotEmpty(),
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${c.email}"))
                            context.startActivity(intent)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {

                    if (c.telefono.isNotEmpty()) {
                        FilaInfoDetalle(
                            icono = Icons.Default.Phone,
                            label = "Teléfono",
                            valor = c.telefono
                        )
                    }

                    if (c.telefono.isNotEmpty() && c.email.isNotEmpty()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 56.dp),
                            color = Color(0xFFF0F0F0)
                        )
                    }

                    if (c.email.isNotEmpty()) {
                        FilaInfoDetalle(
                            icono = Icons.Default.Email,
                            label = "Correo Electrónico",
                            valor = c.email
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BotonAccionDetalle(
    icono: ImageVector,
    label: String,
    habilitado: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 32.dp, vertical = 12.dp)
            .then(if (habilitado) Modifier.clickable { onClick() } else Modifier)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8EEF4)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = label,
                tint = if (habilitado) Color(0xFF3D5A80) else Color(0xFF3D5A80).copy(alpha = 0.3f),
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (habilitado) Color(0xFF3D5A80) else Color(0xFF3D5A80).copy(alpha = 0.3f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun FilaInfoDetalle(
    icono: ImageVector,
    label: String,
    valor: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(AzulPrimario.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = label,
                tint = AzulPrimario,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = label,
                color = Color.Gray,
                fontSize = 12.sp
            )
            Text(
                text = valor,
                color = Color(0xFF1A1A2E),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}