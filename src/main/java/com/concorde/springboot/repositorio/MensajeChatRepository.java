package com.concorde.springboot.repositorio;

import com.concorde.springboot.modelo.MensajeChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acceso a la tabla mensaje_chat (cada mensaje individual dentro de
 * una conversación del chatbot).
 */
public interface MensajeChatRepository extends JpaRepository<MensajeChat, Integer> {
    List<MensajeChat> findByConsulta_IdConsultaOrderByEnviadoEnAsc(Integer idConsulta);
}
