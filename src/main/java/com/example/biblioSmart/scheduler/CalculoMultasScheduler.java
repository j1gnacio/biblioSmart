package com.example.biblioSmart.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.biblioSmart.service.PrestamoService;

@Component
public class CalculoMultasScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(CalculoMultasScheduler.class);
    
    private final PrestamoService prestamoService;
    
    public CalculoMultasScheduler(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }
    
    // Ejecutar todos los días a las 6:00 AM
    @Scheduled(cron = "0 0 6 * * ?")
    public void calcularMultasAutomaticas() {
        logger.info("Iniciando cálculo automático de multas...");
        
        try {
            // Obtener préstamos vencidos
            var prestamosVencidos = prestamoService.findPrestamosVencidos();
            
            int multasCalculadas = 0;
            for (var prestamo : prestamosVencidos) {
                double multa = prestamoService.calcularMultaPendiente(prestamo.getId());
                if (multa > 0) {
                    logger.info("Multa calculada para préstamo {}: ${}", prestamo.getId(), multa);
                    multasCalculadas++;
                }
            }
            
            logger.info("Cálculo de multas completado. {} multas calculadas.", multasCalculadas);
            
        } catch (Exception e) {
            logger.error("Error en el cálculo automático de multas: {}", e.getMessage(), e);
        }
    }
}