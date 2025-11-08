package com.example.biblioSmart.controller;

import com.example.biblioSmart.model.dto.AuthResponse;
import com.example.biblioSmart.model.dto.LoginRequest;
import com.example.biblioSmart.model.dto.UsuarioDTO;
import com.example.biblioSmart.model.entity.Usuario;
import com.example.biblioSmart.model.enums.TipoUsuario;
import com.example.biblioSmart.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.login(loginRequest);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error en login: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody Usuario usuario) {
        try {
            // Por defecto, nuevos usuarios son ESTUDIANTE
            if (usuario.getTipoUsuario() == null) {
                usuario.setTipoUsuario(TipoUsuario.ESTUDIANTE);
            }

            UsuarioDTO usuarioDTO = authService.register(usuario);
            return ResponseEntity.ok(usuarioDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error en registro: " + e.getMessage());
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        try {
            boolean isValid = authService.validateToken(token);
            return ResponseEntity.ok().body("{\"valid\": " + isValid + "}");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error validando token: " + e.getMessage());
        }
    }
}