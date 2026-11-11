package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.EstadoSolicitud;
import com.example.demo.models.SolicitudArco;

@Repository
public interface SolicitudArcoRepository extends JpaRepository<SolicitudArco, Long> {

    List<SolicitudArco> findByUsuarioAuthIdOrderByFechaSolicitudDesc(Long usuarioAuthId);

    List<SolicitudArco> findByEstadoSolicitudOrderByFechaSolicitudDesc(EstadoSolicitud estadoSolicitud);

    List<SolicitudArco> findAllByOrderByFechaSolicitudDesc();
}
