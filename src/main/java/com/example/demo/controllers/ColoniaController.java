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

import com.example.demo.models.CodigoPostal;
import com.example.demo.models.Colonia;
import com.example.demo.models.Municipio;
import com.example.demo.repositories.CodigoPostalRepository;
import com.example.demo.repositories.ColoniaRepository;
import com.example.demo.repositories.MunicipioRepository;

@RestController
@RequestMapping("/api/colonias")
public class ColoniaController {

    private final ColoniaRepository coloniaRepository;
    private final MunicipioRepository municipioRepository;
    private final CodigoPostalRepository codigoPostalRepository;

    public ColoniaController(ColoniaRepository coloniaRepository, MunicipioRepository municipioRepository,
            CodigoPostalRepository codigoPostalRepository) {
        this.coloniaRepository = coloniaRepository;
        this.municipioRepository = municipioRepository;
        this.codigoPostalRepository = codigoPostalRepository;
    }

    @GetMapping
    public List<Colonia> listar() {
        return coloniaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Colonia> obtenerPorId(@PathVariable Long id) {
        return coloniaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Colonias de un código postal. */
    @GetMapping("/cp/{cpId}")
    public ResponseEntity<List<Colonia>> listarPorCodigoPostal(@PathVariable Integer cpId) {
        if (!codigoPostalRepository.existsById(cpId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(coloniaRepository.findByCodigoPostalIdOrderByNombreAsc(cpId));
    }

    /** Colonias de un municipio. */
    @GetMapping("/municipio/{municipioId}")
    public ResponseEntity<List<Colonia>> listarPorMunicipio(@PathVariable Integer municipioId) {
        if (!municipioRepository.existsById(municipioId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(coloniaRepository.findByMunicipioIdOrderByNombreAsc(municipioId));
    }

    @PostMapping
    public ResponseEntity<Colonia> crear(@RequestBody Colonia colonia) {
        if (colonia.getNombre() == null || colonia.getNombre().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Municipio municipio = resolverMunicipio(colonia);
        CodigoPostal cp = resolverCodigoPostal(colonia);
        if (municipio == null || cp == null) {
            return ResponseEntity.badRequest().build();
        }
        colonia.setId(null);
        colonia.setMunicipio(municipio);
        colonia.setCodigoPostal(cp);
        return ResponseEntity.status(HttpStatus.CREATED).body(coloniaRepository.save(colonia));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Colonia> actualizar(@PathVariable Long id, @RequestBody Colonia datos) {
        return coloniaRepository.findById(id).map(existente -> {
            if (datos.getNombre() == null || datos.getNombre().isBlank()) {
                return ResponseEntity.badRequest().<Colonia>build();
            }
            Municipio municipio = resolverMunicipio(datos);
            CodigoPostal cp = resolverCodigoPostal(datos);
            if (municipio == null || cp == null) {
                return ResponseEntity.badRequest().<Colonia>build();
            }
            existente.setMunicipio(municipio);
            existente.setCodigoPostal(cp);
            existente.setNombre(datos.getNombre());
            return ResponseEntity.ok(coloniaRepository.save(existente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!coloniaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            coloniaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // tiene perfiles, horarios o reportes asociados
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.noContent().build();
    }

    /** Busca el Municipio real en BD a partir del "municipio": {"id": X} recibido. */
    private Municipio resolverMunicipio(Colonia colonia) {
        if (colonia.getMunicipio() == null || colonia.getMunicipio().getId() == null) {
            return null;
        }
        return municipioRepository.findById(colonia.getMunicipio().getId()).orElse(null);
    }

    /** Busca el CodigoPostal real en BD a partir del "codigoPostal": {"id": X} recibido. */
    private CodigoPostal resolverCodigoPostal(Colonia colonia) {
        if (colonia.getCodigoPostal() == null || colonia.getCodigoPostal().getId() == null) {
            return null;
        }
        return codigoPostalRepository.findById(colonia.getCodigoPostal().getId()).orElse(null);
    }
}

