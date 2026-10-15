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

import com.example.demo.models.TipoProblema;
import com.example.demo.repositories.TipoProblemaRepository;

@RestController
@RequestMapping("/api/tipos-problema")
public class TipoProblemaController {

    private final TipoProblemaRepository tipoProblemaRepository;

    public TipoProblemaController(TipoProblemaRepository tipoProblemaRepository) {
        this.tipoProblemaRepository = tipoProblemaRepository;
    }

    @GetMapping
    public List<TipoProblema> listar() {
        return tipoProblemaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoProblema> obtenerPorId(@PathVariable Integer id) {
        return tipoProblemaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TipoProblema> crear(@RequestBody TipoProblema tipoProblema) {
        tipoProblema.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoProblemaRepository.save(tipoProblema));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoProblema> actualizar(@PathVariable Integer id, @RequestBody TipoProblema datos) {
        return tipoProblemaRepository.findById(id).map(existente -> {
            existente.setNombre(datos.getNombre());
            existente.setNivelPrioridad(datos.getNivelPrioridad());
            return ResponseEntity.ok(tipoProblemaRepository.save(existente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!tipoProblemaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        tipoProblemaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
