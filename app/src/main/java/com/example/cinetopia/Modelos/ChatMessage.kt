package com.example.cinetopia.Modelos

data class ChatMessage(
    var id: String = "",
    var senderId: String = "",
    var receiverId: String = "",
    var message: String = "",
    var timestamp: Long = 0,
    var imageUrl: String? = null,
    var isDeleted: Boolean = false,
    var type: String = "text" // "text" or "image"
)
