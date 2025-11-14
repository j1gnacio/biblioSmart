package com.example.biblioSmart.repository;

import com.example.biblioSmart.model.entity.Usuario;
import com.example.biblioSmart.model.enums.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);
    
    List<Usuario> findByTipoUsuario(TipoUsuario tipoUsuario);
    
    List<Usuario> findByActivoTrue();
    
    List<Usuario> findByMultasAcumuladasGreaterThan(Double montoMinimo);
    
    @Query("SELECT u FROM Usuario u WHERE SIZE(u.prestamos) >= u.maxPrestamosPermitidos")
    List<Usuario> findUsuariosConMaxPrestamosAlcanzado();
    
    boolean existsByEmail(String email);
    
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.tipoUsuario = :tipo")
    Long countByTipoUsuario(@Param("tipo") TipoUsuario tipo);
}