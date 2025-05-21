package com.claude.springboot.app.security.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claude.springboot.app.security.dto.EmpresaDTO;
import com.claude.springboot.app.security.entities.Empresa;
import com.claude.springboot.app.security.repositories.EmpresaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmpresaServiceImpl implements EmpresaService {
    
    private final EmpresaRepository empresaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EmpresaDTO> listarTodos() {
        return empresaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmpresaDTO obtenerPorId(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
        return convertirADTO(empresa);
    }

    @Override
    @Transactional
    public EmpresaDTO crear(EmpresaDTO empresaDTO) {
        if (empresaRepository.existsByNit(empresaDTO.getNit())) {
            throw new RuntimeException("Ya existe una empresa con este NIT");
        }
        
        Empresa empresa = new Empresa();
        empresa.setNombre(empresaDTO.getNombre());
        empresa.setNit(empresaDTO.getNit());
        empresa.setTelefono(empresaDTO.getTelefono());
        empresa.setEmail(empresaDTO.getEmail());
        
        return convertirADTO(empresaRepository.save(empresa));
    }

    @Override
    @Transactional
    public EmpresaDTO actualizar(Long id, EmpresaDTO empresaDTO) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
        
        if (!empresa.getNit().equals(empresaDTO.getNit()) && 
            empresaRepository.existsByNit(empresaDTO.getNit())) {
            throw new RuntimeException("Ya existe una empresa con este NIT");
        }

        empresa.setNombre(empresaDTO.getNombre());
        empresa.setNit(empresaDTO.getNit());
        empresa.setTelefono(empresaDTO.getTelefono());
        empresa.setEmail(empresaDTO.getEmail());
        empresa.setEstado(empresaDTO.isEstado());
        
        return convertirADTO(empresaRepository.save(empresa));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
                
        if (!empresa.getTerritoriales().isEmpty()) {
            empresa.setEstado(false);
            empresaRepository.save(empresa);
        } else {
            empresaRepository.delete(empresa);
        }
    }

    private EmpresaDTO convertirADTO(Empresa empresa) {
        EmpresaDTO dto = new EmpresaDTO();
        dto.setIdEmpresa(empresa.getIdEmpresa());
        dto.setNombre(empresa.getNombre());
        dto.setNit(empresa.getNit());
        dto.setTelefono(empresa.getTelefono());
        dto.setEmail(empresa.getEmail());
        dto.setEstado(empresa.isEstado());
        return dto;
    }
}
