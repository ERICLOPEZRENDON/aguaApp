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

import com.example.demo.models.EstadoRepublica;
import com.example.demo.repositories.EstadoRepublicaRepository;

@RestController
@RequestMapping("/api/estados-republica")
public class EstadoRepublicaController {

    private final EstadoRepublicaRepository estadoRepublicaRepository;

    public EstadoRepublicaController(EstadoRepublicaRepository estadoRepublicaRepository) {
        this.estadoRepublicaRepository = estadoRepublicaRepository;
    }

    @GetMapping
    public List<EstadoRepublica> listar() {
        return estadoRepublicaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoRepublica> obtenerPorId(@PathVariable Integer id) {
        return estadoRepublicaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EstadoRepublica> crear(@RequestBody EstadoRepublica estado) {
        if (estado.getNombre() == null || estado.getNombre().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        estado.setId(null);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(estadoRepublicaRepository.save(estado));
        } catch (DataIntegrityViolationException e) {
            // nombre duplicado (UNIQUE)
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstadoRepublica> actualizar(@PathVariable Integer id, @RequestBody EstadoRepublica datos) {
        if (datos.getNombre() == null || datos.getNombre().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return estadoRepublicaRepository.findById(id).map(existente -> {
            existente.setNombre(datos.getNombre());
            try {
                return ResponseEntity.ok(estadoRepublicaRepository.save(existente));
            } catch (DataIntegrityViolationException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).<EstadoRepublica>build();
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!estadoRepublicaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            estadoRepublicaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // tiene municipios asociados
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.noContent().build();
    }
}

