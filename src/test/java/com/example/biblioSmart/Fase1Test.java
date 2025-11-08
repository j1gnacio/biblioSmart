package com.example.biblioSmart;

import com.example.biblioSmart.model.entity.Usuario;
import com.example.biblioSmart.model.enums.TipoUsuario;
import com.example.biblioSmart.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class Fase1Test {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void testConexionBaseDatos() {
        // Verificar que el contexto se carga correctamente
        assertNotNull(usuarioRepository);
    }

    @Test
    void testCrearUsuario() {
        // Crear un usuario de prueba
        Usuario usuario = new Usuario();
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("juan.perez@test.com");
        usuario.setPassword("password123");
        usuario.setTipoUsuario(TipoUsuario.ESTUDIANTE);

        // Guardar en base de datos
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Verificar que se guardó correctamente
        assertNotNull(usuarioGuardado.getId());
        assertEquals("Juan", usuarioGuardado.getNombre());
        
        System.out.println("✅ USUARIO CREADO EXITOSAMENTE - ID: " + usuarioGuardado.getId());
    }
}