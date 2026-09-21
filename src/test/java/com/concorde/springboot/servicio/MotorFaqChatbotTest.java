package com.concorde.springboot.servicio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del motor de reglas del chatbot (MotorFaqChatbot). No
 * necesitan levantar Spring ni conectarse a la base de datos: la clase
 * es una lógica pura (texto entra, texto/decisión sale), así que se
 * puede probar de forma aislada y rápida.
 *
 * Sirven como evidencia de "preparación para pruebas": casos de éxito,
 * de escalación y de normalización de texto (ver docs/04-plan-de-pruebas.md
 * para los casos manuales adicionales, como los de la interfaz web).
 */
class MotorFaqChatbotTest {

    private final MotorFaqChatbot motor = new MotorFaqChatbot();

    @Test
    void reconoceUnaPreguntaFrecuenteClara() {
        MotorFaqChatbot.Resultado resultado = motor.responder("¿Cuánto equipaje puedo llevar?");
        assertFalse(resultado.escalar, "una pregunta de equipaje no debería escalar a un asesor");
        assertTrue(resultado.respuesta.toLowerCase().contains("equipaje"));
    }

    @Test
    void esInsensibleATildesYMayusculas() {
        MotorFaqChatbot.Resultado conTildes = motor.responder("¿CUÁNTO CUESTA el tiquete?");
        MotorFaqChatbot.Resultado sinTildes = motor.responder("cuanto cuesta el tiquete");
        assertEquals(conTildes.escalar, sinTildes.escalar);
        assertEquals(conTildes.respuesta, sinTildes.respuesta);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "quiero hablar con un asesor",
            "necesito un agente humano",
            "esto es una queja"
    })
    void escalaCuandoElClientePideUnHumano(String mensaje) {
        MotorFaqChatbot.Resultado resultado = motor.responder(mensaje);
        assertTrue(resultado.escalar, "debería escalar cuando el cliente pide explícitamente un asesor");
    }

    @Test
    void escalaCuandoNoHayNingunaCoincidencia() {
        MotorFaqChatbot.Resultado resultado = motor.responder("xkjhaskjdh asdkjhaskjd");
        assertTrue(resultado.escalar, "un mensaje sin ninguna palabra clave reconocida debe escalar, no inventar una respuesta");
    }

    @Test
    void noEscalaConUnSaludoSimple() {
        MotorFaqChatbot.Resultado resultado = motor.responder("Hola, buenas tardes");
        assertFalse(resultado.escalar);
    }
}
