package com.concorde.movil.ui

import android.content.Intent
import com.concorde.movil.red.ApiCliente

/** Agente/Admin: conversaciones del chatbot que pasaron a un asesor humano. */
class ChatsPendientesActivity : ListaBaseActivity() {
    override val titulo: String get() = "Chat con clientes"
    override val mensajeVacio: String get() = "No hay conversaciones esperando un asesor."

    override suspend fun cargarItems(): List<ItemGenerico> =
        ApiCliente.consultasPendientes().map { c ->
            ItemGenerico(
                titulo = c.nombreContacto,
                subtitulo = "${c.correoContacto} · Ticket #${c.idConsulta}",
                badge = if (c.estado == "ESCALADA") "Esperando asesor" else "En atención",
                colorBadge = Util.colorEstado(c.estado),
                id = c.idConsulta
            )
        }

    override fun alTocar(item: ItemGenerico) {
        startActivity(Intent(this, ChatActivity::class.java).apply {
            putExtra(ChatActivity.EXTRA_ID_CONSULTA, item.id)
            putExtra(ChatActivity.EXTRA_MODO_AGENTE, true)
            putExtra(ChatActivity.EXTRA_TITULO, item.titulo)
        })
    }
}
