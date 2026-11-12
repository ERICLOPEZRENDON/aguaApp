package com.example.demo.controllers;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
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

import com.example.demo.models.Permiso;
import com.example.demo.repositories.PermisoRepository;

@RestController
@RequestMapping("/api/permisos")
public class PermisoController {

    private final PermisoRepository permisoRepository;

    public PermisoController(PermisoRepository permisoRepository) {
        this.permisoRepository = permisoRepository;
    }

    @GetMapping
    public List<Permiso> listar() {
        return permisoRepository.findAll(Sort.by("nombre"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permiso> obtenerPorId(@PathVariable Integer id) {
        return permisoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Permiso> crear(@RequestBody Permiso permiso) {
        if (permiso.getNombre() == null || permiso.getNombre().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        permiso.setId(null);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(permisoRepository.save(permiso));
        } catch (DataIntegrityViolationException e) {
            // nombre duplicado (UNIQUE)
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Permiso> actualizar(@PathVariable Integer id, @RequestBody Permiso datos) {
        if (datos.getNombre() == null || datos.getNombre().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return permisoRepository.findById(id).map(existente -> {
            existente.setNombre(datos.getNombre());
            try {
                return ResponseEntity.ok(permisoRepository.save(existente));
            } catch (DataIntegrityViolationException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).<Permiso>build();
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    /** En BD, sus asignaciones en roles_permisos se eliminan en cascada (ON DELETE CASCADE). */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!permisoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            permisoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.noContent().build();
    }
}

