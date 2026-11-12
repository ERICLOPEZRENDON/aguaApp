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

import com.example.demo.models.Permiso;
import com.example.demo.models.Rol;
import com.example.demo.models.RolPermiso;
import com.example.demo.models.RolPermisoId;
import com.example.demo.repositories.PermisoRepository;
import com.example.demo.repositories.RolPermisoRepository;
import com.example.demo.repositories.RolRepository;

/** Asignación de permisos a roles (tabla puente roles_permisos). No tiene PUT: se asigna o se quita. */
@RestController
@RequestMapping("/api/roles-permisos")
public class RolPermisoController {

    private final RolPermisoRepository rolPermisoRepository;
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;

    public RolPermisoController(RolPermisoRepository rolPermisoRepository, RolRepository rolRepository,
            PermisoRepository permisoRepository) {
        this.rolPermisoRepository = rolPermisoRepository;
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
    }

    @GetMapping
    public List<RolPermiso> listar() {
        return rolPermisoRepository.findAll();
    }

    /** Permisos asignados a un rol, ordenados por nombre de permiso. */
    @GetMapping("/rol/{rolId}")
    public ResponseEntity<List<RolPermiso>> listarPorRol(@PathVariable Integer rolId) {
        if (!rolRepository.existsById(rolId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rolPermisoRepository.findByRolIdOrderByPermisoNombreAsc(rolId));
    }

    /** Recibe {"rol": {"id": X}, "permiso": {"id": Y}}. 409 si ya estaba asignado. */
    @PostMapping
    public ResponseEntity<RolPermiso> asignar(@RequestBody RolPermiso asignacion) {
        if (asignacion.getRol() == null || asignacion.getRol().getId() == null
                || asignacion.getPermiso() == null || asignacion.getPermiso().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        Rol rol = rolRepository.findById(asignacion.getRol().getId()).orElse(null);
        Permiso permiso = permisoRepository.findById(asignacion.getPermiso().getId()).orElse(null);
        if (rol == null || permiso == null) {
            return ResponseEntity.badRequest().build();
        }
        if (rolPermisoRepository.existsById(new RolPermisoId(rol.getId(), permiso.getId()))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(rolPermisoRepository.save(new RolPermiso(rol, permiso)));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/rol/{rolId}/permiso/{permisoId}")
    public ResponseEntity<Void> quitar(@PathVariable Integer rolId, @PathVariable Integer permisoId) {
        RolPermisoId id = new RolPermisoId(rolId, permisoId);
        if (!rolPermisoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        rolPermisoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

