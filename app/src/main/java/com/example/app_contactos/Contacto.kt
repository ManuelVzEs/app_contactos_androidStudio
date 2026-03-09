package com.example.app_contactos

data class Contacto(
    val id: Int = 0,
    val nombre: String = "",
    val telefono: String = "",
    val email: String = "",
    val fotoUri: String = ""
) {
    fun getInicial(): String =
        if (nombre.isNotEmpty()) nombre[0].uppercaseChar().toString() else "?"
}