package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Rol;
import com.example.demo.models.UsuarioAuth;
import com.example.demo.repositories.RolRepository;
import com.example.demo.repositories.UsuarioAuthRepository;

@RestController
@RequestMapping("/api/usuarios-auth")
public class UsuarioAuthController {

    private final UsuarioAuthRepository usuarioAuthRepository;
    private final RolRepository rolRepository;

    public UsuarioAuthController(UsuarioAuthRepository usuarioAuthRepository, RolRepository rolRepository) {
        this.usuarioAuthRepository = usuarioAuthRepository;
        this.rolRepository = rolRepository;
    }

    @GetMapping
    public List<UsuarioAuth> listar() {
        return usuarioAuthRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioAuth> obtenerPorId(@PathVariable Long id) {
        return usuarioAuthRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioAuth> crear(@RequestBody UsuarioAuth usuario) {
        Rol rol = resolverRol(usuario);
        if (rol == null) {
            return ResponseEntity.badRequest().build();
        }
        usuario.setId(null);
        usuario.setRol(rol);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioAuthRepository.save(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioAuth> actualizar(@PathVariable Long id, @RequestBody UsuarioAuth datos) {
        return usuarioAuthRepository.findById(id).map(existente -> {
            Rol rol = resolverRol(datos);
            if (rol == null) {
                return ResponseEntity.badRequest().<UsuarioAuth>build();
            }
            existente.setRol(rol);
            existente.setEmail(datos.getEmail());
            // Solo se cambia la contraseña si se envía una nueva
            if (datos.getPasswordHash() != null && !datos.getPasswordHash().isBlank()) {
                existente.setPasswordHash(datos.getPasswordHash());
            }
            if (datos.getActivo() != null) {
                existente.setActivo(datos.getActivo());
            }
            return ResponseEntity.ok(usuarioAuthRepository.save(existente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!usuarioAuthRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        usuarioAuthRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /** Busca el Rol real en BD a partir del "rol": {"id": X} recibido. */
    private Rol resolverRol(UsuarioAuth usuario) {
        if (usuario.getRol() == null || usuario.getRol().getId() == null) {
            return null;
        }
        return rolRepository.findById(usuario.getRol().getId()).orElse(null);
    }
}
