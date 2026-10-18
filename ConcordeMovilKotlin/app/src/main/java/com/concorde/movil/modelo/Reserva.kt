package com.concorde.movil.modelo

// Corresponde a Reserva.java del backend.
data class Reserva(
    val idReserva: Int,
    val estado: String, // PENDIENTE | CONFIRMADA | CANCELADA
    val fechaInicial: String,
    val ciudadOrigen: String,
    val ciudadDestino: String,
    val fechaViaje: String,
    val nombreCliente: String = "—"
)
