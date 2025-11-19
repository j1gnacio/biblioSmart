package com.example.biblioSmart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.biblioSmart.model.entity.Multa;

@Repository
public interface MultaRepository extends JpaRepository<Multa, Long> {
    
    List<Multa> findByUsuarioId(Long usuarioId);
    
    List<Multa> findByActivaTrue();
    
    @Query("SELECT SUM(m.monto) FROM Multa m WHERE m.usuario.id = :usuarioId AND m.activa = true")
    Double sumMultasActivasByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT m FROM Multa m WHERE m.usuario.id = :usuarioId AND m.activa = true")
    List<Multa> findMultasActivasByUsuarioId(@Param("usuarioId") Long usuarioId);
}