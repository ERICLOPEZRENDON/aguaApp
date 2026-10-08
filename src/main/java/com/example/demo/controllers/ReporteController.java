package com.example.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @GetMapping
    public List<Map<String, Object>> obtenerReportesDePrueba() {
        Map<String, Object> reporte1 = new HashMap<>();
        reporte1.put("id", 101);
        reporte1.put("tipo_problema", "FUGA");
        reporte1.put("estado", "PENDIENTE");
        reporte1.put("colonia_id", 1);

        Map<String, Object> reporte2 = new HashMap<>();
        reporte2.put("id", 102);
        reporte2.put("tipo_problema", "FALTA_AGUA");
        reporte2.put("estado", "EN_REVISION");
        reporte2.put("colonia_id", 2);

        return Arrays.asList(reporte1, reporte2);
    }
}