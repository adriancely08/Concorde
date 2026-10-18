package com.concorde.movil.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.concorde.movil.databinding.ActivityPerfilBinding
import com.concorde.movil.red.ApiCliente
import com.concorde.movil.red.SesionUsuario
import kotlinx.coroutines.launch

/** Ver/editar el perfil propio y cerrar sesión (equivale a p16-perfil.html). */
class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val usuario = SesionUsuario.usuarioActual
        if (usuario == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.textoTitulo.text = "Mi perfil"
        binding.toolbar.botonAtras.visibility = View.VISIBLE
        binding.toolbar.botonAtras.setOnClickListener { finish() }

        binding.textoNombreCabecera.text = usuario.nombreCompleto
        binding.textoCorreo.text = usuario.correoElectronico
        binding.textoRol.text = usuario.rol

        lifecycleScope.launch {
            try {
                val perfil = ApiCliente.obtenerPerfil(usuario.idUsuario)
                binding.campoNombre.setText(perfil.optString("nombreCompleto"))
                binding.campoTelefono.setText(perfil.optString("telefono").takeIf { it != "null" } ?: "")
            } catch (e: Exception) {
                mostrarEstado(e.message ?: "No se pudo cargar el perfil.", error = true)
            }
        }

        binding.botonGuardar.setOnClickListener { guardar(usuario.idUsuario) }

        binding.botonCerrarSesion.setOnClickListener {
            SesionUsuario.cerrarSesion()
            startActivity(Intent(this, ViajesActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }

    private fun guardar(idUsuario: Int) {
        val nombre = binding.campoNombre.text.toString().trim()
        val telefono = binding.campoTelefono.text.toString().trim()
        val nueva = binding.campoContrasena.text.toString()

        if (nombre.isEmpty()) { mostrarEstado("El nombre no puede estar vacío.", error = true); return }
        if (nueva.isNotEmpty() && nueva.length < 8) {
            mostrarEstado("La nueva contraseña debe tener mínimo 8 caracteres.", error = true); return
        }

        binding.botonGuardar.isEnabled = false
        lifecycleScope.launch {
            try {
                ApiCliente.actualizarPerfil(idUsuario, nombre, telefono, nueva)
                binding.textoNombreCabecera.text = nombre
                binding.campoContrasena.setText("")
                mostrarEstado("Cambios guardados.", error = false)
            } catch (e: Exception) {
                mostrarEstado(e.message ?: "No se pudo guardar.", error = true)
            } finally {
                binding.botonGuardar.isEnabled = true
            }
        }
    }

    private fun mostrarEstado(mensaje: String, error: Boolean) {
        binding.textoEstado.text = mensaje
        binding.textoEstado.setTextColor(if (error) Util.ROJO else Util.VERDE)
        binding.textoEstado.visibility = View.VISIBLE
    }
}
