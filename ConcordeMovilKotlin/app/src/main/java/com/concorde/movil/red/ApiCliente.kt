package com.concorde.movil.red

import com.concorde.movil.modelo.Asiento
import com.concorde.movil.modelo.ConsultaChat
import com.concorde.movil.modelo.Mensaje
import com.concorde.movil.modelo.Pqrs
import com.concorde.movil.modelo.Reserva
import com.concorde.movil.modelo.Usuario
import com.concorde.movil.modelo.Viaje
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.util.concurrent.TimeUnit

/**
 * Todo lo que la app necesita del backend, en un solo lugar.
 *
 * IMPORTANTE sobre BASE_URL:
 * - Emulador de Android Studio: deja "10.0.2.2" -- así es como el
 *   emulador ve al "localhost" de tu computador.
 * - Celular físico: cambia esto por la IP de tu computador en la red
 *   wifi (ej. "192.168.1.15"); celular y computador en la MISMA red.
 */
object ApiCliente {
    private const val BASE_URL = "http://10.0.2.2:8082/api"
    private val JSON = "application/json; charset=utf-8".toMediaType()

    private val cliente = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    // ───────────── utilidades internas ─────────────

    private fun peticion(ruta: String, conToken: Boolean = true): Request.Builder {
        val builder = Request.Builder().url("$BASE_URL$ruta")
        val token = SesionUsuario.usuarioActual?.token
        if (conToken && token != null) builder.addHeader("Authorization", "Bearer $token")
        return builder
    }

    private fun cuerpo(json: JSONObject) = json.toString().toRequestBody(JSON)

    /** Ejecuta la petición; si falla, lanza una Exception con el "detail" que mande el backend. */
    private fun ejecutar(req: Request, mensajeError: String): String {
        val (codigo, texto) = cliente.newCall(req).execute().use { res ->
            Pair(res.code, res.body?.string().orEmpty())
        }
        if (codigo !in 200..299) {
            val detalle = runCatching { JSONObject(texto).optString("detail") }.getOrNull()
            throw Exception(detalle?.takeIf { it.isNotBlank() } ?: "$mensajeError ($codigo)")
        }
        return texto
    }

    private fun arreglo(texto: String): List<JSONObject> {
        val a = JSONArray(texto)
        return (0 until a.length()).map { a.getJSONObject(it) }
    }

    // ───────────── autenticación ─────────────

    /** POST /api/login -- ruta pública. */
    suspend fun login(correo: String, contrasena: String): Usuario = withContext(Dispatchers.IO) {
        val req = peticion("/login", conToken = false).post(cuerpo(JSONObject().apply {
            put("correoElectronico", correo)
            put("contrasena", contrasena)
        })).build()
        val json = JSONObject(ejecutar(req, "Correo o contraseña incorrectos"))
        val usuario = Usuario(
            idUsuario = json.getInt("idUsuario"),
            nombreCompleto = json.optString("nombreCompleto"),
            correoElectronico = json.optString("correoElectronico"),
            rol = json.optString("rol", "CLIENTE"),
            token = json.getString("token")
        )
        SesionUsuario.usuarioActual = usuario
        usuario
    }

    /** POST /api/registro -- ruta pública; el backend siempre asigna rol CLIENTE. */
    suspend fun registrar(nombre: String, documento: String, telefono: String, correo: String, contrasena: String) =
        withContext(Dispatchers.IO) {
            val req = peticion("/registro", conToken = false).post(cuerpo(JSONObject().apply {
                put("nombreCompleto", nombre)
                put("numeroDocumento", documento)
                put("telefono", telefono)
                put("correoElectronico", correo)
                put("contrasena", contrasena)
            })).build()
            ejecutar(req, "No se pudo crear la cuenta")
            Unit
        }

    // ───────────── perfil ─────────────

    suspend fun obtenerPerfil(idUsuario: Int): JSONObject = withContext(Dispatchers.IO) {
        JSONObject(ejecutar(peticion("/usuarios/$idUsuario").get().build(), "No se pudo cargar el perfil"))
    }

    /** PUT /api/usuarios/{id}: manda el registro completo con nombre/teléfono nuevos (y contraseña solo si se escribió). */
    suspend fun actualizarPerfil(idUsuario: Int, nombre: String, telefono: String, nuevaContrasena: String?) =
        withContext(Dispatchers.IO) {
            val actual = JSONObject(ejecutar(peticion("/usuarios/$idUsuario").get().build(), "No se pudo cargar el perfil"))
            actual.put("nombreCompleto", nombre)
            actual.put("telefono", telefono)
            // El backend devuelve el hash actual; si no se escribe contraseña nueva se manda vacío
            // para que el backend conserve la existente (ver UsuarioController.actualizar).
            actual.put("contrasenaHash", nuevaContrasena?.takeIf { it.isNotBlank() } ?: "")
            ejecutar(peticion("/usuarios/$idUsuario").put(cuerpo(actual)).build(), "No se pudo actualizar el perfil")
            Unit
        }

