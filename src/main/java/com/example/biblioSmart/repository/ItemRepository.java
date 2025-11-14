package com.example.biblioSmart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.biblioSmart.model.entity.ItemBiblioteca;
import com.example.biblioSmart.model.enums.EstadoItem;
import com.example.biblioSmart.model.enums.TipoItem;

@Repository
public interface ItemRepository extends JpaRepository<ItemBiblioteca, Long> {
    
    List<ItemBiblioteca> findByTituloContainingIgnoreCase(String titulo);
    
    List<ItemBiblioteca> findByAutorContainingIgnoreCase(String autor);
    
    List<ItemBiblioteca> findByTipo(TipoItem tipo);
    
    List<ItemBiblioteca> findByEstado(EstadoItem estado);
    
    List<ItemBiblioteca> findByCopiasDisponiblesGreaterThan(Integer cantidad);

    Optional<ItemBiblioteca> findByIsbn(String isbn);

    @Query("SELECT i FROM ItemBiblioteca i WHERE i.copiasDisponibles > 0 AND i.estado = 'DISPONIBLE'")
    List<ItemBiblioteca> findItemsDisponibles();
    
    @Query("SELECT i FROM ItemBiblioteca i WHERE i.titulo LIKE %:termino% OR i.autor LIKE %:termino% OR i.descripcion LIKE %:termino%")
    List<ItemBiblioteca> buscarPorTermino(@Param("termino") String termino);
    
    Long countByEstado(EstadoItem estado);
}