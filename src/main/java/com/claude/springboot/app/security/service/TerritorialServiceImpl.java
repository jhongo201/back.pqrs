package com.claude.springboot.app.security.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claude.springboot.app.security.dto.TerritorialDTO;
import com.claude.springboot.app.security.entities.Empresa;
import com.claude.springboot.app.security.entities.Territorial;
import com.claude.springboot.app.security.repositories.EmpresaRepository;
import com.claude.springboot.app.security.repositories.TerritorialRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TerritorialServiceImpl implements TerritorialService {
    
    private final TerritorialRepository territorialRepository;
    private final EmpresaRepository empresaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TerritorialDTO> listarTodos() {
        return territorialRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TerritorialDTO obtenerPorId(Long id) {
        Territorial territorial = territorialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Territorial no encontrada"));
        return convertirADTO(territorial);
    }

    @Override
    @Transactional
    public TerritorialDTO crear(TerritorialDTO territorialDTO) {
        Empresa empresa = empresaRepository.findById(territorialDTO.getIdEmpresa())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        if (territorialRepository.existsByCodigoAndEmpresa(territorialDTO.getCodigo(), empresa)) {
            throw new RuntimeException("Ya existe una territorial con este código en la empresa");
        }

        Territorial territorial = new Territorial();
        territorial.setEmpresa(empresa);
        territorial.setNombre(territorialDTO.getNombre());
        territorial.setCodigo(territorialDTO.getCodigo());
        territorial.setDescripcion(territorialDTO.getDescripcion());

        return convertirADTO(territorialRepository.save(territorial));
    }

    @Override
    @Transactional
    public TerritorialDTO actualizar(Long id, TerritorialDTO territorialDTO) {
        Territorial territorial = territorialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Territorial no encontrada"));
        
        if (!territorial.getCodigo().equals(territorialDTO.getCodigo()) &&
            territorialRepository.existsByCodigoAndEmpresa(
                territorialDTO.getCodigo(), 
                territorial.getEmpresa())) {
            throw new RuntimeException("Ya existe una territorial con este código en la empresa");
        }

        territorial.setNombre(territorialDTO.getNombre());
        territorial.setCodigo(territorialDTO.getCodigo());
        territorial.setDescripcion(territorialDTO.getDescripcion());
        territorial.setEstado(territorialDTO.isEstado());

        return convertirADTO(territorialRepository.save(territorial));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Territorial territorial = territorialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Territorial no encontrada"));

        if (!territorial.getDirecciones().isEmpty()) {
            territorial.setEstado(false);
            territorialRepository.save(territorial);
        } else {
            territorialRepository.delete(territorial);
        }
    }

    private TerritorialDTO convertirADTO(Territorial territorial) {
        TerritorialDTO dto = new TerritorialDTO();
        dto.setIdTerritorial(territorial.getIdTerritorial());
        dto.setIdEmpresa(territorial.getEmpresa().getIdEmpresa());
        dto.setNombre(territorial.getNombre());
        dto.setCodigo(territorial.getCodigo());
        dto.setDescripcion(territorial.getDescripcion());
        dto.setEstado(territorial.isEstado());
        return dto;
    }
}
