package com.example.demo.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "historial_estados_reporte")
public class HistorialEstadoReporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** En BD: ON DELETE CASCADE (al borrar el reporte se borra su historial). */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reporte_id")
    private Reporte reporte;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "estado_reporte_id", nullable = false)
    private EstadoReporte estadoReporte;

    /** modificado_por_auth_id admite NULL en BD. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "modificado_por_auth_id")
    private UsuarioAuth modificadoPorAuth;

    @Column(name = "comentarios_resolucion", columnDefinition = "TEXT")
    private String comentariosResolucion;

    @Column(name = "fecha_cambio", updatable = false)
    private LocalDateTime fechaCambio;

    public HistorialEstadoReporte() {
    }

    @PrePersist
    protected void onCreate() {
        if (this.fechaCambio == null) {
            this.fechaCambio = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Reporte getReporte() {
        return reporte;
    }

    public void setReporte(Reporte reporte) {
        this.reporte = reporte;
    }

    public EstadoReporte getEstadoReporte() {
        return estadoReporte;
    }

    public void setEstadoReporte(EstadoReporte estadoReporte) {
        this.estadoReporte = estadoReporte;
    }

    public UsuarioAuth getModificadoPorAuth() {
        return modificadoPorAuth;
    }

    public void setModificadoPorAuth(UsuarioAuth modificadoPorAuth) {
        this.modificadoPorAuth = modificadoPorAuth;
    }

    public String getComentariosResolucion() {
        return comentariosResolucion;
    }

    public void setComentariosResolucion(String comentariosResolucion) {
        this.comentariosResolucion = comentariosResolucion;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }
}

