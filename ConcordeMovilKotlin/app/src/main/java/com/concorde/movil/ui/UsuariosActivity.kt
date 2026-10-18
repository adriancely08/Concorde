package com.concorde.movil.ui

import com.concorde.movil.red.ApiCliente

/** Admin/Agente: lista de todos los usuarios (GET /api/usuarios). */
class UsuariosActivity : ListaBaseActivity() {
    override val titulo: String get() = "Usuarios"

    override suspend fun cargarItems(): List<ItemGenerico> =
        ApiCliente.listarUsuarios().map { u ->
            val rol = u.optJSONObject("rol")?.optString("nombreRol") ?: "—"
            val telefono = u.optString("telefono").let { if (it.isBlank() || it == "null") "—" else it }
            ItemGenerico(
                titulo = u.optString("nombreCompleto"),
                subtitulo = "${u.optString("correoElectronico")} · $telefono",
                badge = rol,
                colorBadge = Util.colorEstado(rol),
                id = u.optInt("idUsuario")
            )
        }
}
