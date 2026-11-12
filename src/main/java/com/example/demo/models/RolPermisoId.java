package com.example.demo.models;

import java.io.Serializable;
import java.util.Objects;

/** Llave primaria compuesta de roles_permisos: (rol_id, permiso_id). */
public class RolPermisoId implements Serializable {

    private Integer rol;
    private Integer permiso;

    public RolPermisoId() {
    }

    public RolPermisoId(Integer rol, Integer permiso) {
        this.rol = rol;
        this.permiso = permiso;
    }

    public Integer getRol() {
        return rol;
    }

    public void setRol(Integer rol) {
        this.rol = rol;
    }

    public Integer getPermiso() {
        return permiso;
    }

    public void setPermiso(Integer permiso) {
        this.permiso = permiso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RolPermisoId otro)) {
            return false;
        }
        return Objects.equals(rol, otro.rol) && Objects.equals(permiso, otro.permiso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rol, permiso);
    }
}

