package com.example.biblioSmart.model.entity;

import java.time.LocalDateTime;

import com.example.biblioSmart.model.enums.EstadoReserva;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @NotNull(message = "El item es obligatorio")
    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemBiblioteca item;

    @NotNull(message = "La fecha de reserva es obligatoria")
    private LocalDateTime fechaReserva;

    @NotNull(message = "La fecha de expiración es obligatoria")
    private LocalDateTime fechaExpiracion;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    // Constructores
    public Reserva() {}

    public Reserva(Usuario usuario, ItemBiblioteca item, LocalDateTime fechaReserva,
                   LocalDateTime fechaExpiracion, EstadoReserva estado) {
        this.usuario = usuario;
        this.item = item;
        this.fechaReserva = fechaReserva;
        this.fechaExpiracion = fechaExpiracion;
        this.estado = estado;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public ItemBiblioteca getItem() {
        return item;
    }

    public void setItem(ItemBiblioteca item) {
        this.item = item;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }
}