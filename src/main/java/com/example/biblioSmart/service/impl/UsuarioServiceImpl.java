package com.example.biblioSmart.service.impl;

import com.example.biblioSmart.model.dto.UsuarioDTO;
import com.example.biblioSmart.model.entity.Usuario;
import com.example.biblioSmart.model.enums.TipoUsuario;
import com.example.biblioSmart.repository.UsuarioRepository;
import com.example.biblioSmart.service.UsuarioService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<UsuarioDTO> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UsuarioDTO> findById(Long id) {
        return usuarioRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    public UsuarioDTO create(Usuario usuario) {
        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado: " + usuario.getEmail());
        }
        
        // Establecer valores por defecto
        if (usuario.getTipoUsuario() == null) {
            usuario.setTipoUsuario(TipoUsuario.ESTUDIANTE);
        }
        if (usuario.getActivo() == null) {
            usuario.setActivo(true);
        }
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return convertToDTO(usuarioGuardado);
    }

    @Override
    public UsuarioDTO update(Long id, Usuario usuario) {
        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    // Actualizar campos permitidos
                    usuarioExistente.setNombre(usuario.getNombre());
                    usuarioExistente.setApellido(usuario.getApellido());
                    
                    // Solo actualizar email si no existe otro usuario con ese email
                    if (!usuarioExistente.getEmail().equals(usuario.getEmail()) && 
                        usuarioRepository.existsByEmail(usuario.getEmail())) {
                        throw new RuntimeException("El email ya está en uso: " + usuario.getEmail());
                    }
                    usuarioExistente.setEmail(usuario.getEmail());
                    
                    usuarioExistente.setTipoUsuario(usuario.getTipoUsuario());
                    usuarioExistente.setActivo(usuario.getActivo());
                    usuarioExistente.setMaxPrestamosPermitidos(usuario.getMaxPrestamosPermitidos());
                    usuarioExistente.setDiasPrestamoPermitidos(usuario.getDiasPrestamoPermitidos());
                    
                    Usuario usuarioActualizado = usuarioRepository.save(usuarioExistente);
                    return convertToDTO(usuarioActualizado);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Override
    public void delete(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        // Soft delete - marcar como inactivo en lugar de eliminar
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Override
    public Optional<UsuarioDTO> findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .map(this::convertToDTO);
    }

    @Override
    public List<UsuarioDTO> findByTipoUsuario(TipoUsuario tipoUsuario) {
        return usuarioRepository.findByTipoUsuario(tipoUsuario)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsuarioDTO> findUsuariosActivos() {
        return usuarioRepository.findByActivoTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    public boolean puedeRealizarPrestamo(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return usuario.getActivo() && 
               usuario.getMultasAcumuladas().doubleValue() == 0 &&
               contarPrestamosActivos(usuarioId) < usuario.getMaxPrestamosPermitidos();
    }

    @Override
    public Integer contarPrestamosActivos(Long usuarioId) {
        // Por ahora retornar 0 - se implementará en Fase 4
        return 0;
    }

    private UsuarioDTO convertToDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        dto.setTipoUsuario(usuario.getTipoUsuario());
        dto.setActivo(usuario.getActivo());
        dto.setMaxPrestamosPermitidos(usuario.getMaxPrestamosPermitidos());
        dto.setDiasPrestamoPermitidos(usuario.getDiasPrestamoPermitidos());
        dto.setFechaRegistro(usuario.getFechaRegistro());
        
        return dto;
    }
}