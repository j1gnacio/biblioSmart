package com.example.biblioSmart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.biblioSmart.model.dto.PrestamoDTO;
import com.example.biblioSmart.service.PrestamoService;

@RestController
@RequestMapping("/api/prestamos")
@CrossOrigin(origins = "*")
public class PrestamoController {
    
    private final PrestamoService prestamoService;
    
    // CONSTANTE para eliminar duplicación
    private static final String ERROR_JSON_FORMAT = "{\"error\": \"%s\"}";
    private static final String PUEDE_RENOVAR_JSON_FORMAT = "{\"puedeRenovar\": %s}";
    private static final String MULTA_PENDIENTE_JSON_FORMAT = "{\"multaPendiente\": %s}";
    
    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }
    
    // MÉTODO HELPER reutilizable para errores
    private ResponseEntity<String> buildErrorResponse(String errorMessage) {
        return ResponseEntity.badRequest()
                .body(String.format(ERROR_JSON_FORMAT, errorMessage));
    }
    
    // MÉTODO HELPER para respuestas booleanas
    private ResponseEntity<String> buildBooleanResponse(boolean value) {
        return ResponseEntity.ok()
                .body(String.format(PUEDE_RENOVAR_JSON_FORMAT, value));
    }
    
    // MÉTODO HELPER para respuestas numéricas
    private ResponseEntity<String> buildNumericResponse(Number value) {
        return ResponseEntity.ok()
                .body(String.format(MULTA_PENDIENTE_JSON_FORMAT, value));
    }
    
    @PostMapping("/usuarios/{usuarioId}/items/{itemId}")
    public ResponseEntity<?> realizarPrestamo(@PathVariable Long usuarioId, @PathVariable Long itemId) {
        try {
            PrestamoDTO prestamo = prestamoService.realizarPrestamo(usuarioId, itemId);
            return ResponseEntity.ok(prestamo);
        } catch (RuntimeException e) {
            return buildErrorResponse(e.getMessage());
        }
    }
    
    @PutMapping("/{prestamoId}/renovar")
    public ResponseEntity<?> renovarPrestamo(@PathVariable Long prestamoId) {
        try {
            PrestamoDTO prestamo = prestamoService.renovarPrestamo(prestamoId);
            return ResponseEntity.ok(prestamo);
        } catch (RuntimeException e) {
            return buildErrorResponse(e.getMessage());
        }
    }
    
    @PutMapping("/{prestamoId}/devolver")
    public ResponseEntity<?> registrarDevolucion(@PathVariable Long prestamoId) {
        try {
            PrestamoDTO prestamo = prestamoService.registrarDevolucion(prestamoId);
            return ResponseEntity.ok(prestamo);
        } catch (RuntimeException e) {
            return buildErrorResponse(e.getMessage());
        }
    }
    
    @GetMapping("/usuarios/{usuarioId}/activos")
    public ResponseEntity<List<PrestamoDTO>> getPrestamosActivosByUsuario(@PathVariable Long usuarioId) {
        List<PrestamoDTO> prestamos = prestamoService.findPrestamosActivosByUsuario(usuarioId);
        return ResponseEntity.ok(prestamos);
    }
    
    @GetMapping("/vencidos")
    public ResponseEntity<List<PrestamoDTO>> getPrestamosVencidos() {
        List<PrestamoDTO> prestamos = prestamoService.findPrestamosVencidos();
        return ResponseEntity.ok(prestamos);
    }
    
    @GetMapping("/{prestamoId}")
    public ResponseEntity<PrestamoDTO> getPrestamoById(@PathVariable Long prestamoId) {
        PrestamoDTO prestamo = prestamoService.findById(prestamoId);
        return ResponseEntity.ok(prestamo);
    }
    
    @GetMapping("/{prestamoId}/puede-renovar")
    public ResponseEntity<?> puedeRenovarPrestamo(@PathVariable Long prestamoId) {
        try {
            boolean puedeRenovar = prestamoService.puedeRenovarPrestamo(prestamoId);
            return buildBooleanResponse(puedeRenovar);
        } catch (RuntimeException e) {
            return buildErrorResponse(e.getMessage());
        }
    }
    
    @GetMapping("/{prestamoId}/multa-pendiente")
    public ResponseEntity<?> getMultaPendiente(@PathVariable Long prestamoId) {
        try {
            Double multa = prestamoService.calcularMultaPendiente(prestamoId);
            return buildNumericResponse(multa);
        } catch (RuntimeException e) {
            return buildErrorResponse(e.getMessage());
        }
    }
}