package com.concorde.springboot.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Una salida programada: une una Ruta, un Vehiculo y un Conductor en una
 * fecha y hora concretas. Es lo que el cliente busca y reserva.
 */
@Entity
@Table(name = "viaje")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Viaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idViaje;

    @ManyToOne
    @JoinColumn(name = "id_ruta")
    private Ruta ruta;

    @ManyToOne
    @JoinColumn(name = "id_vehiculo")
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "id_conductor")
    private Conductor conductor;

    private LocalDate fechaViaje;
    private LocalTime horaSalida;
    private Float precio;
    private String estadoViaje = "PROGRAMADO";
}
