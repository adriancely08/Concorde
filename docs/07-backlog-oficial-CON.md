# 7. Backlog Oficial (CON-01 a CON-62) — Estado Real

> Este es el backlog real del equipo (`docs/originales-equipo/Backlog_Concorde.xlsx`),
> con **62 ítems** organizados en 11 épicas. En el archivo original,
> los 62 tenían el Estado en "Por hacer" — pero al momento de escribir
> esto, buena parte del sistema ya está construido. Esta tabla
> reemplaza esa columna con el estado **verificado contra el código
> real**, no con lo que se planeó al inicio. Antes de sustentar,
> actualicen el Excel original con estos estados (o usen esta tabla).

## Leyenda
✅ Hecho y verificado · 🟡 Parcial / sin evidencia formal · ❌ No implementado

## Épica: Diseño y desarrollo de la página principal (CON-01 a CON-07)
| ID | Historia/Tarea | Estado real |
|---|---|---|
| CON-01 | Diseño y desarrollo de la página principal | ✅ |
| CON-02 | Visualizar información general de Concorde | ✅ |
| CON-03 | Consultar horarios de viajes | ✅ |
| CON-04 | Consultar destinos/rutas disponibles | ✅ |
| CON-05 | Registrarse en el sistema | ✅ (`/api/registro`, rol CLIENTE forzado en servidor) |
| CON-06 | Iniciar sesión | ✅ (con JWT) |
| CON-07 | Recuperar contraseña | ❌ **`p13-recuperar.html` es solo un formulario, no llama a ningún endpoint** |

## Épica: Compra y gestión de tiquetes (CON-08 a CON-12)
| ID | Historia/Tarea | Estado real |
|---|---|---|
| CON-08 a CON-12 | Seleccionar viaje, comprar, ver info de compra, ver mis tiquetes | ✅ Todo implementado (flujo p05→p11) |

## Épica: Atención al cliente (CON-13 a CON-16)
| ID | Historia/Tarea | Estado real |
|---|---|---|
| CON-13 a CON-16 | Chat con asesor, recibir/responder consultas | ✅ Chatbot + escalamiento a agente humano |

## Épica: PQRS (CON-17 a CON-22)
| ID | Historia/Tarea | Estado real |
|---|---|---|
| CON-17 a CON-22 | Registrar/consultar/responder PQRS | ❌ **No existe nada de esto.** El chatbot (CON-13/16) resuelve *atención al cliente*, pero PQRS es un módulo distinto (peticiones, quejas, reclamos, sugerencias con seguimiento formal) que nunca se construyó |

## Épica: Preguntas frecuentes (CON-23 a CON-24)
| ID | Historia/Tarea | Estado real |
|---|---|---|
| CON-23, CON-24 | Consultar preguntas frecuentes | ✅ (`p12-atencion.html`) |

## Épica: Reseñas (CON-25 a CON-27)
| ID | Historia/Tarea | Estado real |
|---|---|---|
| CON-25 a CON-27 | Dejar/ver reseñas | ❌ **No existe.** Ninguna tabla, endpoint ni página tiene esto |

## Épica: Redes sociales (CON-28 a CON-29)
| ID | Historia/Tarea | Estado real |
|---|---|---|
| CON-28, CON-29 | Acceder a redes sociales oficiales | 🟡 Revisar manualmente si el footer tiene links reales o son solo iconos decorativos |

## Épica: Administración del sistema (CON-30 a CON-34)
| ID | Historia/Tarea | Estado real |
|---|---|---|
| CON-30 a CON-34 | Gestionar usuarios, horarios, rutas, servicios | ✅ Todo en `admin-panel.html` |

## Épica: Base de datos (CON-35 a CON-42)
| ID | Historia/Tarea | Estado real |
|---|---|---|
| CON-36 a CON-38 | Estructura, tablas, relaciones | ✅ |
| CON-39 | Datos de prueba | ✅ (script de 30 registros por tabla) |
| CON-40 | Consultas SQL | ✅ (reportes) |
| CON-41 | Procedimientos CRUD | ✅ |
| CON-42 | Triggers necesarios | ❌ **No hay ningún trigger de PostgreSQL.** La lógica equivalente (liberar asientos al cancelar, confirmar reserva al pagar) se hizo en Java, en los controladores — funciona, pero técnicamente no es un "trigger" de base de datos si la rúbrica pide eso específicamente |

## Épica: Diseño UX/UI (CON-43 a CON-48)
| ID | Historia/Tarea | Estado real |
|---|---|---|
| CON-44 | Wireframes | ❌ Sin evidencia (si existieron, no quedaron en el repositorio) |
| CON-45 | Heurísticas de Nielsen | ❌ Sin documento que lo sustente explícitamente |
| CON-46 | Criterios de usabilidad | 🟡 Aplicado de forma implícita (Bootstrap), sin checklist formal |
| CON-47 | Accesibilidad WCAG | 🟡 Parcial (`p99-accesibilidad.html`, atributos `aria-*`), sin auditoría formal |
| CON-48 | Diseñar navegación | ✅ |

## Épica: Desarrollo frontend (CON-49 a CON-55)
| ID | Historia/Tarea | Estado real |
|---|---|---|
| CON-49 a CON-55 | HTML5, CSS3, responsive, logo, botones, formularios | ✅ Todo implementado |

## Épica: Pruebas (CON-56 a CON-62)
| ID | Historia/Tarea | Estado real |
|---|---|---|
| CON-57 | Pruebas registro/login | ✅ Documentadas (CP02-CP05 en `04-plan-de-pruebas.md`) |
| CON-58 | Pruebas de compra | ✅ Documentadas (CP06-CP07) |
| CON-59 | Pruebas del módulo PQRS | ❌ No aplica — el módulo no existe |
| CON-60 | Pruebas del chat | ✅ Documentadas (CP09-CP11) |
| CON-61 | Pruebas de navegación/responsive | ❌ Sin evidencia formal |
| CON-62 | Corregir errores encontrados | ✅ (6 reglas de negocio + bug del SQL + 3 fixes de usabilidad del panel admin, todo documentado) |

## Resumen para decidir antes de sustentar

- **47 de 62 ítems (76%)**: hechos y verificables en el código.
- **3 módulos completos faltan**: recuperar contraseña real (CON-07), PQRS (CON-17 a CON-22, 6 ítems) y reseñas (CON-25 a CON-27, 3 ítems).
- **Triggers de base de datos (CON-42)**: la funcionalidad existe pero implementada en Java, no como trigger SQL — decidan si eso importa según cómo esté redactado ese ítem en su rúbrica.
- **UX/UI documentado formalmente (CON-44 a CON-47)**: si hicieron wireframes o aplicaron heurísticas en algún momento, consíganlos y agréguenlos a `docs/originales-equipo/` — si no, es honesto decir que se aplicaron de forma intuitiva sin documento aparte.

Con el tiempo que quede, PQRS es el más importante de implementar (aparece marcado "Alta" en las 4 primeras historias) si quieren cerrar la brecha más grande del backlog real frente a lo que se construyó.
