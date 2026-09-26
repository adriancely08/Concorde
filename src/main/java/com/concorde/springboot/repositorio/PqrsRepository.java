package com.concorde.springboot.repositorio;

import com.concorde.springboot.modelo.Pqrs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acceso a la tabla pqrs. findByUsuario_IdUsuario... es para que un
 * cliente vea solo las suyas; findAllByOrderBy... es para el panel de
 * agente/admin, que necesita verlas todas, más recientes primero.
 */
public interface PqrsRepository extends JpaRepository<Pqrs, Integer> {
    List<Pqrs> findByUsuario_IdUsuarioOrderByCreadoEnDesc(Integer idUsuario);
    List<Pqrs> findAllByOrderByCreadoEnDesc();
}
