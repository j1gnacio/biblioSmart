package com.example.biblioSmart.service;

import java.util.List;

import com.example.biblioSmart.model.dto.ReservaDTO;

public interface ReservaService {
    
    ReservaDTO crearReserva(Long usuarioId, Long itemId);
    
    void cancelarReserva(Long reservaId);
    
    List<ReservaDTO> findReservasByUsuario(Long usuarioId);
    
    List<ReservaDTO> findReservasPendientesByItem(Long itemId);
    
    void procesarReservasExpiradas();
    
    void notificarProximaReserva(Long itemId);
    
    boolean convertirReservaEnPrestamo(Long reservaId);
}