# 4. Preparación para Pruebas — Proyecto Concorde

## 4.1 Pruebas automatizadas (JUnit 5)

Ubicadas en `src/test/java/com/concorde/springboot/servicio/`. Se
enfocan en la lógica que no depende de la base de datos (servicios
puros), para que corran rápido y sin necesitar PostgreSQL levantado:

| Clase de prueba | Qué verifica |
|---|---|
| `MotorFaqChatbotTest` | El chatbot reconoce preguntas frecuentes, es insensible a tildes/mayúsculas, escala cuando el cliente pide un asesor y escala cuando no reconoce nada (nunca inventa una respuesta) |
| `PasswordUtilTest` | El hash generado es BCrypt válido, una contraseña correcta coincide y una incorrecta no, y el modo de compatibilidad con contraseñas antiguas en texto plano funciona |

**Cómo correrlas:** `mvn test` desde la raíz del proyecto, o el botón
▶️ verde al lado de cada método/clase en IntelliJ.

## 4.2 Casos de prueba manuales (funcionales, sobre la interfaz web)

| ID | Caso | Datos de entrada | Resultado esperado |
|---|---|---|---|
| CP01 | Búsqueda de viajes sin sesión | Origen, destino y fecha válidos | Se muestra la lista de viajes disponibles sin pedir login |
| CP02 | Registro de cliente nuevo | Nombre, documento, correo no usado antes, contraseña ≥8 caracteres | Cuenta creada con rol CLIENTE, redirige a confirmación |
| CP03 | Registro con correo repetido | Correo ya existente en la base | Error 409 "Ya existe una cuenta con ese correo" |
| CP04 | Login correcto | Correo y contraseña válidos | Se recibe token JWT y se redirige al panel/página según el rol |
| CP05 | Login incorrecto | Contraseña equivocada | Error 401 "Correo o contraseña incorrectos", sin dar pistas de cuál campo falló |
| CP06 | Reserva completa | Viaje válido, asiento libre, datos de pago | Reserva creada en estado CONFIRMADA, aparece en "Mis reservas" |
| CP07 | Reservar el último asiento disponible | Viaje con 1 cupo libre | El viaje pasa a marcarse como agotado (RF13) |
| CP08 | Cancelar una reserva propia | Una reserva existente del cliente logueado | Estado pasa a CANCELADA |
| CP09 | Chatbot responde una FAQ | "¿Cuánto equipaje puedo llevar?" | Responde la información de equipaje sin escalar |
| CP10 | Chatbot escala a un asesor | "Quiero hablar con un asesor" | Conversación pasa a estado ESCALADA y aparece en el panel de agente |
| CP11 | Agente responde un chat escalado | Un ticket en estado ESCALADA | Pasa a EN_ATENCION, el cliente ve la respuesta del agente |
| CP12 | Cliente intenta acceder a `/api/usuarios` (listar todos) | Token de un usuario con rol CLIENTE | 403 Forbidden |
| CP13 | Petición sin token a una ruta protegida | `GET /api/reservas` sin header Authorization | 401 Unauthorized |
| CP14 | Generar reporte individual de un cliente | Un `idUsuario` válido | Se muestra su historial de reservas y el total gastado |
| CP15 | Exportar reporte a PDF | Cualquier reporte generado | Se descarga un PDF con el logo de Concorde en la esquina superior izquierda |

## 4.3 Datos de prueba sugeridos

Para sustentar sin depender de datos reales de producción, se
recomienda tener precargados en la base:
- Al menos 1 usuario de cada rol (ADMIN, AGENTE, CLIENTE) con
  contraseña conocida por el equipo.
- Al menos 2 rutas con terminales distintas.
- Al menos 1 viaje con cupo disponible y 1 viaje agotado (para
  demostrar CP07 sin tener que llenarlo en vivo).
- Al menos 1 conversación de chat ya escalada (para no depender de
  escribirla en vivo frente al jurado).

## 4.4 Criterios de aceptación

Un caso de prueba se considera **aprobado** cuando:
1. El resultado observado coincide con el resultado esperado.
2. No se genera ningún error 500 (error del servidor) en la consola
   del backend durante la prueba.
3. La respuesta llega en menos de 3 segundos (RNF07 para el chatbot;
   para el resto, tiempo razonable en entorno local).

Un caso se considera **fallido** si el resultado no coincide, si
aparece un error no controlado, o si expone información que no debería
(por ejemplo, un mensaje de error con detalles internos del servidor).

## 4.5 Limitación conocida

No hay pruebas automatizadas de integración (que levanten Spring
completo con una base de datos de prueba) ni pruebas end-to-end del
frontend (tipo Selenium/Cypress). Las pruebas automatizadas actuales
cubren la lógica de negocio pura; el resto del flujo se valida
manualmente con la tabla de casos de prueba de la sección 4.2.
