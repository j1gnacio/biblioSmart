package com.example.biblioSmart.controller;

import com.example.biblioSmart.model.dto.UsuarioDTO;
import com.example.biblioSmart.model.entity.Usuario;
import com.example.biblioSmart.model.enums.TipoUsuario;
import com.example.biblioSmart.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getAllUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.findAll();
        return ResponseEntity.ok(usuarios);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getUsuarioById(@PathVariable Long id) {
        return usuarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> createUsuario(@Valid @RequestBody Usuario usuario) {
        try {
            UsuarioDTO usuarioCreado = usuarioService.create(usuario);
            return ResponseEntity.ok(usuarioCreado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        try {
            UsuarioDTO usuarioActualizado = usuarioService.update(id, usuario);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id) {
        try {
            usuarioService.delete(id);
            return ResponseEntity.ok().body("{\"message\": \"Usuario eliminado correctamente\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDTO> getUsuarioByEmail(@PathVariable String email) {
        return usuarioService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/tipo/{tipoUsuario}")
    public ResponseEntity<List<UsuarioDTO>> getUsuariosByTipo(@PathVariable TipoUsuario tipoUsuario) {
        List<UsuarioDTO> usuarios = usuarioService.findByTipoUsuario(tipoUsuario);
        return ResponseEntity.ok(usuarios);
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioDTO>> getUsuariosActivos() {
        List<UsuarioDTO> usuarios = usuarioService.findUsuariosActivos();
        return ResponseEntity.ok(usuarios);
    }
    
    @GetMapping("/{id}/puede-prestar")
    public ResponseEntity<?> puedeRealizarPrestamo(@PathVariable Long id) {
        try {
            boolean puedePrestar = usuarioService.puedeRealizarPrestamo(id);
            return ResponseEntity.ok().body("{\"puedePrestar\": " + puedePrestar + "}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}