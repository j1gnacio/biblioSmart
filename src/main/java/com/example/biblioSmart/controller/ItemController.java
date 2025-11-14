package com.example.biblioSmart.controller;

import com.example.biblioSmart.model.dto.ItemDTO;
import com.example.biblioSmart.model.entity.ItemBiblioteca;
import com.example.biblioSmart.model.enums.EstadoItem;
import com.example.biblioSmart.model.enums.TipoItem;
import com.example.biblioSmart.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
public class ItemController {
    
    private final ItemService itemService;
    
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }
    
    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAllItems() {
        List<ItemDTO> items = itemService.findAll();
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable Long id) {
        return itemService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> createItem(@Valid @RequestBody ItemBiblioteca item) {
        try {
            ItemDTO itemCreado = itemService.create(item);
            return ResponseEntity.ok(itemCreado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @Valid @RequestBody ItemBiblioteca item) {
        try {
            ItemDTO itemActualizado = itemService.update(id, item);
            return ResponseEntity.ok(itemActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        try {
            itemService.delete(id);
            return ResponseEntity.ok().body("{\"message\": \"Item eliminado correctamente\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Búsquedas específicas
    @GetMapping("/buscar/titulo/{titulo}")
    public ResponseEntity<List<ItemDTO>> getItemsByTitulo(@PathVariable String titulo) {
        List<ItemDTO> items = itemService.findByTitulo(titulo);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/buscar/autor/{autor}")
    public ResponseEntity<List<ItemDTO>> getItemsByAutor(@PathVariable String autor) {
        List<ItemDTO> items = itemService.findByAutor(autor);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ItemDTO>> getItemsByTipo(@PathVariable TipoItem tipo) {
        List<ItemDTO> items = itemService.findByTipo(tipo);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ItemDTO>> getItemsByEstado(@PathVariable EstadoItem estado) {
        List<ItemDTO> items = itemService.findByEstado(estado);
        return ResponseEntity.ok(items);
    }
    
    // Búsqueda avanzada
    @GetMapping("/buscar/{termino}")
    public ResponseEntity<List<ItemDTO>> buscarItems(@PathVariable String termino) {
        List<ItemDTO> items = itemService.buscarPorTermino(termino);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/disponibles")
    public ResponseEntity<List<ItemDTO>> getItemsDisponibles() {
        List<ItemDTO> items = itemService.findItemsDisponibles();
        return ResponseEntity.ok(items);
    }
    
    // Gestión de stock
    @GetMapping("/{id}/disponible")
    public ResponseEntity<?> estaDisponible(@PathVariable Long id) {
        boolean disponible = itemService.estaDisponible(id);
        return ResponseEntity.ok().body("{\"disponible\": " + disponible + "}");
    }
    
    @GetMapping("/{id}/stock")
    public ResponseEntity<?> getStockDisponible(@PathVariable Long id) {
        Integer stock = itemService.obtenerStockDisponible(id);
        return ResponseEntity.ok().body("{\"stockDisponible\": " + stock + "}");
    }
    
    @PutMapping("/{id}/stock/{nuevasCopias}")
    public ResponseEntity<?> actualizarStock(@PathVariable Long id, @PathVariable Integer nuevasCopias) {
        try {
            itemService.actualizarStock(id, nuevasCopias);
            return ResponseEntity.ok().body("{\"message\": \"Stock actualizado correctamente\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}