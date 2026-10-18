package com.concorde.movil.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.concorde.movil.databinding.ActivityAtencionBinding
import com.concorde.movil.red.ApiCliente
import com.concorde.movil.red.SesionUsuario
import kotlinx.coroutines.launch

/** Atención al cliente: chat, preguntas frecuentes y PQRS (equivale a p12-atencion.html). */
class AtencionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAtencionBinding

    private val tipos = listOf("PETICION", "QUEJA", "RECLAMO", "SUGERENCIA")
    private val tiposVisibles = listOf("Petición", "Queja", "Reclamo", "Sugerencia")

    private val faq = listOf(
        "¿Cómo reservo un tiquete?" to "Busca tu viaje en la pantalla de inicio, elige tu asiento, el método de pago y confirma. ¡En un minuto tienes tu reserva!",
        "¿Cuánto equipaje puedo llevar?" to "Un artículo personal y un equipaje de mano de hasta 10 kg, más una maleta en bodega de hasta 23 kg en clase económica.",
        "¿Puedo cancelar mi reserva?" to "Sí. Entra a \"Mis reservas\", toca la reserva y elige cancelar. Tu asiento queda libre para otra persona.",
        "¿Qué medios de pago aceptan?" to "Tarjeta de crédito o débito, PSE, efectivo en puntos autorizados y transferencia bancaria.",
        "¿Puedo viajar con mi mascota?" to "Sí: mascotas pequeñas en transportador (hasta 8 kg). Las más grandes viajan en bodega según el destino."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAtencionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.textoTitulo.text = "Atención al cliente"
        binding.toolbar.botonAtras.visibility = View.VISIBLE
        binding.toolbar.botonAtras.setOnClickListener { finish() }

        binding.botonAbrirChat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        pintarFaq()

        binding.spinnerTipo.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tiposVisibles)
        binding.botonRadicar.setOnClickListener { radicar() }
    }

    override fun onResume() {
        super.onResume()
        cargarMisPqrs()
    }

    private fun pintarFaq() {
        binding.contenedorFaq.removeAllViews()
        faq.forEach { (pregunta, respuesta) ->
            val tarjeta = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(32, 24, 32, 24)
                background = GradientDrawable().apply { cornerRadius = 28f; setColor(Color.WHITE) }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = 14 }
            }
            tarjeta.addView(TextView(this).apply {
                text = pregunta; setTextColor(Util.AZUL); textSize = 14f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            })
            tarjeta.addView(TextView(this).apply {
                text = respuesta; setTextColor(Color.parseColor("#555555")); textSize = 13f
                setPadding(0, 6, 0, 0)
            })
            binding.contenedorFaq.addView(tarjeta)
        }
    }

    private fun radicar() {
        val asunto = binding.campoAsunto.text.toString().trim()
        val descripcion = binding.campoDescripcion.text.toString().trim()
        if (asunto.isEmpty() || descripcion.isEmpty()) {
            mostrarEstado("Completa el asunto y la descripción.", error = true)
            return
        }
        val tipo = tipos[binding.spinnerTipo.selectedItemPosition]

        binding.botonRadicar.isEnabled = false
        lifecycleScope.launch {
            try {
                ApiCliente.radicarPqrs(tipo, asunto, descripcion)
                binding.campoAsunto.setText("")
                binding.campoDescripcion.setText("")
                mostrarEstado("Tu PQRS fue radicada. Un asesor te responderá pronto.", error = false)
                cargarMisPqrs()
            } catch (e: Exception) {
                mostrarEstado(e.message ?: "No se pudo radicar la PQRS.", error = true)
            } finally {
                binding.botonRadicar.isEnabled = true
            }
        }
    }

    private fun cargarMisPqrs() {
        val usuario = SesionUsuario.usuarioActual
        if (usuario == null) {
            binding.tituloMisPqrs.visibility = View.GONE
            binding.contenedorMisPqrs.removeAllViews()
            return
        }
        lifecycleScope.launch {
            try {
                val lista = ApiCliente.misPqrs(usuario.idUsuario)
                binding.contenedorMisPqrs.removeAllViews()
                binding.tituloMisPqrs.visibility = if (lista.isEmpty()) View.GONE else View.VISIBLE
                lista.forEach { p ->
                    val tarjeta = LinearLayout(this@AtencionActivity).apply {
                        orientation = LinearLayout.VERTICAL
                        setPadding(32, 24, 32, 24)
                        background = GradientDrawable().apply { cornerRadius = 28f; setColor(Color.WHITE) }
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { topMargin = 14 }
                    }
                    tarjeta.addView(TextView(this@AtencionActivity).apply {
                        text = "${p.estado} · ${p.tipo} · ${p.asunto}"
                        setTextColor(Util.colorEstado(p.estado)); textSize = 13f
                        setTypeface(typeface, android.graphics.Typeface.BOLD)
                    })
                    tarjeta.addView(TextView(this@AtencionActivity).apply {
                        text = p.descripcion; setTextColor(Color.parseColor("#555555")); textSize = 13f
                    })
                    if (p.respuesta != null) {
                        tarjeta.addView(TextView(this@AtencionActivity).apply {
                            text = "Respuesta: ${p.respuesta}"
                            setTextColor(Util.AZUL); textSize = 13f; setPadding(0, 8, 0, 0)
                        })
                    }
                    binding.contenedorMisPqrs.addView(tarjeta)
                }
            } catch (e: Exception) {
                // Si no se puede cargar el historial, el formulario sigue funcionando.
            }
        }
    }

    private fun mostrarEstado(mensaje: String, error: Boolean) {
        binding.textoEstadoPqrs.text = mensaje
        binding.textoEstadoPqrs.setTextColor(if (error) Util.ROJO else Util.VERDE)
        binding.textoEstadoPqrs.visibility = View.VISIBLE
    }
}
