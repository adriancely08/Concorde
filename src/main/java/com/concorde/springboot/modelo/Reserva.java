package com.concorde.springboot.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * La reserva que hace un Usuario (cliente) para un Viaje. Tiene un
 * estado (PENDIENTE, CONFIRMADA, CANCELADA) y fecha de creación.
 */
@Entity
@Table(name = "reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReserva;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_viaje")
    private Viaje viaje;

    private LocalDate fechaInicial = LocalDate.now();
    private String estado = "PENDIENTE";
}
