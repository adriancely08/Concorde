package com.concorde.springboot.controlador;

import com.concorde.springboot.modelo.Usuario;
import com.concorde.springboot.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @GetMapping
    public List<Usuario> listar() { return repository.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable Integer id) {
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody Usuario u) {
        // AQUÍ ESTÁ EL ARREGLO:
        // Le asignamos la fecha y hora actual antes de guardar
        // para que la base de datos no reciba un valor null en "creado_en".
        if (u.getCreadoEn() == null) {
            u.setCreadoEn(LocalDateTime.now());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(u));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Integer id, @RequestBody Usuario datos) {
        return repository.findById(id).map(e -> {
            e.setTelefono(datos.getTelefono());
            e.setNumeroDocumento(datos.getNumeroDocumento());
            e.setCorreoElectronico(datos.getCorreoElectronico());
            e.setContrasenaHash(datos.getContrasenaHash());
            e.setNombreCompleto(datos.getNombreCompleto());
            e.setRol(datos.getRol());
            // Si al actualizar no traía fecha, conserva la que ya tenía previamente
            if (e.getCreadoEn() == null) {
                e.setCreadoEn(LocalDateTime.now());
            }
            return ResponseEntity.ok(repository.save(e));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}