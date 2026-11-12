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

import com.example.demo.models.EvidenciaReporte;
import com.example.demo.models.Reporte;
import com.example.demo.repositories.EvidenciaReporteRepository;
import com.example.demo.repositories.ReporteRepository;

@RestController
@RequestMapping("/api/evidencias-reporte")
public class EvidenciaReporteController {

    private final EvidenciaReporteRepository evidenciaReporteRepository;
    private final ReporteRepository reporteRepository;

    public EvidenciaReporteController(EvidenciaReporteRepository evidenciaReporteRepository,
            ReporteRepository reporteRepository) {
        this.evidenciaReporteRepository = evidenciaReporteRepository;
        this.reporteRepository = reporteRepository;
    }

    @GetMapping
    public List<EvidenciaReporte> listar() {
        return evidenciaReporteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvidenciaReporte> obtenerPorId(@PathVariable Long id) {
        return evidenciaReporteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Evidencias de un reporte, en el orden en que se subieron. */
    @GetMapping("/reporte/{reporteId}")
    public ResponseEntity<List<EvidenciaReporte>> listarPorReporte(@PathVariable Long reporteId) {
        if (!reporteRepository.existsById(reporteId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(evidenciaReporteRepository.findByReporteIdOrderByFechaSubidaAsc(reporteId));
    }

    @PostMapping
    public ResponseEntity<EvidenciaReporte> crear(@RequestBody EvidenciaReporte evidencia) {
        if (!rutaValida(evidencia)) {
            return ResponseEntity.badRequest().build();
        }
        Reporte reporte = resolverReporte(evidencia);
        if (reporte == null) {
            return ResponseEntity.badRequest().build();
        }
        evidencia.setId(null);
        evidencia.setReporte(reporte);
        evidencia.setFechaSubida(null); // la asigna el servidor en @PrePersist
        return ResponseEntity.status(HttpStatus.CREATED).body(evidenciaReporteRepository.save(evidencia));
    }

    /** Solo se corrige la ruta del archivo; el reporte y la fechaSubida no cambian. */
    @PutMapping("/{id}")
    public ResponseEntity<EvidenciaReporte> actualizar(@PathVariable Long id, @RequestBody EvidenciaReporte datos) {
        return evidenciaReporteRepository.findById(id).map(existente -> {
            if (!rutaValida(datos)) {
                return ResponseEntity.badRequest().<EvidenciaReporte>build();
            }
            existente.setRutaArchivo(datos.getRutaArchivo());
            return ResponseEntity.ok(evidenciaReporteRepository.save(existente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!evidenciaReporteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            evidenciaReporteRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.noContent().build();
    }

    /** ruta_archivo es VARCHAR(255) NOT NULL. */
    private boolean rutaValida(EvidenciaReporte evidencia) {
        return evidencia.getRutaArchivo() != null && !evidencia.getRutaArchivo().isBlank()
                && evidencia.getRutaArchivo().length() <= 255;
    }

    /** Busca el Reporte real en BD a partir del "reporte": {"id": X} recibido. */
    private Reporte resolverReporte(EvidenciaReporte evidencia) {
        if (evidencia.getReporte() == null || evidencia.getReporte().getId() == null) {
            return null;
        }
        return reporteRepository.findById(evidencia.getReporte().getId()).orElse(null);
    }
}

