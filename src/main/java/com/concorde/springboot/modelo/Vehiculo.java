package com.concorde.springboot.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Un bus de la flota: placa, capacidad de pasajeros, modelo. Cada Viaje
 * se hace en un Vehiculo, y cada Vehiculo tiene varios Asientos.
 */
@Entity
@Table(name = "vehiculo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idVehiculo;

    private String placa;
    private Integer capacidad;
    private String modelo;
}
