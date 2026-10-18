package com.concorde.movil.modelo

// Un mensaje del chat (MensajeChat.java del backend).
data class Mensaje(
    val remitente: String, // CLIENTE | BOT | AGENTE
    val contenido: String
)
