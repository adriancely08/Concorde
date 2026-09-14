package com.concorde.springboot.repositorio;

import com.concorde.springboot.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Acceso a la tabla usuario. Usada por login, registro y por los
 * paneles de administrador/agente para gestionar clientes.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}
