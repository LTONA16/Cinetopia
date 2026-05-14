package com.example.cinetopia.Modelos

data class UserRole(
    var uid: String = "",
    var email: String = "",
    var name: String = "",
    var role: String = "cliente" // cliente, soporte, admin
)
