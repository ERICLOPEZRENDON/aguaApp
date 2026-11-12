package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.HistorialEstadoReporte;

@Repository
public interface HistorialEstadoReporteRepository extends JpaRepository<HistorialEstadoReporte, Long> {

    List<HistorialEstadoReporte> findAllByOrderByFechaCambioDesc();

    List<HistorialEstadoReporte> findByReporteIdOrderByFechaCambioDesc(Long reporteId);
}

