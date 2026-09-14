package com.concorde.springboot.repositorio;

import com.concorde.springboot.modelo.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a la tabla persona. Sin consultas personalizadas: solo lo
 * básico que ya trae JpaRepository (findAll, findById, save, delete).
 */
public interface PersonaRepository extends JpaRepository<Persona, Integer> {
}
