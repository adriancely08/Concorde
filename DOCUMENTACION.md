# Documentación — Proyecto Concorde (Spring Boot + Frontend)

Esta documentación explica, de forma sencilla, qué hace cada parte del código. No repite el código línea por línea — explica el **propósito** de cada archivo y cómo se conecta con los demás.

---

## 1. Estructura general

```
concorde-springboot/
├── src/main/java/com/concorde/springboot/
│   ├── modelo/         → las "tablas" de la base de datos, como clases Java
│   ├── repositorio/     → acceso a la base de datos (uno por modelo)
│   ├── controlador/     → los endpoints de la API (lo que el frontend consume)
│   ├── servicio/        → lógica reutilizable (seguridad, chatbot)
│   ├── config/          → configuración global (seguridad, CORS, errores)
│   └── ConcordeSpringbootApplication.java  → arranca la aplicación
├── src/main/resources/
│   ├── application.properties  → configuración de la base de datos
│   └── static/          → todo el frontend (HTML, CSS, JS, imágenes)
└── *.sql                → scripts para crear las tablas
```

**Cómo fluye una petición típica:** el navegador llama a un endpoint (ej. `GET /api/viajes`) → `ViajeController` recibe la petición → le pide los datos a `ViajeRepository` → Spring Data JPA traduce eso a SQL → Hibernate convierte las filas de la base en objetos `Viaje` → el controlador los devuelve como JSON → el frontend los pinta en pantalla.

---

## 2. Backend — Modelos (`modelo/`)

Cada clase aquí representa una tabla de la base de datos. Los `@ManyToOne` son las llaves foráneas (relaciones entre tablas).

| Clase | Qué representa | Se relaciona con |
|---|---|---|
| `Persona` | Datos personales base (nombre, documento) | Padre de `Conductor` |
| `Conductor` | Un conductor de bus | `Persona` |
| `Usuario` | Cualquiera que inicia sesión (cliente, agente o admin) | `Rol` |
| `Rol` | ADMIN / AGENTE / CLIENTE | — |
| `Terminal` | Una terminal de transporte en una ciudad | — |
| `Ruta` | Un trayecto entre dos terminales y su precio | 2× `Terminal` |
| `Vehiculo` | Un bus (placa, capacidad, modelo) | — |
| `Asiento` | Un puesto físico dentro de un vehículo | `Vehiculo` |
| `Viaje` | Una salida programada de un vehículo por una ruta en fecha/hora | `Ruta`, `Vehiculo`, `Conductor` |
| `Reserva` | La reserva que hace un cliente para un viaje | `Usuario`, `Viaje` |
| `DetalleReserva` | Qué asiento específico ocupa cada reserva | `Reserva`, `Asiento` |
| `Pago` | El pago asociado a una reserva | `Reserva` |
| `ConsultaChat` | Una conversación del chatbot de atención al cliente | `Usuario` (cliente), `Usuario` (agente) |
| `MensajeChat` | Cada mensaje individual dentro de una `ConsultaChat` | `ConsultaChat` |

---

## 3. Backend — Repositorios (`repositorio/`)

Uno por cada modelo. Todos extienden `JpaRepository`, lo que les da gratis `findAll()`, `findById()`, `save()`, `deleteById()`, etc. — no hay que escribir SQL a mano para lo básico. Los que necesitan una búsqueda especial (como `MensajeChatRepository.findByConsulta_IdConsultaOrderByEnviadoEnAsc`) la declaran como nombre de método y Spring Data JPA la traduce sola a la consulta SQL correspondiente.

---

## 4. Backend — Controladores (`controlador/`)

### CRUD estándar (mismo patrón en los 11 siguientes)
`AsientoController`, `ConductorController`, `DetalleReservaController`, `PagoController`, `PersonaController`, `ReservaController`, `RolController`, `RutaController`, `TerminalController`, `VehiculoController`, `ViajeController`.

Todos exponen exactamente:
- `GET /api/<recurso>` — lista todo
- `GET /api/<recurso>/{id}` — uno por id
- `POST /api/<recurso>` — crear
- `PUT /api/<recurso>/{id}` — actualizar
- `DELETE /api/<recurso>/{id}` — borrar

### Controladores especiales

**`UsuarioController`** (`/api/usuarios`) — CRUD de usuarios, pero además: antes de guardar una contraseña, la hashea con BCrypt si todavía no lo está (así nunca queda en texto plano en la base).

**`LoginController`** (`/api/login`) — recibe correo + contraseña, verifica contra el hash guardado, y si es correcto genera y devuelve un **token JWT** (ver sección 6).

