package com.concorde.springboot.controlador;

import com.concorde.springboot.modelo.Asiento;
import com.concorde.springboot.modelo.DetalleReserva;
import com.concorde.springboot.repositorio.AsientoRepository;
import com.concorde.springboot.repositorio.DetalleReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * CRUD de detalle_reserva (qué asiento le corresponde a cada reserva).
 *
 * Reglas de negocio (historias de usuario):
 *  - Solo se pueden reservar asientos marcados como "disponible".
 *  - Al reservarse, el asiento pasa a "no disponible".
 *  - Al eliminar el detalle (por ejemplo, al cancelar una reserva), el
 *    asiento vuelve a quedar disponible.
 */
@RestController
@RequestMapping("/api/detalle-reservas")
public class DetalleReservaController {

    @Autowired
    private DetalleReservaRepository repository;

    @Autowired
    private AsientoRepository asientoRepository;

    @GetMapping
    public List<DetalleReserva> listar() { return repository.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleReserva> obtener(@PathVariable Integer id) {
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody DetalleReserva d) {
        if (d.getAsiento() == null) {
            return ResponseEntity.badRequest().body(Map.of("detail", "Debes indicar el asiento a reservar."));
        }
        Asiento asiento = asientoRepository.findById(d.getAsiento().getIdAsiento())
                .orElse(null);
        if (asiento == null) {
            return ResponseEntity.badRequest().body(Map.of("detail", "El asiento indicado no existe."));
        }
        if (!Boolean.TRUE.equals(asiento.getDisponible())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("detail", "Ese asiento ya no está disponible."));
        }

        asiento.setDisponible(false);
        asientoRepository.save(asiento);
        d.setAsientoAsignado(true);

        DetalleReserva guardado = repository.save(d);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleReserva> actualizar(@PathVariable Integer id, @RequestBody DetalleReserva datos) {
        return repository.findById(id).map(e -> {
            e.setReserva(datos.getReserva());
            e.setAsiento(datos.getAsiento());
            e.setAsientoAsignado(datos.getAsientoAsignado());
            return ResponseEntity.ok(repository.save(e));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        return repository.findById(id).map(detalle -> {
            // Libera el asiento antes de borrar el detalle, para que
            // vuelva a aparecer disponible para otra reserva.
            if (detalle.getAsiento() != null) {
                asientoRepository.findById(detalle.getAsiento().getIdAsiento()).ifPresent(asiento -> {
                    asiento.setDisponible(true);
                    asientoRepository.save(asiento);
                });
            }
            repository.deleteById(id);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
