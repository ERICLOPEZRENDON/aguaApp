package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.TipoProblema;

@Repository
public interface TipoProblemaRepository extends JpaRepository<TipoProblema, Integer> {
}
