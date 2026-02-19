package com.example.cinetopia.Modelos

data class Promocion(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val descuento: String,
    val imagenResId: String,
    val colorFondo: String // Color hex para el fondo
)