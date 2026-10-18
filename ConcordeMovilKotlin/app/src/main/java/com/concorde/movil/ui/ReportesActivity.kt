package com.concorde.movil.ui

import com.concorde.movil.red.ApiCliente

/** Admin: resumen de reportes (los mismos que el panel web: reservas por estado, ingresos y rutas más vendidas). */
class ReportesActivity : ListaBaseActivity() {
    override val titulo: String get() = "Reportes"

    override suspend fun cargarItems(): List<ItemGenerico> {
        val items = mutableListOf<ItemGenerico>()

        ApiCliente.reporteReservasPorEstado().forEach { (estado, cantidad) ->
            items += ItemGenerico("Reservas $estado", "Cantidad total", "$cantidad", Util.colorEstado(estado))
        }
        ApiCliente.reporteIngresosPorRuta().forEach { (ruta, ingresos) ->
            items += ItemGenerico("Ingresos · $ruta", Util.dinero(ingresos), "INGRESOS", Util.AZUL)
        }
        ApiCliente.reporteRutasMasVendidas().forEach { (ruta, reservas) ->
            items += ItemGenerico("Más vendida · $ruta", "$reservas reservas no canceladas", "TOP", Util.DORADO)
        }
        return items
    }
}
