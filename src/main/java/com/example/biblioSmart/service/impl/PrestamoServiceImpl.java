package com.example.biblioSmart.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.biblioSmart.model.dto.PrestamoDTO;
import com.example.biblioSmart.model.entity.ItemBiblioteca;
import com.example.biblioSmart.model.entity.Multa;
import com.example.biblioSmart.model.entity.Prestamo;
import com.example.biblioSmart.model.entity.Reserva;
import com.example.biblioSmart.model.entity.Usuario;
import com.example.biblioSmart.model.enums.EstadoItem;
import com.example.biblioSmart.model.enums.EstadoPrestamo;
import com.example.biblioSmart.model.enums.EstadoReserva;
import com.example.biblioSmart.repository.ItemRepository;
import com.example.biblioSmart.repository.MultaRepository;
import com.example.biblioSmart.repository.PrestamoRepository;
import com.example.biblioSmart.repository.ReservaRepository;
import com.example.biblioSmart.repository.UsuarioRepository;
import com.example.biblioSmart.service.PrestamoService;
import com.example.biblioSmart.service.UsuarioService;

@Service
@Transactional
public class PrestamoServiceImpl implements PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ItemRepository itemRepository;
    private final ReservaRepository reservaRepository;
    private final MultaRepository multaRepository;
    private final UsuarioService usuarioService;

    public PrestamoServiceImpl(PrestamoRepository prestamoRepository,
                              UsuarioRepository usuarioRepository,
                              ItemRepository itemRepository,
                              ReservaRepository reservaRepository,
                              MultaRepository multaRepository,
                              UsuarioService usuarioService) {
        this.prestamoRepository = prestamoRepository;
        this.usuarioRepository = usuarioRepository;
        this.itemRepository = itemRepository;
        this.reservaRepository = reservaRepository;
        this.multaRepository = multaRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    public PrestamoDTO realizarPrestamo(Long usuarioId, Long itemId) {
        // Validar usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!usuarioService.puedeRealizarPrestamo(usuarioId)) {
            throw new RuntimeException("Usuario no puede realizar préstamos en este momento");
        }

        // Validar item
        ItemBiblioteca item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));
        
        if (item.getCopiasDisponibles() <= 0) {
            throw new RuntimeException("No hay copias disponibles de este item");
        }

        if (item.getEstado() != EstadoItem.DISPONIBLE) {
            throw new RuntimeException("El item no está disponible para préstamo");
        }

        // Verificar si hay reservas pendientes para este item
        List<Reserva> reservasPendientes = reservaRepository.findReservasPendientesByItemId(itemId);
        if (!reservasPendientes.isEmpty()) {
            Reserva primeraReserva = reservasPendientes.get(0);
            if (!primeraReserva.getUsuario().getId().equals(usuarioId)) {
                throw new RuntimeException("El item tiene reservas pendientes de otros usuarios");
            }
        }

        // Crear préstamo
        LocalDateTime fechaVencimiento = LocalDateTime.now().plusDays(usuario.getDiasPrestamoPermitidos());
        
        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setItem(item);
        prestamo.setFechaVencimiento(fechaVencimiento);
        prestamo.setEstado(EstadoPrestamo.ACTIVO);

        Prestamo prestamoGuardado = prestamoRepository.save(prestamo);

        // Actualizar stock del item
        item.setCopiasDisponibles(item.getCopiasDisponibles() - 1);
        if (item.getCopiasDisponibles() == 0) {
            item.setEstado(EstadoItem.PRESTADO);
        }
        itemRepository.save(item);

        // Si había una reserva de este usuario, marcarla como completada
        reservasPendientes.stream()
                .filter(reserva -> reserva.getUsuario().getId().equals(usuarioId))
                .findFirst()
                .ifPresent(reserva -> {
                    reserva.setEstado(EstadoReserva.COMPLETADA);
                    reservaRepository.save(reserva);
                });

        return convertToDTO(prestamoGuardado);
    }

    @Override
    public PrestamoDTO renovarPrestamo(Long prestamoId) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        if (!puedeRenovarPrestamo(prestamoId)) {
            throw new RuntimeException("No se puede renovar este préstamo");
        }

        // Calcular nueva fecha de vencimiento
        Usuario usuario = prestamo.getUsuario();
        LocalDateTime nuevaFechaVencimiento = prestamo.getFechaVencimiento()
                .plusDays(usuario.getDiasPrestamoPermitidos());

        prestamo.setFechaVencimiento(nuevaFechaVencimiento);
        prestamo.setRenovaciones(prestamo.getRenovaciones() + 1);

        Prestamo prestamoActualizado = prestamoRepository.save(prestamo);
        return convertToDTO(prestamoActualizado);
    }

    @Override
    public PrestamoDTO registrarDevolucion(Long prestamoId) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        if (prestamo.getEstado() != EstadoPrestamo.ACTIVO) {
            throw new RuntimeException("El préstamo no está activo");
        }

        // Marcar como devuelto
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        prestamo.setFechaDevolucion(LocalDateTime.now());

        // Calcular multa si hay retraso
        if (prestamo.getFechaDevolucion().isAfter(prestamo.getFechaVencimiento())) {
            double multa = calcularMulta(prestamo);
            prestamo.setMontoMulta(prestamo.getMontoMulta().add(java.math.BigDecimal.valueOf(multa)));
            
            // Crear registro de multa
            Multa multaEntity = new Multa();
            multaEntity.setUsuario(prestamo.getUsuario());
            multaEntity.setPrestamo(prestamo);
            multaEntity.setMonto(java.math.BigDecimal.valueOf(multa));
            multaEntity.setDescripcion("Multa por retraso en devolución");
            multaRepository.save(multaEntity);
        }

        Prestamo prestamoActualizado = prestamoRepository.save(prestamo);

        // Actualizar stock del item
        ItemBiblioteca item = prestamo.getItem();
        item.setCopiasDisponibles(item.getCopiasDisponibles() + 1);
        item.setEstado(EstadoItem.DISPONIBLE);
        itemRepository.save(item);

        return convertToDTO(prestamoActualizado);
    }

    @Override
    public List<PrestamoDTO> findPrestamosActivosByUsuario(Long usuarioId) {
        return prestamoRepository.findPrestamosActivosByUsuarioId(usuarioId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrestamoDTO> findPrestamosVencidos() {
        return prestamoRepository.findPrestamosVencidos(LocalDateTime.now())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PrestamoDTO findById(Long id) {
        return prestamoRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
    }

    @Override
    public boolean puedeRenovarPrestamo(Long prestamoId) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        // Verificar que no esté vencido
        if (prestamo.getFechaVencimiento().isBefore(LocalDateTime.now())) {
            return false;
        }

        // Verificar que no tenga multas pendientes
        Double multasPendientes = multaRepository.sumMultasActivasByUsuarioId(prestamo.getUsuario().getId());
        if (multasPendientes != null && multasPendientes > 0) {
            return false;
        }

        // Verificar límite de renovaciones (máximo 2 renovaciones)
        return prestamo.getRenovaciones() < 2;
    }

    @Override
    public Double calcularMultaPendiente(Long prestamoId) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        if (prestamo.getEstado() == EstadoPrestamo.ACTIVO && 
            prestamo.getFechaVencimiento().isBefore(LocalDateTime.now())) {
            return calcularMulta(prestamo);
        }

        return 0.0;
    }

    private double calcularMulta(Prestamo prestamo) {
        long diasRetraso = java.time.Duration.between(
            prestamo.getFechaVencimiento(), 
            LocalDateTime.now()
        ).toDays();

        if (diasRetraso <= 0) return 0.0;

        double tarifaDiaria = prestamo.getItem().getTarifaMultaDiaria().doubleValue();
        return diasRetraso * tarifaDiaria;
    }

    private PrestamoDTO convertToDTO(Prestamo prestamo) {
        PrestamoDTO dto = new PrestamoDTO();
        dto.setId(prestamo.getId());
        dto.setUsuarioId(prestamo.getUsuario().getId());
        dto.setItemId(prestamo.getItem().getId());
        dto.setFechaPrestamo(prestamo.getFechaPrestamo());
        dto.setFechaVencimiento(prestamo.getFechaVencimiento());
        dto.setFechaDevolucion(prestamo.getFechaDevolucion());
        dto.setEstado(prestamo.getEstado());
        dto.setRenovaciones(prestamo.getRenovaciones());
        dto.setMontoMulta(prestamo.getMontoMulta());
        
        return dto;
    }
}