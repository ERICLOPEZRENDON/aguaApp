package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.RolPermiso;
import com.example.demo.models.RolPermisoId;

@Repository
public interface RolPermisoRepository extends JpaRepository<RolPermiso, RolPermisoId> {

    List<RolPermiso> findByRolIdOrderByPermisoNombreAsc(Integer rolId);
}

