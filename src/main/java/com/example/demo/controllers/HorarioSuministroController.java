package com.example.demo.controllers;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Colonia;
import com.example.demo.models.DiaSemana;
import com.example.demo.models.EstadoSemaforo;
import com.example.demo.models.HorarioSuministro;
import com.example.demo.repositories.ColoniaRepository;
import com.example.demo.repositories.DiaSemanaRepository;
import com.example.demo.repositories.EstadoSemaforoRepository;
import com.example.demo.repositories.HorarioSuministroRepository;

@RestController
@RequestMapping("/api/horarios-suministro")
public class HorarioSuministroController {

    private final HorarioSuministroRepository horarioSuministroRepository;
    private final ColoniaRepository coloniaRepository;
    private final DiaSemanaRepository diaSemanaRepository;
    private final EstadoSemaforoRepository estadoSemaforoRepository;

    public HorarioSuministroController(HorarioSuministroRepository horarioSuministroRepository,
            ColoniaRepository coloniaRepository, DiaSemanaRepository diaSemanaRepository,
            EstadoSemaforoRepository estadoSemaforoRepository) {
        this.horarioSuministroRepository = horarioSuministroRepository;
        this.coloniaRepository = coloniaRepository;
        this.diaSemanaRepository = diaSemanaRepository;
        this.estadoSemaforoRepository = estadoSemaforoRepository;
    }

    @GetMapping
    public List<HorarioSuministro> listar() {
        return horarioSuministroRepository.findAllByOrderByColoniaIdAscDiaIdAscHoraInicioAsc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioSuministro> obtenerPorId(@PathVariable Long id) {
        return horarioSuministroRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Horarios de una colonia, ordenados por día y hora de inicio. */
    @GetMapping("/colonia/{coloniaId}")
    public ResponseEntity<List<HorarioSuministro>> listarPorColonia(@PathVariable Long coloniaId) {
        if (!coloniaRepository.existsById(coloniaId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(horarioSuministroRepository.findByColoniaIdOrderByDiaIdAscHoraInicioAsc(coloniaId));
    }

    @PostMapping
    public ResponseEntity<HorarioSuministro> crear(@RequestBody HorarioSuministro horario) {
        if (!horasValidas(horario)) {
            return ResponseEntity.badRequest().build();
        }
        Colonia colonia = resolverColonia(horario);
        DiaSemana dia = resolverDia(horario);
        EstadoSemaforo estado = resolverEstadoSemaforo(horario);
        if (colonia == null || dia == null || estado == null) {
            return ResponseEntity.badRequest().build();
        }
        horario.setId(null);
        horario.setColonia(colonia);
        horario.setDia(dia);
        horario.setEstadoSemaforo(estado);
        return ResponseEntity.status(HttpStatus.CREATED).body(horarioSuministroRepository.save(horario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorarioSuministro> actualizar(@PathVariable Long id, @RequestBody HorarioSuministro datos) {
        return horarioSuministroRepository.findById(id).map(existente -> {
            if (!horasValidas(datos)) {
                return ResponseEntity.badRequest().<HorarioSuministro>build();
            }
            Colonia colonia = resolverColonia(datos);
            DiaSemana dia = resolverDia(datos);
            EstadoSemaforo estado = resolverEstadoSemaforo(datos);
            if (colonia == null || dia == null || estado == null) {
                return ResponseEntity.badRequest().<HorarioSuministro>build();
            }
            existente.setColonia(colonia);
            existente.setDia(dia);
            existente.setEstadoSemaforo(estado);
            existente.setHoraInicio(datos.getHoraInicio());
            existente.setHoraFin(datos.getHoraFin());
            return ResponseEntity.ok(horarioSuministroRepository.save(existente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!horarioSuministroRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            horarioSuministroRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.noContent().build();
    }

    /** Ambas horas obligatorias y horaInicio debe ser anterior a horaFin. */
    private boolean horasValidas(HorarioSuministro horario) {
        return horario.getHoraInicio() != null && horario.getHoraFin() != null
                && horario.getHoraInicio().isBefore(horario.getHoraFin());
    }

    /** Busca la Colonia real en BD a partir del "colonia": {"id": X} recibido. */
    private Colonia resolverColonia(HorarioSuministro horario) {
        if (horario.getColonia() == null || horario.getColonia().getId() == null) {
            return null;
        }
        return coloniaRepository.findById(horario.getColonia().getId()).orElse(null);
    }

    /** Busca el DiaSemana real en BD a partir del "dia": {"id": X} recibido. */
    private DiaSemana resolverDia(HorarioSuministro horario) {
        if (horario.getDia() == null || horario.getDia().getId() == null) {
            return null;
        }
        return diaSemanaRepository.findById(horario.getDia().getId()).orElse(null);
    }

    /** Busca el EstadoSemaforo real en BD a partir del "estadoSemaforo": {"id": X} recibido. */
    private EstadoSemaforo resolverEstadoSemaforo(HorarioSuministro horario) {
        if (horario.getEstadoSemaforo() == null || horario.getEstadoSemaforo().getId() == null) {
            return null;
        }
        return estadoSemaforoRepository.findById(horario.getEstadoSemaforo().getId()).orElse(null);
    }
}