**`RegistroController`** (`/api/registro`) — registro público de clientes. A propósito es distinto de `UsuarioController`: aquí el rol **siempre** se asigna como CLIENTE desde el servidor, sin importar qué mande el navegador, para que nadie pueda auto-asignarse el rol ADMIN.

**`ChatbotController`** (`/api/chatbot`) — todo el flujo del chatbot de atención al cliente (ver sección 7).

**`ReporteController`** (`/api/reportes`) — calcula todos los reportes (ingresos, ocupación, reporte por cliente) a partir de los datos ya existentes, usando streams de Java en vez de SQL complejo (ver sección 8).

---

## 5. Backend — Servicios (`servicio/`)

- **`PasswordUtil`** — hashea contraseñas con BCrypt y las compara. Si encuentra una contraseña vieja en texto plano, la valida igual (compatibilidad hacia atrás) y de paso la reemplaza por su hash.
- **`JwtUtil`** — genera y valida los tokens JWT (firma HMAC-SHA, expiran en 8 horas).
- **`MotorFaqChatbot`** — el "cerebro" del chatbot: normaliza el texto del cliente (quita tildes/mayúsculas) y lo compara contra una lista de preguntas frecuentes por palabras clave. Si nada coincide, o si el cliente pide un asesor, marca la conversación para escalar a un humano.

---

## 6. Backend — Seguridad (`config/`)

- **`SecurityConfig`** — define qué rutas son públicas (login, registro, chatbot, búsqueda de viajes) y cuáles exigen estar logueado o tener rol ADMIN/AGENTE.
- **`JwtAuthFilter`** — se ejecuta en cada petición HTTP; si trae `Authorization: Bearer <token>` válido, identifica al usuario y su rol para que `SecurityConfig` decida si tiene permiso.
- **`ManejadorErrores`** — captura errores comunes (ej. un id que no existe) y los devuelve como JSON ordenado en vez de un error feo de Spring.

**Cómo funciona en la práctica:** el cliente hace login → recibe un token → el frontend (`auth.js`) lo guarda y lo manda automáticamente en cada petición → `JwtAuthFilter` lo valida → `SecurityConfig` decide si esa ruta está permitida para ese rol.

---

## 7. Backend — Chatbot

1. El cliente abre el widget de chat → `POST /api/chatbot/iniciar` crea una `ConsultaChat` (estado `BOT`) y guarda el mensaje de bienvenida.
2. Cada mensaje del cliente → `POST /api/chatbot/mensaje` → `MotorFaqChatbot` busca la mejor respuesta.
   - Si encuentra una coincidencia clara → responde el bot, la conversación sigue en estado `BOT`.
   - Si no encuentra nada, o el cliente pide un asesor → estado pasa a `ESCALADA`.
3. El panel de agente (`agente-panel.html`) lista las conversaciones `ESCALADA`/`EN_ATENCION` y permite responder (`POST /api/chatbot/consultas/{id}/responder`, pasa a `EN_ATENCION`) o cerrarlas (`POST /api/chatbot/consultas/{id}/cerrar`).
4. Tanto el widget del cliente como el panel del agente refrescan la conversación cada pocos segundos (`GET /api/chatbot/consultas/{id}/mensajes`) para simular tiempo real sin necesitar WebSockets.

---

## 8. Backend — Reportes

Todos se calculan en memoria con streams de Java a partir de lo que ya hay en la base (sin SQL complejo, para que sea fácil de explicar):

- **Ingresos por ruta / por fecha** — suma los pagos `PROCESADO`, agrupados por ruta o por fecha.
- **Ocupación de viajes** — por cada viaje, cuenta cuántos asientos están ocupados vs. la capacidad del vehículo; marca como "agotado" el que llegó al límite.
- **Viajes / reservas por estado, usuarios por rol** — cuenta cuántos hay de cada categoría (un histograma).
- **Rutas más vendidas** — cuenta reservas no canceladas, agrupadas por ruta.
- **Reporte individual por cliente** (`GET /api/reportes/cliente/{idUsuario}`) — junta los datos del usuario, todas sus reservas (con la ruta y el pago de cada una), y un resumen: total de reservas, cuántas confirmadas/canceladas, y cuánto ha gastado en total.

En el frontend (`admin-panel.html`), la sección "Reportes" tiene el **reporte general** (todo lo de arriba) y, debajo, un selector para elegir un cliente y generar su **reporte individual**. Ambos se pueden exportar a PDF con el botón correspondiente (usa la librería `html2pdf.js`), y ambos llevan el logo de Concorde en la esquina superior izquierda del encabezado.

