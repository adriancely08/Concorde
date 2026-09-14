package com.concorde.springboot.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * El pago asociado a una Reserva: valor, método de pago y estado
 * (por ejemplo PROCESADO). Los reportes de ingresos se calculan a
 * partir de estos registros.
 */
@Entity
@Table(name = "pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPago;

    @ManyToOne
    @JoinColumn(name = "id_reserva")
    private Reserva reserva;

    private String metodoPago;
    private Float valorPagado;
    private LocalDate fechaPago = LocalDate.now();
    private String estado = "PROCESADO";
}
