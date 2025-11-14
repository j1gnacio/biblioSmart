package com.example.biblioSmart.service.impl;

import com.example.biblioSmart.model.dto.ItemDTO;
import com.example.biblioSmart.model.entity.ItemBiblioteca;
import com.example.biblioSmart.model.enums.EstadoItem;
import com.example.biblioSmart.model.enums.TipoItem;
import com.example.biblioSmart.repository.ItemRepository;
import com.example.biblioSmart.service.ItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public List<ItemDTO> findAll() {
        return itemRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ItemDTO> findById(Long id) {
        return itemRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    public ItemDTO create(ItemBiblioteca item) {
        // Validaciones básicas
        if (item.getCopiasTotales() == null || item.getCopiasTotales() <= 0) {
            throw new RuntimeException("El número de copias debe ser mayor a 0");
        }
        
        // Establecer valores por defecto
        if (item.getEstado() == null) {
            item.setEstado(EstadoItem.DISPONIBLE);
        }
        if (item.getCopiasDisponibles() == null) {
            item.setCopiasDisponibles(item.getCopiasTotales());
        }
        
        ItemBiblioteca itemGuardado = itemRepository.save(item);
        return convertToDTO(itemGuardado);
    }

    @Override
    public ItemDTO update(Long id, ItemBiblioteca item) {
        return itemRepository.findById(id)
                .map(itemExistente -> {
                    // Actualizar campos permitidos
                    itemExistente.setTitulo(item.getTitulo());
                    itemExistente.setAutor(item.getAutor());
                    itemExistente.setIsbn(item.getIsbn());
                    itemExistente.setTipo(item.getTipo());
                    itemExistente.setDescripcion(item.getDescripcion());
                    itemExistente.setUbicacion(item.getUbicacion());
                    itemExistente.setTarifaMultaDiaria(item.getTarifaMultaDiaria());
                    
                    // Manejo especial del stock
                    if (!itemExistente.getCopiasTotales().equals(item.getCopiasTotales())) {
                        int diferencia = item.getCopiasTotales() - itemExistente.getCopiasTotales();
                        itemExistente.setCopiasTotales(item.getCopiasTotales());
                        itemExistente.setCopiasDisponibles(itemExistente.getCopiasDisponibles() + diferencia);
                    }
                    
                    ItemBiblioteca itemActualizado = itemRepository.save(itemExistente);
                    return convertToDTO(itemActualizado);
                })
                .orElseThrow(() -> new RuntimeException("Item no encontrado con ID: " + id));
    }

    @Override
    public void delete(Long id) {
        ItemBiblioteca item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item no encontrado con ID: " + id));
        
        // Soft delete - marcar como retirado
        item.setEstado(EstadoItem.RETIRADO);
        itemRepository.save(item);
    }

    @Override
    public List<ItemDTO> findByTitulo(String titulo) {
        return itemRepository.findByTituloContainingIgnoreCase(titulo)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDTO> findByAutor(String autor) {
        return itemRepository.findByAutorContainingIgnoreCase(autor)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDTO> findByTipo(TipoItem tipo) {
        return itemRepository.findByTipo(tipo)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDTO> findByEstado(EstadoItem estado) {
        return itemRepository.findByEstado(estado)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDTO> buscarPorTermino(String termino) {
        return itemRepository.buscarPorTermino(termino)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDTO> findItemsDisponibles() {
        return itemRepository.findItemsDisponibles()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean estaDisponible(Long itemId) {
        return itemRepository.findById(itemId)
                .map(item -> item.getCopiasDisponibles() > 0 && 
                             item.getEstado() == EstadoItem.DISPONIBLE)
                .orElse(false);
    }

    @Override
    public Integer obtenerStockDisponible(Long itemId) {
        return itemRepository.findById(itemId)
                .map(ItemBiblioteca::getCopiasDisponibles)
                .orElse(0);
    }

    @Override
    public void actualizarStock(Long itemId, Integer nuevasCopias) {
        ItemBiblioteca item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado con ID: " + itemId));
        
        if (nuevasCopias < 0) {
            throw new RuntimeException("El número de copias no puede ser negativo");
        }
        
        item.setCopiasTotales(nuevasCopias);
        // Ajustar disponibles si es necesario
        if (item.getCopiasDisponibles() > nuevasCopias) {
            item.setCopiasDisponibles(nuevasCopias);
        }
        
        itemRepository.save(item);
    }

    private ItemDTO convertToDTO(ItemBiblioteca item) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setTitulo(item.getTitulo());
        dto.setAutor(item.getAutor());
        dto.setIsbn(item.getIsbn());
        dto.setTipo(item.getTipo());
        dto.setEstado(item.getEstado());
        dto.setCopiasTotales(item.getCopiasTotales());
        dto.setCopiasDisponibles(item.getCopiasDisponibles());
        dto.setUbicacion(item.getUbicacion());
        dto.setTarifaMultaDiaria(item.getTarifaMultaDiaria());
        dto.setFechaAdquisicion(item.getFechaAdquisicion());
        dto.setDescripcion(item.getDescripcion());
        
        return dto;
    }
}