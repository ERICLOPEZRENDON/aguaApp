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
import com.example.demo.models.Municipio;
import com.example.demo.repositories.EstadoRepublicaRepository;
import com.example.demo.repositories.MunicipioRepository;

@RestController
@RequestMapping("/api/municipios")
public class MunicipioController {

    private final MunicipioRepository municipioRepository;
    private final EstadoRepublicaRepository estadoRepublicaRepository;

    public MunicipioController(MunicipioRepository municipioRepository,
            EstadoRepublicaRepository estadoRepublicaRepository) {
        this.municipioRepository = municipioRepository;
        this.estadoRepublicaRepository = estadoRepublicaRepository;
    }

    @GetMapping
    public List<Municipio> listar() {
        return municipioRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Municipio> obtenerPorId(@PathVariable Integer id) {
        return municipioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Municipios de un estado (para combos dependientes en el frontend). */
    @GetMapping("/estado/{estadoId}")
    public ResponseEntity<List<Municipio>> listarPorEstado(@PathVariable Integer estadoId) {
        if (!estadoRepublicaRepository.existsById(estadoId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(municipioRepository.findByEstadoIdOrderByNombreAsc(estadoId));
    }

    @PostMapping
    public ResponseEntity<Municipio> crear(@RequestBody Municipio municipio) {
        if (municipio.getNombre() == null || municipio.getNombre().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        EstadoRepublica estado = resolverEstado(municipio);
        if (estado == null) {
            return ResponseEntity.badRequest().build();
        }
        municipio.setId(null);
        municipio.setEstado(estado);
        return ResponseEntity.status(HttpStatus.CREATED).body(municipioRepository.save(municipio));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Municipio> actualizar(@PathVariable Integer id, @RequestBody Municipio datos) {
        return municipioRepository.findById(id).map(existente -> {
            if (datos.getNombre() == null || datos.getNombre().isBlank()) {
                return ResponseEntity.badRequest().<Municipio>build();
            }
            EstadoRepublica estado = resolverEstado(datos);
            if (estado == null) {
                return ResponseEntity.badRequest().<Municipio>build();
            }
            existente.setEstado(estado);
            existente.setNombre(datos.getNombre());
            return ResponseEntity.ok(municipioRepository.save(existente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!municipioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            municipioRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // tiene colonias asociadas
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.noContent().build();
    }

    /** Busca el EstadoRepublica real en BD a partir del "estado": {"id": X} recibido. */
    private EstadoRepublica resolverEstado(Municipio municipio) {
        if (municipio.getEstado() == null || municipio.getEstado().getId() == null) {
            return null;
        }
        return estadoRepublicaRepository.findById(municipio.getEstado().getId()).orElse(null);
    }
}

