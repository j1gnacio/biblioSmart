package com.example.biblioSmart.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.biblioSmart.model.enums.EstadoPrestamo;

public class PrestamoDTO {
    
    private Long id;
    private Long usuarioId;
    private Long itemId;
    private LocalDateTime fechaPrestamo;
    private LocalDateTime fechaVencimiento;
    private LocalDateTime fechaDevolucion;
    private EstadoPrestamo estado;
    private Integer renovaciones;
    private BigDecimal montoMulta;

    // Constructores, Getters y Setters
    public PrestamoDTO() {}
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    
    public LocalDateTime getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(LocalDateTime fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }
    
    public LocalDateTime getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDateTime fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    
    public LocalDateTime getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDateTime fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }
    
    public EstadoPrestamo getEstado() { return estado; }
    public void setEstado(EstadoPrestamo estado) { this.estado = estado; }
    
    public Integer getRenovaciones() { return renovaciones; }
    public void setRenovaciones(Integer renovaciones) { this.renovaciones = renovaciones; }
    
    public BigDecimal getMontoMulta() { return montoMulta; }
    public void setMontoMulta(BigDecimal montoMulta) { this.montoMulta = montoMulta; }
}