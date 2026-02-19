package com.example.cinetopia.Modelos

data class Boleto(
    val id: Int,
    val pelicula: String,
    val posterResId: String, // nombre del drawable (ej: "p1")
    val cine: String,
    val sala: String,
    val fecha: String,
    val hora: String,
    val asientos: String,
    val cantidad: Int,
    val total: Double,
    val codigoQR: String,
    val fechaTimestamp: Long // Para ordenar por proximidad
)