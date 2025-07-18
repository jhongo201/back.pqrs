package com.claude.springboot.app.services;

import com.claude.springboot.app.dto.MunicipioDTO;
import com.claude.springboot.app.entities.Departamento;
import com.claude.springboot.app.entities.Municipio;
import com.claude.springboot.app.repositories.DepartamentoRepository;
import com.claude.springboot.app.repositories.MunicipioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MunicipioServiceImpl implements MunicipioService {
    
    @Autowired
    private MunicipioRepository municipioRepository;
    
    @Autowired
    private DepartamentoRepository departamentoRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<MunicipioDTO> obtenerTodosLosMunicipios() {
        return municipioRepository.findByActivoTrueOrderByNombre()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<MunicipioDTO> obtenerMunicipioPorCodigo(String codigoDane) {
        return municipioRepository.findByCodigoDaneAndActivoTrue(codigoDane)
                .map(this::convertirADTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MunicipioDTO> obtenerMunicipiosPorDepartamento(String codigoDepartamento) {
        return municipioRepository.findByCodigoDepartamentoAndActivoTrueOrderByNombre(codigoDepartamento)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MunicipioDTO> buscarMunicipiosPorNombre(String nombre) {
        return municipioRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MunicipioDTO> obtenerMunicipiosPorCategoria(String categoria) {
        return municipioRepository.findByCategoriaAndActivoTrue(categoria)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MunicipioDTO> obtenerMunicipiosPorPoblacionMinima(Integer poblacionMinima) {
        return municipioRepository.findByPoblacionEstimadaGreaterThanEqualAndActivoTrue(poblacionMinima)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MunicipioDTO> buscarMunicipiosPorNombreYDepartamento(String nombreMunicipio, String nombreDepartamento) {
        return municipioRepository.findByNombreAndDepartamento(nombreMunicipio, nombreDepartamento)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MunicipioDTO> obtenerMunicipiosConDepartamento() {
        List<Object[]> resultados = municipioRepository.findMunicipiosConDepartamento();
        
        return resultados.stream()
                .map(row -> {
                    MunicipioDTO dto = new MunicipioDTO();
                    dto.setCodigoDane((String) row[0]);
                    dto.setNombre((String) row[1]);
                    dto.setCodigoDepartamento((String) row[2]);
                    dto.setCategoria((String) row[3]);
                    dto.setPoblacionEstimada((Integer) row[4]);
                    dto.setNombreDepartamento((String) row[5]);
                    dto.setCapitalDepartamento((String) row[6]);
                    dto.setRegion((String) row[7]);
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> obtenerCategorias() {
        return municipioRepository.findDistinctCategorias();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MunicipioDTO> obtenerMunicipiosPorRegion(String region) {
        return municipioRepository.findByRegion(region)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MunicipioDTO> obtenerMunicipiosMasPoblados(int limite) {
        return municipioRepository.findTopMunicipiosByPoblacion()
                .stream()
                .limit(limite)
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public MunicipioDTO crearMunicipio(MunicipioDTO municipioDTO) {
        if (municipioRepository.existsByCodigoDaneAndActivoTrue(municipioDTO.getCodigoDane())) {
            throw new IllegalArgumentException("Ya existe un municipio con el código DANE: " + municipioDTO.getCodigoDane());
        }
        
        // Validar que el departamento exista
        if (!departamentoRepository.existsByCodigoDaneAndActivoTrue(municipioDTO.getCodigoDepartamento())) {
            throw new IllegalArgumentException("El departamento no existe: " + municipioDTO.getCodigoDepartamento());
        }
        
        // Validar que el código DANE del municipio corresponda al departamento
        if (!municipioDTO.validarCodigoDane()) {
            throw new IllegalArgumentException("El código DANE del municipio no corresponde al departamento");
        }
        
        Municipio municipio = convertirAEntidad(municipioDTO);
        municipio.setFechaCreacion(LocalDateTime.now());
        municipio.setActivo(true);
        
        Municipio municipioGuardado = municipioRepository.save(municipio);
        return convertirADTO(municipioGuardado);
    }
    
    @Override
    public MunicipioDTO actualizarMunicipio(String codigoDane, MunicipioDTO municipioDTO) {
        Municipio municipio = municipioRepository.findByCodigoDaneAndActivoTrue(codigoDane)
                .orElseThrow(() -> new IllegalArgumentException("Municipio no encontrado: " + codigoDane));
        
        // Actualizar campos
        municipio.setNombre(municipioDTO.getNombre());
        municipio.setCategoria(municipioDTO.getCategoria());
        municipio.setPoblacionEstimada(municipioDTO.getPoblacionEstimada());
        
        // Si se cambia el departamento, validar
        if (!municipio.getCodigoDepartamento().equals(municipioDTO.getCodigoDepartamento())) {
            if (!departamentoRepository.existsByCodigoDaneAndActivoTrue(municipioDTO.getCodigoDepartamento())) {
                throw new IllegalArgumentException("El departamento no existe: " + municipioDTO.getCodigoDepartamento());
            }
            municipio.setCodigoDepartamento(municipioDTO.getCodigoDepartamento());
        }
        
        Municipio municipioActualizado = municipioRepository.save(municipio);
        return convertirADTO(municipioActualizado);
    }
    
    @Override
    public void desactivarMunicipio(String codigoDane) {
        Municipio municipio = municipioRepository.findById(codigoDane)
                .orElseThrow(() -> new IllegalArgumentException("Municipio no encontrado: " + codigoDane));
        
        municipio.setActivo(false);
        municipioRepository.save(municipio);
    }
    
    @Override
    public void activarMunicipio(String codigoDane) {
        Municipio municipio = municipioRepository.findById(codigoDane)
                .orElseThrow(() -> new IllegalArgumentException("Municipio no encontrado: " + codigoDane));
        
        municipio.setActivo(true);
        municipioRepository.save(municipio);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existeMunicipio(String codigoDane) {
        return municipioRepository.existsByCodigoDaneAndActivoTrue(codigoDane);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validarCodigoDaneMunicipio(String codigoMunicipio, String codigoDepartamento) {
        return municipioRepository.validarCodigoDaneMunicipio(codigoMunicipio, codigoDepartamento);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> obtenerEstadisticasPorDepartamento() {
        return municipioRepository.countMunicipiosByDepartamento();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> obtenerEstadisticasPorCategoria() {
        return municipioRepository.countMunicipiosByCategoria();
    }
    
    @Override
    public MunicipioDTO convertirADTO(Municipio municipio) {
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
        
        // Incluir información del departamento si está cargado
        if (municipio.getDepartamento() != null) {
            Departamento departamento = municipio.getDepartamento();
            dto.setNombreDepartamento(departamento.getNombre());
            dto.setCapitalDepartamento(departamento.getCapital());
            dto.setRegion(departamento.getRegion());
        }
        
        return dto;
    }
    
    @Override
    public Municipio convertirAEntidad(MunicipioDTO municipioDTO) {
        if (municipioDTO == null) {
            return null;
        }
        
        Municipio municipio = new Municipio();
        municipio.setCodigoDane(municipioDTO.getCodigoDane());
        municipio.setNombre(municipioDTO.getNombre());
        municipio.setCodigoDepartamento(municipioDTO.getCodigoDepartamento());
        municipio.setCategoria(municipioDTO.getCategoria());
        municipio.setPoblacionEstimada(municipioDTO.getPoblacionEstimada());
        municipio.setFechaCreacion(municipioDTO.getFechaCreacion());
        municipio.setActivo(municipioDTO.getActivo());
        
        return municipio;
    }
}
