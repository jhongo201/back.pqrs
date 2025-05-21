package com.claude.springboot.app.security.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claude.springboot.app.security.dto.DireccionDTO;
import com.claude.springboot.app.security.entities.Direccion;
import com.claude.springboot.app.security.entities.Territorial;
import com.claude.springboot.app.security.repositories.DireccionRepository;
import com.claude.springboot.app.security.repositories.TerritorialRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DireccionServiceImpl implements DireccionService {
   
   private final DireccionRepository direccionRepository;
   private final TerritorialRepository territorialRepository;

   @Override
   @Transactional(readOnly = true)
   public List<DireccionDTO> listarTodos() {
       return direccionRepository.findAll().stream()
               .map(this::convertirADTO)
               .collect(Collectors.toList());
   }

   @Override
   @Transactional(readOnly = true)
   public DireccionDTO obtenerPorId(Long id) {
       Direccion direccion = direccionRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));
       return convertirADTO(direccion);
   }

   @Override
   @Transactional
   public DireccionDTO crear(DireccionDTO direccionDTO) {
       Territorial territorial = territorialRepository.findById(direccionDTO.getIdTerritorial())
               .orElseThrow(() -> new RuntimeException("Territorial no encontrada"));

       if (direccionRepository.existsByNombreAndTerritorial(direccionDTO.getNombre(), territorial)) {
           throw new RuntimeException("Ya existe una dirección con este nombre en la territorial");
       }

       Direccion direccion = new Direccion();
       direccion.setTerritorial(territorial);
       direccion.setNombre(direccionDTO.getNombre());
       direccion.setDescripcion(direccionDTO.getDescripcion());

       return convertirADTO(direccionRepository.save(direccion));
   }

   @Override
   @Transactional
   public DireccionDTO actualizar(Long id, DireccionDTO direccionDTO) {
       Direccion direccion = direccionRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));

       if (!direccion.getNombre().equals(direccionDTO.getNombre()) &&
           direccionRepository.existsByNombreAndTerritorial(
               direccionDTO.getNombre(), 
               direccion.getTerritorial())) {
           throw new RuntimeException("Ya existe una dirección con este nombre en la territorial");
       }

       direccion.setNombre(direccionDTO.getNombre());
       direccion.setDescripcion(direccionDTO.getDescripcion());
       direccion.setEstado(direccionDTO.isEstado());

       return convertirADTO(direccionRepository.save(direccion));
   }

   @Override
   @Transactional
   public void eliminar(Long id) {
       Direccion direccion = direccionRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));

       if (!direccion.getAreas().isEmpty()) {
           direccion.setEstado(false);
           direccionRepository.save(direccion);
       } else {
           direccionRepository.delete(direccion);
       }
   }

   private DireccionDTO convertirADTO(Direccion direccion) {
       DireccionDTO dto = new DireccionDTO();
       dto.setIdDireccion(direccion.getIdDireccion());
       dto.setIdTerritorial(direccion.getTerritorial().getIdTerritorial());
       dto.setNombre(direccion.getNombre());
       dto.setDescripcion(direccion.getDescripcion());
       dto.setEstado(direccion.isEstado());
       return dto;
   }
}
