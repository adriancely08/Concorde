# concorde-springboot

Proyecto Spring Boot + PostgreSQL basado en el sistema Concorde, con las 10
tablas completas (ya no solo Vehiculo).

## Antes de correr

1. Crea la base si no existe: `CREATE DATABASE concorde_springboot;`
2. Corre `crear_tablas_faltantes.sql` (crea todas las tablas si no existen).
3. Edita `src/main/resources/application.properties` con tu contraseña real
   de PostgreSQL:
   ```properties
   spring.datasource.password=TU_PASSWORD
   ```
4. Nota: `spring.jpa.hibernate.ddl-auto=none` a propósito -- Hibernate NO va
   a intentar crear/alterar tablas solo. El esquema lo manejas tú con los
   scripts `.sql`. Esto evita los errores que ya tuviste antes con el otro
   proyecto (Hibernate chocando con columnas/vistas existentes).

## Endpoints disponibles

Todos siguen el mismo patron GET/POST/PUT/DELETE:

| Entidad | Endpoint base |
|---|---|
| Usuario | `/api/usuarios` |
| Terminal | `/api/terminales` |
| Conductor | `/api/conductores` |
| Ruta | `/api/rutas` |
| Vehiculo | `/api/vehiculos` |
| Viaje | `/api/viajes` |
| Asiento | `/api/asientos` |
| Reserva | `/api/reservas` |
| DetalleReserva | `/api/detalle-reservas` |
| Pago | `/api/pagos` |

Ejemplo para ver todos los usuarios: `http://localhost:8082/api/usuarios`

## Crear un registro con llave foranea (POST)

Para las entidades que referencian a otra (por ejemplo Ruta necesita dos
Terminal), el JSON del body debe traer el id anidado. Ejemplo, crear una
Ruta con Postman/Thunder Client (`POST http://localhost:8082/api/rutas`):

```json
{
  "terminalOrigen": { "idTerminal": 1 },
  "terminalDestino": { "idTerminal": 2 },
  "valorTiquete": 25000
}
```

Mismo patron para Viaje (`ruta`, `vehiculo`, `conductor`), Reserva
(`usuario`, `viaje`), DetalleReserva (`reserva`, `asiento`) y Pago
(`reserva`) -- siempre con el id ya existente en la base.
