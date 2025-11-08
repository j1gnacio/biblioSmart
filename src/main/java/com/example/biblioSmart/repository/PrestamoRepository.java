package com.example.biblioSmart.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.biblioSmart.model.entity.Prestamo;
import com.example.biblioSmart.model.enums.EstadoPrestamo;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    
    List<Prestamo> findByUsuarioId(Long usuarioId);
    
    List<Prestamo> findByItemId(Long itemId);
    
    List<Prestamo> findByEstado(EstadoPrestamo estado);
    
    @Query("SELECT p FROM Prestamo p WHERE p.fechaVencimiento < :fecha AND p.estado = 'ACTIVO'")
    List<Prestamo> findPrestamosVencidos(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT p FROM Prestamo p WHERE p.usuario.id = :usuarioId AND p.estado = 'ACTIVO'")
    List<Prestamo> findPrestamosActivosByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT COUNT(p) FROM Prestamo p WHERE p.usuario.id = :usuarioId AND p.estado = 'ACTIVO'")
    Long countPrestamosActivosByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT p FROM Prestamo p WHERE p.fechaDevolucion BETWEEN :inicio AND :fin")
    List<Prestamo> findPrestamosDevueltosEnPeriodo(@Param("inicio") LocalDateTime inicio, 
                                                  @Param("fin") LocalDateTime fin);
}