---

## 9. Frontend — Páginas (`static/*.html`)

| Página | Qué hace |
|---|---|
| `index.html` | Página de inicio, buscador de viajes |
| `p02-resultados.html` | Lista de viajes que coinciden con la búsqueda |
| `p03-login.html` | Inicio de sesión |
| `p04-registro.html` | Registro de cuenta nueva (cliente) |
| `p04b-confirmacion-registro.html` | Mensaje de "cuenta creada" |
| `p05-detalle-viaje.html` | Detalle de un viaje antes de reservar |
| `p06-pasajero.html` | Datos del pasajero |
| `p07-asientos.html` | Selección de asiento |
| `p08-resumen-compra.html` | Resumen antes de pagar |
| `p09-pago.html` | Formulario de pago |
| `p10-confirmacion.html` | Confirmación de compra exitosa |
| `p11-reservas.html` | "Mis reservas" del cliente logueado |
| `p12-atencion.html` | Atención al cliente (FAQ + botón para abrir el chatbot) |
| `p13-recuperar.html` | Recuperar contraseña |
| `p14-detalle-reserva.html` | Ver el detalle de una reserva puntual |
| `p15-cancelacion.html` / `p15b-cancelacion-ok.html` | Cancelar una reserva |
| `p16-perfil.html` | Ver/editar el perfil propio del usuario logueado |
| `p17-confirmacion-mensaje.html` | Página genérica de "mensaje enviado" |
| `p99-accesibilidad.html` | Declaración de accesibilidad del sitio |
| `admin-panel.html` | Panel del rol ADMIN: usuarios, catálogo (rutas/viajes/terminales/vehículos/conductores), pagos, reservas, reportes, configuración |
| `agente-panel.html` | Panel del rol AGENTE: chat con clientes, reservas, usuarios (clientes), pagos, viajes |

---

## 10. Frontend — Scripts compartidos (`static/js/`)

- **`auth.js`** — maneja login/logout, guarda el usuario y el token JWT en `sessionStorage`, y **intercepta `fetch` globalmente** para agregarle el header `Authorization` a toda petición a la API automáticamente (así ninguna otra página tuvo que modificarse para quedar protegida).
- **`componentes.js`** — arma el navbar y el footer dinámicamente según si hay sesión activa y qué rol tiene, e inyecta el widget del chatbot (`chatbot.js`) en cada página.
- **`chatbot.js`** — el widget flotante de chat: abre/cierra el panel, envía mensajes al backend, muestra la respuesta del bot o el aviso de "te estamos comunicando con un asesor", y hace *polling* (pregunta cada pocos segundos) mientras espera respuesta humana.
- **`main.js`** — funciones generales de UI compartidas entre páginas (validaciones de formularios, toasts, etc.).
- **`passwordPolicy.js`** — valida en el navegador que una contraseña nueva cumpla las reglas mínimas (longitud, mayúsculas, números) antes de enviarla al backend.
- **`aviso-agotados.js`** — muestra un aviso cuando un viaje ya no tiene cupo disponible.

---

## 11. Seguridad — resumen para quien vaya a sustentar

- Las contraseñas se guardan hasheadas con **BCrypt**, nunca en texto plano.
- El login devuelve un **token JWT** que dura 8 horas; el frontend lo adjunta solo en cada petición.
- Rutas públicas (sin token): búsqueda de viajes, login, registro, chatbot.
- Rutas que exigen sesión: reservas, pagos, perfil propio.
- Rutas solo para ADMIN/AGENTE: gestión de usuarios, reportes, catálogo, conductores.
- Limitación conocida (documentada a propósito, no un descuido): no hay verificación de que un `id` en la URL sea el del propio usuario — por ejemplo, un cliente autenticado podría en teoría pedir el perfil de otro cliente si le adivina el id. Implementarlo requeriría comparar el usuario del token contra cada recurso en cada controlador.

---

## 12. Cómo levantar el proyecto desde cero

1. Tener Java 17 y PostgreSQL instalados.
2. Crear la base `concorde_springboot` y correr, en orden: `crear_tablas_faltantes.sql` y `crear_tablas_chat.sql`.
3. Ajustar `spring.datasource.password` en `application.properties` con la contraseña real de Postgres en tu máquina.
4. `mvn clean install -U` (la primera vez, para bajar las dependencias).
5. Ejecutar `ConcordeSpringbootApplication` (botón ▶️ en IntelliJ, o `mvn spring-boot:run`).
6. Abrir `http://localhost:8082` en el navegador.
