package com.example.cinetopia.Modelos

data class Pelicula(
    val id: Int,
    val titulo: String,
    val imagenNombre: String,
    val calificacion: Double = 0.0,
    val genero: String = "",
    val duracion: Int = 0,
    val sinopsis: String = ""
)