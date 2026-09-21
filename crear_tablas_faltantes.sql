-- =========================================================
-- Tablas de Concorde para la base concorde_springboot
-- Corregido para coincidir EXACTAMENTE con:
--   - Diccionario_de_Datos_Concorde_final.xlsx (fuente oficial)
--   - Las entidades JPA reales (Usuario, Rol, Persona, Conductor, ...)
-- El script anterior tenía dos tablas desactualizadas (usuario.rol
-- como texto plano en vez de tabla ROL, y conductor sin tabla PERSONA)
-- que no coincidían con el resto del proyecto. Quedó documentado en
-- EVALUACION-RUBRICA.md por qué se corrigió.
-- =========================================================

CREATE TABLE IF NOT EXISTS rol (
    id_rol      SERIAL PRIMARY KEY,
    nombre_rol  VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO rol (nombre_rol) VALUES ('ADMIN'), ('AGENTE'), ('CLIENTE')
    ON CONFLICT (nombre_rol) DO NOTHING;

CREATE TABLE IF NOT EXISTS persona (
    id_persona          SERIAL PRIMARY KEY,
    numero_documento    VARCHAR(20) NOT NULL UNIQUE,
    nombre              VARCHAR(80) NOT NULL,
    direccion           VARCHAR(200),
    correo_electronico  VARCHAR(150),
    telefono            VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS vehiculo (
    id_vehiculo   SERIAL PRIMARY KEY,
    placa         VARCHAR(10) NOT NULL UNIQUE,
    capacidad     INTEGER NOT NULL CHECK (capacidad > 0),
    modelo        VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario          SERIAL PRIMARY KEY,
    telefono            VARCHAR(20),
    numero_documento    VARCHAR(30) NOT NULL UNIQUE,
    correo_electronico  VARCHAR(150) NOT NULL UNIQUE,
    contrasena_hash     VARCHAR(255) NOT NULL,
    nombre_completo     VARCHAR(150) NOT NULL,
    id_rol              INTEGER NOT NULL REFERENCES rol(id_rol),
    creado_en           TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS terminal (
    id_terminal   SERIAL PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    ciudad        VARCHAR(100) NOT NULL,
    direccion     VARCHAR(150)
);

CREATE TABLE IF NOT EXISTS conductor (
    id_conductor                  SERIAL PRIMARY KEY,
    numero_licencia               VARCHAR(30) NOT NULL UNIQUE,
    fecha_vencimiento_licencia    DATE NOT NULL,
    id_persona                    INTEGER NOT NULL REFERENCES persona(id_persona)
);

CREATE TABLE IF NOT EXISTS ruta (
    id_ruta               SERIAL PRIMARY KEY,
    id_terminal_origen    INTEGER NOT NULL REFERENCES terminal(id_terminal),
    id_terminal_destino   INTEGER NOT NULL REFERENCES terminal(id_terminal),
    valor_tiquete         NUMERIC(10,2) NOT NULL CHECK (valor_tiquete >= 0),
    CHECK (id_terminal_origen <> id_terminal_destino)
);

CREATE TABLE IF NOT EXISTS viaje (
    id_viaje       SERIAL PRIMARY KEY,
    id_ruta        INTEGER NOT NULL REFERENCES ruta(id_ruta),
    id_vehiculo    INTEGER NOT NULL REFERENCES vehiculo(id_vehiculo),
    id_conductor   INTEGER NOT NULL REFERENCES conductor(id_conductor),
    fecha_viaje    DATE NOT NULL,
    hora_salida    TIME NOT NULL,
    precio         NUMERIC(10,2) NOT NULL CHECK (precio >= 0),
    estado_viaje   VARCHAR(20) NOT NULL DEFAULT 'PROGRAMADO'
                   CHECK (estado_viaje IN ('PROGRAMADO','EN_CURSO','FINALIZADO','CANCELADO'))
);

CREATE TABLE IF NOT EXISTS asiento (
    id_asiento      SERIAL PRIMARY KEY,
    id_vehiculo     INTEGER NOT NULL REFERENCES vehiculo(id_vehiculo),
    numero_asiento  VARCHAR(10) NOT NULL,
    tipo            VARCHAR(20) NOT NULL DEFAULT 'ESTANDAR',
    disponible      BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE (id_vehiculo, numero_asiento)
);

CREATE TABLE IF NOT EXISTS reserva (
    id_reserva      SERIAL PRIMARY KEY,
    id_usuario      INTEGER NOT NULL REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    id_viaje        INTEGER NOT NULL REFERENCES viaje(id_viaje),
    fecha_inicial   DATE NOT NULL DEFAULT CURRENT_DATE,
    estado          VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                    CHECK (estado IN ('PENDIENTE','CONFIRMADA','CANCELADA'))
);

CREATE TABLE IF NOT EXISTS detalle_reserva (
    id_detalle          SERIAL PRIMARY KEY,
    id_reserva          INTEGER NOT NULL REFERENCES reserva(id_reserva) ON DELETE CASCADE,
    id_asiento          INTEGER NOT NULL REFERENCES asiento(id_asiento),
    asiento_asignado    BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (id_reserva, id_asiento)
);

CREATE TABLE IF NOT EXISTS pago (
    id_pago        SERIAL PRIMARY KEY,
    id_reserva     INTEGER NOT NULL REFERENCES reserva(id_reserva) ON DELETE CASCADE,
    metodo_pago    VARCHAR(30) NOT NULL,
    valor_pagado   NUMERIC(10,2) NOT NULL CHECK (valor_pagado >= 0),
    fecha_pago     DATE NOT NULL DEFAULT CURRENT_DATE,
    estado         VARCHAR(20) NOT NULL DEFAULT 'PROCESADO'
                   CHECK (estado IN ('PROCESADO','REEMBOLSADO','FALLIDO'))
);

-- Indices utiles
CREATE INDEX IF NOT EXISTS idx_viaje_fecha ON viaje(fecha_viaje);
CREATE INDEX IF NOT EXISTS idx_reserva_usuario ON reserva(id_usuario);
CREATE INDEX IF NOT EXISTS idx_reserva_viaje ON reserva(id_viaje);
CREATE INDEX IF NOT EXISTS idx_pago_reserva ON pago(id_reserva);
CREATE INDEX IF NOT EXISTS idx_viaje_conductor ON viaje(id_conductor);
CREATE INDEX IF NOT EXISTS idx_ruta_terminales ON ruta(id_terminal_origen, id_terminal_destino);
CREATE INDEX IF NOT EXISTS idx_conductor_persona ON conductor(id_persona);
