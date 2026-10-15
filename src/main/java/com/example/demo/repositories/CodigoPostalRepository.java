package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.CodigoPostal;

@Repository
public interface CodigoPostalRepository extends JpaRepository<CodigoPostal, Integer> {
}
