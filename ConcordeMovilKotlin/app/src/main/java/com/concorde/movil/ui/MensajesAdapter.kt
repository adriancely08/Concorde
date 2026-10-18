package com.concorde.movil.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.concorde.movil.databinding.ItemMensajeBinding
import com.concorde.movil.modelo.Mensaje

/**
 * Burbujas del chat. "propio" es el remitente que se muestra a la
 * derecha en azul (CLIENTE para el cliente, AGENTE para el asesor).
 */
class MensajesAdapter(private val propio: String) : RecyclerView.Adapter<MensajesAdapter.VH>() {

    private var mensajes: List<Mensaje> = emptyList()

    fun reemplazar(nuevos: List<Mensaje>) {
        mensajes = nuevos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemMensajeBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(mensajes[position], propio)

    override fun getItemCount(): Int = mensajes.size

    class VH(private val binding: ItemMensajeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mensaje: Mensaje, propio: String) {
            val esPropio = mensaje.remitente == propio
            val etiqueta = when (mensaje.remitente) {
                "BOT" -> "Asistente\n"
                "AGENTE" -> if (esPropio) "" else "Asesor\n"
                "CLIENTE" -> if (esPropio) "" else "Cliente\n"
                else -> ""
            }
            binding.textoMensaje.text = etiqueta + mensaje.contenido
            binding.textoMensaje.setTextColor(if (esPropio) Color.WHITE else Color.parseColor("#1A1A2E"))

            val fondo = GradientDrawable().apply {
                cornerRadius = 36f
                setColor(if (esPropio) Color.parseColor("#003580") else Color.WHITE)
                if (!esPropio) setStroke(2, Color.parseColor("#DDE3EE"))
            }
            binding.textoMensaje.background = fondo
            binding.filaMensaje.gravity = if (esPropio) Gravity.END else Gravity.START
        }
    }
}
