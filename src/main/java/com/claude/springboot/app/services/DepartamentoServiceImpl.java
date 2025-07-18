package com.claude.springboot.app.services;

import com.claude.springboot.app.dto.DepartamentoDTO;
import com.claude.springboot.app.dto.MunicipioDTO;
import com.claude.springboot.app.entities.Departamento;
import com.claude.springboot.app.entities.Municipio;
import com.claude.springboot.app.repositories.DepartamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepartamentoServiceImpl implements DepartamentoService {
    
    @Autowired
    private DepartamentoRepository departamentoRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<DepartamentoDTO> obtenerTodosLosDepartamentos() {
        return departamentoRepository.findByActivoTrueOrderByNombre()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<DepartamentoDTO> obtenerDepartamentoPorCodigo(String codigoDane) {
        return departamentoRepository.findByCodigoDaneAndActivoTrue(codigoDane)
                .map(this::convertirADTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DepartamentoDTO> buscarDepartamentosPorNombre(String nombre) {
        return departamentoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DepartamentoDTO> obtenerDepartamentosPorRegion(String region) {
        return departamentoRepository.findByRegionAndActivoTrue(region)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DepartamentoDTO> buscarDepartamentosPorCapital(String capital) {
        return departamentoRepository.findByCapitalContainingIgnoreCaseAndActivoTrue(capital)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> obtenerRegiones() {
        return departamentoRepository.findDistinctRegiones();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DepartamentoDTO> obtenerEstadisticasDepartamentos() {
        List<Object[]> estadisticas = departamentoRepository.findEstadisticasDepartamentos();
        
        return estadisticas.stream()
                .map(row -> {
                    DepartamentoDTO dto = new DepartamentoDTO();
                    dto.setCodigoDane((String) row[0]);
                    dto.setNombre((String) row[1]);
                    dto.setRegion((String) row[2]);
                    dto.setTotalMunicipios(((Number) row[3]).intValue());
                    dto.setPoblacionTotalEstimada(((Number) row[4]).longValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public DepartamentoDTO crearDepartamento(DepartamentoDTO departamentoDTO) {
        if (departamentoRepository.existsByCodigoDaneAndActivoTrue(departamentoDTO.getCodigoDane())) {
            throw new IllegalArgumentException("Ya existe un departamento con el código DANE: " + departamentoDTO.getCodigoDane());
        }
        
        Departamento departamento = convertirAEntidad(departamentoDTO);
        departamento.setFechaCreacion(LocalDateTime.now());
        departamento.setActivo(true);
        
        Departamento departamentoGuardado = departamentoRepository.save(departamento);
        return convertirADTO(departamentoGuardado);
    }
    
    @Override
    public DepartamentoDTO actualizarDepartamento(String codigoDane, DepartamentoDTO departamentoDTO) {
        Departamento departamento = departamentoRepository.findByCodigoDaneAndActivoTrue(codigoDane)
                .orElseThrow(() -> new IllegalArgumentException("Departamento no encontrado: " + codigoDane));
        
        // Actualizar campos
        departamento.setNombre(departamentoDTO.getNombre());
        departamento.setCapital(departamentoDTO.getCapital());
        departamento.setRegion(departamentoDTO.getRegion());
        
        Departamento departamentoActualizado = departamentoRepository.save(departamento);
        return convertirADTO(departamentoActualizado);
    }
    
    @Override
    public void desactivarDepartamento(String codigoDane) {
        Departamento departamento = departamentoRepository.findById(codigoDane)
                .orElseThrow(() -> new IllegalArgumentException("Departamento no encontrado: " + codigoDane));
        
        departamento.setActivo(false);
        departamentoRepository.save(departamento);
    }
    
    @Override
    public void activarDepartamento(String codigoDane) {
        Departamento departamento = departamentoRepository.findById(codigoDane)
                .orElseThrow(() -> new IllegalArgumentException("Departamento no encontrado: " + codigoDane));
        
        departamento.setActivo(true);
        departamentoRepository.save(departamento);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existeDepartamento(String codigoDane) {
        return departamentoRepository.existsByCodigoDaneAndActivoTrue(codigoDane);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DepartamentoDTO> obtenerDepartamentosConMunicipios() {
        return departamentoRepository.findDepartamentosConMunicipios()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public DepartamentoDTO convertirADTO(Departamento departamento) {
        if (departamento == null) {
            return null;
        }
        
        DepartamentoDTO dto = new DepartamentoDTO();
        dto.setCodigoDane(departamento.getCodigoDane());
        dto.setNombre(departamento.getNombre());
        dto.setCapital(departamento.getCapital());
        dto.setRegion(departamento.getRegion());
        dto.setFechaCreacion(departamento.getFechaCreacion());
        dto.setActivo(departamento.getActivo());
        
        // Convertir municipios si están cargados
        if (departamento.getMunicipios() != null) {
            List<MunicipioDTO> municipiosDTO = departamento.getMunicipios().stream()
                    .filter(m -> m.getActivo())
                    .map(this::convertirMunicipioADTO)
                    .collect(Collectors.toList());
            dto.setMunicipios(municipiosDTO);
            dto.setTotalMunicipios(municipiosDTO.size());
            
            // Calcular población total
            long poblacionTotal = municipiosDTO.stream()
                    .mapToLong(m -> m.getPoblacionEstimada() != null ? m.getPoblacionEstimada() : 0)
                    .sum();
            dto.setPoblacionTotalEstimada(poblacionTotal);
        }
        
        return dto;
    }
    
    @Override
    public Departamento convertirAEntidad(DepartamentoDTO departamentoDTO) {
        if (departamentoDTO == null) {
            return null;
        }
        
        Departamento departamento = new Departamento();
        departamento.setCodigoDane(departamentoDTO.getCodigoDane());
        departamento.setNombre(departamentoDTO.getNombre());
        departamento.setCapital(departamentoDTO.getCapital());
        departamento.setRegion(departamentoDTO.getRegion());
        departamento.setFechaCreacion(departamentoDTO.getFechaCreacion());
        departamento.setActivo(departamentoDTO.getActivo());
        
        return departamento;
    }
    
    private MunicipioDTO convertirMunicipioADTO(Municipio municipio) {
        if (municipio == null) {
            return null;
        }
        
        MunicipioDTO dto = new MunicipioDTO();
        dto.setCodigoDane(municipio.getCodigoDane());
        dto.setNombre(municipio.getNombre());
        dto.setCodigoDepartamento(municipio.getCodigoDepartamento());
        dto.setCategoria(municipio.getCategoria());
        dto.setPoblacionEstimada(municipio.getPoblacionEstimada());
        dto.setFechaCreacion(municipio.getFechaCreacion());
        dto.setActivo(municipio.getActivo());
        
        return dto;
    }
}
