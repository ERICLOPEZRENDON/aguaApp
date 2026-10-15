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

import com.example.demo.models.EstadoSemaforo;
import com.example.demo.repositories.EstadoSemaforoRepository;

@RestController
@RequestMapping("/api/estados-semaforo")
public class EstadoSemaforoController {

    private final EstadoSemaforoRepository estadoSemaforoRepository;

    public EstadoSemaforoController(EstadoSemaforoRepository estadoSemaforoRepository) {
        this.estadoSemaforoRepository = estadoSemaforoRepository;
    }

    @GetMapping
    public List<EstadoSemaforo> listar() {
        return estadoSemaforoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoSemaforo> obtenerPorId(@PathVariable Integer id) {
        return estadoSemaforoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EstadoSemaforo> crear(@RequestBody EstadoSemaforo estado) {
        estado.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(estadoSemaforoRepository.save(estado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstadoSemaforo> actualizar(@PathVariable Integer id, @RequestBody EstadoSemaforo datos) {
        return estadoSemaforoRepository.findById(id).map(existente -> {
            existente.setColor(datos.getColor());
            existente.setDescripcion(datos.getDescripcion());
            return ResponseEntity.ok(estadoSemaforoRepository.save(existente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!estadoSemaforoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        estadoSemaforoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
