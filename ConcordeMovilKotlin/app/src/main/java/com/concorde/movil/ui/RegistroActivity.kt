package com.concorde.movil.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.concorde.movil.databinding.ActivityRegistroBinding
import com.concorde.movil.red.ApiCliente
import kotlinx.coroutines.launch

/** Crear cuenta (equivale a p04-registro.html). El backend siempre asigna el rol CLIENTE. */
class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.textoTitulo.text = "Registro"
        binding.toolbar.botonAtras.visibility = View.VISIBLE
        binding.toolbar.botonAtras.setOnClickListener { finish() }

        binding.botonRegistrar.setOnClickListener { registrar() }
    }

    private fun registrar() {
        val nombre = binding.campoNombre.text.toString().trim()
        val documento = binding.campoDocumento.text.toString().trim()
        val telefono = binding.campoTelefono.text.toString().trim()
        val correo = binding.campoCorreo.text.toString().trim()
        val contrasena = binding.campoContrasena.text.toString()
        val confirmar = binding.campoConfirmar.text.toString()

        val problema = when {
            nombre.isEmpty() || documento.isEmpty() || correo.isEmpty() -> "Completa nombre, documento y correo."
            contrasena.length < 8 -> "La contraseña debe tener mínimo 8 caracteres."
            contrasena != confirmar -> "Las contraseñas no coinciden."
            else -> null
        }
        if (problema != null) { mostrarError(problema); return }

        binding.progreso.visibility = View.VISIBLE
        binding.botonRegistrar.isEnabled = false
        binding.textoError.visibility = View.GONE

        lifecycleScope.launch {
            try {
                ApiCliente.registrar(nombre, documento, telefono, correo, contrasena)
                Toast.makeText(this@RegistroActivity, "¡Cuenta creada! Ya puedes iniciar sesión.", Toast.LENGTH_LONG).show()
                finish() // vuelve al login
            } catch (e: Exception) {
                mostrarError(e.message ?: "No se pudo crear la cuenta.")
            } finally {
                binding.progreso.visibility = View.GONE
                binding.botonRegistrar.isEnabled = true
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        binding.textoError.text = mensaje
        binding.textoError.visibility = View.VISIBLE
    }
}
