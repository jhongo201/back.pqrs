package com.claude.springboot.app.services;

import com.claude.springboot.app.dto.TipoDocumentoDTO;
import com.claude.springboot.app.entities.TipoDocumento;
import com.claude.springboot.app.repositories.TipoDocumentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para TipoDocumento
 * 
 * @author Sistema PQRS
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TipoDocumentoServiceImpl implements TipoDocumentoService {

    private final TipoDocumentoRepository tipoDocumentoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TipoDocumentoDTO> obtenerTodosLosTiposDocumento() {
        log.debug("Obteniendo todos los tipos de documento");
        return tipoDocumentoRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoDocumentoDTO> obtenerTiposDocumentoActivos() {
        log.debug("Obteniendo tipos de documento activos");
        return tipoDocumentoRepository.findByEstadoTrueOrderByNombreAsc()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoDocumentoDTO> obtenerTipoDocumentoPorId(Long id) {
        log.debug("Obteniendo tipo de documento por ID: {}", id);
        return tipoDocumentoRepository.findById(id)
                .map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoDocumentoDTO> obtenerTipoDocumentoPorCodigo(String codigo) {
        log.debug("Obteniendo tipo de documento por código: {}", codigo);
        return tipoDocumentoRepository.findByCodigoIgnoreCase(codigo)
                .map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoDocumentoDTO> obtenerTipoDocumentoPorNombre(String nombre) {
        log.debug("Obteniendo tipo de documento por nombre: {}", nombre);
        return tipoDocumentoRepository.findByNombreIgnoreCase(nombre)
                .map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoDocumentoDTO> buscarTiposDocumentoPorNombre(String nombre) {
        log.debug("Buscando tipos de documento por nombre: {}", nombre);
        return tipoDocumentoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoDocumentoDTO> buscarTiposDocumento(String termino) {
        log.debug("Buscando tipos de documento por término: {}", termino);
        return tipoDocumentoRepository.buscarPorCodigoONombre(termino)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoDocumentoDTO> buscarTiposDocumentoActivos(String termino) {
        log.debug("Buscando tipos de documento activos por término: {}", termino);
        return tipoDocumentoRepository.buscarActivosPorCodigoONombre(termino)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public TipoDocumentoDTO crearTipoDocumento(TipoDocumentoDTO tipoDocumentoDTO) {
        log.info("Creando nuevo tipo de documento: {}", tipoDocumentoDTO.getCodigo());
        
        // Validar que no exista el código
        if (tipoDocumentoRepository.existsByCodigoIgnoreCase(tipoDocumentoDTO.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un tipo de documento con el código: " + tipoDocumentoDTO.getCodigo());
        }
        
        // Validar que no exista el nombre
        if (tipoDocumentoRepository.existsByNombreIgnoreCase(tipoDocumentoDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe un tipo de documento con el nombre: " + tipoDocumentoDTO.getNombre());
        }
        
        TipoDocumento tipoDocumento = convertirAEntidad(tipoDocumentoDTO);
        TipoDocumento tipoDocumentoGuardado = tipoDocumentoRepository.save(tipoDocumento);
        
        log.info("Tipo de documento creado exitosamente con ID: {}", tipoDocumentoGuardado.getId());
        return convertirADTO(tipoDocumentoGuardado);
    }

    @Override
    public Optional<TipoDocumentoDTO> actualizarTipoDocumento(Long id, TipoDocumentoDTO tipoDocumentoDTO) {
        log.info("Actualizando tipo de documento con ID: {}", id);
        
        return tipoDocumentoRepository.findById(id)
                .map(tipoDocumentoExistente -> {
                    // Validar código único (si cambió)
                    if (!tipoDocumentoExistente.getCodigo().equalsIgnoreCase(tipoDocumentoDTO.getCodigo()) &&
                        tipoDocumentoRepository.existsByCodigoIgnoreCase(tipoDocumentoDTO.getCodigo())) {
                        throw new IllegalArgumentException("Ya existe un tipo de documento con el código: " + tipoDocumentoDTO.getCodigo());
                    }
                    
                    // Validar nombre único (si cambió)
                    if (!tipoDocumentoExistente.getNombre().equalsIgnoreCase(tipoDocumentoDTO.getNombre()) &&
                        tipoDocumentoRepository.existsByNombreIgnoreCase(tipoDocumentoDTO.getNombre())) {
                        throw new IllegalArgumentException("Ya existe un tipo de documento con el nombre: " + tipoDocumentoDTO.getNombre());
                    }
                    
                    // Actualizar campos
                    tipoDocumentoExistente.setCodigo(tipoDocumentoDTO.getCodigo());
                    tipoDocumentoExistente.setNombre(tipoDocumentoDTO.getNombre());
                    tipoDocumentoExistente.setDescripcion(tipoDocumentoDTO.getDescripcion());
                    tipoDocumentoExistente.setEstado(tipoDocumentoDTO.getEstado());
                    
                    TipoDocumento tipoDocumentoActualizado = tipoDocumentoRepository.save(tipoDocumentoExistente);
                    log.info("Tipo de documento actualizado exitosamente: {}", tipoDocumentoActualizado.getId());
                    return convertirADTO(tipoDocumentoActualizado);
                });
    }

    @Override
    public boolean activarTipoDocumento(Long id) {
        log.info("Activando tipo de documento con ID: {}", id);
        
        return tipoDocumentoRepository.findById(id)
                .map(tipoDocumento -> {
                    tipoDocumento.activar();
                    tipoDocumentoRepository.save(tipoDocumento);
                    log.info("Tipo de documento activado exitosamente: {}", id);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean desactivarTipoDocumento(Long id) {
        log.info("Desactivando tipo de documento con ID: {}", id);
        
        return tipoDocumentoRepository.findById(id)
                .map(tipoDocumento -> {
                    tipoDocumento.desactivar();
                    tipoDocumentoRepository.save(tipoDocumento);
                    log.info("Tipo de documento desactivado exitosamente: {}", id);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean eliminarTipoDocumento(Long id) {
        log.info("Eliminando tipo de documento con ID: {}", id);
        
        if (tipoDocumentoRepository.existsById(id)) {
            tipoDocumentoRepository.deleteById(id);
            log.info("Tipo de documento eliminado exitosamente: {}", id);
            return true;
        }
        
        log.warn("No se encontró tipo de documento con ID: {}", id);
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeTipoDocumentoPorCodigo(String codigo) {
        return tipoDocumentoRepository.existsByCodigoIgnoreCase(codigo);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeTipoDocumentoPorNombre(String nombre) {
        return tipoDocumentoRepository.existsByNombreIgnoreCase(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public Long[] obtenerEstadisticas() {
        log.debug("Obteniendo estadísticas de tipos de documento");
        Object[] resultado = tipoDocumentoRepository.obtenerEstadisticas();
        return new Long[]{
            ((Number) resultado[0]).longValue(), // Total
            ((Number) resultado[1]).longValue(), // Activos
            ((Number) resultado[2]).longValue()  // Inactivos
        };
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTiposDocumento() {
        return tipoDocumentoRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTiposDocumentoActivos() {
        return tipoDocumentoRepository.countByEstadoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTiposDocumentoInactivos() {
        return tipoDocumentoRepository.countByEstadoFalse();
    }

    /**
     * Convierte una entidad TipoDocumento a DTO
     * 
     * @param tipoDocumento Entidad a convertir
     * @return DTO convertido
     */
    private TipoDocumentoDTO convertirADTO(TipoDocumento tipoDocumento) {
        return new TipoDocumentoDTO(
                tipoDocumento.getId(),
                tipoDocumento.getCodigo(),
                tipoDocumento.getNombre(),
                tipoDocumento.getDescripcion(),
                tipoDocumento.getEstado(),
                tipoDocumento.getFechaCreacion(),
                tipoDocumento.getFechaActualizacion()
        );
    }

    /**
     * Convierte un DTO a entidad TipoDocumento
     * 
     * @param tipoDocumentoDTO DTO a convertir
     * @return Entidad convertida
     */
    private TipoDocumento convertirAEntidad(TipoDocumentoDTO tipoDocumentoDTO) {
        return new TipoDocumento(
                tipoDocumentoDTO.getCodigo(),
                tipoDocumentoDTO.getNombre(),
                tipoDocumentoDTO.getDescripcion(),
                tipoDocumentoDTO.getEstado()
        );
    }
}
