# 6. Gestión del Proyecto — Historias de Usuario y Backlog

> Las historias de usuario de este documento son las que el equipo ya
> tenía escritas en `docs/originales-equipo/Historias_Usuario_Concorde.docx`
> (documento oficial). Aquí se transcriben y, para cada una, se marca
> si el criterio de aceptación **de verdad se cumple en el código**
> — varias no se cumplían y se corrigieron en esta revisión (ver
> columna "Estado").

## 6.1 Autenticación y Roles

| Historia | Criterios de aceptación | Estado |
|---|---|---|
| Como visitante, quiero registrarme con documento, correo y contraseña, para acceder como pasajero | Correo y documento únicos · contraseña encriptada · rol "pasajero" automático | ✅ Cumple (rol se llama `CLIENTE` en el código, no "pasajero" — mismo concepto, nombre distinto) |
| Como usuario registrado, quiero iniciar sesión con correo y contraseña | Mensaje de error si son incorrectas · no revela cuál campo falló | ✅ Cumple (`LoginController` devuelve un solo mensaje genérico) |
| Como administrador, quiero gestionar los roles del sistema | No se puede eliminar un rol ya asignado a usuarios | ⚠️ **No cumplía** → ✅ corregido en esta revisión (`RolController.eliminar()` ahora lo valida) |

## 6.2 Rutas y Terminales

| Historia | Criterios de aceptación | Estado |
|---|---|---|
| Como pasajero, quiero buscar rutas entre dos terminales | Filtrar por ciudad origen/destino · ver el valor del tiquete | ✅ Cumple |
| Como administrador, quiero crear/editar terminales | No se puede eliminar una terminal con rutas asociadas | ⚠️ **No cumplía** (el `DELETE` de `TerminalController` no valida esto; la base sí lo impediría con un error de llave foránea, pero el usuario vería un error 500 feo en vez de un mensaje claro) — queda como mejora pendiente |
| Como administrador, quiero crear rutas con origen y destino | El terminal de origen no puede ser igual al de destino | ✅ Cumple (`CHECK` en el SQL: `crear_tablas_faltantes.sql`) |

## 6.3 Flota y Conductores

| Historia | Criterios de aceptación | Estado |
|---|---|---|
| Como administrador, quiero registrar vehículos | La placa debe ser única | ✅ Cumple (`UNIQUE` en la base) |
| Como administrador, quiero que al registrar un vehículo se generen sus asientos automáticamente | La cantidad de asientos coincide con la capacidad | ⚠️ **No cumplía** → ✅ corregido en esta revisión (`VehiculoController.crear()` ahora los genera) |
| Como administrador, quiero registrar conductores con licencia y vencimiento | El sistema alerta si la licencia está por vencer o venció | ❌ **Sigue sin cumplirse** — el campo `fecha_vencimiento_licencia` existe, pero no hay ninguna alerta automática en el panel. Pendiente. |

## 6.4 Viajes

| Historia | Criterios de aceptación | Estado |
|---|---|---|
| Como administrador, quiero programar un viaje (ruta, vehículo, conductor, fecha, hora) | No se puede asignar el mismo conductor/vehículo a dos viajes que se cruzan | ❌ **Sigue sin cumplirse** — `ViajeController` no valida cruces de horario. Pendiente (requeriría revisar, por cada viaje nuevo, si el conductor/vehículo ya tiene otro viaje en un rango de horas cercano). |
| Como pasajero, quiero ver el estado de un viaje | El estado se actualiza en tiempo real según fecha/hora | ❌ **Sigue sin cumplirse** — `estado_viaje` se cambia manualmente (por un agente/admin) o se queda en `PROGRAMADO`; no hay un proceso que lo pase a `EN_CURSO`/`FINALIZADO` solo. Pendiente. |

## 6.5 Reservas y Asientos

| Historia | Criterios de aceptación | Estado |
|---|---|---|
| Como pasajero, quiero reservar asientos disponibles | Solo se reservan asientos "disponible" · al reservarse, pasa a no disponible | ⚠️ **No cumplía** → ✅ corregido en esta revisión (`DetalleReservaController.crear()` ahora lo valida y lo actualiza) |
| Como pasajero, quiero ver el historial de mis reservas | Ordenadas por fecha, con estado actual | ✅ Cumple (`p11-reservas.html`) |
| Como pasajero, quiero cancelar una reserva a tiempo | Al cancelar, el/los asiento(s) vuelven a estar disponibles | ⚠️ **No cumplía** → ✅ corregido en esta revisión (`ReservaController.actualizar()` libera los asientos al pasar a `CANCELADA`). El límite de "dentro de un tiempo permitido" no está validado todavía (pendiente). |

## 6.6 Pagos

| Historia | Criterios de aceptación | Estado |
|---|---|---|
| Como pasajero, quiero pagar mi reserva | Se registra fecha/valor/estado · la reserva solo pasa a "confirmada" si el pago fue exitoso | ⚠️ **No cumplía** → ✅ corregido en esta revisión (`PagoController` ahora sincroniza el estado de la reserva) |
| Como administrador, quiero ver el historial de pagos | Filtrar por rango de fechas y estado | ⚠️ Parcial — el CRUD permite listar todos los pagos; el filtro por fecha/estado no está en la interfaz todavía (pendiente en el frontend) |

## 6.7 Resumen de esta revisión

- **6 criterios de aceptación** que no se cumplían quedaron corregidos
  en el código (roles, asientos automáticos, disponibilidad de
  asientos, liberación al cancelar, confirmación por pago).
- **4 quedan pendientes** porque requieren lógica más compleja
  (validar cruces de horario, alertas de vencimiento de licencia,
  actualización automática de estado por tiempo, filtros de fecha en
  el historial de pagos) — se documentan aquí para que el equipo
  decida si alcanza a implementarlos antes de sustentar o los presenta
  como "trabajo futuro" con conocimiento de causa.

## 6.8 Cómo completar la evidencia de seguimiento (criterios 5 y 6 de la rúbrica)

Esto depende del proceso real del equipo, no del código:

1. **Historial de Git:** `git log --oneline --all --graph` o
   `Insights → Contributors` en GitHub.
2. **Backlog con seguimiento en el tiempo:** si usaron Trello/Jira/
   GitHub Projects, tomen captura del tablero tal cual quedó. Si no,
   presenten esta tabla como el backlog de cierre, aclarando que el
   seguimiento fue informal.
