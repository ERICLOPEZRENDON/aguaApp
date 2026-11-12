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

import com.example.demo.models.EstadoReporte;
import com.example.demo.repositories.EstadoReporteRepository;

@RestController
@RequestMapping("/api/estados-reporte")
public class EstadoReporteController {

    private final EstadoReporteRepository estadoReporteRepository;

    public EstadoReporteController(EstadoReporteRepository estadoReporteRepository) {
        this.estadoReporteRepository = estadoReporteRepository;
    }

    /** Ordenado por id para respetar el orden del flujo (ej. RECIBIDO, EN_PROCESO, RESUELTO). */
    @GetMapping
    public List<EstadoReporte> listar() {
        return estadoReporteRepository.findAll(Sort.by("id"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoReporte> obtenerPorId(@PathVariable Integer id) {
        return estadoReporteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EstadoReporte> crear(@RequestBody EstadoReporte estado) {
        if (estado.getNombre() == null || estado.getNombre().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        estado.setId(null);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(estadoReporteRepository.save(estado));
        } catch (DataIntegrityViolationException e) {
            // nombre duplicado (UNIQUE)
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstadoReporte> actualizar(@PathVariable Integer id, @RequestBody EstadoReporte datos) {
        if (datos.getNombre() == null || datos.getNombre().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return estadoReporteRepository.findById(id).map(existente -> {
            existente.setNombre(datos.getNombre());
            try {
                return ResponseEntity.ok(estadoReporteRepository.save(existente));
            } catch (DataIntegrityViolationException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).<EstadoReporte>build();
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!estadoReporteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            estadoReporteRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // está usado en el historial de algún reporte
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.noContent().build();
    }
}

