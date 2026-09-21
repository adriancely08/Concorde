package com.concorde.springboot.servicio;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Genera y valida los tokens JWT que usa toda la API para saber quién
 * hace cada petición, sin depender de sesiones de servidor (stateless).
 *
 * La clave de firma ya NO está fija en el código: se lee de la
 * propiedad "concorde.jwt.secret" (application.properties), que a su
 * vez toma la variable de entorno JWT_SECRET si existe (ver
 * docs/05-despliegue.md). Así, en un servidor real, la clave real
 * nunca queda en el código fuente ni en el repositorio de Git.
 */
@Service
public class JwtUtil {

    private static final long DURACION_MS = 8L * 60 * 60 * 1000; // 8 horas

    private final SecretKey clave;

    public JwtUtil(@Value("${concorde.jwt.secret}") String claveSecreta) {
        this.clave = Keys.hmacShaKeyFor(claveSecreta.getBytes());
    }

    public String generarToken(Integer idUsuario, String correo, String rol) {
        Date ahora = new Date();
        Date expira = new Date(ahora.getTime() + DURACION_MS);
        return Jwts.builder()
                .subject(correo)
                .claim("idUsuario", idUsuario)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(expira)
                .signWith(clave)
                .compact();
    }

    /** Lanza JwtException si el token no es válido, está corrupto o ya expiró. */
    public Claims validarYObtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
