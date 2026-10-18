package com.concorde.movil.ui

/** Panel del administrador (equivale a admin-panel.html). */
class AdminPanelActivity : PanelBaseActivity() {
    override val rolesPermitidos: Set<String> get() = setOf("ADMIN")
    override val tituloPanel: String get() = "Panel de administrador"
    override val opciones: List<Opcion>
        get() = listOf(
            Opcion("📊", "Reportes", "Reservas, ingresos y rutas más vendidas", ReportesActivity::class.java),
            Opcion("👥", "Usuarios", "Clientes, agentes y administradores", UsuariosActivity::class.java),
            Opcion("🎫", "Reservas", "Todas las reservas de los clientes", ReservasAdminActivity::class.java),
            Opcion("💳", "Pagos", "Historial de pagos y su estado", PagosActivity::class.java),
            Opcion("📝", "PQRS", "Peticiones, quejas, reclamos y sugerencias", PqrsPendientesActivity::class.java),
            Opcion("💬", "Chat con clientes", "Conversaciones escaladas por el chatbot", ChatsPendientesActivity::class.java),
            Opcion("🚌", "Viajes", "Ver los viajes programados", ViajesActivity::class.java),
            Opcion("👤", "Mi perfil", "Mis datos y contraseña", PerfilActivity::class.java)
        )
}
