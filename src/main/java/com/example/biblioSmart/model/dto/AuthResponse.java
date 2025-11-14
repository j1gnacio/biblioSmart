package com.example.biblioSmart.model.dto;

public class AuthResponse {
    
    private String token;
    private String type = "Bearer";
    private UsuarioDTO usuario;

    // Constructores
    public AuthResponse() {}

    public AuthResponse(String token, UsuarioDTO usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    // Getters y Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public UsuarioDTO getUsuario() { return usuario; }
    public void setUsuario(UsuarioDTO usuario) { this.usuario = usuario; }
}