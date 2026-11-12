package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.EstadoReporte;

@Repository
public interface EstadoReporteRepository extends JpaRepository<EstadoReporte, Integer> {
}

