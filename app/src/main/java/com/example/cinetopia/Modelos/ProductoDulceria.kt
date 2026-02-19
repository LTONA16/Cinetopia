package com.example.cinetopia.Modelos

data class ProductoDulceria(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagenResId: String,
    val categoria: CategoriaProducto,
    val disponible: Boolean = true
)

enum class CategoriaProducto {
    DULCES,
    BEBIDAS,
    PALOMITAS,
    NACHOS,
    HOTDOG,
    COMBO
}