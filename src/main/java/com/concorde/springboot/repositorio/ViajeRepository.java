package com.concorde.springboot.repositorio;

import com.concorde.springboot.modelo.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Acceso a la tabla viaje.
 */
public interface ViajeRepository extends JpaRepository<Viaje, Integer> {
}
