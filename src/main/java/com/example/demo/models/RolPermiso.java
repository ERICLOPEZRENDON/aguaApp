package com.example.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Tabla puente roles_permisos. Se mapea como entidad propia (y no como @ManyToMany en Rol)
 * para no alterar el JSON de /api/roles, que ya está evaluado.
 */
@Entity
@Table(name = "roles_permisos")
@IdClass(RolPermisoId.class)
public class RolPermiso {

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "permiso_id", nullable = false)
    private Permiso permiso;

    public RolPermiso() {
    }

    public RolPermiso(Rol rol, Permiso permiso) {
        this.rol = rol;
        this.permiso = permiso;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Permiso getPermiso() {
        return permiso;
    }

    public void setPermiso(Permiso permiso) {
        this.permiso = permiso;
    }
}

