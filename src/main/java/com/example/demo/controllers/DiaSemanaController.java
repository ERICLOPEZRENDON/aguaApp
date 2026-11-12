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

import com.example.demo.models.DiaSemana;
import com.example.demo.repositories.DiaSemanaRepository;

@RestController
@RequestMapping("/api/dias-semana")
public class DiaSemanaController {

    private final DiaSemanaRepository diaSemanaRepository;

    public DiaSemanaController(DiaSemanaRepository diaSemanaRepository) {
        this.diaSemanaRepository = diaSemanaRepository;
    }

    /** Ordenado por id para que salgan de Lunes a Domingo. */
    @GetMapping
    public List<DiaSemana> listar() {
        return diaSemanaRepository.findAll(Sort.by("id"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiaSemana> obtenerPorId(@PathVariable Integer id) {
        return diaSemanaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DiaSemana> crear(@RequestBody DiaSemana dia) {
        if (dia.getNombre() == null || dia.getNombre().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        dia.setId(null);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(diaSemanaRepository.save(dia));
        } catch (DataIntegrityViolationException e) {
            // nombre duplicado (UNIQUE)
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiaSemana> actualizar(@PathVariable Integer id, @RequestBody DiaSemana datos) {
        if (datos.getNombre() == null || datos.getNombre().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return diaSemanaRepository.findById(id).map(existente -> {
            existente.setNombre(datos.getNombre());
            try {
                return ResponseEntity.ok(diaSemanaRepository.save(existente));
            } catch (DataIntegrityViolationException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).<DiaSemana>build();
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!diaSemanaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            diaSemanaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // tiene horarios de suministro asociados
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.noContent().build();
    }
}

