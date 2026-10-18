package com.concorde.movil.ui

/** Panel del agente (equivale a agente-panel.html). */
class AgentePanelActivity : PanelBaseActivity() {
    override val rolesPermitidos: Set<String> get() = setOf("AGENTE", "ADMIN")
    override val tituloPanel: String get() = "Panel de agente"
    override val opciones: List<Opcion>
        get() = listOf(
            Opcion("💬", "Chat con clientes", "Conversaciones que esperan un asesor", ChatsPendientesActivity::class.java),
            Opcion("📝", "PQRS", "Responder peticiones, quejas y reclamos", PqrsPendientesActivity::class.java),
            Opcion("🎫", "Reservas", "Todas las reservas de los clientes", ReservasAdminActivity::class.java),
            Opcion("💳", "Pagos", "Historial de pagos y su estado", PagosActivity::class.java),
            Opcion("👥", "Usuarios", "Clientes registrados", UsuariosActivity::class.java),
            Opcion("🚌", "Viajes", "Ver los viajes programados", ViajesActivity::class.java),
            Opcion("👤", "Mi perfil", "Mis datos y contraseña", PerfilActivity::class.java)
        )
}
