package com.concorde.movil.ui

import android.graphics.Color
import java.text.NumberFormat
import java.util.Locale

/** Formatos y colores compartidos por todas las pantallas (misma paleta que la web). */
object Util {
    val AZUL = Color.parseColor("#003580")
    val DORADO = Color.parseColor("#F5A623")
    val VERDE = Color.parseColor("#2ECC71")
    val ROJO = Color.parseColor("#E74C3C")
    val GRIS = Color.parseColor("#7F8C8D")

    fun dinero(valor: Double): String =
        "$" + NumberFormat.getNumberInstance(Locale("es", "CO")).format(valor)

    /** Color del "chip" de estado, igual que los badges de la web. */
    fun colorEstado(estado: String): Int = when (estado.uppercase()) {
        "CONFIRMADA", "PROCESADO", "RESUELTA", "CERRADA", "PROGRAMADO", "ADMIN" -> VERDE
        "CANCELADA", "CANCELADO", "FALLIDO", "REEMBOLSADO" -> ROJO
        "ESCALADA", "PENDIENTE", "ABIERTA", "AGENTE" -> DORADO
        "EN_ATENCION", "EN_PROCESO", "EN_CURSO" -> AZUL
        else -> GRIS
    }
}
