package com.concorde.movil.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.concorde.movil.databinding.ActivityPanelBinding
import com.concorde.movil.databinding.ItemOpcionPanelBinding
import com.concorde.movil.red.SesionUsuario

/** Una tarjeta de opción dentro de un panel (admin o agente). */
data class Opcion(val icono: String, val titulo: String, val descripcion: String, val destino: Class<*>)

/**
 * Base de los paneles por rol (equivalen a admin-panel.html y
 * agente-panel.html de la web). Si alguien llega aquí sin sesión, o
 * con un rol que no corresponde, se le manda al login: es la misma
 * protección que hace auth.js en la web.
 */
abstract class PanelBaseActivity : AppCompatActivity() {

    abstract val rolesPermitidos: Set<String>
    abstract val tituloPanel: String
    abstract val opciones: List<Opcion>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val usuario = SesionUsuario.usuarioActual
        if (usuario == null || usuario.rol !in rolesPermitidos) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val binding = ActivityPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.textoTitulo.text = tituloPanel
        binding.toolbar.botonAtras.visibility = View.GONE
        binding.textoBienvenida.text = "Hola, ${usuario.nombreCompleto}"
        binding.textoRol.text = usuario.rol

        opciones.forEach { opcion ->
            val tarjeta = ItemOpcionPanelBinding.inflate(layoutInflater, binding.contenedorOpciones, false)
            tarjeta.textoIcono.text = opcion.icono
            tarjeta.textoTituloOpcion.text = opcion.titulo
            tarjeta.textoDescOpcion.text = opcion.descripcion
            tarjeta.root.setOnClickListener { startActivity(Intent(this, opcion.destino)) }
            binding.contenedorOpciones.addView(tarjeta.root)
        }

        binding.botonCerrarSesion.setOnClickListener {
            SesionUsuario.cerrarSesion()
            startActivity(Intent(this, ViajesActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }
}
