# Autoevaluación contra la Rúbrica de Sustentación

Esta es una revisión honesta, criterio por criterio, actualizada
después de que el equipo compartió sus documentos de diseño reales
(`docs/originales-equipo/`): diagrama de clases, diagrama relacional,
ERD, diccionario de datos, casos de uso e historias de usuario.

## Resumen de lo que cambió en esta revisión

Cruzar el código real contra esos documentos oficiales sacó a la luz
**un bug real de despliegue** y **6 reglas de negocio** que las
historias de usuario pedían pero el código no cumplía. Ya se
corrigieron:

1. **`crear_tablas_faltantes.sql` estaba desactualizado** — no creaba
   las tablas `ROL` ni `PERSONA` que el código Java sí espera (tenía
   `usuario.rol` como texto plano y `conductor` con sus propios
   campos de nombre/teléfono, de una versión anterior del proyecto).
   Si alguien lo corría desde cero, la aplicación no arrancaba.
   **Corregido.**
2. Un rol asignado a usuarios no se podía "proteger" de borrado.
   **Corregido** (`RolController`).
3. Al registrar un vehículo, sus asientos no se generaban solos según
   la capacidad. **Corregido** (`VehiculoController`).
4. Se podía reservar un asiento ya ocupado, y no quedaba marcado como
   ocupado al reservarse. **Corregido** (`DetalleReservaController`).
5. Cancelar una reserva no liberaba los asientos. **Corregido**
   (`ReservaController`).
6. Un pago exitoso no confirmaba automáticamente la reserva.
   **Corregido** (`PagoController`).

Quedan 4 reglas de negocio pendientes (requieren lógica más compleja):
validar cruces de horario de conductor/vehículo, alertar licencias por
vencer, actualizar el estado de un viaje automáticamente según la
hora, y filtros de fecha en el historial de pagos. Ver el detalle en
`docs/06-historias-de-usuario-backlog.md`, sección 6.7.

También se encontró que el diagrama de casos de uso original le da al
actor **Conductor** casos de uso propios (ver sesión, consultar sus
viajes asignados) que **no están implementados** — hoy Conductor es
solo un dato administrado, no un usuario que inicia sesión. Ver
`docs/02-diseno-sistema.md`, sección 2.5.

## Tabla por criterio

| # | Criterio | Peso | Estado | Evidencia |
|---|---|---|---|---|
| 1 | Análisis y requerimientos | 15% | ✅ Fuerte | `docs/01-analisis-requerimientos.md` + documentos oficiales del equipo en `docs/originales-equipo/` |
| 2 | Diseño del sistema | 15% | ✅ Fuerte | `docs/02-diseno-sistema.md` + diagramas de clases/relacional/ERD reales del equipo |
| 3 | Desarrollo y funcionalidad | 25% | ✅ Fuerte, y más completo que antes | CRUD, chatbot, seguridad JWT, reportes, **más las 6 reglas de negocio corregidas en esta revisión** |
| 4 | Base de datos | 10% | ✅ Fuerte | `docs/03-base-de-datos.md` + diccionario de datos oficial + **bug del script SQL corregido** |
| 5 | Control de versiones y trabajo colaborativo | 10% | ⚠️ **No evaluable desde aquí** | Depende del historial real de Git/GitHub del equipo |
| 6 | Gestión del proyecto | 10% | ✅ Backlog real, seguimiento pendiente | `docs/06-historias-de-usuario-backlog.md` con las historias oficiales del equipo, verificadas contra el código |
| 7 | Documentación | 5% | ✅ Fuerte | README + `DOCUMENTACION.md` + toda la carpeta `docs/` |
| 8 | Preparación para pruebas | 5% | ✅ Fuerte | `docs/04-plan-de-pruebas.md` + `MotorFaqChatbotTest` + `PasswordUtilTest` (JUnit real) |
| 9 | Preparación para el despliegue | 5% | ✅ Fuerte | `docs/05-despliegue.md` + contraseñas y clave JWT movidas a variables de entorno |

## Lo que el equipo debe hacer (no lo puedo hacer yo)

**Criterio 5 (Control de versiones, 10%)** — se evalúa sobre el
historial real de Git/GitHub. Antes de sustentar:
1. `git log --oneline --all --graph` — tomen captura.
2. `Insights → Contributors` en GitHub — tomen captura.
3. Si el trabajo quedó concentrado en pocos commits de una persona,
   no lo oculten: expliquen en la sustentación cómo se repartió el
   trabajo realmente (análisis, diagramas, pruebas manuales, etc. no
   siempre dejan rastro en Git).

**Criterio 6 (seguimiento en el tiempo)** — si usaron Trello/Jira,
saquen captura del tablero. Si no, presenten el backlog de
`docs/06-historias-de-usuario-backlog.md` como cierre, con honestidad
sobre qué tan formal fue el seguimiento.

## Decisión pendiente del equipo

Con el tiempo que quede antes de sustentar, decidan si vale la pena
implementar alguna de las 4 reglas de negocio pendientes (la más
rápida de las cuatro sería la alerta de licencia por vencer: solo
compara `fecha_vencimiento_licencia` contra la fecha actual). Si no
alcanza el tiempo, preséntenlo como "trabajo futuro" con conocimiento
de causa — es mejor que el jurado vea que lo identificaron ustedes
mismos, a que lo descubran preguntando.
