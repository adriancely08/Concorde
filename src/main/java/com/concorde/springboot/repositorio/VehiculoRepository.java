package com.concorde.springboot.repositorio;

import com.concorde.springboot.modelo.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Acceso a la tabla vehiculo.
 */
public interface VehiculoRepository extends JpaRepository<Vehiculo, Integer> {
}
