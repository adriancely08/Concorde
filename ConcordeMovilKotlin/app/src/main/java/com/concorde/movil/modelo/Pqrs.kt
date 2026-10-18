package com.concorde.movil.modelo

// Corresponde a Pqrs.java del backend (PqrsController).
data class Pqrs(
    val idPqrs: Int,
    val tipo: String,       // PETICION | QUEJA | RECLAMO | SUGERENCIA
    val asunto: String,
    val descripcion: String,
    val estado: String,     // ABIERTA | EN_PROCESO | RESUELTA
    val respuesta: String?,
    val nombreCliente: String,
    val correoCliente: String
)
