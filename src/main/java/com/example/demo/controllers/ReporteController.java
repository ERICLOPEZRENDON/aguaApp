package com.example.demo.controllers;

import java.math.BigDecimal;
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

import com.example.demo.models.Colonia;
import com.example.demo.models.PerfilUsuario;
import com.example.demo.models.Reporte;
import com.example.demo.models.TipoProblema;
import com.example.demo.repositories.ColoniaRepository;
import com.example.demo.repositories.PerfilUsuarioRepository;
import com.example.demo.repositories.ReporteRepository;
import com.example.demo.repositories.TipoProblemaRepository;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private static final BigDecimal LAT_MAX = new BigDecimal("90");
    private static final BigDecimal LON_MAX = new BigDecimal("180");

    private final ReporteRepository reporteRepository;
    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final ColoniaRepository coloniaRepository;
    private final TipoProblemaRepository tipoProblemaRepository;

    public ReporteController(ReporteRepository reporteRepository, PerfilUsuarioRepository perfilUsuarioRepository,
            ColoniaRepository coloniaRepository, TipoProblemaRepository tipoProblemaRepository) {
        this.reporteRepository = reporteRepository;
        this.perfilUsuarioRepository = perfilUsuarioRepository;
        this.coloniaRepository = coloniaRepository;
        this.tipoProblemaRepository = tipoProblemaRepository;
    }

    @GetMapping
    public List<Reporte> listar() {
        return reporteRepository.findAllByOrderByFechaCreacionDesc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reporte> obtenerPorId(@PathVariable Long id) {
        return reporteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Reportes de una colonia, del más reciente al más antiguo. */
    @GetMapping("/colonia/{coloniaId}")
    public ResponseEntity<List<Reporte>> listarPorColonia(@PathVariable Long coloniaId) {
        if (!coloniaRepository.existsById(coloniaId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reporteRepository.findByColoniaIdOrderByFechaCreacionDesc(coloniaId));
    }

    /** Reportes levantados por un ciudadano, del más reciente al más antiguo. */
    @GetMapping("/perfil/{perfilUsuarioId}")
    public ResponseEntity<List<Reporte>> listarPorPerfil(@PathVariable Long perfilUsuarioId) {
        if (!perfilUsuarioRepository.existsById(perfilUsuarioId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reporteRepository.findByPerfilUsuarioIdOrderByFechaCreacionDesc(perfilUsuarioId));
    }

    @PostMapping
    public ResponseEntity<Reporte> crear(@RequestBody Reporte reporte) {
        if (!coordenadasValidas(reporte)) {
            return ResponseEntity.badRequest().build();
        }
        PerfilUsuario perfil = resolverPerfil(reporte);
        Colonia colonia = resolverColonia(reporte);
        TipoProblema tipo = resolverTipoProblema(reporte);
        if (perfil == null || colonia == null || tipo == null) {
            return ResponseEntity.badRequest().build();
        }
        reporte.setId(null);
        reporte.setPerfilUsuario(perfil);
        reporte.setColonia(colonia);
        reporte.setTipoProblema(tipo);
        reporte.setFechaCreacion(null); // la asigna el servidor en @PrePersist
        return ResponseEntity.status(HttpStatus.CREATED).body(reporteRepository.save(reporte));
    }

    /** El autor (perfilUsuario) y la fechaCreacion no se modifican. */
    @PutMapping("/{id}")
    public ResponseEntity<Reporte> actualizar(@PathVariable Long id, @RequestBody Reporte datos) {
        return reporteRepository.findById(id).map(existente -> {
            if (!coordenadasValidas(datos)) {
                return ResponseEntity.badRequest().<Reporte>build();
            }
            Colonia colonia = resolverColonia(datos);
            TipoProblema tipo = resolverTipoProblema(datos);
            if (colonia == null || tipo == null) {
                return ResponseEntity.badRequest().<Reporte>build();
            }
            existente.setColonia(colonia);
            existente.setTipoProblema(tipo);
            existente.setDescripcion(datos.getDescripcion());
            existente.setLatitud(datos.getLatitud());
            existente.setLongitud(datos.getLongitud());
            return ResponseEntity.ok(reporteRepository.save(existente));
        }).orElse(ResponseEntity.notFound().build());
    }

    /** En BD, evidencias e historial del reporte se eliminan en cascada (ON DELETE CASCADE). */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!reporteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            reporteRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.noContent().build();
    }

    /** latitud y longitud son opcionales; si vienen, deben estar en rango geográfico válido. */
    private boolean coordenadasValidas(Reporte reporte) {
        BigDecimal lat = reporte.getLatitud();
        BigDecimal lon = reporte.getLongitud();
        if (lat != null && lat.abs().compareTo(LAT_MAX) > 0) {
            return false;
        }
        return lon == null || lon.abs().compareTo(LON_MAX) <= 0;
    }

    /** Busca el PerfilUsuario real en BD a partir del "perfilUsuario": {"id": X} recibido. */
    private PerfilUsuario resolverPerfil(Reporte reporte) {
        if (reporte.getPerfilUsuario() == null || reporte.getPerfilUsuario().getId() == null) {
            return null;
        }
        return perfilUsuarioRepository.findById(reporte.getPerfilUsuario().getId()).orElse(null);
    }

    /** Busca la Colonia real en BD a partir del "colonia": {"id": X} recibido. */
    private Colonia resolverColonia(Reporte reporte) {
        if (reporte.getColonia() == null || reporte.getColonia().getId() == null) {
            return null;
        }
        return coloniaRepository.findById(reporte.getColonia().getId()).orElse(null);
    }

    /** Busca el TipoProblema real en BD a partir del "tipoProblema": {"id": X} recibido. */
    private TipoProblema resolverTipoProblema(Reporte reporte) {
        if (reporte.getTipoProblema() == null || reporte.getTipoProblema().getId() == null) {
            return null;
        }
        return tipoProblemaRepository.findById(reporte.getTipoProblema().getId()).orElse(null);
    }
}

