# Concorde Móvil (Android Studio + Kotlin)

App Android nativa que reutiliza el backend Spring Boot + PostgreSQL
del proyecto — no duplica lógica de negocio, solo consume la API REST
(JWT, viajes, reservas, pagos, chat, PQRS, reportes). Mismo estilo
visual de la web (azul `#003580` + dorado `#F5A623`) y **16 pantallas**
organizadas por rol, igual que la web.

## Pantallas

| Rol | Pantalla | Equivale en la web a |
|---|---|---|
| Todos (sin login) | **Viajes** (inicio, con buscador por ciudad) | `index.html` + `p02-resultados` |
| Todos | **Detalle del viaje** + elegir asiento + pagar | `p05`, `p07`, `p08`, `p09`, `p10` |
| Todos | **Atención**: FAQ + PQRS + acceso al chat | `p12-atencion.html` |
| Todos | **Chat** con el asistente (escala a un asesor) | widget de chat |
| Todos | **Login** y **Registro** | `p03`, `p04` |
| Cliente | **Mis reservas** (tocar una = cancelarla) | `p11`, `p15` |
| Cliente/Staff | **Mi perfil** (editar datos, cambiar contraseña) | `p16-perfil.html` |
| Agente | **Panel de agente** → Chat con clientes, PQRS, Reservas, Pagos, Usuarios | `agente-panel.html` |
| Admin | **Panel de administrador** → Reportes, Usuarios, Reservas, Pagos, PQRS, Chat | `admin-panel.html` |

**Al iniciar sesión cada rol aterriza en su propio panel** (cliente → vuelve a donde estaba, agente → panel de agente, admin → panel de administrador). **No hay login obligatorio:** cualquiera ve los viajes, el chat y las preguntas frecuentes sin cuenta; solo se pide sesión para comprar, ver "Mis reservas" o el perfil.

## Cómo abrirlo

1. Android Studio → **Open** → carpeta `ConcordeMovilKotlin`.
2. Espera el *Gradle Sync* (la primera vez descarga dependencias; necesita internet).
3. Backend corriendo (`mvn spring-boot:run` o ▶️ en IntelliJ).
4. Elige un emulador (Tools → Device Manager) y dale al ▶️ verde.

## URL del backend (importante)

Archivo `app/src/main/java/com/concorde/movil/red/ApiCliente.kt`, constante `BASE_URL`:

- **Emulador**: deja `http://10.0.2.2:8082/api` (así el emulador ve el "localhost" de tu PC).
- **Celular físico / la instructora en su celular**: pon la IP de tu PC (ej. `http://192.168.1.15:8082/api`; mírala con `ipconfig` → IPv4). Celular y PC en la **misma red wifi**.

## Usuarios de prueba (si cargaron `seed_concorde_30.sql`)

Contraseña de todos: `Concorde2026*`

- Administrador: `admin@concorde.com`
- Agentes: `agente1@concorde.com`, `agente2@concorde.com`, `agente3@concorde.com`
- Clientes: cualquier otro correo de la tabla `usuario`

> Ojo: los asientos del seed solo existen para los primeros 6 vehículos; un viaje de otro vehículo mostrará "no tiene asientos disponibles" en la compra (es un tema de datos, no un error de la app). Si crean un vehículo nuevo desde el panel web, el backend le genera sus asientos solo.

## El logo

No tengo el PNG real del logo, así que dejé uno vectorial de reemplazo (`res/drawable/logo_concorde.xml`). **Para poner el real:** copien `LogoConcorde.png` a `app/src/main/res/drawable/` **renombrándolo `logo_concorde.png`** (minúsculas, sin mayúsculas) y borren `logo_concorde.xml`. No hay que tocar nada más: todas las pantallas ya apuntan a `@drawable/logo_concorde`.

## APK para la instructora

**Build → Build Bundle(s) / APK(s) → Build APK(s)** → archivo en `app/build/outputs/apk/debug/app-debug.apk`. Pásenlo por WhatsApp/Drive/USB; en el celular hay que permitir "instalar de orígenes desconocidos". Para que cargue datos, el celular debe estar en la misma wifi que el PC con el backend (con la IP configurada arriba).

## Nota honesta sobre la compilación

Este proyecto se escribió **sin poder compilarlo aquí** (no hay Android SDK ni acceso a los repositorios de Google). Sí verifiqué: que el compilador de Kotlin no encuentre errores de sintaxis en ningún archivo, que cada ID usado en el código exista en su layout XML, que todos los XML estén bien formados y que cada pantalla esté declarada en el manifest. **El primer *Gradle Sync* en tu Android Studio es la prueba real**: si sale algún error, pégalo completo y lo arreglamos.
