package com.concorde.springboot.repositorio;

import com.concorde.springboot.modelo.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Acceso a la tabla reserva.
 */
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
}
