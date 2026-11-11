package com.example.demo.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.EstadoSolicitud;
import com.example.demo.models.SolicitudArco;
import com.example.demo.models.UsuarioAuth;
import com.example.demo.repositories.SolicitudArcoRepository;
import com.example.demo.repositories.UsuarioAuthRepository;

@RestController
@RequestMapping("/api/solicitudes-arco")
public class SolicitudArcoController {

    private final SolicitudArcoRepository solicitudArcoRepository;
    private final UsuarioAuthRepository usuarioAuthRepository;

    public SolicitudArcoController(SolicitudArcoRepository solicitudArcoRepository,
            UsuarioAuthRepository usuarioAuthRepository) {
        this.solicitudArcoRepository = solicitudArcoRepository;
        this.usuarioAuthRepository = usuarioAuthRepository;
    }

    @GetMapping
    public List<SolicitudArco> listar(@RequestParam(required = false) EstadoSolicitud estado) {
        if (estado != null) {
            return solicitudArcoRepository.findByEstadoSolicitudOrderByFechaSolicitudDesc(estado);
        }
        return solicitudArcoRepository.findAllByOrderByFechaSolicitudDesc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudArco> obtenerPorId(@PathVariable Long id) {
        return solicitudArcoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Solicitudes de un titular concreto. */
    @GetMapping("/usuario/{usuarioAuthId}")
    public ResponseEntity<List<SolicitudArco>> listarPorUsuario(@PathVariable Long usuarioAuthId) {
        if (!usuarioAuthRepository.existsById(usuarioAuthId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(
                solicitudArcoRepository.findByUsuarioAuthIdOrderByFechaSolicitudDesc(usuarioAuthId));
    }

    @PostMapping
    public ResponseEntity<SolicitudArco> crear(@RequestBody SolicitudArco solicitud) {
        if (solicitud.getTipoDerecho() == null
                || solicitud.getMotivo() == null || solicitud.getMotivo().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        UsuarioAuth usuario = resolverUsuario(solicitud);
        if (usuario == null) {
            return ResponseEntity.badRequest().build();
        }
      
        solicitud.setId(null);
        solicitud.setUsuarioAuth(usuario);
        solicitud.setEstadoSolicitud(EstadoSolicitud.PENDIENTE);
        solicitud.setRespuestaAdmin(null);
        solicitud.setFechaSolicitud(null);
        solicitud.setFechaResolucion(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitudArcoRepository.save(solicitud));
    }

    @PutMapping("/{id}/resolucion")
    public ResponseEntity<SolicitudArco> resolver(@PathVariable Long id, @RequestBody SolicitudArco datos) {
        return solicitudArcoRepository.findById(id).map(existente -> {
            EstadoSolicitud nuevoEstado = datos.getEstadoSolicitud();
            if (nuevoEstado == null || nuevoEstado == EstadoSolicitud.PENDIENTE) {
                return ResponseEntity.badRequest().<SolicitudArco>build();
            }
            if (existente.getEstadoSolicitud() == EstadoSolicitud.ATENDIDA
                    || existente.getEstadoSolicitud() == EstadoSolicitud.RECHAZADA) {
                return ResponseEntity.status(HttpStatus.CONFLICT).<SolicitudArco>build();
            }
            boolean cierra = nuevoEstado == EstadoSolicitud.ATENDIDA
                    || nuevoEstado == EstadoSolicitud.RECHAZADA;
            boolean sinRespuesta = datos.getRespuestaAdmin() == null || datos.getRespuestaAdmin().isBlank();
            if (cierra && sinRespuesta) {
                return ResponseEntity.badRequest().<SolicitudArco>build();
            }
            existente.setEstadoSolicitud(nuevoEstado);
            if (!sinRespuesta) {
                existente.setRespuestaAdmin(datos.getRespuestaAdmin());
            }
            if (cierra) {
                existente.setFechaResolucion(LocalDateTime.now());
            }
            return ResponseEntity.ok(solicitudArcoRepository.save(existente));
        }).orElse(ResponseEntity.notFound().build());
    }

    private UsuarioAuth resolverUsuario(SolicitudArco solicitud) {
        if (solicitud.getUsuarioAuth() == null || solicitud.getUsuarioAuth().getId() == null) {
            return null;
        }
        return usuarioAuthRepository.findById(solicitud.getUsuarioAuth().getId()).orElse(null);
    }
}
