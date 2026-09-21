package com.concorde.springboot.servicio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de PasswordUtil: que el hash generado sea reconocible como
 * BCrypt, que una contraseña correcta coincida y una incorrecta no, y
 * que el modo de compatibilidad con contraseñas antiguas en texto
 * plano funcione (para la migración transparente, ver LoginController).
 */
class PasswordUtilTest {

    private final PasswordUtil passwordUtil = new PasswordUtil();

    @Test
    void generaUnHashConFormatoBcrypt() {
        String hash = passwordUtil.hash("MiClave123!");
        assertTrue(passwordUtil.esHashBcrypt(hash), "el hash generado debería reconocerse como BCrypt");
    }

    @Test
    void unaContrasenaCorrectaCoincideConSuHash() {
        String hash = passwordUtil.hash("MiClave123!");
        assertTrue(passwordUtil.coincide("MiClave123!", hash));
    }

    @Test
    void unaContrasenaIncorrectaNoCoincide() {
        String hash = passwordUtil.hash("MiClave123!");
        assertFalse(passwordUtil.coincide("OtraClave456!", hash));
    }

    @Test
    void unaContrasenaEnTextoPlanoSeComparaLiteralmentePorCompatibilidad() {
        // Simula un registro viejo, de antes de que existiera BCrypt en el proyecto.
        String valorGuardadoAntiguo = "Clave2026!";
        assertFalse(passwordUtil.esHashBcrypt(valorGuardadoAntiguo));
        assertTrue(passwordUtil.coincide("Clave2026!", valorGuardadoAntiguo));
        assertFalse(passwordUtil.coincide("otraCosa", valorGuardadoAntiguo));
    }
}
