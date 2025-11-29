package com.example.biblioSmart.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    // REFACTORIZADO: Replace Primitive with Object
    private static final Logger logger = LoggerFactory.getLogger(ReservaServiceImpl.class);
    
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
        
        logger.info("ReservaServiceImpl inicializado correctamente");
    }

    @Override
    public ReservaDTO crearReserva(Long usuarioId, Long itemId) {
        logger.debug("Intentando crear reserva - Usuario: {}, Item: {}", usuarioId, itemId);
        
        // Validar usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> {
                    logger.warn("Usuario no encontrado al crear reserva: {}", usuarioId);
                    return new RuntimeException("Usuario no encontrado");
                });

        // Validar item
        ItemBiblioteca item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    logger.warn("Item no encontrado al crear reserva: {}", itemId);
                    return new RuntimeException("Item no encontrado");
                });

        // Verificar si el usuario ya tiene una reserva activa para este item
        List<Reserva> reservasExistentes = reservaRepository.findByUsuarioIdAndItemId(usuarioId, itemId);
        boolean tieneReservaActiva = reservasExistentes.stream()
                .anyMatch(reserva -> reserva.getEstado() == EstadoReserva.PENDIENTE);
        
        if (tieneReservaActiva) {
            logger.warn("Usuario {} ya tiene reserva activa para item {}", usuarioId, itemId);
            throw new RuntimeException("El usuario ya tiene una reserva activa para este item");
        }

        // Calcular posición en cola
        Long numeroEnCola = reservaRepository.countReservasPendientesByItemId(itemId) + 1;
        logger.debug("Posición en cola calculada: {} para item {}", numeroEnCola, itemId);

        // Crear reserva (48 horas de expiración)
        LocalDateTime fechaExpiracion = LocalDateTime.now().plusHours(48);

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setItem(item);
        reserva.setFechaExpiracion(fechaExpiracion);
        reserva.setPosicionCola(numeroEnCola.intValue());
        reserva.setEstado(EstadoReserva.PENDIENTE);

        Reserva reservaGuardada = reservaRepository.save(reserva);
        logger.info("Reserva creada exitosamente - ID: {}, Usuario: {}, Item: {}, Posición: {}", 
                   reservaGuardada.getId(), usuarioId, itemId, numeroEnCola);
        
        return convertToDTO(reservaGuardada);
    }

    @Override
    public void cancelarReserva(Long reservaId) {
        logger.debug("Cancelando reserva: {}", reservaId);
        
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> {
                    logger.warn("Reserva no encontrada al cancelar: {}", reservaId);
                    return new RuntimeException("Reserva no encontrada");
                });

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            logger.warn("Intento de cancelar reserva no pendiente - ID: {}, Estado: {}", 
                       reservaId, reserva.getEstado());
            throw new RuntimeException("Solo se pueden cancelar reservas pendientes");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
        logger.info("Reserva cancelada - ID: {}", reservaId);

        // Reorganizar cola para este item
        reorganizarCola(reserva.getItem().getId());
    }

    @Override
    public List<ReservaDTO> findReservasByUsuario(Long usuarioId) {
        logger.debug("Buscando reservas del usuario: {}", usuarioId);
        List<ReservaDTO> reservas = reservaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        logger.debug("Encontradas {} reservas para usuario {}", reservas.size(), usuarioId);
        return reservas;
    }

    @Override
    public List<ReservaDTO> findReservasPendientesByItem(Long itemId) {
        logger.debug("Buscando reservas pendientes para item: {}", itemId);
        List<ReservaDTO> reservas = reservaRepository.findReservasPendientesByItemId(itemId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        logger.debug("Encontradas {} reservas pendientes para item {}", reservas.size(), itemId);
        return reservas;
    }

    @Override
    public void procesarReservasExpiradas() {
        logger.info("Iniciando procesamiento de reservas expiradas");
        List<Reserva> reservasExpiradas = reservaRepository.findReservasExpiradas(LocalDateTime.now());
        
        logger.info("Encontradas {} reservas expiradas", reservasExpiradas.size());
        
        for (Reserva reserva : reservasExpiradas) {
            reserva.setEstado(EstadoReserva.EXPIRADA);
            reservaRepository.save(reserva);
            logger.debug("Reserva marcada como expirada - ID: {}", reserva.getId());
        }

        // Reorganizar colas para los items afectados
        reservasExpiradas.stream()
                .map(reserva -> reserva.getItem().getId())
                .distinct()
                .forEach(this::reorganizarCola);
                
        logger.info("Procesamiento de reservas expiradas completado");
    }

    @Override
    public void notificarProximaReserva(Long itemId) {
        logger.info("Iniciando notificación de próximas reservas para item: {}", itemId);
        List<Reserva> proximasReservas = reservaRepository.findProximaReservaEnCola(itemId);
        
        logger.debug("Encontradas {} reservas para notificar en item {}", proximasReservas.size(), itemId);
        
        for (Reserva reserva : proximasReservas) {
            if (!reserva.getNotificado()) {
                // ✅ REFACTORIZADO: System.out → Logger
                logger.info("📧 Notificando a usuario: {} - Item disponible: {}", 
                           reserva.getUsuario().getEmail(), 
                           reserva.getItem().getTitulo());
                
                // Aquí iría la lógica real de notificación (email, SMS, etc.)
                // enviarNotificacionEmail(reserva);
                
                reserva.setNotificado(true);
                reservaRepository.save(reserva);
                logger.debug("Reserva {} marcada como notificada", reserva.getId());
            }
        }
        
        logger.info("Notificación de reservas completada para item: {}", itemId);
    }

    @Override
    public boolean convertirReservaEnPrestamo(Long reservaId) {
        logger.info("Convirtiendo reserva en préstamo - Reserva ID: {}", reservaId);
        
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> {
                    logger.error("Reserva no encontrada al convertir en préstamo: {}", reservaId);
                    return new RuntimeException("Reserva no encontrada");
                });

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            logger.warn("Intento de convertir reserva no pendiente - ID: {}, Estado: {}", 
                       reservaId, reserva.getEstado());
            throw new RuntimeException("Solo se pueden convertir reservas pendientes");
        }

        if (reserva.getPosicionCola() != 1) {
            logger.warn("Intento de convertir reserva que no es primera en cola - ID: {}, Posición: {}", 
                       reservaId, reserva.getPosicionCola());
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

            logger.info("Reserva {} convertida exitosamente en préstamo", reservaId);
            return true;
        } catch (Exception e) {
            logger.error("Error al convertir reserva {} en préstamo: {}", reservaId, e.getMessage(), e);
            throw new RuntimeException("Error al convertir reserva en préstamo: " + e.getMessage());
        }
    }

    private void reorganizarCola(Long itemId) {
        logger.debug("Reorganizando cola para item: {}", itemId);
        List<Reserva> reservasPendientes = reservaRepository.findReservasPendientesByItemId(itemId);
        
        logger.debug("Reorganizando {} reservas pendientes", reservasPendientes.size());
        
        for (int i = 0; i < reservasPendientes.size(); i++) {
            Reserva reserva = reservasPendientes.get(i);
            reserva.setPosicionCola(i + 1);
            reservaRepository.save(reserva);
        }
        
        logger.debug("Cola reorganizada para item: {}", itemId);
    }

    private ReservaDTO convertToDTO(Reserva reserva) {
        logger.trace("Convirtiendo reserva a DTO - ID: {}", reserva.getId());
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