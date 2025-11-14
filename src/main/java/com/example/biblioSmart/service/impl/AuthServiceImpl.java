package com.example.biblioSmart.service.impl;

import com.example.biblioSmart.config.JwtUtil;
import com.example.biblioSmart.model.dto.AuthResponse;
import com.example.biblioSmart.model.dto.LoginRequest;
import com.example.biblioSmart.model.dto.UsuarioDTO;
import com.example.biblioSmart.model.entity.Usuario;
import com.example.biblioSmart.repository.UsuarioRepository;
import com.example.biblioSmart.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(loginRequest.getEmail());

            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                String jwt = jwtUtil.generateToken(usuario.getEmail());

                UsuarioDTO usuarioDTO = convertToDTO(usuario);

                return new AuthResponse(jwt, usuarioDTO);
            }

            throw new RuntimeException("Usuario no encontrado después de autenticación");

        } catch (Exception e) {
            throw new RuntimeException("Error en autenticación: " + e.getMessage());
        }
    }

    @Override
    public UsuarioDTO register(Usuario usuario) {
        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Guardar usuario
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return convertToDTO(usuarioGuardado);
    }

    @Override
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    @Override
    public String getUsernameFromToken(String token) {
        return jwtUtil.extractUsername(token);
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