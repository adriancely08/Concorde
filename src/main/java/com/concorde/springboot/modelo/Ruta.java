package com.concorde.springboot.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Un trayecto entre dos terminales (origen y destino) con su precio base.
 * Cada Viaje programado usa una Ruta.
 */
@Entity
@Table(name = "ruta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRuta;

    @ManyToOne
    @JoinColumn(name = "id_terminal_origen")
    private Terminal terminalOrigen;

    @ManyToOne
    @JoinColumn(name = "id_terminal_destino")
    private Terminal terminalDestino;

    private Float valorTiquete;
}
