package com.concorde.movil.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.concorde.movil.databinding.ActivityDetalleViajeBinding
import com.concorde.movil.modelo.Asiento
import com.concorde.movil.red.ApiCliente
import com.concorde.movil.red.SesionUsuario
import kotlinx.coroutines.launch

/**
 * Detalle de un viaje + compra completa en una sola pantalla. Reúne lo
 * que en la web son p05 (detalle), p07 (asientos), p08/p09 (resumen y
 * pago) y p10 (confirmación): elegir asiento, elegir método de pago,
 * y al confirmar se crea la reserva, se asigna el asiento y se
 * registra el pago (el backend pasa la reserva a CONFIRMADA solo).
 */
class DetalleViajeActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID_VIAJE = "idViaje"
        const val EXTRA_ID_VEHICULO = "idVehiculo"
        const val EXTRA_RUTA = "ruta"
        const val EXTRA_FECHA_HORA = "fechaHora"
        const val EXTRA_PRECIO = "precio"
    }

    private lateinit var binding: ActivityDetalleViajeBinding
    private var asientos: List<Asiento> = emptyList()
    private var idViaje = 0
    private var idVehiculo = 0
    private var precio = 0.0

    private val metodos = listOf("Tarjeta de crédito", "PSE", "Efectivo", "Transferencia bancaria")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleViajeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idViaje = intent.getIntExtra(EXTRA_ID_VIAJE, 0)
        idVehiculo = intent.getIntExtra(EXTRA_ID_VEHICULO, 0)
        precio = intent.getDoubleExtra(EXTRA_PRECIO, 0.0)

        binding.toolbar.textoTitulo.text = "Detalle del viaje"
        binding.toolbar.botonAtras.visibility = View.VISIBLE
        binding.toolbar.botonAtras.setOnClickListener { finish() }

        binding.textoRuta.text = intent.getStringExtra(EXTRA_RUTA) ?: "—"
        binding.textoFechaHora.text = intent.getStringExtra(EXTRA_FECHA_HORA) ?: ""
        binding.textoPrecio.text = Util.dinero(precio)

        binding.spinnerMetodo.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, metodos)

        binding.botonComprar.setOnClickListener { comprar() }
    }

    override fun onResume() {
        super.onResume()
        // Se refresca aquí por si el usuario vuelve del login ya con sesión.
        if (SesionUsuario.haySesion()) {
            binding.botonComprar.text = "Reservar y pagar ${Util.dinero(precio)}"
            cargarAsientos()
        } else {
            binding.botonComprar.text = "Inicia sesión para comprar"
        }
    }

    private fun cargarAsientos() {
        lifecycleScope.launch {
            try {
                asientos = ApiCliente.asientosDisponibles(idVehiculo)
                val etiquetas = asientos.map { "Asiento ${it.numeroAsiento} (${it.tipo})" }
                binding.spinnerAsiento.adapter =
                    ArrayAdapter(this@DetalleViajeActivity, android.R.layout.simple_spinner_dropdown_item, etiquetas)
                binding.textoSinAsientos.visibility = if (asientos.isEmpty()) View.VISIBLE else View.GONE
                binding.textoSinAsientos.text = "Este viaje no tiene asientos disponibles."
            } catch (e: Exception) {
                mostrarError(e.message ?: "No se pudieron cargar los asientos.")
            }
        }
    }

    private fun comprar() {
        if (!SesionUsuario.haySesion()) {
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }
        val posicion = binding.spinnerAsiento.selectedItemPosition
        if (asientos.isEmpty() || posicion < 0) {
            mostrarError("No hay un asiento disponible para reservar.")
            return
        }
        val asiento = asientos[posicion]
        val metodo = metodos[binding.spinnerMetodo.selectedItemPosition]

        binding.progreso.visibility = View.VISIBLE
        binding.botonComprar.isEnabled = false
        binding.textoError.visibility = View.GONE

        lifecycleScope.launch {
            var reservaCreada: Int? = null
            try {
                val idReserva = ApiCliente.crearReserva(idViaje)
                reservaCreada = idReserva
                ApiCliente.agregarAsiento(idReserva, asiento.idAsiento)
                ApiCliente.pagar(idReserva, metodo, precio)

                AlertDialog.Builder(this@DetalleViajeActivity)
                    .setTitle("¡Compra exitosa!")
                    .setMessage("Tu reserva #$idReserva quedó confirmada.\nAsiento ${asiento.numeroAsiento} · ${Util.dinero(precio)} · $metodo")
                    .setCancelable(false)
                    .setPositiveButton("Ver mis reservas") { _, _ ->
                        startActivity(Intent(this@DetalleViajeActivity, MisReservasActivity::class.java))
                        finish()
                    }
                    .show()
            } catch (e: Exception) {
                // Si algo falló después de crear la reserva, se cancela para no dejar una reserva "huérfana" pendiente.
                reservaCreada?.let { id -> runCatching { ApiCliente.cancelarReserva(id) } }
                mostrarError(e.message ?: "No se pudo completar la compra.")
                cargarAsientos()
            } finally {
                binding.progreso.visibility = View.GONE
                binding.botonComprar.isEnabled = true
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        binding.textoError.text = mensaje
        binding.textoError.visibility = View.VISIBLE
    }
}
