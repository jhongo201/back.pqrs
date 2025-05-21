package com.claude.springboot.app.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claude.springboot.app.repositories.PqrsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RadicadoGeneratorService {
    
    private final PqrsRepository pqrsRepository;

    @Transactional
    public String generarNumeroRadicado() {
        LocalDateTime now = LocalDateTime.now();
        String año = String.valueOf(now.getYear());
        String mes = String.format("%02d", now.getMonthValue());
        
        // Obtener el prefijo para el mes actual
        String prefijo = String.format("PQRS-%s-%s", año, mes);
        
        // Obtener el último secuencial usando un query específico
      
        Integer ultimoSecuencial = pqrsRepository.findUltimoSecuencial(prefijo);
        
        // Si no hay números previos, empezar desde 1
        int siguienteSecuencial = (ultimoSecuencial != null ? ultimoSecuencial : 0) + 1;
        
        // Formatear el número completo
        String numeroRadicado = String.format("%s-%04d", prefijo, siguienteSecuencial);
        
        // Verificar que no exista
        while(pqrsRepository.existsByNumeroRadicado(numeroRadicado)) {
            siguienteSecuencial++;
            numeroRadicado = String.format("%s-%04d", prefijo, siguienteSecuencial);
        }
        
        return numeroRadicado;
    }
}
