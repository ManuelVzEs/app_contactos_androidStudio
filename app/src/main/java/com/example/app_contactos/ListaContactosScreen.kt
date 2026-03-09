package com.example.app_contactos

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaContactosScreen(
    db: DatabaseHelper,
    onAgregar: () -> Unit,
    onContactoClick: (Int) -> Unit
) {
    var busqueda by remember { mutableStateOf("") }
    var contactos by remember { mutableStateOf(listOf<Contacto>()) }

    LaunchedEffect(Unit) {
        contactos = db.obtenerTodos()
    }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                contactos = db.obtenerTodos()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val filtrados = contactos.filter {
        it.nombre.contains(busqueda, ignoreCase = true) ||
                it.telefono.contains(busqueda) ||
                it.email.contains(busqueda, ignoreCase = true)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAgregar,
                containerColor = AzulPrimario,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar contacto")
            }
        },
        containerColor = FondoGris,
        contentWindowInsets = WindowInsets(0) // quita el padding del sistema
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // Header azul hasta arriba
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AzulPrimario)
                    .statusBarsPadding() // respeta la barra de estado
                    .padding(bottom = 13.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    placeholder = { Text("Buscar...", color = Color.Gray) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                    },
                    modifier = Modifier.fillMaxWidth(0.82f),
                    shape = RoundedCornerShape(26.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val agrupados = filtrados.groupBy { it.getInicial() }
                agrupados.forEach { (letra, lista) ->
                    item {
                        Text(
                            text = letra,
                            color = AzulPrimario,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 2.dp)
                        )
                    }
                    items(lista) { contacto ->
                        ItemContacto(contacto = contacto, onClick = { onContactoClick(contacto.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun ItemContacto(contacto: Contacto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarContacto(contacto = contacto, size = 48)
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    contacto.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A1A2E)
                )
                val sub = if (contacto.telefono.isNotEmpty()) contacto.telefono else contacto.email
                if (sub.isNotEmpty()) {
                    Text(sub, fontSize = 13.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun AvatarContacto(contacto: Contacto, size: Int) {
    val context = LocalContext.current
    val colorIndex = kotlin.math.abs(contacto.nombre.hashCode()) % coloresAvatar.size

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(coloresAvatar[colorIndex]),
        contentAlignment = Alignment.Center
    ) {
        if (contacto.fotoUri.isNotEmpty()) {
            val bitmap = remember(contacto.fotoUri) {
                try {
                    val uri = Uri.parse(contacto.fotoUri)
                    val stream = context.contentResolver.openInputStream(uri)
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                } catch (e: Exception) { null }
            }
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "Foto de ${contacto.nombre}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    contacto.getInicial(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size / 2.5).sp
                )
            }
        } else {
            Text(
                contacto.getInicial(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (size / 2.5).sp
            )
        }
    }
}