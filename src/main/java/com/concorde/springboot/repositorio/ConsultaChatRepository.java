package com.concorde.springboot.repositorio;

import com.concorde.springboot.modelo.ConsultaChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acceso a la tabla consulta_chat (las conversaciones del chatbot).
 * Los métodos personalizados sirven para listar tickets pendientes en
 * el panel de agente, ordenados por los más recientes primero.
 */
public interface ConsultaChatRepository extends JpaRepository<ConsultaChat, Integer> {
    List<ConsultaChat> findByEstadoInOrderByActualizadoEnDesc(List<String> estados);
    List<ConsultaChat> findAllByOrderByActualizadoEnDesc();
}
