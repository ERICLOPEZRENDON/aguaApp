package com.example.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @GetMapping
    public List<Map<String, Object>> obtenerUsuariosDePrueba() {
        Map<String, Object> usuario1 = new HashMap<>();
        usuario1.put("id", 1);
        usuario1.put("colonia_id", 1);
        usuario1.put("nombre_completo", "Eric Lopez Rendon");
        usuario1.put("email", "eric.lopez@example.com");
        usuario1.put("rol", "ADMIN");

        Map<String, Object> usuario2 = new HashMap<>();
        usuario2.put("id", 2);
        usuario2.put("colonia_id", 2);
        usuario2.put("nombre_completo", "Ana García");
        usuario2.put("email", "ana.garcia@example.com");
        usuario2.put("rol", "CIUDADANO");

        return Arrays.asList(usuario1, usuario2);
    }
}