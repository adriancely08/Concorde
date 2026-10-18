package com.concorde.movil.modelo

// Corresponde a Viaje.java del backend (ruta, vehiculo y conductor
// vienen anidados en el JSON porque son @ManyToOne).
data class Viaje(
    val idViaje: Int,
    val idVehiculo: Int,
    val fechaViaje: String,
    val horaSalida: String,
    val precio: Double,
    val estadoViaje: String,
    val ciudadOrigen: String,
    val ciudadDestino: String
)
