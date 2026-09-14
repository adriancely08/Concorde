package com.concorde.springboot.modelo;

import jakarta.persistence.*;

/**
 * Los roles del sistema: ADMIN, AGENTE o CLIENTE. Cada Usuario tiene uno,
 * y de ahí depende qué rutas de la API puede usar (ver SecurityConfig).
 */
@Entity
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRol;

    private String nombreRol;

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }
}
