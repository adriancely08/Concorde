# 7. Gestión del Proyecto — Backlog, Sprints y Seguimiento (SIMAC)

> Backlog oficial y definitivo del equipo:
> `docs/originales-equipo/gestion_proyecto_simac.xlsx` (de Sergio —
> reemplaza las dos versiones anteriores, `Backlog_Concorde.xlsx` y
> `Backlog_Concorde_Completo.xlsx`, que quedaron superadas). Tiene 5
> hojas: **Backlog** (12 ítems BL-01 a BL-12), **Historias de
> Usuario** (HU-01 a HU-12 con criterios de aceptación), **Tareas**
> (20 tareas T-01 a T-20, con responsable Backend/Frontend/QA y
> horas), **Sprints** (4 sprints con fechas reales, del 4 de agosto al
> 28 de septiembre de 2026) y **Seguimiento** (tareas planificadas vs.
> completadas, % de cumplimiento con fórmula automática).
>
> El archivo que se subió aquí **ya tiene los estados corregidos**
> contra el código real (varios decían "En progreso" o "Pendiente"
> cuando en realidad ya estaban terminados). Esta tabla explica qué se
> corrigió y por qué.

## Qué se corrigió y por qué

| Ítem | Decía | Queda | Por qué |
|---|---|---|---|
| BL-06 Reserva de asientos | En progreso | **Completado** | `DetalleReservaController` ya valida disponibilidad y libera asientos al cancelar |
| BL-07 Gestión de pagos | En progreso | **Completado** | `PagoController` ya confirma la reserva automáticamente al procesar el pago |
| BL-08 Chatbot | En progreso | **Completado** | Motor de FAQ + escalamiento a agente humano, funcionando de punta a punta |
| BL-09 Detalle de reservas | Pendiente | **Completado** | `p11-reservas.html` / `p14-detalle-reserva.html` ya existen y funcionan |
| BL-10 Módulo de reportes | Pendiente | **Completado** | Reporte general + individual por cliente, con logo y exportación a PDF |
| BL-12 Notificaciones al cliente | Por iniciar | **Parcial** | Hay confirmación visual (`p10-confirmacion.html`) pero no notificación real por correo/SMS |
| Sprint 3 (reservas, pagos, chatbot) | En progreso, 0/6 tareas | **Completado, 6/6 tareas** | Las 3 historias que contiene ya están hechas |
| Sprint 4 (detalle, reportes, notificaciones) | Pendiente, 0/5 tareas | **En progreso, 3/5 tareas** | Detalle de reservas y reportes listos; falta el filtro de fechas en reportes (T-18) y la notificación real (T-19) |
| Total del proyecto | 9/20 tareas (45%) | **18/20 tareas (90%)** | Refleja lo que realmente se construyó durante todas las sesiones de trabajo |

## Lo único que sigue sin cerrar

- **T-18**: la vista de reportes no tiene todavía un filtro de fechas en el frontend (el backend sí soporta ingresos-por-fecha).
- **T-19**: la "confirmación" de reserva/pago es solo visual en la página; no se envía un correo o SMS real.
- **PQRS**: este backlog (SIMAC) nunca incluyó un módulo de PQRS como ítem propio — apareció en el backlog anterior (`Historias_Usuario_Concorde.docx` / CON-17 a CON-22) y ya se implementó una versión mínima (modelo `Pqrs`, `PqrsController`, formulario en `p12-atencion.html`, gestión en `agente-panel.html`). Si quieren, se puede agregar como BL-13 al Excel para que quede reflejado ahí también.

## Para la sustentación

Con el Excel ya corregido, el equipo puede mostrar en vivo la hoja
**Seguimiento** como evidencia real de que fueron cumpliendo la
planificación sprint a sprint — que es exactamente lo que pide el
criterio 6 de la rúbrica. La hoja **Sprints** con fechas reales
(4 sprints de 2 semanas cada uno) también sirve para explicar cómo se
organizó el tiempo del proyecto.
