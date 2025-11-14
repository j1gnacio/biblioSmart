package com.example.biblioSmart.service;

import com.example.biblioSmart.model.dto.UsuarioDTO;
import com.example.biblioSmart.model.entity.Usuario;
import com.example.biblioSmart.model.enums.TipoUsuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    
    // CRUD básico
    List<UsuarioDTO> findAll();
    Optional<UsuarioDTO> findById(Long id);
    UsuarioDTO create(Usuario usuario);
    UsuarioDTO update(Long id, Usuario usuario);
    void delete(Long id);
    
    // Búsquedas específicas
    Optional<UsuarioDTO> findByEmail(String email);
    List<UsuarioDTO> findByTipoUsuario(TipoUsuario tipoUsuario);
    List<UsuarioDTO> findUsuariosActivos();
    
    // Validaciones de negocio
    boolean existsByEmail(String email);
    boolean puedeRealizarPrestamo(Long usuarioId);
    Integer contarPrestamosActivos(Long usuarioId);
}