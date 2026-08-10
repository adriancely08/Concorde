package com.concorde.springboot.controlador;

import com.concorde.springboot.modelo.Usuario;
import com.concorde.springboot.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// Endpoint de login: valida el correo y la contraseña contra la tabla
// usuario real. Nota: aquí se compara la contraseña tal cual está
// guardada (sin hashear de verdad), igual que en la versión Python,
// porque así está el dato en la base actualmente -- para un sistema en
// producción debería guardarse con un hash real (ej. BCrypt) y
// compararse con ese hash, nunca en texto plano.
@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private UsuarioRepository repository;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String correo = body.get("correoElectronico");
        String contrasena = body.get("contrasena");

        List<Usuario> usuarios = repository.findAll();
        Optional<Usuario> encontrado = usuarios.stream()
                .filter(u -> u.getCorreoElectronico().equalsIgnoreCase(correo))
                .findFirst();

        if (encontrado.isEmpty() || !encontrado.get().getContrasenaHash().equals(contrasena)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("detail", "Correo o contraseña incorrectos"));
        }

        Usuario u = encontrado.get();
        return ResponseEntity.ok(Map.of(
                "idUsuario", u.getIdUsuario(),
                "nombreCompleto", u.getNombreCompleto(),
                "correoElectronico", u.getCorreoElectronico(),
                "rol", u.getRol().getNombreRol()
        ));
    }
}
