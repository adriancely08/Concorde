# 5. Preparación para el Despliegue — Proyecto Concorde

## 5.1 Tecnologías y versiones

| Componente | Versión / detalle |
|---|---|
| Java | 17 (obligatorio, definido en `pom.xml`) |
| Spring Boot | 3.3.4 |
| Base de datos | PostgreSQL |
| Gestor de dependencias | Maven |
| Autenticación | JWT (io.jsonwebtoken, `jjwt` 0.12.6) + Spring Security |
| Contraseñas | BCrypt (`spring-security-crypto`, incluido en `spring-boot-starter-security`) |
| Frontend | HTML + CSS + JavaScript "vanilla" (sin framework), servido como recurso estático del propio backend |
| Exportación de reportes | `html2pdf.js` (vía CDN) |

## 5.2 Variables de entorno

El proyecto ya no tiene contraseñas ni claves fijas en el código. Las
siguientes variables de entorno se pueden definir en el servidor de
despliegue; si no se definen, se usa un valor de respaldo pensado solo
para desarrollo local (ver `application.properties`):

| Variable | Para qué | Valor de respaldo (solo desarrollo) |
|---|---|---|
| `SERVER_PORT` | Puerto en el que corre la aplicación | `8082` |
| `DB_URL` | Cadena de conexión JDBC a PostgreSQL | `jdbc:postgresql://localhost:5432/concorde_springboot` |
| `DB_USERNAME` | Usuario de la base de datos | `postgres` |
| `DB_PASSWORD` | Contraseña de la base de datos | *(la de cada entorno local)* |
| `JWT_SECRET` | Clave para firmar los tokens JWT | *(clave de desarrollo, débil a propósito)* |

**Importante para producción:** `JWT_SECRET` y `DB_PASSWORD` **deben**
definirse como variables de entorno reales y distintas de los valores
de respaldo. Nunca deben subirse a Git.

### Cómo definirlas (ejemplos)

En Windows (PowerShell), antes de correr la aplicación:
```powershell
$env:DB_PASSWORD = "una-contraseña-real-y-secreta"
$env:JWT_SECRET = "otra-clave-larga-y-aleatoria-de-produccion"
mvn spring-boot:run
```

En Linux/Mac o en un servidor:
```bash
export DB_PASSWORD="una-contraseña-real-y-secreta"
export JWT_SECRET="otra-clave-larga-y-aleatoria-de-produccion"
mvn spring-boot:run
```

## 5.3 Dependencias externas necesarias en el servidor

- **Java 17** instalado.
- **PostgreSQL** accesible desde el servidor (puede ser local o un
  servicio administrado).
- Acceso saliente a internet para: descargar dependencias de Maven la
  primera vez, y para que el frontend cargue `html2pdf.js` y los
  íconos de Bootstrap Icons desde su CDN (si el servidor de despliegue
  no tiene salida a internet, esas dos cosas dejarían de funcionar).

## 5.4 Procedimiento de despliegue

1. Clonar el repositorio en el servidor.
2. Crear la base de datos PostgreSQL y correr, en orden:
   `crear_tablas_faltantes.sql`, luego `crear_tablas_chat.sql`.
3. Definir las variables de entorno de la sección 5.2.
4. Empaquetar la aplicación: `mvn clean package -DskipTests` (genera
   un `.jar` ejecutable en `target/`).
5. Ejecutar: `java -jar target/concorde-springboot-1.0.0.jar`.
6. Verificar que responde: `curl http://localhost:8082/api/roles`
   (debería devolver una lista en JSON, ya que es una ruta pública).
7. Configurar un proxy inverso (por ejemplo Nginx) si se va a exponer
   con HTTPS y un dominio propio — no incluido en el alcance actual
   del proyecto (ver `01-analisis-requerimientos.md`).

## 5.5 Checklist antes de sustentar/entregar

- [ ] `DB_PASSWORD` y `JWT_SECRET` definidos como variables de entorno
      (no los valores de respaldo) si se va a mostrar en un servidor real.
- [ ] Las tablas de la base de datos están creadas y con datos de
      prueba (ver `04-plan-de-pruebas.md`, sección 4.3).
- [ ] `mvn clean install` corre sin errores.
- [ ] La aplicación arranca y responde en `http://localhost:8082`.
