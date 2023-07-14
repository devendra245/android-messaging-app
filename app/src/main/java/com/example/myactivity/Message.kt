package com.example.myactivity

data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String ="",
    val messageText: String = "",
    val timestamp: Long = 0
)
