package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Colonia;

@Repository
public interface ColoniaRepository extends JpaRepository<Colonia, Long> {

    List<Colonia> findByCodigoPostalIdOrderByNombreAsc(Integer cpId);

    List<Colonia> findByMunicipioIdOrderByNombreAsc(Integer municipioId);
}

