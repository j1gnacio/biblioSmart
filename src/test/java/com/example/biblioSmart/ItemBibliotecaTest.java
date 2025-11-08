package com.example.biblioSmart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.example.biblioSmart.model.entity.ItemBiblioteca;
import com.example.biblioSmart.model.enums.EstadoItem;
import com.example.biblioSmart.model.enums.TipoItem;
import com.example.biblioSmart.repository.ItemRepository;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class ItemBibliotecaTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void testConexionBaseDatos() {
        // Verificar que el contexto se carga correctamente
        assertNotNull(itemRepository);
    }

    @Test
    void testCrearItemBiblioteca() {
        // Crear un item de biblioteca de prueba (tipo LIBRO)
        ItemBiblioteca item = new ItemBiblioteca();
        item.setTitulo("El Quijote");
        item.setAutor("Miguel de Cervantes");
        item.setIsbn("978-84-376-0494-7");
        item.setEditorial("Editorial Planeta");
        item.setAñoPublicacion(1605);
        item.setTipo(TipoItem.LIBRO);
        item.setEstado(EstadoItem.DISPONIBLE);
        item.setEjemplaresTotales(5);
        item.setUbicacion("Estantería A1");
        item.setDescripcion("Clásico de la literatura española");

        // Guardar en base de datos
        ItemBiblioteca itemGuardado = itemRepository.save(item);

        // Verificar que se guardó correctamente
        assertNotNull(itemGuardado.getId());
        assertEquals("El Quijote", itemGuardado.getTitulo());
        assertEquals(TipoItem.LIBRO, itemGuardado.getTipo());
        assertEquals(EstadoItem.DISPONIBLE, itemGuardado.getEstado());
        assertEquals(5, itemGuardado.getEjemplaresTotales());
        assertEquals(5, itemGuardado.getEjemplaresDisponibles()); // Debe coincidir inicialmente

        System.out.println("✅ ITEM BIBLIOTECA CREADO EXITOSAMENTE - ID: " + itemGuardado.getId());
    }

    @Test
    void testBuscarItemPorTitulo() {
        // Crear y guardar un item
        ItemBiblioteca item = new ItemBiblioteca("Cien años de soledad", TipoItem.LIBRO, 3);
        item.setAutor("Gabriel García Márquez");
        itemRepository.save(item);

        // Buscar por título usando findByTituloContainingIgnoreCase
        java.util.List<ItemBiblioteca> encontrados = itemRepository.findByTituloContainingIgnoreCase("Cien años");
        assertFalse(encontrados.isEmpty());
        ItemBiblioteca encontrado = encontrados.get(0);
        assertEquals("Gabriel García Márquez", encontrado.getAutor());
        assertEquals(TipoItem.LIBRO, encontrado.getTipo());
    }

    @Test
    void testCrearItemBibliotecaDiferentesTipos() {
        // Probar creación de items con diferentes tipos
        ItemBiblioteca dvd = new ItemBiblioteca("Inception", TipoItem.DVD, 2);
        dvd.setAñoPublicacion(2010);
        ItemBiblioteca revista = new ItemBiblioteca("National Geographic", TipoItem.REVISTA, 10);
        revista.setAñoPublicacion(2023);

        ItemBiblioteca dvdGuardado = itemRepository.save(dvd);
        ItemBiblioteca revistaGuardada = itemRepository.save(revista);

        assertNotNull(dvdGuardado.getId());
        assertEquals(TipoItem.DVD, dvdGuardado.getTipo());
        assertEquals(2, dvdGuardado.getEjemplaresTotales());

        assertNotNull(revistaGuardada.getId());
        assertEquals(TipoItem.REVISTA, revistaGuardada.getTipo());
        assertEquals(10, revistaGuardada.getEjemplaresTotales());
    }

    @Test
    void testValidacionCamposObligatorios() {
        // Probar que titulo y tipo son obligatorios
        ItemBiblioteca itemSinTitulo = new ItemBiblioteca();
        itemSinTitulo.setTipo(TipoItem.LIBRO);

        // Intentar guardar sin titulo debería fallar en validación
        // Usar @Valid en servicios para activar validaciones
        // Para esta prueba, esperamos ConstraintViolationException
        jakarta.validation.ConstraintViolationException exception = assertThrows(
            jakarta.validation.ConstraintViolationException.class,
            () -> itemRepository.save(itemSinTitulo)
        );
        assertTrue(exception.getMessage().contains("El título es obligatorio"));
    }

    @Test
    void testActualizarEjemplaresDisponibles() {
        // Crear item con 5 ejemplares
        ItemBiblioteca item = new ItemBiblioteca("Test Item", TipoItem.LIBRO, 5);
        ItemBiblioteca guardado = itemRepository.save(item);

        // Simular préstamo: reducir disponibles
        guardado.setEjemplaresDisponibles(3);
        ItemBiblioteca actualizado = itemRepository.save(guardado);

        assertEquals(5, actualizado.getEjemplaresTotales());
        assertEquals(3, actualizado.getEjemplaresDisponibles());
    }

    @Test
    void testDatosInvalidos() {
        // Probar con titulo vacío
        ItemBiblioteca item = new ItemBiblioteca("", TipoItem.LIBRO, 1);
        // Esperar ConstraintViolationException para titulo vacío
        jakarta.validation.ConstraintViolationException exception = assertThrows(
            jakarta.validation.ConstraintViolationException.class,
            () -> itemRepository.save(item)
        );
        assertTrue(exception.getMessage().contains("El título es obligatorio"));
    }

    @Test
    void testBuscarPorEjemplaresDisponibles() {
        // Crear items con diferentes ejemplares disponibles
        ItemBiblioteca item1 = new ItemBiblioteca("Item 1", TipoItem.LIBRO, 5);
        item1.setEjemplaresDisponibles(3);
        ItemBiblioteca item2 = new ItemBiblioteca("Item 2", TipoItem.DVD, 2);
        item2.setEjemplaresDisponibles(0);
        itemRepository.save(item1);
        itemRepository.save(item2);

        // Buscar items con más de 1 ejemplar disponible
        java.util.List<ItemBiblioteca> encontrados = itemRepository.findByEjemplaresDisponiblesGreaterThan(1);
        assertFalse(encontrados.isEmpty());
        assertTrue(encontrados.stream().anyMatch(i -> i.getTitulo().equals("Item 1")));
        assertFalse(encontrados.stream().anyMatch(i -> i.getTitulo().equals("Item 2")));
    }
}
