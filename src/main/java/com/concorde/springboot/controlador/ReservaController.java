package com.concorde.springboot.controlador;

import com.concorde.springboot.modelo.DetalleReserva;
import com.concorde.springboot.modelo.Reserva;
import com.concorde.springboot.repositorio.DetalleReservaRepository;
import com.concorde.springboot.repositorio.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD de reservas. Lo usa el cliente (crear su propia reserva, ver
 * "Mis reservas") y también el personal desde los paneles de gestión.
 * Cualquier usuario logueado puede usar estos endpoints (ver
 * SecurityConfig); no hay verificación de que la reserva sea "suya".
 *
 * Regla de negocio (historia de usuario): "al cancelar, el/los
 * asiento(s) vuelven a estar disponibles" -- por eso actualizar()
 * libera los asientos automáticamente cuando el estado cambia a
 * CANCELADA, en vez de dejar el asiento marcado como ocupado para
 * siempre.
 */
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaRepository repository;

    @Autowired
    private DetalleReservaRepository detalleReservaRepository;

    @Autowired
    private com.concorde.springboot.repositorio.AsientoRepository asientoRepository;

    @GetMapping
    public List<Reserva> listar() { return repository.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtener(@PathVariable Integer id) {
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Reserva> crear(@RequestBody Reserva r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(r));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reserva> actualizar(@PathVariable Integer id, @RequestBody Reserva datos) {
        return repository.findById(id).map(e -> {
            boolean seEstaCancel = !"CANCELADA".equals(e.getEstado()) && "CANCELADA".equals(datos.getEstado());

            e.setUsuario(datos.getUsuario());
            e.setViaje(datos.getViaje());
            e.setFechaInicial(datos.getFechaInicial());
            e.setEstado(datos.getEstado());
            Reserva guardada = repository.save(e);

            if (seEstaCancel) {
                liberarAsientosDe(guardada);
            }
            return ResponseEntity.ok(guardada);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void liberarAsientosDe(Reserva reserva) {
        List<DetalleReserva> detalles = detalleReservaRepository.findAll().stream()
                .filter(d -> d.getReserva() != null && reserva.getIdReserva().equals(d.getReserva().getIdReserva()))
                .toList();
        for (DetalleReserva d : detalles) {
            if (d.getAsiento() == null) continue;
            asientoRepository.findById(d.getAsiento().getIdAsiento()).ifPresent(asiento -> {
                asiento.setDisponible(true);
                asientoRepository.save(asiento);
            });
        }
    }
}
