package com.concorde.movil.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.concorde.movil.databinding.ActivityLoginBinding
import com.concorde.movil.red.ApiCliente
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.textoTitulo.text = "Iniciar sesión"
        binding.toolbar.botonAtras.visibility = View.VISIBLE
        binding.toolbar.botonAtras.setOnClickListener { finish() }

        binding.botonIngresar.setOnClickListener { iniciarSesion() }
        binding.botonCancelar.setOnClickListener { finish() }
        binding.textoIrRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }

    private fun iniciarSesion() {
        val correo = binding.campoCorreo.text.toString().trim()
        val contrasena = binding.campoContrasena.text.toString()

        if (correo.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Completa correo y contraseña.")
            return
        }

        binding.progreso.visibility = View.VISIBLE
        binding.botonIngresar.isEnabled = false
        binding.textoError.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val usuario = ApiCliente.login(correo, contrasena)
                // Cada rol aterriza en su propia pantalla, igual que en la
                // web (cliente -> index, agente -> agente-panel, admin ->
                // admin-panel).
                val destino: Class<*>? = when (usuario.rol) {
                    "ADMIN" -> AdminPanelActivity::class.java
                    "AGENTE" -> AgentePanelActivity::class.java
                    else -> null // cliente: simplemente vuelve a la pantalla desde donde llegó
                }
                if (destino != null) startActivity(Intent(this@LoginActivity, destino))
                finish()
            } catch (e: Exception) {
                mostrarError(e.message ?: "No se pudo iniciar sesión.")
            } finally {
                binding.progreso.visibility = View.GONE
                binding.botonIngresar.isEnabled = true
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        binding.textoError.text = mensaje
        binding.textoError.visibility = View.VISIBLE
    }
}
