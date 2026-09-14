package com.concorde.springboot.repositorio;

import com.concorde.springboot.modelo.Asiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Acceso a la tabla asiento.
 */
public interface AsientoRepository extends JpaRepository<Asiento, Integer> {
}
