-- =========================================================
-- Tabla PQRS (Peticiones, Quejas, Reclamos, Sugerencias)
-- Corre esto sobre la base concorde_springboot ya existente.
-- =========================================================

CREATE TABLE IF NOT EXISTS pqrs (
    id_pqrs        SERIAL PRIMARY KEY,
    id_usuario     INTEGER REFERENCES usuario(id_usuario),
    tipo           VARCHAR(20) NOT NULL
                   CHECK (tipo IN ('PETICION','QUEJA','RECLAMO','SUGERENCIA')),
    asunto         VARCHAR(200) NOT NULL,
    descripcion    TEXT NOT NULL,
    estado         VARCHAR(20) NOT NULL DEFAULT 'ABIERTA'
                   CHECK (estado IN ('ABIERTA','EN_PROCESO','RESUELTA')),
    respuesta      TEXT,
    creado_en      TIMESTAMP NOT NULL DEFAULT now(),
    respondido_en  TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_pqrs_usuario ON pqrs(id_usuario);
CREATE INDEX IF NOT EXISTS idx_pqrs_estado ON pqrs(estado);
