package com.example.biblioSmart.model.entity;

import com.example.biblioSmart.model.enums.EstadoPrestamo;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "prestamos")
public class Prestamo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemBiblioteca item;
    
    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDateTime fechaPrestamo = LocalDateTime.now();
    
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDateTime fechaVencimiento;
    
    @Column(name = "fecha_devolucion")
    private LocalDateTime fechaDevolucion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPrestamo estado = EstadoPrestamo.ACTIVO;
    
    @Column(name = "renovaciones")
    private Integer renovaciones = 0;
    
    @Column(name = "monto_multa", precision = 10, scale = 2)
    private BigDecimal montoMulta = BigDecimal.ZERO;
    
    // Constructores
    public Prestamo() {}
    
    public Prestamo(Usuario usuario, ItemBiblioteca item, LocalDateTime fechaVencimiento) {
        this.usuario = usuario;
        this.item = item;
        this.fechaVencimiento = fechaVencimiento;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public ItemBiblioteca getItem() { return item; }
    public void setItem(ItemBiblioteca item) { this.item = item; }
    
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