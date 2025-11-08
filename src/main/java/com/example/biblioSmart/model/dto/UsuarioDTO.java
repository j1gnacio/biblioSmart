package com.example.biblioSmart.model.dto;

import com.example.biblioSmart.model.enums.TipoUsuario;
import java.time.LocalDateTime;

public class UsuarioDTO {
    
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private TipoUsuario tipoUsuario;
    private Boolean activo;
    private Integer maxPrestamosPermitidos;
    private Integer diasPrestamoPermitidos;
    private LocalDateTime fechaRegistro;

    // Constructores
    public UsuarioDTO() {}

    public UsuarioDTO(Long id, String nombre, String apellido, String email, TipoUsuario tipoUsuario) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.tipoUsuario = tipoUsuario;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Integer getMaxPrestamosPermitidos() { return maxPrestamosPermitidos; }
    public void setMaxPrestamosPermitidos(Integer maxPrestamosPermitidos) { this.maxPrestamosPermitidos = maxPrestamosPermitidos; }

    public Integer getDiasPrestamoPermitidos() { return diasPrestamoPermitidos; }
    public void setDiasPrestamoPermitidos(Integer diasPrestamoPermitidos) { this.diasPrestamoPermitidos = diasPrestamoPermitidos; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}