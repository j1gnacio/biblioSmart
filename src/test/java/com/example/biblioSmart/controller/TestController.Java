package com.example.biblioSmart.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test/fase1")
    public String testFase1() {
        return "✅ FASE 1 COMPLETADA - Entidades Base Configuradas Correctamente";
    }
}