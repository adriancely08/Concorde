package com.concorde.movil.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.concorde.movil.databinding.ActivityChatBinding
import com.concorde.movil.red.ApiCliente
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Chat de atención al cliente. Sirve para dos casos:
 *  - Cliente/visitante: habla con el chatbot; si el bot no puede, la
 *    conversación pasa a un asesor humano (igual que el widget web).
 *  - Agente/admin (EXTRA_MODO_AGENTE): abre una conversación ya
 *    escalada y responde como asesor.
 * Se refresca sola cada 5 segundos (polling, igual que la web).
 */
class ChatActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID_CONSULTA = "idConsulta"
        const val EXTRA_MODO_AGENTE = "modoAgente"
        const val EXTRA_TITULO = "titulo"
    }

    private lateinit var binding: ActivityChatBinding
    private lateinit var adaptador: MensajesAdapter
    private var idConsulta: Int = -1
    private var modoAgente = false
    private var estado = "BOT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        modoAgente = intent.getBooleanExtra(EXTRA_MODO_AGENTE, false)
        idConsulta = intent.getIntExtra(EXTRA_ID_CONSULTA, -1)

        binding.toolbar.textoTitulo.text =
            intent.getStringExtra(EXTRA_TITULO) ?: if (modoAgente) "Conversación" else "Asistente Concorde"
        binding.toolbar.botonAtras.visibility = View.VISIBLE
        binding.toolbar.botonAtras.setOnClickListener { finish() }

        adaptador = MensajesAdapter(if (modoAgente) "AGENTE" else "CLIENTE")
        binding.listaMensajes.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        binding.listaMensajes.adapter = adaptador

        binding.botonEnviar.setOnClickListener { enviarDesdeCampo() }

        if (modoAgente) {
            binding.contenedorChips.visibility = View.GONE
            binding.botonCerrarTicket.visibility = View.VISIBLE
            binding.botonCerrarTicket.setOnClickListener { cerrarTicket() }
        } else {
            binding.chipHorarios.setOnClickListener { enviar("¿Cuál es el horario de los buses?") }
            binding.chipEquipaje.setOnClickListener { enviar("¿Cuánto equipaje puedo llevar?") }
            binding.chipCambios.setOnClickListener { enviar("Necesito cambiar o cancelar mi reserva") }
            binding.chipAsesor.setOnClickListener { enviar("Quiero hablar con un asesor") }
        }

        lifecycleScope.launch { arrancar() }

        // Refresco automático mientras la pantalla está visible.
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    delay(5000)
                    if (idConsulta != -1) refrescar()
                }
            }
        }
    }

    private suspend fun arrancar() {
        try {
            if (idConsulta == -1) {
                val (id, mensajes) = ApiCliente.iniciarChat()
                idConsulta = id
                adaptador.reemplazar(mensajes)
            } else {
                refrescar()
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message ?: "No se pudo conectar con el chat.", Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun refrescar() {
        try {
            val (nuevoEstado, mensajes) = ApiCliente.mensajesChat(idConsulta)
            val habiaMensajes = binding.listaMensajes.adapter?.itemCount ?: 0
            estado = nuevoEstado
            adaptador.reemplazar(mensajes)
            if (mensajes.size != habiaMensajes) binding.listaMensajes.scrollToPosition(maxOf(mensajes.size - 1, 0))
            actualizarAviso()
        } catch (e: Exception) {
            // Si falla un refresco automático no se molesta al usuario; se reintenta en el siguiente ciclo.
        }
    }

    private fun actualizarAviso() {
        val texto = when (estado) {
            "ESCALADA" -> if (modoAgente) "Esta conversación espera un asesor." else "Tu consulta fue trasladada a un asesor. En breve te responderá por aquí."
            "EN_ATENCION" -> if (modoAgente) "Estás atendiendo esta conversación." else "Un asesor está atendiendo tu consulta."
            "CERRADA" -> "Esta conversación fue cerrada."
            else -> null
        }
        binding.textoAviso.visibility = if (texto == null) View.GONE else View.VISIBLE
        binding.textoAviso.text = texto ?: ""
        if (!modoAgente) {
            binding.contenedorChips.visibility = if (estado == "BOT" || estado == "CERRADA") View.VISIBLE else View.GONE
        }
    }

    private fun enviarDesdeCampo() {
        val texto = binding.campoMensaje.text.toString().trim()
        if (texto.isEmpty()) return
        binding.campoMensaje.setText("")
        enviar(texto)
    }

    private fun enviar(texto: String) {
        if (idConsulta == -1) {
            Toast.makeText(this, "Conectando con el chat, intenta de nuevo en un momento.", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            try {
                if (modoAgente) {
                    ApiCliente.responderChat(idConsulta, texto)
                } else {
                    ApiCliente.enviarMensajeChat(idConsulta, texto)
                }
                refrescar()
            } catch (e: Exception) {
                Toast.makeText(this@ChatActivity, e.message ?: "No se pudo enviar el mensaje.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun cerrarTicket() {
        lifecycleScope.launch {
            try {
                ApiCliente.cerrarChat(idConsulta)
                Toast.makeText(this@ChatActivity, "Ticket cerrado", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@ChatActivity, e.message ?: "No se pudo cerrar.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
