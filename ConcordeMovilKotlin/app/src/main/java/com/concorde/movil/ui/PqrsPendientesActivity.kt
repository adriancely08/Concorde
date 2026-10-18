package com.concorde.movil.ui

import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.concorde.movil.modelo.Pqrs
import com.concorde.movil.red.ApiCliente
import kotlinx.coroutines.launch

/** Agente/Admin: ver y responder las PQRS de los clientes. */
class PqrsPendientesActivity : ListaBaseActivity() {
    override val titulo: String get() = "PQRS"
    override val mensajeVacio: String get() = "No hay PQRS radicadas."

    private var actuales: List<Pqrs> = emptyList()

    override suspend fun cargarItems(): List<ItemGenerico> {
        actuales = ApiCliente.listarTodasPqrs()
        return actuales.map { p ->
            ItemGenerico(
                titulo = "${p.tipo} · ${p.asunto}",
                subtitulo = "${p.nombreCliente}: ${p.descripcion}",
                badge = p.estado,
                colorBadge = Util.colorEstado(p.estado),
                id = p.idPqrs
            )
        }
    }

    override fun alTocar(item: ItemGenerico) {
        val pqrs = actuales.firstOrNull { it.idPqrs == item.id } ?: return
        val detalle = "De: ${pqrs.nombreCliente} (${pqrs.correoCliente})\n\n${pqrs.descripcion}" +
                (pqrs.respuesta?.let { "\n\nRespuesta enviada:\n$it" } ?: "")

        val dialogo = AlertDialog.Builder(this)
            .setTitle("${pqrs.tipo} · ${pqrs.asunto}")
            .setMessage(detalle)
            .setNegativeButton("Cerrar", null)

        if (pqrs.respuesta == null) {
            val campo = EditText(this).apply { hint = "Escribe tu respuesta"; setPadding(48, 24, 48, 24) }
            dialogo.setView(campo)
            dialogo.setPositiveButton("Responder") { _, _ ->
                val texto = campo.text.toString().trim()
                if (texto.isEmpty()) {
                    Toast.makeText(this, "La respuesta no puede estar vacía.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                lifecycleScope.launch {
                    try {
                        ApiCliente.responderPqrs(pqrs.idPqrs, texto)
                        Toast.makeText(this@PqrsPendientesActivity, "Respuesta enviada", Toast.LENGTH_SHORT).show()
                        cargar()
                    } catch (e: Exception) {
                        Toast.makeText(this@PqrsPendientesActivity, e.message ?: "Error", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        dialogo.show()
    }
}
