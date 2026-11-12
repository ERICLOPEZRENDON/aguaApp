package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.HorarioSuministro;

@Repository
public interface HorarioSuministroRepository extends JpaRepository<HorarioSuministro, Long> {

    List<HorarioSuministro> findAllByOrderByColoniaIdAscDiaIdAscHoraInicioAsc();

    List<HorarioSuministro> findByColoniaIdOrderByDiaIdAscHoraInicioAsc(Long coloniaId);
}

