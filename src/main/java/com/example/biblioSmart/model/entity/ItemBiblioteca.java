package com.example.biblioSmart.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.biblioSmart.model.enums.EstadoItem;
import com.example.biblioSmart.model.enums.TipoItem;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "items_biblioteca")
public class ItemBiblioteca {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El título es obligatorio")
    @Column(nullable = false)
    private String titulo;
    
    private String autor;

    private String isbn;

    private String editorial;

    private Integer añoPublicacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoItem tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoItem estado = EstadoItem.DISPONIBLE;

    @Column(name = "ejemplares_totales")
    private Integer ejemplaresTotales = 1;

    @Column(name = "ejemplares_disponibles")
    private Integer ejemplaresDisponibles = 1;

    private String ubicacion;
    
    @Column(name = "tarifa_multa_diaria", precision = 8, scale = 2)
    private BigDecimal tarifaMultaDiaria = new BigDecimal("2.00");
    
    @Column(name = "fecha_adquisicion")
    private LocalDateTime fechaAdquisicion = LocalDateTime.now();
    
    private String descripcion;
    
    @OneToMany(mappedBy = "item")
    private List<Prestamo> prestamos = new ArrayList<>();
    
    @OneToMany(mappedBy = "item")
    private List<Reserva> reservas = new ArrayList<>();
    
    // Constructores
    public ItemBiblioteca() {}
    
    public ItemBiblioteca(String titulo, TipoItem tipo, Integer ejemplaresTotales) {
        this.titulo = titulo;
        this.tipo = tipo;
        this.ejemplaresTotales = ejemplaresTotales;
        this.ejemplaresDisponibles = ejemplaresTotales;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public TipoItem getTipo() { return tipo; }
    public void setTipo(TipoItem tipo) { this.tipo = tipo; }
    
    public EstadoItem getEstado() { return estado; }
    public void setEstado(EstadoItem estado) { this.estado = estado; }
    
    public Integer getEjemplaresTotales() { return ejemplaresTotales; }
    public void setEjemplaresTotales(Integer ejemplaresTotales) {
        this.ejemplaresTotales = ejemplaresTotales;
        this.ejemplaresDisponibles = ejemplaresTotales; // Reset al cambiar total
    }

    public Integer getEjemplaresDisponibles() { return ejemplaresDisponibles; }
    public void setEjemplaresDisponibles(Integer ejemplaresDisponibles) { this.ejemplaresDisponibles = ejemplaresDisponibles; }

    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }

    public Integer getAñoPublicacion() { return añoPublicacion; }
    public void setAñoPublicacion(Integer añoPublicacion) { this.añoPublicacion = añoPublicacion; }
    
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    
    public BigDecimal getTarifaMultaDiaria() { return tarifaMultaDiaria; }
    public void setTarifaMultaDiaria(BigDecimal tarifaMultaDiaria) { this.tarifaMultaDiaria = tarifaMultaDiaria; }
    
    public LocalDateTime getFechaAdquisicion() { return fechaAdquisicion; }
    public void setFechaAdquisicion(LocalDateTime fechaAdquisicion) { this.fechaAdquisicion = fechaAdquisicion; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public List<Prestamo> getPrestamos() { return prestamos; }
    public void setPrestamos(List<Prestamo> prestamos) { this.prestamos = prestamos; }
    
    public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }
}