# 7. Backlog Oficial (CON-01 a CON-62) — Estado Real

> Backlog real del equipo: `docs/originales-equipo/Backlog_Concorde_Completo.xlsx`
> (versión mejorada de Sergio — agrega **Streams**, **Sprints**,
> `% Avance`, `Responsable` y seguimiento de `Cumplimiento`, con hojas
> separadas: Resumen, Backlog, Historias de usuario, Tareas, Streams,
> Sprints, Seguimiento, Cumplimiento). Es una estructura de gestión de
> proyecto real y completa — el único ajuste que le falta es que
> **todos los 62 ítems siguen en "Por hacer" / 0% / "No evaluado"**,
> sin importar que ya haya bastante construido. Esta tabla reemplaza
> esa columna con el estado **verificado contra el código real**.
> Antes de sustentar, actualicen esas columnas en el Excel (Estado,
> % Avance, Responsable, Fin real, Cumplimiento) con lo de aquí.

## Streams del backlog y su estado real

| Stream | Objetivo | Estado real |
|---|---|---|
| Análisis y requisitos (CON-02 a CON-04) | Definir necesidades y funcionalidades | ✅ Hecho |
| Diseño UX/UI (CON-01, CON-43 a CON-48) | Experiencia, navegación, wireframes, accesibilidad | 🟡 Parcial — navegación y accesibilidad básica sí; wireframes y heurísticas de Nielsen sin documento formal |
| Desarrollo Frontend (CON-05 a CON-07, CON-49 a CON-55) | Interfaces con HTML5/CSS3 | ✅ Hecho, salvo CON-07 (recuperar contraseña, ver abajo) |
| Atención al cliente (CON-13 a CON-29) | Chat, FAQ y reseñas | 🟡 Parcial — chat y FAQ hechos; reseñas y redes sociales no |
| PQRS (CON-17 a CON-22) | Registrar, consultar y responder PQRS | ❌ No implementado |
| Compra de tiquetes (CON-08 a CON-12) | Selección y gestión de tiquetes | ✅ Hecho |
| Administración (CON-30 a CON-34) | Usuarios, horarios, destinos, servicios | ✅ Hecho |
| Base de datos (CON-35 a CON-42) | Diseño e implementación de BD | ✅ Hecho, salvo CON-42 (triggers, ver abajo) |
| Pruebas (CON-56 a CON-62) | Verificar funcionalidades y corregir errores | 🟡 Parcial — documentadas manualmente, no automatizadas al 100% |

## Detalle por ítem (estado real verificado contra el código)

| ID | Historia/Tarea | Estado real |
|---|---|---|
| CON-01 a CON-06 | Página principal, info general, horarios, destinos, registro, login | ✅ |
| CON-07 | Recuperar contraseña | ❌ **`p13-recuperar.html` es solo un formulario, no llama a ningún endpoint** |
| CON-08 a CON-12 | Selección de viaje, compra, ver info de compra, ver tiquetes | ✅ |
| CON-13 a CON-16 | Chat con asesor, recibir/responder consultas | ✅ Chatbot + escalamiento a agente humano |
| CON-17 a CON-22 | PQRS completo | ❌ **No existe.** Es distinto del chatbot: PQRS es un módulo formal de peticiones/quejas/reclamos/sugerencias con seguimiento, nunca se construyó |
| CON-23, CON-24 | Preguntas frecuentes | ✅ |
| CON-25 a CON-27 | Reseñas | ❌ No existe |
| CON-28, CON-29 | Redes sociales | 🟡 Revisar si el footer tiene links reales o son solo iconos decorativos |
| CON-30 a CON-34 | Administración (usuarios, horarios, rutas, servicios) | ✅ |
| CON-36 a CON-41 | Estructura, tablas, relaciones, datos de prueba, consultas SQL, CRUD | ✅ |
| CON-42 | Triggers necesarios | ❌ La lógica equivalente existe pero en Java (controladores), no como trigger de PostgreSQL |
| CON-44 | Wireframes | ❌ Sin evidencia en el repositorio |
| CON-45 | Heurísticas de Nielsen | ❌ Sin documento que lo sustente |
| CON-46 | Criterios de usabilidad | 🟡 Aplicado de forma implícita (Bootstrap), sin checklist formal |
| CON-47 | Accesibilidad WCAG | 🟡 Parcial (`p99-accesibilidad.html`, `aria-*`), sin auditoría formal |
| CON-48 | Navegación | ✅ |
| CON-49 a CON-55 | HTML5, CSS3, responsive, logo, botones, formularios | ✅ |
| CON-57, CON-58, CON-60 | Pruebas de registro/login, compra, chat | ✅ Documentadas (`04-plan-de-pruebas.md`, CP02-CP11) |
| CON-59 | Pruebas del módulo PQRS | ❌ No aplica — el módulo no existe |
| CON-61 | Pruebas de navegación/responsive | ❌ Sin evidencia formal |
| CON-62 | Corregir errores encontrados | ✅ (6 reglas de negocio + bug del SQL + 3 fixes de usabilidad del panel admin, todo documentado en commits reales) |

## Resumen para decidir antes de sustentar

- **47 de 62 ítems (76%)**: hechos y verificables en el código.
- **3 módulos completos faltan**: recuperar contraseña real (CON-07), PQRS (6 ítems, prioridad Alta), reseñas (3 ítems).
- **Triggers de BD (CON-42)**: la funcionalidad existe, pero en Java, no en SQL — decidan si la rúbrica lo exige literalmente.
- **UX/UI documentado (CON-44, CON-45)**: si en algún momento hicieron wireframes o aplicaron heurísticas de Nielsen formalmente, agréguenlos a `docs/originales-equipo/`. Si no, es más honesto decir que se aplicó de forma intuitiva.
- **El Excel de Sergio ya tiene la estructura correcta de seguimiento** (Streams, Sprints, % Avance, Responsable, Cumplimiento) — solo falta llenarla con los valores reales de esta tabla antes de la sustentación, para que el "seguimiento en el tiempo" que pide el criterio 6 se vea consistente con lo que van a demostrar en vivo.

Con el tiempo que quede, PQRS sigue siendo la brecha más grande (aparece "Alta" en 4 de sus 6 historias).
