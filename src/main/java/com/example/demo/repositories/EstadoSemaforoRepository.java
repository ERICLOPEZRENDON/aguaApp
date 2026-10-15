package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.EstadoSemaforo;

@Repository
public interface EstadoSemaforoRepository extends JpaRepository<EstadoSemaforo, Integer> {
}