    // ───────────── viajes y compra ─────────────

    /** GET /api/viajes -- ruta pública, funciona sin sesión. */
    suspend fun buscarViajes(): List<Viaje> = withContext(Dispatchers.IO) {
        val texto = ejecutar(peticion("/viajes", conToken = false).get().build(), "No se pudieron cargar los viajes")
        arreglo(texto).map { parsearViaje(it) }
    }

    /** GET /api/asientos filtrado por vehículo y disponibilidad. Exige sesión. */
    suspend fun asientosDisponibles(idVehiculo: Int): List<Asiento> = withContext(Dispatchers.IO) {
        val texto = ejecutar(peticion("/asientos").get().build(), "No se pudieron cargar los asientos")
        arreglo(texto)
            .filter {
                it.optJSONObject("vehiculo")?.optInt("idVehiculo") == idVehiculo &&
                        it.optBoolean("disponible", false)
            }
            .map {
                Asiento(
                    idAsiento = it.getInt("idAsiento"),
                    numeroAsiento = it.optString("numeroAsiento"),
                    tipo = it.optString("tipo")
                )
            }
    }

    /** POST /api/reservas -- devuelve el id de la reserva creada. */
    suspend fun crearReserva(idViaje: Int): Int = withContext(Dispatchers.IO) {
        val usuario = SesionUsuario.usuarioActual ?: throw Exception("Debes iniciar sesión para reservar.")
        val req = peticion("/reservas").post(cuerpo(JSONObject().apply {
            put("usuario", JSONObject().put("idUsuario", usuario.idUsuario))
            put("viaje", JSONObject().put("idViaje", idViaje))
            put("estado", "PENDIENTE")
        })).build()
        JSONObject(ejecutar(req, "No se pudo crear la reserva")).getInt("idReserva")
    }

    /** POST /api/detalle-reservas -- el backend valida que el asiento esté libre y lo marca ocupado. */
    suspend fun agregarAsiento(idReserva: Int, idAsiento: Int) = withContext(Dispatchers.IO) {
        val req = peticion("/detalle-reservas").post(cuerpo(JSONObject().apply {
            put("reserva", JSONObject().put("idReserva", idReserva))
            put("asiento", JSONObject().put("idAsiento", idAsiento))
            put("asientoAsignado", true)
        })).build()
        ejecutar(req, "No se pudo asignar el asiento")
        Unit
    }

    /** POST /api/pagos -- si el pago es PROCESADO, el backend confirma la reserva solo. */
    suspend fun pagar(idReserva: Int, metodo: String, valor: Double) = withContext(Dispatchers.IO) {
        val req = peticion("/pagos").post(cuerpo(JSONObject().apply {
            put("reserva", JSONObject().put("idReserva", idReserva))
            put("metodoPago", metodo)
            put("valorPagado", valor)
            put("fechaPago", LocalDate.now().toString())
            put("estado", "PROCESADO")
        })).build()
        ejecutar(req, "No se pudo procesar el pago")
        Unit
    }

    /** GET /api/reservas filtrado en la app por el usuario logueado. */
    suspend fun misReservas(): List<Reserva> = withContext(Dispatchers.IO) {
        val usuario = SesionUsuario.usuarioActual ?: return@withContext emptyList()
        val texto = ejecutar(peticion("/reservas").get().build(), "No se pudieron cargar tus reservas")
        arreglo(texto)
            .filter { it.optJSONObject("usuario")?.optInt("idUsuario") == usuario.idUsuario }
            .map { parsearReserva(it) }
            .sortedByDescending { it.idReserva }
    }

    /** PUT /api/reservas/{id} cambiando el estado a CANCELADA (el backend libera los asientos). */
    suspend fun cancelarReserva(idReserva: Int) = withContext(Dispatchers.IO) {
        val actual = JSONObject(ejecutar(peticion("/reservas/$idReserva").get().build(), "No se pudo cargar la reserva"))
        actual.put("estado", "CANCELADA")
        ejecutar(peticion("/reservas/$idReserva").put(cuerpo(actual)).build(), "No se pudo cancelar la reserva")
        Unit
    }

    // ───────────── PQRS ─────────────

    suspend fun radicarPqrs(tipo: String, asunto: String, descripcion: String) = withContext(Dispatchers.IO) {
        val usuario = SesionUsuario.usuarioActual
        val req = peticion("/pqrs", conToken = usuario != null).post(cuerpo(JSONObject().apply {
            put("tipo", tipo)
            put("asunto", asunto)
            put("descripcion", descripcion)
            if (usuario != null) put("idUsuario", usuario.idUsuario)
        })).build()
        ejecutar(req, "No se pudo radicar la PQRS")
        Unit
    }

