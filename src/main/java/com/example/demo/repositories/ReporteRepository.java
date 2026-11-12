package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Reporte;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findAllByOrderByFechaCreacionDesc();

    List<Reporte> findByColoniaIdOrderByFechaCreacionDesc(Long coloniaId);

    List<Reporte> findByPerfilUsuarioIdOrderByFechaCreacionDesc(Long perfilUsuarioId);
}

