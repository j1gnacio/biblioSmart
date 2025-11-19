package com.example.biblioSmart.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.biblioSmart.model.dto.ReservaDTO;
import com.example.biblioSmart.model.entity.ItemBiblioteca;
import com.example.biblioSmart.model.entity.Reserva;
import com.example.biblioSmart.model.entity.Usuario;
import com.example.biblioSmart.model.enums.EstadoReserva;
import com.example.biblioSmart.repository.ItemRepository;
import com.example.biblioSmart.repository.ReservaRepository;
import com.example.biblioSmart.repository.UsuarioRepository;
import com.example.biblioSmart.service.PrestamoService;
import com.example.biblioSmart.service.ReservaService;

@Service
@Transactional
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ItemRepository itemRepository;
    private final PrestamoService prestamoService;

    public ReservaServiceImpl(ReservaRepository reservaRepository,
                            UsuarioRepository usuarioRepository,
                            ItemRepository itemRepository,
                            PrestamoService prestamoService) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.itemRepository = itemRepository;
        this.prestamoService = prestamoService;
    }

    @Override
    public ReservaDTO crearReserva(Long usuarioId, Long itemId) {
        // Validar usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar item
        ItemBiblioteca item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        // Verificar si el usuario ya tiene una reserva activa para este item
        List<Reserva> reservasExistentes = reservaRepository.findByUsuarioIdAndItemId(usuarioId, itemId);
        boolean tieneReservaActiva = reservasExistentes.stream()
                .anyMatch(reserva -> reserva.getEstado() == EstadoReserva.PENDIENTE);
        
        if (tieneReservaActiva) {
            throw new RuntimeException("El usuario ya tiene una reserva activa para este item");
        }

        // Calcular posición en cola
        Long numeroEnCola = reservaRepository.countReservasPendientesByItemId(itemId) + 1;

        // Crear reserva (48 horas de expiración)
        LocalDateTime fechaExpiracion = LocalDateTime.now().plusHours(48);

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setItem(item);
        reserva.setFechaExpiracion(fechaExpiracion);
        reserva.setPosicionCola(numeroEnCola.intValue());
        reserva.setEstado(EstadoReserva.PENDIENTE);

        Reserva reservaGuardada = reservaRepository.save(reserva);
        return convertToDTO(reservaGuardada);
    }

    @Override
    public void cancelarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new RuntimeException("Solo se pueden cancelar reservas pendientes");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);

        // Reorganizar cola para este item
        reorganizarCola(reserva.getItem().getId());
    }

    @Override
    public List<ReservaDTO> findReservasByUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservaDTO> findReservasPendientesByItem(Long itemId) {
        return reservaRepository.findReservasPendientesByItemId(itemId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void procesarReservasExpiradas() {
        List<Reserva> reservasExpiradas = reservaRepository.findReservasExpiradas(LocalDateTime.now());
        
        for (Reserva reserva : reservasExpiradas) {
            reserva.setEstado(EstadoReserva.EXPIRADA);
            reservaRepository.save(reserva);
        }

        // Reorganizar colas para los items afectados
        reservasExpiradas.stream()
                .map(reserva -> reserva.getItem().getId())
                .distinct()
                .forEach(this::reorganizarCola);
    }

    @Override
    public void notificarProximaReserva(Long itemId) {
        List<Reserva> proximasReservas = reservaRepository.findProximaReservaEnCola(itemId);
        
        for (Reserva reserva : proximasReservas) {
            if (!reserva.getNotificado()) {
                // Aquí iría la lógica de notificación (email, SMS, etc.)
                System.out.println("Notificando a usuario: " + reserva.getUsuario().getEmail() + 
                                 " - Item disponible: " + reserva.getItem().getTitulo());
                
                reserva.setNotificado(true);
                reservaRepository.save(reserva);
            }
        }
    }

    @Override
    public boolean convertirReservaEnPrestamo(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new RuntimeException("Solo se pueden convertir reservas pendientes");
        }

        if (reserva.getPosicionCola() != 1) {
            throw new RuntimeException("Solo la primera reserva en cola puede convertirse en préstamo");
        }

        try {
            // Intentar crear el préstamo
            prestamoService.realizarPrestamo(reserva.getUsuario().getId(), reserva.getItem().getId());
            
            // Marcar reserva como completada
            reserva.setEstado(EstadoReserva.COMPLETADA);
            reservaRepository.save(reserva);

            // Reorganizar cola
            reorganizarCola(reserva.getItem().getId());

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir reserva en préstamo: " + e.getMessage());
        }
    }

    private void reorganizarCola(Long itemId) {
        List<Reserva> reservasPendientes = reservaRepository.findReservasPendientesByItemId(itemId);
        
        for (int i = 0; i < reservasPendientes.size(); i++) {
            Reserva reserva = reservasPendientes.get(i);
            reserva.setPosicionCola(i + 1);
            reservaRepository.save(reserva);
        }
    }

    private ReservaDTO convertToDTO(Reserva reserva) {
        ReservaDTO dto = new ReservaDTO();
        dto.setId(reserva.getId());
        dto.setUsuarioId(reserva.getUsuario().getId());
        dto.setItemId(reserva.getItem().getId());
        dto.setFechaReserva(reserva.getFechaReserva());
        dto.setFechaExpiracion(reserva.getFechaExpiracion());
        dto.setEstado(reserva.getEstado());
        dto.setPosicionCola(reserva.getPosicionCola());
        dto.setNotificado(reserva.getNotificado());
        
        return dto;
    }
}