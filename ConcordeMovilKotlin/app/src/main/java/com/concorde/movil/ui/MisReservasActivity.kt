package com.concorde.movil.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.concorde.movil.red.ApiCliente
import com.concorde.movil.red.SesionUsuario
import kotlinx.coroutines.launch

/** Mis reservas (equivale a p11-reservas.html + cancelar de p15). Tocar una reserva permite cancelarla. */
class MisReservasActivity : ListaBaseActivity() {
    override val titulo: String get() = "Mis reservas"
    override val mensajeVacio: String get() = "Todavía no tienes reservas. ¡Busca tu próximo viaje!"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ver "Mis reservas" sí requiere sesión: sin ella se manda al login.
        if (!SesionUsuario.haySesion()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override suspend fun cargarItems(): List<ItemGenerico> =
        ApiCliente.misReservas().map { r ->
            ItemGenerico(
                titulo = "#${r.idReserva} · ${r.ciudadOrigen} → ${r.ciudadDestino}",
                subtitulo = "Viaje: ${r.fechaViaje} · Reservado el ${r.fechaInicial}",
                badge = r.estado,
                colorBadge = Util.colorEstado(r.estado),
                id = r.idReserva
            )
        }

    override fun alTocar(item: ItemGenerico) {
        if (item.badge == "CANCELADA") {
            Toast.makeText(this, "Esta reserva ya está cancelada.", Toast.LENGTH_SHORT).show()
            return
        }
        AlertDialog.Builder(this)
            .setTitle("Reserva #${item.id}")
            .setMessage("¿Quieres cancelar esta reserva? Tu asiento quedará libre para otra persona.")
            .setNegativeButton("No", null)
            .setPositiveButton("Sí, cancelar") { _, _ ->
                lifecycleScope.launch {
                    try {
                        ApiCliente.cancelarReserva(item.id)
                        Toast.makeText(this@MisReservasActivity, "Reserva cancelada", Toast.LENGTH_SHORT).show()
                        cargar()
                    } catch (e: Exception) {
                        Toast.makeText(this@MisReservasActivity, e.message ?: "No se pudo cancelar.", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .show()
    }
}
