package com.concorde.movil.modelo

// Una conversación del chat (ConsultaChat.java del backend).
data class ConsultaChat(
    val idConsulta: Int,
    val estado: String, // BOT | ESCALADA | EN_ATENCION | CERRADA
    val nombreContacto: String,
    val correoContacto: String
)
