package com.concorde.movil.ui

import com.concorde.movil.red.ApiCliente

/** Admin/Agente: todas las reservas de todos los clientes. */
class ReservasAdminActivity : ListaBaseActivity() {
    override val titulo: String get() = "Reservas"
    override val mensajeVacio: String get() = "Todavía no hay reservas."

    override suspend fun cargarItems(): List<ItemGenerico> =
        ApiCliente.listarTodasLasReservas().map { r ->
            ItemGenerico(
                titulo = "#${r.idReserva} · ${r.ciudadOrigen} → ${r.ciudadDestino}",
                subtitulo = "${r.nombreCliente} · Viaje ${r.fechaViaje} · Reservado ${r.fechaInicial}",
                badge = r.estado,
                colorBadge = Util.colorEstado(r.estado),
                id = r.idReserva
            )
        }
}
