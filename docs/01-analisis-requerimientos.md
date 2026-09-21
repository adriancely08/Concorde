# 1. Análisis y Requerimientos — Proyecto Concorde

## 1.1 Problema

Concorde es una empresa de transporte terrestre intermunicipal. Hoy en
día no tiene un canal digital propio: sus clientes no pueden buscar
horarios, comprar tiquetes ni resolver dudas simples sin llamar o
acercarse físicamente a una terminal, y el personal no tiene una
herramienta centralizada para gestionar reservas, pagos y atención al
cliente.

## 1.2 Objetivos

**Objetivo general:** desarrollar una plataforma web que permita a un
cliente buscar, reservar y pagar tiquetes de bus en línea, y a la
empresa gestionar su operación (rutas, viajes, vehículos, reservas,
pagos) y atender a sus clientes de forma más eficiente.

**Objetivos específicos:**
- Permitir la búsqueda y compra de tiquetes sin intervención humana.
- Dar a cada rol (cliente, agente, administrador) las herramientas que
  necesita, sin exponerle lo que no le corresponde.
- Resolver automáticamente las preguntas más comunes de atención al
  cliente (chatbot), y trasladar a un asesor humano solo lo que de
  verdad lo necesite.
- Generar reportes que le permitan a la empresa tomar decisiones
  (ingresos por ruta, ocupación de viajes, rutas más vendidas).
- Proteger los datos de los usuarios (contraseñas, sesiones) con
  prácticas de seguridad reales, no solo funcionales.

## 1.3 Alcance

**Incluido en esta versión:**
- Búsqueda pública de viajes (sin necesidad de iniciar sesión).
- Registro y autenticación de clientes.
- Reserva, pago y cancelación de tiquetes.
- Panel de administrador: gestión completa del catálogo (rutas,
  terminales, vehículos, conductores), usuarios, pagos y reportes.
- Panel de agente: gestión de reservas/usuarios/pagos/viajes y
  atención de conversaciones de chat escaladas por el bot.
- Chatbot de atención al cliente basado en palabras clave, con
  escalamiento a un agente humano.
- Reportes generales e individuales por cliente, exportables a PDF.

**Fuera de alcance (por ahora):**
- Pagos con pasarela real (el pago se simula/registra, no se conecta a
  un banco o procesador de pagos externo).
- Aplicación móvil nativa (solo web, responsive).
- Notificaciones por correo o SMS.
- Chat en tiempo real con WebSockets (se resolvió con *polling*, ver
  `DOCUMENTACION.md`).

## 1.4 Usuarios del sistema

| Rol | Qué puede hacer |
|---|---|
| **Visitante** (sin cuenta) | Buscar viajes, ver preguntas frecuentes, usar el chatbot, registrarse |
| **Cliente** | Todo lo del visitante + reservar, pagar, ver/cancelar sus reservas, editar su perfil |
| **Agente** | Atender chats escalados, gestionar reservas/usuarios/pagos/viajes |
| **Administrador** | Todo lo del agente + gestión completa del catálogo, reportes, configuración |

## 1.5 Requerimientos funcionales

| # | Requerimiento |
|---|---|
| RF01 | El sistema debe permitir buscar viajes por origen, destino y fecha sin iniciar sesión |
| RF02 | El sistema debe permitir a un visitante registrarse como cliente |
| RF03 | El sistema debe permitir a un cliente iniciar sesión y mantener su sesión activa |
| RF04 | El sistema debe permitir a un cliente reservar un viaje, elegir asiento y pagar |
| RF05 | El sistema debe permitir a un cliente ver, cancelar y consultar el detalle de sus reservas |
| RF06 | El sistema debe permitir a un cliente editar su propio perfil |
| RF07 | El sistema debe ofrecer un chatbot que responda preguntas frecuentes de atención al cliente |
| RF08 | El sistema debe trasladar la conversación a un agente humano cuando el bot no pueda resolverla o el cliente lo pida |
| RF09 | El sistema debe permitir a un agente responder y cerrar conversaciones de chat |
| RF10 | El sistema debe permitir a un administrador/agente gestionar (CRUD) rutas, terminales, vehículos, conductores, viajes, reservas, pagos y usuarios |
| RF11 | El sistema debe generar reportes de ingresos por ruta/fecha, ocupación de viajes, rutas más vendidas y conteos por estado |
| RF12 | El sistema debe generar un reporte individual por cliente, exportable a PDF |
| RF13 | El sistema debe avisar cuando un viaje se queda sin cupo disponible |

## 1.6 Requerimientos no funcionales

| # | Requerimiento |
|---|---|
| RNF01 | Las contraseñas deben almacenarse hasheadas (BCrypt), nunca en texto plano |
| RNF02 | La API debe exigir autenticación (JWT) para las operaciones que no son públicas |
| RNF03 | Cada rol solo debe poder acceder a las rutas de la API que le corresponden |
| RNF04 | El sistema debe funcionar correctamente en pantallas de escritorio y móviles (responsive) |
| RNF05 | El sistema debe seguir pautas básicas de accesibilidad (ver `p99-accesibilidad.html`) |
| RNF06 | La configuración sensible (contraseñas, claves) debe poder definirse por variables de entorno, no quedar fija en el código |
| RNF07 | El sistema debe dar una respuesta de atención al cliente en segundos, no minutos, para las preguntas frecuentes |
| RNF08 | El código debe estar documentado de forma que otro integrante del equipo pueda entenderlo sin explicación adicional |

## 1.7 Limitaciones conocidas (documentadas a propósito)

- No hay verificación de que un `id` de la URL sea el del propio
  usuario autenticado (por ejemplo, en `/api/usuarios/{id}`).
- El chat usa *polling* en vez de WebSockets (no es verdadero tiempo real).
- El pago no se conecta a una pasarela real.

Estas limitaciones se dejan documentadas explícitamente en vez de
ocultarlas, porque mostrar que se conocen y se entienden sus
implicaciones es parte de un análisis honesto del alcance del proyecto.
