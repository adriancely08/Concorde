package com.concorde.movil.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.concorde.movil.databinding.ItemGenericoBinding

/** Una fila de cualquier lista (usuarios, pagos, PQRS, chats, reportes...). */
data class ItemGenerico(
    val titulo: String,
    val subtitulo: String,
    val badge: String? = null,
    val colorBadge: Int = Color.GRAY,
    val id: Int = 0
)

class GenericoAdapter(
    private var items: List<ItemGenerico>,
    private val alTocar: (ItemGenerico) -> Unit
) : RecyclerView.Adapter<GenericoAdapter.VH>() {

    fun actualizar(nuevos: List<ItemGenerico>) {
        items = nuevos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemGenericoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position], alTocar)

    override fun getItemCount(): Int = items.size

    class VH(private val binding: ItemGenericoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemGenerico, alTocar: (ItemGenerico) -> Unit) {
            binding.textoTitulo.text = item.titulo
            binding.textoSubtitulo.text = item.subtitulo
            if (item.badge != null) {
                binding.textoBadge.visibility = View.VISIBLE
                binding.textoBadge.text = item.badge
                binding.textoBadge.setBackgroundColor(item.colorBadge)
            } else {
                binding.textoBadge.visibility = View.GONE
            }
            binding.root.setOnClickListener { alTocar(item) }
        }
    }
}
