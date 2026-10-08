package com.example.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/colonias")
public class ColoniaController {

    @GetMapping
    public List<Map<String, Object>> obtenerColoniasDePrueba() {
        Map<String, Object> colonia1 = new HashMap<>();
        colonia1.put("id", 1);
        colonia1.put("nombre", "Ojo de Agua");
        colonia1.put("codigo_postal", "55770");
        colonia1.put("municipio", "Tecámac");

        Map<String, Object> colonia2 = new HashMap<>();
        colonia2.put("id", 2);
        colonia2.put("nombre", "Héroes Tecámac");
        colonia2.put("codigo_postal", "55763");
        colonia2.put("municipio", "Tecámac");

        return Arrays.asList(colonia1, colonia2);
    }
}