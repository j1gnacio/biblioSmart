package com.example.biblioSmart.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.biblioSmart.model.entity.Reserva;
import com.example.biblioSmart.model.enums.EstadoReserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    List<Reserva> findByUsuarioId(Long usuarioId);
    
    List<Reserva> findByItemId(Long itemId);
    
    List<Reserva> findByEstado(EstadoReserva estado);
    
    @Query("SELECT r FROM Reserva r WHERE r.item.id = :itemId AND r.estado = 'PENDIENTE' ORDER BY r.fechaReserva ASC")
    List<Reserva> findReservasPendientesByItemId(@Param("itemId") Long itemId);
    
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.item.id = :itemId AND r.estado = 'PENDIENTE'")
    Long countReservasPendientesByItemId(@Param("itemId") Long itemId);
    
    @Query("SELECT r FROM Reserva r WHERE r.fechaExpiracion < :fecha AND r.estado = 'PENDIENTE'")
    List<Reserva> findReservasExpiradas(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT r FROM Reserva r WHERE r.item.id = :itemId AND r.estado = 'PENDIENTE' AND r.posicionCola = 1")
    List<Reserva> findProximaReservaEnCola(@Param("itemId") Long itemId);
    
    // Método para validaciones - BUSCAR POR USUARIO E ITEM
    List<Reserva> findByUsuarioIdAndItemId(@Param("usuarioId") Long usuarioId, @Param("itemId") Long itemId);
}