    suspend fun misPqrs(idUsuario: Int): List<Pqrs> = withContext(Dispatchers.IO) {
        arreglo(ejecutar(peticion("/pqrs/usuario/$idUsuario").get().build(), "No se pudieron cargar tus PQRS"))
            .map { parsearPqrs(it) }
    }

    /** GET /api/pqrs -- todas, solo ADMIN/AGENTE. */
    suspend fun listarTodasPqrs(): List<Pqrs> = withContext(Dispatchers.IO) {
        arreglo(ejecutar(peticion("/pqrs").get().build(), "No se pudieron cargar las PQRS"))
            .map { parsearPqrs(it) }
    }

    suspend fun responderPqrs(idPqrs: Int, respuesta: String) = withContext(Dispatchers.IO) {
        val req = peticion("/pqrs/$idPqrs/responder").put(cuerpo(JSONObject().apply {
            put("respuesta", respuesta)
            put("estado", "RESUELTA")
        })).build()
        ejecutar(req, "No se pudo responder la PQRS")
        Unit
    }

    // ───────────── chat ─────────────

    /** POST /api/chatbot/iniciar -- devuelve el id de la conversación y el saludo del bot. */
    suspend fun iniciarChat(): Pair<Int, List<Mensaje>> = withContext(Dispatchers.IO) {
        val usuario = SesionUsuario.usuarioActual
        val cuerpoJson = JSONObject().apply {
            if (usuario != null) {
                put("usuarioId", usuario.idUsuario)
                put("nombre", usuario.nombreCompleto)
                put("correo", usuario.correoElectronico)
            }
        }
        val req = peticion("/chatbot/iniciar", conToken = false).post(cuerpo(cuerpoJson)).build()
        val json = JSONObject(ejecutar(req, "No se pudo iniciar el chat"))
        Pair(json.getInt("idConsulta"), parsearMensajes(json.optJSONArray("mensajes")))
    }

    /** POST /api/chatbot/mensaje -- devuelve (respuestaDelBot o null, estadoDeLaConversacion). */
    suspend fun enviarMensajeChat(idConsulta: Int, texto: String): Pair<String?, String> = withContext(Dispatchers.IO) {
        val req = peticion("/chatbot/mensaje", conToken = false).post(cuerpo(JSONObject().apply {
            put("idConsulta", idConsulta)
            put("texto", texto)
        })).build()
        val json = JSONObject(ejecutar(req, "No se pudo enviar el mensaje"))
        val respuesta = if (json.isNull("respuesta")) null else json.optString("respuesta")
        Pair(respuesta, json.optString("estado", "BOT"))
    }

    /** GET /api/chatbot/consultas/{id}/mensajes -- (estado, mensajes). */
    suspend fun mensajesChat(idConsulta: Int): Pair<String, List<Mensaje>> = withContext(Dispatchers.IO) {
        val json = JSONObject(ejecutar(peticion("/chatbot/consultas/$idConsulta/mensajes", conToken = false).get().build(),
            "No se pudo cargar la conversación"))
        Pair(json.optString("estado", "BOT"), parsearMensajes(json.optJSONArray("mensajes")))
    }

    /** GET /api/chatbot/consultas?estados=ESCALADA,EN_ATENCION -- tickets pendientes para el agente. */
    suspend fun consultasPendientes(): List<ConsultaChat> = withContext(Dispatchers.IO) {
        val texto = ejecutar(peticion("/chatbot/consultas?estados=ESCALADA,EN_ATENCION", conToken = false).get().build(),
            "No se pudieron cargar las conversaciones")
        arreglo(texto).map {
            ConsultaChat(
                idConsulta = it.getInt("idConsulta"),
                estado = it.optString("estado"),
                nombreContacto = it.optString("nombreContacto").takeIf { n -> n.isNotBlank() && n != "null" } ?: "Visitante",
                correoContacto = it.optString("correoContacto").takeIf { c -> c.isNotBlank() && c != "null" } ?: "—"
            )
        }
    }

    /** POST /api/chatbot/consultas/{id}/responder -- un agente responde. */
    suspend fun responderChat(idConsulta: Int, texto: String) = withContext(Dispatchers.IO) {
        val agente = SesionUsuario.usuarioActual
        val req = peticion("/chatbot/consultas/$idConsulta/responder", conToken = false).post(cuerpo(JSONObject().apply {
            put("texto", texto)
            if (agente != null) put("agenteId", agente.idUsuario)
        })).build()
        ejecutar(req, "No se pudo enviar la respuesta")
        Unit
    }

