# 3. Base de Datos — Proyecto Concorde

> **Fuente oficial:** este documento se corrigió para coincidir
> exactamente con `docs/originales-equipo/Diccionario_de_Datos_Concorde_final.xlsx`
> (el diccionario de datos real del equipo) y con las entidades JPA del
> código. Ahí mismo están también los diagramas de clases, relacional
> y ERD originales del equipo (en PDF) — este `.md` es un complemento
> en texto/Mermaid para quien no pueda abrir los PDF fácilmente,
> **no un reemplazo**.

Motor: **PostgreSQL**. Scripts en la raíz del proyecto:
`crear_tablas_faltantes.sql` (catálogo, usuarios, reservas, pagos) y
`crear_tablas_chat.sql` (chatbot).

## 3.1 Diagrama entidad-relación

```mermaid
erDiagram
    ROL ||--o{ USUARIO : tiene
    PERSONA ||--o| CONDUCTOR : es
    TERMINAL ||--o{ RUTA : "origen/destino"
    RUTA ||--o{ VIAJE : programa
    VEHICULO ||--o{ VIAJE : asignado_a
    CONDUCTOR ||--o{ VIAJE : conduce
    VEHICULO ||--o{ ASIENTO : tiene
    USUARIO ||--o{ RESERVA : realiza
    VIAJE ||--o{ RESERVA : recibe
    RESERVA ||--o{ DETALLE_RESERVA : detalla
    ASIENTO ||--o{ DETALLE_RESERVA : ocupado_en
    RESERVA ||--o| PAGO : genera
    USUARIO ||--o{ CONSULTA_CHAT : "inicia (cliente)"
    USUARIO ||--o{ CONSULTA_CHAT : "atiende (agente)"
    CONSULTA_CHAT ||--o{ MENSAJE_CHAT : contiene

    ROL {
        int id_rol PK
        varchar nombre_rol
    }
    USUARIO {
        int id_usuario PK
        varchar nombre_completo
        varchar correo_electronico UK
        varchar contrasena_hash
        varchar telefono
        varchar numero_documento UK
        timestamp creado_en
        int id_rol FK
    }
    PERSONA {
        int id_persona PK
        varchar numero_documento UK
        varchar nombre
        varchar direccion
        varchar correo_electronico
        varchar telefono
    }
    CONDUCTOR {
        int id_conductor PK
        varchar numero_licencia UK
        date fecha_vencimiento_licencia
        int id_persona FK
    }
    TERMINAL {
        int id_terminal PK
        varchar nombre
        varchar ciudad
        varchar direccion
    }
    RUTA {
        int id_ruta PK
        int id_terminal_origen FK
        int id_terminal_destino FK
        decimal valor_tiquete
    }
    VEHICULO {
        int id_vehiculo PK
        varchar placa UK
        varchar modelo
        int capacidad
    }
    ASIENTO {
        int id_asiento PK
        varchar numero_asiento
        varchar tipo
        boolean disponible
        int id_vehiculo FK
    }
    VIAJE {
        int id_viaje PK
        date fecha_viaje
        time hora_salida
        decimal precio
        varchar estado_viaje
        int id_ruta FK
        int id_vehiculo FK
        int id_conductor FK
    }
    RESERVA {
        int id_reserva PK
        date fecha_inicial
        varchar estado
        int id_usuario FK
        int id_viaje FK
    }
    DETALLE_RESERVA {
        int id_detalle_reserva PK
        boolean asiento_asignado
        int id_reserva FK
        int id_asiento FK
    }
    PAGO {
        int id_pago PK
        varchar metodo_pago
        decimal valor_pagado
        date fecha_pago
        varchar estado
        int id_reserva FK
    }
    CONSULTA_CHAT {
        int id_consulta PK
        varchar estado
        int id_usuario FK
        int id_agente FK
    }
    MENSAJE_CHAT {
        int id_mensaje PK
        varchar remitente
        text contenido
        int id_consulta FK
    }
```

**Nota sobre PERSONA vs. USUARIO:** son dos tablas independientes a
propósito, no una heredando de la otra. `PERSONA` es la base de
`CONDUCTOR` (personal interno, sin acceso al sistema); `USUARIO` es
quien puede iniciar sesión (cliente, agente o administrador). Por eso
ambas tienen campos parecidos (nombre, documento, teléfono) sin que
esto sea una falla de normalización: modelan personas en dos contextos
distintos del negocio.

## 3.2 Normalización

- **1FN:** todos los campos son atómicos (no hay listas ni campos
  compuestos guardados como texto).
- **2FN:** todas las tablas tienen una llave primaria simple
  (`id_...`), así que no hay dependencias parciales posibles.
- **3FN:** los datos que dependen unos de otros están separados en su
  propia tabla en vez de repetirse. Ejemplos concretos:
  - La ciudad de una terminal vive en `TERMINAL`, no repetida en cada
    `RUTA` o `VIAJE` que la usa.
  - El precio de una ruta vive en `RUTA`, no repetido en cada `VIAJE`
    que la usa.
  - Los datos de un conductor viven en `PERSONA`/`CONDUCTOR`, no
    repetidos en cada `VIAJE` que conduce.

## 3.3 Integridad referencial

Todas las relaciones se implementan con llaves foráneas (`@ManyToOne` /
`@JoinColumn` en las entidades Java, `REFERENCES` en el SQL). Ejemplos
de reglas de integridad ya aplicadas:
- `mensaje_chat.id_consulta` tiene `ON DELETE CASCADE`: si se borra una
  conversación, sus mensajes se borran con ella (no quedan mensajes
  huérfanos).
- `usuario.correo_electronico` y `vehiculo.placa` son únicos (`UK`):
  no puede haber dos cuentas con el mismo correo ni dos buses con la
  misma placa.

### Corrección importante hecha en esta revisión

Al comparar `crear_tablas_faltantes.sql` contra el diccionario de
datos oficial y contra las entidades Java reales, se encontró que el
script tenía **dos tablas desactualizadas**, de una versión anterior
del proyecto:
- `usuario.rol` estaba como texto plano (`VARCHAR` con `CHECK`) en vez
  de una llave foránea a una tabla `ROL` — y esa tabla `ROL` nunca se
  creaba en el script.
- `conductor` tenía sus propios campos `nombre_completo`/`telefono` en
  vez de referenciar una tabla `PERSONA` aparte — y esa tabla `PERSONA`
  tampoco se creaba.

Esto significa que si alguien corría este script en una base nueva
desde cero, la aplicación **no habría podido arrancar** (el código
Java espera esas dos tablas). Ya se corrigió el script para que cree
`ROL` y `PERSONA` correctamente y coincida con el resto del proyecto.

## 3.4 Correspondencia con los requerimientos

| Requerimiento (ver `01-analisis-requerimientos.md`) | Tablas involucradas |
|---|---|
| RF01 — Búsqueda de viajes | `VIAJE`, `RUTA`, `TERMINAL` |
| RF04 — Reservar y pagar | `RESERVA`, `DETALLE_RESERVA`, `ASIENTO`, `PAGO` |
| RF07/RF08/RF09 — Chatbot y escalamiento | `CONSULTA_CHAT`, `MENSAJE_CHAT` |
| RF10 — Gestión de catálogo | `RUTA`, `TERMINAL`, `VEHICULO`, `CONDUCTOR`, `VIAJE` |
| RF11/RF12 — Reportes | `PAGO`, `RESERVA`, `VIAJE` (agregados, sin tablas nuevas) |
| RNF01 — Contraseñas hasheadas | `USUARIO.contrasena_hash` |
