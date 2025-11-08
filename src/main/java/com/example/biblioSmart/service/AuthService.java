package com.example.biblioSmart.service;

import com.example.biblioSmart.model.dto.AuthResponse;
import com.example.biblioSmart.model.dto.LoginRequest;
import com.example.biblioSmart.model.dto.UsuarioDTO;
import com.example.biblioSmart.model.entity.Usuario;

public interface AuthService {

    AuthResponse login(LoginRequest loginRequest);

    UsuarioDTO register(Usuario usuario);

    boolean validateToken(String token);

    String getUsernameFromToken(String token);
}