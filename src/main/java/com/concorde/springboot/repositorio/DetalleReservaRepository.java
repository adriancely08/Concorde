package com.concorde.springboot.repositorio;

import com.concorde.springboot.modelo.DetalleReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Acceso a la tabla detalle_reserva (qué asiento ocupa cada reserva).
 */
public interface DetalleReservaRepository extends JpaRepository<DetalleReserva, Integer> {
}
