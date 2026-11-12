package com.example.demo.controllers;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
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

import com.example.demo.models.Colonia;
import com.example.demo.models.PerfilUsuario;
import com.example.demo.models.UsuarioAuth;
import com.example.demo.repositories.ColoniaRepository;
import com.example.demo.repositories.PerfilUsuarioRepository;
import com.example.demo.repositories.UsuarioAuthRepository;

@RestController
@RequestMapping("/api/perfiles-usuario")
public class PerfilUsuarioController {

    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final UsuarioAuthRepository usuarioAuthRepository;
    private final ColoniaRepository coloniaRepository;

    public PerfilUsuarioController(PerfilUsuarioRepository perfilUsuarioRepository,
            UsuarioAuthRepository usuarioAuthRepository, ColoniaRepository coloniaRepository) {
        this.perfilUsuarioRepository = perfilUsuarioRepository;
        this.usuarioAuthRepository = usuarioAuthRepository;
        this.coloniaRepository = coloniaRepository;
    }

    @GetMapping
    public List<PerfilUsuario> listar() {
        return perfilUsuarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfilUsuario> obtenerPorId(@PathVariable Long id) {
        return perfilUsuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Perfil de una cuenta (útil después del login). */
    @GetMapping("/usuario-auth/{usuarioAuthId}")
    public ResponseEntity<PerfilUsuario> obtenerPorUsuarioAuth(@PathVariable Long usuarioAuthId) {
        return perfilUsuarioRepository.findByUsuarioAuthId(usuarioAuthId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PerfilUsuario> crear(@RequestBody PerfilUsuario perfil) {
        if (!datosObligatoriosValidos(perfil)) {
            return ResponseEntity.badRequest().build();
        }
        UsuarioAuth usuario = resolverUsuario(perfil);
        if (usuario == null) {
            return ResponseEntity.badRequest().build();
        }
        // usuario_auth_id es UNIQUE: una cuenta solo puede tener un perfil
        if (perfilUsuarioRepository.existsByUsuarioAuthId(usuario.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Colonia colonia = null;
        if (traeColonia(perfil)) {
            colonia = resolverColonia(perfil);
            if (colonia == null) {
                return ResponseEntity.badRequest().build();
            }
        }
        perfil.setId(null);
        perfil.setUsuarioAuth(usuario);
        perfil.setColonia(colonia);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(perfilUsuarioRepository.save(perfil));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /** La cuenta (usuarioAuth) del perfil no se cambia; se actualizan colonia y datos personales. */
    @PutMapping("/{id}")
    public ResponseEntity<PerfilUsuario> actualizar(@PathVariable Long id, @RequestBody PerfilUsuario datos) {
        return perfilUsuarioRepository.findById(id).map(existente -> {
            if (!datosObligatoriosValidos(datos)) {
                return ResponseEntity.badRequest().<PerfilUsuario>build();
            }
            Colonia colonia = null;
            if (traeColonia(datos)) {
                colonia = resolverColonia(datos);
                if (colonia == null) {
                    return ResponseEntity.badRequest().<PerfilUsuario>build();
                }
            }
            existente.setColonia(colonia);
            existente.setNombres(datos.getNombres());
            existente.setApellidoPaterno(datos.getApellidoPaterno());
            existente.setApellidoMaterno(datos.getApellidoMaterno());
            existente.setTelefono(datos.getTelefono());
            return ResponseEntity.ok(perfilUsuarioRepository.save(existente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!perfilUsuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            perfilUsuarioRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // tiene reportes asociados
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.noContent().build();
    }

    private boolean datosObligatoriosValidos(PerfilUsuario perfil) {
        return perfil.getNombres() != null && !perfil.getNombres().isBlank()
                && perfil.getApellidoPaterno() != null && !perfil.getApellidoPaterno().isBlank();
    }

    private boolean traeColonia(PerfilUsuario perfil) {
        return perfil.getColonia() != null && perfil.getColonia().getId() != null;
    }

    /** Busca el UsuarioAuth real en BD a partir del "usuarioAuth": {"id": X} recibido. */
    private UsuarioAuth resolverUsuario(PerfilUsuario perfil) {
        if (perfil.getUsuarioAuth() == null || perfil.getUsuarioAuth().getId() == null) {
            return null;
        }
        return usuarioAuthRepository.findById(perfil.getUsuarioAuth().getId()).orElse(null);
    }

    /** Busca la Colonia real en BD a partir del "colonia": {"id": X} recibido. */
    private Colonia resolverColonia(PerfilUsuario perfil) {
        return coloniaRepository.findById(perfil.getColonia().getId()).orElse(null);
    }
}

