package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.EvidenciaReporte;

@Repository
public interface EvidenciaReporteRepository extends JpaRepository<EvidenciaReporte, Long> {

    List<EvidenciaReporte> findByReporteIdOrderByFechaSubidaAsc(Long reporteId);
}

