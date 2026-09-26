package com.concorde.springboot.controlador;

import com.concorde.springboot.modelo.Pqrs;
import com.concorde.springboot.modelo.Usuario;
import com.concorde.springboot.repositorio.PqrsRepository;
import com.concorde.springboot.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * PQRS: Peticiones, Quejas, Reclamos y Sugerencias. Es un módulo
 * distinto del chatbot -- aquí queda un registro formal con
 * seguimiento (estado + respuesta), no una conversación en vivo.
 *
 *  - POST /api/pqrs                    -> el cliente radica una PQRS
 *  - GET  /api/pqrs                    -> listar todas (solo ADMIN/AGENTE, ver SecurityConfig)
 *  - GET  /api/pqrs/usuario/{idUsuario}-> las PQRS de un cliente puntual
 *  - GET  /api/pqrs/{id}                -> el detalle de una
 *  - PUT  /api/pqrs/{id}/responder      -> un agente/admin responde (solo ADMIN/AGENTE)
 */
@RestController
@RequestMapping("/api/pqrs")
public class PqrsController {

    @Autowired
    private PqrsRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {
        String tipo = valor(body, "tipo");
        String asunto = valor(body, "asunto");
        String descripcion = valor(body, "descripcion");

        if (tipo == null || asunto == null || descripcion == null) {
            return ResponseEntity.badRequest().body(Map.of("detail",
                    "Tipo, asunto y descripción son obligatorios."));
        }
        List<String> tiposValidos = List.of("PETICION", "QUEJA", "RECLAMO", "SUGERENCIA");
        if (!tiposValidos.contains(tipo)) {
            return ResponseEntity.badRequest().body(Map.of("detail",
                    "El tipo debe ser PETICION, QUEJA, RECLAMO o SUGERENCIA."));
        }

        Pqrs p = new Pqrs();
        if (body.get("idUsuario") != null) {
            Integer idUsuario = Integer.valueOf(body.get("idUsuario").toString());
            usuarioRepository.findById(idUsuario).ifPresent(p::setUsuario);
        }
        p.setTipo(tipo);
        p.setAsunto(asunto);
        p.setDescripcion(descripcion);
        p.setEstado("ABIERTA");
        p.setCreadoEn(LocalDateTime.now());

        Pqrs guardada = repository.save(p);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto(guardada));
    }

    @GetMapping
    public List<Map<String, Object>> listar() {
        return repository.findAllByOrderByCreadoEnDesc().stream().map(this::dto).collect(Collectors.toList());
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<Map<String, Object>> misPqrs(@PathVariable Integer idUsuario) {
        return repository.findByUsuario_IdUsuarioOrderByCreadoEnDesc(idUsuario)
                .stream().map(this::dto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return repository.findById(id)
                .map(p -> ResponseEntity.ok(dto(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/responder")
    public ResponseEntity<?> responder(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        Optional<Pqrs> opt = repository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        String respuesta = valor(body, "respuesta");
        if (respuesta == null) {
            return ResponseEntity.badRequest().body(Map.of("detail", "La respuesta no puede estar vacía."));
        }
        String nuevoEstado = valor(body, "estado");
        if (nuevoEstado == null) nuevoEstado = "RESUELTA";

        Pqrs p = opt.get();
        p.setRespuesta(respuesta);
        p.setEstado(nuevoEstado);
        p.setRespondidoEn(LocalDateTime.now());
        return ResponseEntity.ok(dto(repository.save(p)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private String valor(Map<String, Object> body, String clave) {
        Object v = body.get(clave);
        if (v == null) return null;
        String s = v.toString().trim();
        return s.isEmpty() ? null : s;
    }

    private Map<String, Object> dto(Pqrs p) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("idPqrs", p.getIdPqrs());
        map.put("tipo", p.getTipo());
        map.put("asunto", p.getAsunto());
        map.put("descripcion", p.getDescripcion());
        map.put("estado", p.getEstado());
        map.put("respuesta", p.getRespuesta());
        map.put("creadoEn", p.getCreadoEn());
        map.put("respondidoEn", p.getRespondidoEn());
        Usuario u = p.getUsuario();
        map.put("nombreCliente", u != null ? u.getNombreCompleto() : "—");
        map.put("correoCliente", u != null ? u.getCorreoElectronico() : "—");
        map.put("idUsuario", u != null ? u.getIdUsuario() : null);
        return map;
    }
}
