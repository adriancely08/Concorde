package com.concorde.springboot.controlador;

import com.concorde.springboot.modelo.Rol;
import com.concorde.springboot.repositorio.RolRepository;
import com.concorde.springboot.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * CRUD de roles (ADMIN/AGENTE/CLIENTE). Normalmente solo se consulta
 * (GET) para llenar selects en los formularios de usuarios; rara vez
 * se crean roles nuevos desde aquí.
 *
 * Regla de negocio (historia de usuario): no se puede eliminar un rol
 * que todavía tiene usuarios asignados, para no dejar usuarios "sin
 * rol" huérfanos en la base.
 */
@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolRepository repositorio;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Rol> listar() {
        return repositorio.findAll();
    }

    @GetMapping("/{id}")
    public Rol obtenerUno(@PathVariable Integer id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + id));
    }

    @PostMapping
    public Rol crear(@RequestBody Rol rol) {
        return repositorio.save(rol);
    }

    @PutMapping("/{id}")
    public Rol actualizar(@PathVariable Integer id, @RequestBody Rol datos) {
        Rol rol = repositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + id));
        rol.setNombreRol(datos.getNombreRol());
        return repositorio.save(rol);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        boolean tieneUsuarios = usuarioRepository.findAll().stream()
                .anyMatch(u -> u.getRol() != null && id.equals(u.getRol().getIdRol()));
        if (tieneUsuarios) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("detail", "No se puede eliminar este rol: todavía tiene usuarios asignados."));
        }
        repositorio.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
