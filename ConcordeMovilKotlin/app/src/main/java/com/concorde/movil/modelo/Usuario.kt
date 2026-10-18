package com.concorde.movil.modelo

// Lo que devuelve POST /api/login (LoginController.java del backend).
data class Usuario(
    val idUsuario: Int,
    val nombreCompleto: String,
    val correoElectronico: String,
    val rol: String,   // ADMIN | AGENTE | CLIENTE
    val token: String  // JWT -- se manda en el header Authorization
)
