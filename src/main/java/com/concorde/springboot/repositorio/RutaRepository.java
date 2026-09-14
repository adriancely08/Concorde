package com.concorde.springboot.repositorio;

import com.concorde.springboot.modelo.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Acceso a la tabla ruta.
 */
public interface RutaRepository extends JpaRepository<Ruta, Integer> {
}
