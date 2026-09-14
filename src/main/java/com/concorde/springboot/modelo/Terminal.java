package com.concorde.springboot.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Una terminal de transporte terrestre en una ciudad (punto de origen o
 * destino de una Ruta).
 */
@Entity
@Table(name = "terminal")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Terminal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTerminal;

    private String nombre;
    private String ciudad;
    private String direccion;
}
