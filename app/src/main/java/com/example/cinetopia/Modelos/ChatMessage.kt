package com.example.cinetopia.Modelos

import com.google.firebase.database.PropertyName

data class ChatMessage(
    var id: String = "",
    var senderId: String = "",
    var receiverId: String = "",
    var message: String = "",
    var timestamp: Long = 0,
    var imageUrl: String? = null,
    var audioUrl: String? = null,
    var audioDuration: Long = 0,
    @get:PropertyName("isDeleted")
    @set:PropertyName("isDeleted")
    var isDeleted: Boolean = false,
    var type: String = "text" // text, image, audio
)
