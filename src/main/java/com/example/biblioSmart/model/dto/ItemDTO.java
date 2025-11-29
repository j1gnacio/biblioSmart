package com.example.biblioSmart.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.biblioSmart.model.enums.EstadoItem;
import com.example.biblioSmart.model.enums.TipoItem;

public class ItemDTO {
    
    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
    private TipoItem tipo;
    private EstadoItem estado;
    private Integer copiasTotales;
    private Integer copiasDisponibles;
    private String ubicacion;
    private BigDecimal tarifaMultaDiaria;
    private LocalDateTime fechaAdquisicion;
    private String descripcion;

    // Constructores
    public ItemDTO() {} // Necesario para Spring/Jackson

    public ItemDTO(Long id, String titulo, String autor, TipoItem tipo, EstadoItem estado, 
                  Integer copiasTotales, Integer copiasDisponibles) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.tipo = tipo;
        this.estado = estado;
        this.copiasTotales = copiasTotales;
        this.copiasDisponibles = copiasDisponibles;
    }

    public ItemDTO(String titulo, String autor, TipoItem tipo, Integer copiasTotales) {
        this.titulo = titulo;
        this.autor = autor;
        this.tipo = tipo;
        this.copiasTotales = copiasTotales;
        this.copiasDisponibles = copiasTotales;
        this.estado = EstadoItem.DISPONIBLE;
        this.tarifaMultaDiaria = new BigDecimal("2.00");
        this.fechaAdquisicion = LocalDateTime.now();
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
    
    public Integer getCopiasTotales() { return copiasTotales; }
    public void setCopiasTotales(Integer copiasTotales) { this.copiasTotales = copiasTotales; }
    
    public Integer getCopiasDisponibles() { return copiasDisponibles; }
    public void setCopiasDisponibles(Integer copiasDisponibles) { this.copiasDisponibles = copiasDisponibles; }
    
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    
    public BigDecimal getTarifaMultaDiaria() { return tarifaMultaDiaria; }
    public void setTarifaMultaDiaria(BigDecimal tarifaMultaDiaria) { this.tarifaMultaDiaria = tarifaMultaDiaria; }
    
    public LocalDateTime getFechaAdquisicion() { return fechaAdquisicion; }
    public void setFechaAdquisicion(LocalDateTime fechaAdquisicion) { this.fechaAdquisicion = fechaAdquisicion; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    // Método toString para debugging
    @Override
    public String toString() {
        return "ItemDTO{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", tipo=" + tipo +
                ", estado=" + estado +
                ", copiasTotales=" + copiasTotales +
                ", copiasDisponibles=" + copiasDisponibles +
                '}';
    }
}