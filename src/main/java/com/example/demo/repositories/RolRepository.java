package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
}
