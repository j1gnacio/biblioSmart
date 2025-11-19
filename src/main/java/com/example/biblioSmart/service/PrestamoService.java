package com.example.biblioSmart.service;

import java.util.List;

import com.example.biblioSmart.model.dto.PrestamoDTO;

public interface PrestamoService {
    
    PrestamoDTO realizarPrestamo(Long usuarioId, Long itemId);
    
    PrestamoDTO renovarPrestamo(Long prestamoId);
    
    PrestamoDTO registrarDevolucion(Long prestamoId);
    
    List<PrestamoDTO> findPrestamosActivosByUsuario(Long usuarioId);
    
    List<PrestamoDTO> findPrestamosVencidos();
    
    PrestamoDTO findById(Long id);
    
    boolean puedeRenovarPrestamo(Long prestamoId);
    
    Double calcularMultaPendiente(Long prestamoId);
}