    suspend fun cerrarChat(idConsulta: Int) = withContext(Dispatchers.IO) {
        ejecutar(peticion("/chatbot/consultas/$idConsulta/cerrar", conToken = false)
            .post(cuerpo(JSONObject())).build(), "No se pudo cerrar la conversación")
        Unit
    }

    // ───────────── administración (ADMIN / AGENTE) ─────────────

    suspend fun listarUsuarios(): List<JSONObject> = withContext(Dispatchers.IO) {
        arreglo(ejecutar(peticion("/usuarios").get().build(), "No se pudieron cargar los usuarios"))
    }

    suspend fun listarTodasLasReservas(): List<Reserva> = withContext(Dispatchers.IO) {
        arreglo(ejecutar(peticion("/reservas").get().build(), "No se pudieron cargar las reservas"))
            .map { parsearReserva(it) }
            .sortedByDescending { it.idReserva }
    }

    suspend fun listarPagos(): List<JSONObject> = withContext(Dispatchers.IO) {
        arreglo(ejecutar(peticion("/pagos").get().build(), "No se pudieron cargar los pagos"))
            .sortedByDescending { it.optInt("idPago") }
    }

    /** GET /api/reportes/ingresos-por-ruta -> [{ruta, ingresos}] */
    suspend fun reporteIngresosPorRuta(): List<Pair<String, Double>> = withContext(Dispatchers.IO) {
        arreglo(ejecutar(peticion("/reportes/ingresos-por-ruta").get().build(), "No se pudo cargar el reporte"))
            .map { Pair(it.optString("ruta"), it.optDouble("ingresos", 0.0)) }
    }

    /** GET /api/reportes/rutas-mas-vendidas -> [{ruta, reservas}] */
    suspend fun reporteRutasMasVendidas(): List<Pair<String, Int>> = withContext(Dispatchers.IO) {
        arreglo(ejecutar(peticion("/reportes/rutas-mas-vendidas").get().build(), "No se pudo cargar el reporte"))
            .map { Pair(it.optString("ruta"), it.optInt("reservas", 0)) }
    }

    /** GET /api/reportes/reservas-por-estado -> {ESTADO: cantidad} */
    suspend fun reporteReservasPorEstado(): List<Pair<String, Int>> = withContext(Dispatchers.IO) {
        val json = JSONObject(ejecutar(peticion("/reportes/reservas-por-estado").get().build(), "No se pudo cargar el reporte"))
        json.keys().asSequence().map { Pair(it, json.optInt(it, 0)) }.toList()
    }

    // ───────────── parsers ─────────────

    private fun parsearMensajes(arr: JSONArray?): List<Mensaje> {
        if (arr == null) return emptyList()
        return (0 until arr.length()).map {
            val m = arr.getJSONObject(it)
            Mensaje(m.optString("remitente"), m.optString("contenido"))
        }
    }

    private fun parsearPqrs(json: JSONObject) = Pqrs(
        idPqrs = json.getInt("idPqrs"),
        tipo = json.optString("tipo"),
        asunto = json.optString("asunto"),
        descripcion = json.optString("descripcion"),
        estado = json.optString("estado"),
        respuesta = json.optString("respuesta").takeIf { it.isNotBlank() && it != "null" },
        nombreCliente = json.optString("nombreCliente", "—"),
        correoCliente = json.optString("correoCliente", "—")
    )

    private fun parsearViaje(json: JSONObject): Viaje {
        val ruta = json.optJSONObject("ruta")
        val origen = ruta?.optJSONObject("terminalOrigen")
        val destino = ruta?.optJSONObject("terminalDestino")
        return Viaje(
            idViaje = json.getInt("idViaje"),
            idVehiculo = json.optJSONObject("vehiculo")?.optInt("idVehiculo") ?: 0,
            fechaViaje = json.optString("fechaViaje"),
            horaSalida = json.optString("horaSalida"),
            precio = json.optDouble("precio", 0.0),
            estadoViaje = json.optString("estadoViaje"),
            ciudadOrigen = origen?.optString("ciudad") ?: "—",
            ciudadDestino = destino?.optString("ciudad") ?: "—"
        )
    }

    private fun parsearReserva(json: JSONObject): Reserva {
        val viaje = json.optJSONObject("viaje")
        val ruta = viaje?.optJSONObject("ruta")
        val origen = ruta?.optJSONObject("terminalOrigen")
        val destino = ruta?.optJSONObject("terminalDestino")
        return Reserva(
            idReserva = json.getInt("idReserva"),
            estado = json.optString("estado"),
            fechaInicial = json.optString("fechaInicial"),
            ciudadOrigen = origen?.optString("ciudad") ?: "—",
            ciudadDestino = destino?.optString("ciudad") ?: "—",
            fechaViaje = viaje?.optString("fechaViaje") ?: "—",
            nombreCliente = json.optJSONObject("usuario")?.optString("nombreCompleto") ?: "—"
        )
    }
}
