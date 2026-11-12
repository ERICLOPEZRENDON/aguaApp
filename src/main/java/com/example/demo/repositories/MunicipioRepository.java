package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Municipio;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipio, Integer> {

    List<Municipio> findByEstadoIdOrderByNombreAsc(Integer estadoId);
}

