package com.example.nerevian.ui

/**
 * Modelo de datos para representar un mensaje en el chat.
 * @param text El contenido del mensaje.
 * @param isUser Indica si el mensaje lo envió el usuario (true) o el Bot/IA (false).
 */
data class ChatMessage(
    val text: String,
    val isUser: Boolean
)