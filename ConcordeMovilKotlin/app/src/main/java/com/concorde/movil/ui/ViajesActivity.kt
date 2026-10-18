package com.concorde.movil.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.concorde.movil.databinding.ActivityViajesBinding
import com.concorde.movil.modelo.Viaje
import com.concorde.movil.red.ApiCliente
import com.concorde.movil.red.SesionUsuario
import kotlinx.coroutines.launch

/**
 * Pantalla de inicio de la app (equivale a index.html + p02-resultados).
 * A propósito NO exige iniciar sesión: cualquiera ve los viajes
 * disponibles, igual que en la web (GET /api/viajes es público). Solo
 * se pide sesión al comprar, ver "Mis reservas" o abrir el perfil.
 */
class ViajesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViajesBinding
    private lateinit var adaptador: ViajesAdapter
    private var todos: List<Viaje> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViajesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.textoTitulo.text = "Concorde"
        // Si llegamos desde un panel (admin/agente) se puede volver atrás; si es la pantalla de inicio, no.
        binding.toolbar.botonAtras.visibility = if (isTaskRoot) View.GONE else View.VISIBLE
        binding.toolbar.botonAtras.setOnClickListener { finish() }

        adaptador = ViajesAdapter(emptyList()) { viaje -> abrirDetalle(viaje) }
        binding.listaViajes.layoutManager = LinearLayoutManager(this)
        binding.listaViajes.adapter = adaptador

        binding.swipeRefresh.setOnRefreshListener { cargarViajes() }
        binding.campoBuscar.doOnTextChanged { _, _, _, _ -> aplicarFiltro() }

        binding.navReservas.setOnClickListener { irSiHaySesion(MisReservasActivity::class.java) }
        binding.navAyuda.setOnClickListener { startActivity(Intent(this, AtencionActivity::class.java)) }
        binding.navPerfil.setOnClickListener { irSiHaySesion(PerfilActivity::class.java) }

        cargarViajes()
    }

    /** Si hay sesión abre la pantalla pedida; si no, manda al login (la navegación general sigue libre). */
    private fun irSiHaySesion(destino: Class<*>) {
        val clase = if (SesionUsuario.haySesion()) destino else LoginActivity::class.java
        startActivity(Intent(this, clase))
    }

    private fun cargarViajes() {
        binding.swipeRefresh.isRefreshing = true
        binding.textoError.visibility = View.GONE
        lifecycleScope.launch {
            try {
                todos = ApiCliente.buscarViajes()
                aplicarFiltro()
            } catch (e: Exception) {
                binding.textoError.text = e.message ?: "No se pudieron cargar los viajes."
                binding.textoError.visibility = View.VISIBLE
            } finally {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun aplicarFiltro() {
        val texto = binding.campoBuscar.text.toString().trim().lowercase()
        val filtrados = if (texto.isEmpty()) todos else todos.filter {
            it.ciudadOrigen.lowercase().contains(texto) || it.ciudadDestino.lowercase().contains(texto)
        }
        adaptador.actualizar(filtrados)
        binding.textoVacio.visibility = if (filtrados.isEmpty() && binding.textoError.visibility != View.VISIBLE) View.VISIBLE else View.GONE
    }

    private fun abrirDetalle(viaje: Viaje) {
        startActivity(Intent(this, DetalleViajeActivity::class.java).apply {
            putExtra(DetalleViajeActivity.EXTRA_ID_VIAJE, viaje.idViaje)
            putExtra(DetalleViajeActivity.EXTRA_ID_VEHICULO, viaje.idVehiculo)
            putExtra(DetalleViajeActivity.EXTRA_RUTA, "${viaje.ciudadOrigen} → ${viaje.ciudadDestino}")
            putExtra(DetalleViajeActivity.EXTRA_FECHA_HORA, "${viaje.fechaViaje} · ${viaje.horaSalida}")
            putExtra(DetalleViajeActivity.EXTRA_PRECIO, viaje.precio)
        })
    }
}
