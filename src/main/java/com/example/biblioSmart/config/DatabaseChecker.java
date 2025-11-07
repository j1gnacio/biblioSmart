package com.example.biblioSmart.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

@Component
public class DatabaseChecker implements CommandLineRunner {

    private final DataSource dataSource;

    // Inyección por constructor - más moderna y evita @Autowired
    public DatabaseChecker(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔍 INICIANDO VERIFICACIÓN DE BASE DE DATOS...");

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            System.out.println("=========================================");
            System.out.println("✅ CONEXIÓN EXITOSA A POSTGRESQL");
            System.out.println("=========================================");
            System.out.println("📊 URL: " + metaData.getURL());
            System.out.println("👤 Usuario: " + metaData.getUserName());
            System.out.println("🐘 Driver: " + metaData.getDriverName());
            System.out.println("🔢 Versión BD: " + metaData.getDatabaseProductVersion());
            System.out.println("🚀 Versión Driver: " + metaData.getDriverVersion());
            System.out.println("=========================================");

        } catch (Exception e) {
            System.err.println("=========================================");
            System.err.println("❌ ERROR DE CONEXIÓN A POSTGRESQL");
            System.err.println("=========================================");
            System.err.println("🔧 Mensaje: " + e.getMessage());
            System.err.println("💡 SOLUCIÓN: Verifica que:");
            System.err.println("   1. PostgreSQL esté ejecutándose en puerto 5432");
            System.err.println("   2. La base de datos 'bibliodb' exista");
            System.err.println("   3. Usuario: postgres, Password: postgres");
            System.err.println("   4. No haya otra aplicación usando el puerto 5432");
            System.err.println("=========================================");
        }
    }
}