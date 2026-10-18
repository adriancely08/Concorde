package com.concorde.movil.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.concorde.movil.databinding.ActivityListaBinding
import kotlinx.coroutines.launch

/**
 * Pantalla base para todas las listas de administración (usuarios,
 * reservas, pagos, PQRS, chats, reportes). Cada una solo define su
 * título y cómo obtener los datos; el resto (barra con logo, botón
 * volver, "tirar para refrescar", mensajes de error/vacío) es común.
 */
abstract class ListaBaseActivity : AppCompatActivity() {

    protected lateinit var binding: ActivityListaBinding
    private lateinit var adaptador: GenericoAdapter

    abstract val titulo: String
    open val mensajeVacio: String get() = "No hay información para mostrar."
    abstract suspend fun cargarItems(): List<ItemGenerico>
    open fun alTocar(item: ItemGenerico) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.textoTitulo.text = titulo
        binding.toolbar.botonAtras.visibility = View.VISIBLE
        binding.toolbar.botonAtras.setOnClickListener { finish() }

        adaptador = GenericoAdapter(emptyList()) { alTocar(it) }
        binding.lista.layoutManager = LinearLayoutManager(this)
        binding.lista.adapter = adaptador
        binding.swipeRefresh.setOnRefreshListener { cargar() }
        cargar()
    }

    override fun onResume() {
        super.onResume()
        // Si el usuario vuelve de otra pantalla (ej. el chat), se refresca la lista.
        if (::adaptador.isInitialized) cargar()
    }

    protected fun cargar() {
        binding.swipeRefresh.isRefreshing = true
        binding.textoError.visibility = View.GONE
        lifecycleScope.launch {
            try {
                val items = cargarItems()
                adaptador.actualizar(items)
                binding.textoVacio.text = mensajeVacio
                binding.textoVacio.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            } catch (e: Exception) {
                binding.textoError.text = e.message ?: "No se pudo cargar la información."
                binding.textoError.visibility = View.VISIBLE
            } finally {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }
}
