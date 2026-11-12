package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.UsuarioAuth;

@Repository
public interface UsuarioAuthRepository extends JpaRepository<UsuarioAuth, Long> {

    Optional<UsuarioAuth> findByEmail(String email);
}
