package com.concorde.movil.red

import com.concorde.movil.modelo.Usuario

/**
 * Guarda la sesión activa mientras la app está abierta (en memoria,
 * no persiste si se cierra la app -- eso queda como mejora futura con
 * SharedPreferences). No es obligatorio tener sesión para usar la
 * app: solo se exige para reservar y para ver "Mis reservas".
 */
object SesionUsuario {
    var usuarioActual: Usuario? = null

    fun haySesion(): Boolean = usuarioActual != null

    fun cerrarSesion() {
        usuarioActual = null
    }
}
