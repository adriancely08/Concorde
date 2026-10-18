package com.concorde.movil.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.concorde.movil.databinding.ItemViajeBinding
import com.concorde.movil.modelo.Viaje

class ViajesAdapter(
    private var viajes: List<Viaje>,
    private val alElegir: (Viaje) -> Unit
) : RecyclerView.Adapter<ViajesAdapter.ViajeViewHolder>() {

    fun actualizar(nuevos: List<Viaje>) {
        viajes = nuevos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViajeViewHolder =
        ViajeViewHolder(ItemViajeBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViajeViewHolder, position: Int) = holder.bind(viajes[position], alElegir)

    override fun getItemCount(): Int = viajes.size

    class ViajeViewHolder(private val binding: ItemViajeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viaje: Viaje, alElegir: (Viaje) -> Unit) {
            binding.textoRuta.text = "${viaje.ciudadOrigen} → ${viaje.ciudadDestino}"
            binding.textoDetalle.text = "${viaje.fechaViaje} · ${viaje.horaSalida} · ${viaje.estadoViaje}"
            binding.textoPrecio.text = Util.dinero(viaje.precio)
            val disponible = viaje.estadoViaje != "CANCELADO" && viaje.estadoViaje != "FINALIZADO"
            binding.botonReservar.isEnabled = disponible
            binding.botonReservar.text = if (disponible) "Comprar" else viaje.estadoViaje
            binding.botonReservar.setOnClickListener { alElegir(viaje) }
            binding.root.setOnClickListener { if (disponible) alElegir(viaje) }
        }
    }
}
