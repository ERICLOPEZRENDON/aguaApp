package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.PerfilUsuario;

@Repository
public interface PerfilUsuarioRepository extends JpaRepository<PerfilUsuario, Long> {

    Optional<PerfilUsuario> findByUsuarioAuthId(Long usuarioAuthId);

    boolean existsByUsuarioAuthId(Long usuarioAuthId);
}

