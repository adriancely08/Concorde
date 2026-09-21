package com.concorde.springboot.controlador;

import com.concorde.springboot.modelo.Pago;
import com.concorde.springboot.modelo.Reserva;
import com.concorde.springboot.repositorio.PagoRepository;
import com.concorde.springboot.repositorio.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD de pagos. Los reportes de ingresos (ver ReporteController) se
 * calculan a partir de estos registros, filtrando por estado PROCESADO.
 *
 * Regla de negocio (historia de usuario): "la reserva solo pasa a
 * CONFIRMADA si el pago fue exitoso" -- por eso crear() y actualizar()
 * empujan ese cambio de estado a la Reserva asociada, en vez de dejar
 * que el pago y la reserva queden como dos registros independientes
 * que nadie sincroniza.
 */
@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoRepository repository;

    @Autowired
    private ReservaRepository reservaRepository;

    @GetMapping
    public List<Pago> listar() { return repository.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Pago> obtener(@PathVariable Integer id) {
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Pago> crear(@RequestBody Pago p) {
        Pago guardado = repository.save(p);
        sincronizarEstadoReserva(guardado);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pago> actualizar(@PathVariable Integer id, @RequestBody Pago datos) {
        return repository.findById(id).map(e -> {
            e.setReserva(datos.getReserva());
            e.setMetodoPago(datos.getMetodoPago());
            e.setValorPagado(datos.getValorPagado());
            e.setFechaPago(datos.getFechaPago());
            e.setEstado(datos.getEstado());
            Pago guardado = repository.save(e);
            sincronizarEstadoReserva(guardado);
            return ResponseEntity.ok(guardado);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void sincronizarEstadoReserva(Pago pago) {
        if (pago.getReserva() == null || !"PROCESADO".equals(pago.getEstado())) return;
        reservaRepository.findById(pago.getReserva().getIdReserva()).ifPresent(reserva -> {
            reserva.setEstado("CONFIRMADA");
            reservaRepository.save(reserva);
        });
    }
}
