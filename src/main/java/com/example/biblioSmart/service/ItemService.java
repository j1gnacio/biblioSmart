package com.example.biblioSmart.service;

import com.example.biblioSmart.model.dto.ItemDTO;
import com.example.biblioSmart.model.entity.ItemBiblioteca;
import com.example.biblioSmart.model.enums.EstadoItem;
import com.example.biblioSmart.model.enums.TipoItem;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    
    // CRUD básico
    List<ItemDTO> findAll();
    Optional<ItemDTO> findById(Long id);
    ItemDTO create(ItemBiblioteca item);
    ItemDTO update(Long id, ItemBiblioteca item);
    void delete(Long id);
    
    // Búsquedas específicas
    List<ItemDTO> findByTitulo(String titulo);
    List<ItemDTO> findByAutor(String autor);
    List<ItemDTO> findByTipo(TipoItem tipo);
    List<ItemDTO> findByEstado(EstadoItem estado);
    
    // Búsqueda avanzada
    List<ItemDTO> buscarPorTermino(String termino);
    List<ItemDTO> findItemsDisponibles();
    
    // Gestión de stock
    boolean estaDisponible(Long itemId);
    Integer obtenerStockDisponible(Long itemId);
    void actualizarStock(Long itemId, Integer nuevasCopias);
}