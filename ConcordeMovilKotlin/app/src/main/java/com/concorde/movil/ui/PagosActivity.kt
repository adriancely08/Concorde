package com.concorde.movil.ui

import com.concorde.movil.red.ApiCliente

/** Admin/Agente: historial de pagos (GET /api/pagos). */
class PagosActivity : ListaBaseActivity() {
    override val titulo: String get() = "Pagos"
    override val mensajeVacio: String get() = "Todavía no hay pagos registrados."

    override suspend fun cargarItems(): List<ItemGenerico> =
        ApiCliente.listarPagos().map { p ->
            val cliente = p.optJSONObject("reserva")?.optJSONObject("usuario")?.optString("nombreCompleto") ?: "—"
            val estado = p.optString("estado")
            ItemGenerico(
                titulo = "Pago #${p.optInt("idPago")} · ${Util.dinero(p.optDouble("valorPagado", 0.0))}",
                subtitulo = "$cliente · ${p.optString("metodoPago")} · ${p.optString("fechaPago")}",
                badge = estado,
                colorBadge = Util.colorEstado(estado),
                id = p.optInt("idPago")
            )
        }
}
