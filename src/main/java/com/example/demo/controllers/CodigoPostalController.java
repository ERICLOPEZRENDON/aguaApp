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

import com.example.demo.models.CodigoPostal;
import com.example.demo.repositories.CodigoPostalRepository;

@RestController
@RequestMapping("/api/codigos-postales")
public class CodigoPostalController {

    private final CodigoPostalRepository codigoPostalRepository;

    public CodigoPostalController(CodigoPostalRepository codigoPostalRepository) {
        this.codigoPostalRepository = codigoPostalRepository;
    }

    @GetMapping
    public List<CodigoPostal> listar() {
        return codigoPostalRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CodigoPostal> obtenerPorId(@PathVariable Integer id) {
        return codigoPostalRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CodigoPostal> crear(@RequestBody CodigoPostal codigoPostal) {
        codigoPostal.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(codigoPostalRepository.save(codigoPostal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CodigoPostal> actualizar(@PathVariable Integer id, @RequestBody CodigoPostal datos) {
        return codigoPostalRepository.findById(id).map(existente -> {
            existente.setCodigo(datos.getCodigo());
            return ResponseEntity.ok(codigoPostalRepository.save(existente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!codigoPostalRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        codigoPostalRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
