package com.example.biblioSmart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.biblioSmart.model.dto.ReservaDTO;
import com.example.biblioSmart.service.ReservaService;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {
    
    private final ReservaService reservaService;
    
    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }
    
    @PostMapping("/usuarios/{usuarioId}/items/{itemId}")
    public ResponseEntity<?> crearReserva(@PathVariable Long usuarioId, @PathVariable Long itemId) {
        try {
            ReservaDTO reserva = reservaService.crearReserva(usuarioId, itemId);
            return ResponseEntity.ok(reserva);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    @DeleteMapping("/{reservaId}")
    public ResponseEntity<?> cancelarReserva(@PathVariable Long reservaId) {
        try {
            reservaService.cancelarReserva(reservaId);
            return ResponseEntity.ok().body("{\"message\": \"Reserva cancelada correctamente\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    @GetMapping("/usuarios/{usuarioId}")
    public ResponseEntity<List<ReservaDTO>> getReservasByUsuario(@PathVariable Long usuarioId) {
        List<ReservaDTO> reservas = reservaService.findReservasByUsuario(usuarioId);
        return ResponseEntity.ok(reservas);
    }
    
    @GetMapping("/items/{itemId}/pendientes")
    public ResponseEntity<List<ReservaDTO>> getReservasPendientesByItem(@PathVariable Long itemId) {
        List<ReservaDTO> reservas = reservaService.findReservasPendientesByItem(itemId);
        return ResponseEntity.ok(reservas);
    }
    
    @PostMapping("/{reservaId}/convertir-prestamo")
    public ResponseEntity<?> convertirReservaEnPrestamo(@PathVariable Long reservaId) {
        try {
            boolean exito = reservaService.convertirReservaEnPrestamo(reservaId);
            return ResponseEntity.ok().body("{\"convertido\": " + exito + "}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    @PostMapping("/procesar-expiradas")
    public ResponseEntity<?> procesarReservasExpiradas() {
        try {
            reservaService.procesarReservasExpiradas();
            return ResponseEntity.ok().body("{\"message\": \"Reservas expiradas procesadas correctamente\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}