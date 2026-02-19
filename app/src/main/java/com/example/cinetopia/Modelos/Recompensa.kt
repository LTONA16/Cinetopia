package com.example.cinetopia.Modelos

data class Recompensa(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val puntos: Int,
    val icono: String,
    val desbloqueado: Boolean = false
)

data class UsuarioRecompensas(
    val nombre: String,
    val email: String,
    val puntosActuales: Int,
    val nivel: String,
    val codigoQR: String,
    val miembroDesde: String
)