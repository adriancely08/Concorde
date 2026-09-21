package com.concorde.springboot.controlador;

import com.concorde.springboot.modelo.Asiento;
import com.concorde.springboot.modelo.Vehiculo;
import com.concorde.springboot.repositorio.AsientoRepository;
import com.concorde.springboot.repositorio.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD de vehículos (buses de la flota). Al crear uno nuevo, genera
 * automáticamente sus asientos según el campo "capacidad" (historia de
 * usuario: "para no tener que crearlos uno por uno manualmente"), en
 * vez de dejar que el administrador los cree a mano después.
 */
@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    @Autowired
    private VehiculoRepository repository;

    @Autowired
    private AsientoRepository asientoRepository;

    @GetMapping
    public List<Vehiculo> listar() { return repository.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Vehiculo> obtener(@PathVariable Integer id) {
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Vehiculo> crear(@RequestBody Vehiculo v) {
        Vehiculo guardado = repository.save(v);
        generarAsientos(guardado);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehiculo> actualizar(@PathVariable Integer id, @RequestBody Vehiculo datos) {
        return repository.findById(id).map(e -> {
            e.setPlaca(datos.getPlaca());
            e.setCapacidad(datos.getCapacidad());
            e.setModelo(datos.getModelo());
            return ResponseEntity.ok(repository.save(e));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Crea un asiento "ESTANDAR" por cada puesto de capacidad, numerados
     * 1..capacidad. Si el vehículo ya tenía asientos (por ejemplo, se
     * volvió a llamar por error), no duplica: solo genera si todavía no
     * tiene ninguno.
     */
    private void generarAsientos(Vehiculo vehiculo) {
        boolean yaTieneAsientos = !asientoRepository.findAll().stream()
                .filter(a -> a.getVehiculo() != null && vehiculo.getIdVehiculo().equals(a.getVehiculo().getIdVehiculo()))
                .toList().isEmpty();
        if (yaTieneAsientos || vehiculo.getCapacidad() == null) return;

        for (int numero = 1; numero <= vehiculo.getCapacidad(); numero++) {
            Asiento asiento = new Asiento();
            asiento.setVehiculo(vehiculo);
            asiento.setNumeroAsiento(String.valueOf(numero));
            asiento.setTipo("ESTANDAR");
            asiento.setDisponible(true);
            asientoRepository.save(asiento);
        }
    }
}
