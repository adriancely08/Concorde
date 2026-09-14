package com.concorde.springboot.repositorio;

import com.concorde.springboot.modelo.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a la tabla rol (ADMIN/AGENTE/CLIENTE).
 */
public interface RolRepository extends JpaRepository<Rol, Integer> {
}
