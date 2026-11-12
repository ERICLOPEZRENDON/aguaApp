package com.example.demo.controllers;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.EstadoReporte;
import com.example.demo.models.HistorialEstadoReporte;
import com.example.demo.models.Reporte;
import com.example.demo.models.UsuarioAuth;
import com.example.demo.repositories.EstadoReporteRepository;
import com.example.demo.repositories.HistorialEstadoReporteRepository;
import com.example.demo.repositories.ReporteRepository;
import com.example.demo.repositories.UsuarioAuthRepository;

/**
 * Bitácora de cambios de estado de los reportes. Es un registro de auditoría:
 * se crea y se consulta, pero no se edita (no hay PUT; Spring responde 405).
 * El estado actual de un reporte es el primer elemento de GET /reporte/{reporteId}.
 */
@RestController
@RequestMapping("/api/historial-reportes")
public class HistorialEstadoReporteController {

    private final HistorialEstadoReporteRepository historialRepository;
    private final ReporteRepository reporteRepository;
    private final EstadoReporteRepository estadoReporteRepository;
    private final UsuarioAuthRepository usuarioAuthRepository;

    public HistorialEstadoReporteController(HistorialEstadoReporteRepository historialRepository,
            ReporteRepository reporteRepository, EstadoReporteRepository estadoReporteRepository,
            UsuarioAuthRepository usuarioAuthRepository) {
        this.historialRepository = historialRepository;
        this.reporteRepository = reporteRepository;
        this.estadoReporteRepository = estadoReporteRepository;
        this.usuarioAuthRepository = usuarioAuthRepository;
    }

    @GetMapping
    public List<HistorialEstadoReporte> listar() {
        return historialRepository.findAllByOrderByFechaCambioDesc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistorialEstadoReporte> obtenerPorId(@PathVariable Long id) {
        return historialRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Historial de un reporte, del cambio más reciente al más antiguo. */
    @GetMapping("/reporte/{reporteId}")
    public ResponseEntity<List<HistorialEstadoReporte>> listarPorReporte(@PathVariable Long reporteId) {
        if (!reporteRepository.existsById(reporteId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(historialRepository.findByReporteIdOrderByFechaCambioDesc(reporteId));
    }

    @PostMapping
    public ResponseEntity<HistorialEstadoReporte> crear(@RequestBody HistorialEstadoReporte historial) {
        Reporte reporte = resolverReporte(historial);
        EstadoReporte estado = resolverEstado(historial);
        if (reporte == null || estado == null) {
            return ResponseEntity.badRequest().build();
        }
        // modificadoPorAuth es opcional (columna NULL), pero si viene debe existir
        UsuarioAuth modificadoPor = null;
        if (historial.getModificadoPorAuth() != null && historial.getModificadoPorAuth().getId() != null) {
            modificadoPor = usuarioAuthRepository.findById(historial.getModificadoPorAuth().getId()).orElse(null);
            if (modificadoPor == null) {
                return ResponseEntity.badRequest().build();
            }
        }
        historial.setId(null);
        historial.setReporte(reporte);
        historial.setEstadoReporte(estado);
        historial.setModificadoPorAuth(modificadoPor);
        historial.setFechaCambio(null); // la asigna el servidor en @PrePersist
        return ResponseEntity.status(HttpStatus.CREATED).body(historialRepository.save(historial));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!historialRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            historialRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.noContent().build();
    }

    /** Busca el Reporte real en BD a partir del "reporte": {"id": X} recibido. */
    private Reporte resolverReporte(HistorialEstadoReporte historial) {
        if (historial.getReporte() == null || historial.getReporte().getId() == null) {
            return null;
        }
        return reporteRepository.findById(historial.getReporte().getId()).orElse(null);
    }

    /** Busca el EstadoReporte real en BD a partir del "estadoReporte": {"id": X} recibido. */
    private EstadoReporte resolverEstado(HistorialEstadoReporte historial) {
        if (historial.getEstadoReporte() == null || historial.getEstadoReporte().getId() == null) {
            return null;
        }
        return estadoReporteRepository.findById(historial.getEstadoReporte().getId()).orElse(null);
    }
}

