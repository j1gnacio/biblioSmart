package com.example.biblioSmart.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.biblioSmart.service.ReservaService;

@Component
public class ReservaScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(ReservaScheduler.class);
    
    private final ReservaService reservaService;
    
    public ReservaScheduler(ReservaService reservaService) {
        this.reservaService = reservaService;
    }
    
    // Ejecutar cada hora
    @Scheduled(cron = "0 0 * * * ?")
    public void procesarReservasExpiradas() {
        logger.info("Procesando reservas expiradas...");
        
        try {
            reservaService.procesarReservasExpiradas();
            logger.info("Procesamiento de reservas expiradas completado.");
        } catch (Exception e) {
            logger.error("Error procesando reservas expiradas: {}", e.getMessage(), e);
        }
    }
    
    // Ejecutar cada 30 minutos
    @Scheduled(cron = "0 */30 * * * ?")
    public void notificarProximasReservas() {
        logger.info("Verificando notificaciones de reservas...");
        
        try {
            // En una implementación real, aquí se iteraría sobre todos los items
            // con reservas pendientes y se notificaría a los primeros en cola
            logger.info("Verificación de notificaciones completada.");
        } catch (Exception e) {
            logger.error("Error en notificaciones de reservas: {}", e.getMessage(), e);
        }
    }
}