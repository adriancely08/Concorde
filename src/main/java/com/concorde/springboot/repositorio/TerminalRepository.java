package com.concorde.springboot.repositorio;

import com.concorde.springboot.modelo.Terminal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Acceso a la tabla terminal.
 */
public interface TerminalRepository extends JpaRepository<Terminal, Integer> {
}
