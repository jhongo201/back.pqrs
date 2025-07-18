package com.claude.springboot.app.services;

import com.claude.springboot.app.dto.DepartamentoDTO;
import com.claude.springboot.app.entities.Departamento;

import java.util.List;
import java.util.Optional;

public interface DepartamentoService {
    
    /**
     * Obtener todos los departamentos activos
     */
    List<DepartamentoDTO> obtenerTodosLosDepartamentos();
    
    /**
     * Obtener departamento por código DANE
     */
    Optional<DepartamentoDTO> obtenerDepartamentoPorCodigo(String codigoDane);
    
    /**
     * Buscar departamentos por nombre
     */
    List<DepartamentoDTO> buscarDepartamentosPorNombre(String nombre);
    
    /**
     * Obtener departamentos por región
     */
    List<DepartamentoDTO> obtenerDepartamentosPorRegion(String region);
    
    /**
     * Buscar departamentos por capital
     */
    List<DepartamentoDTO> buscarDepartamentosPorCapital(String capital);
    
    /**
     * Obtener todas las regiones
     */
    List<String> obtenerRegiones();
    
    /**
     * Obtener estadísticas de departamentos
     */
    List<DepartamentoDTO> obtenerEstadisticasDepartamentos();
    
    /**
     * Crear nuevo departamento
     */
    DepartamentoDTO crearDepartamento(DepartamentoDTO departamentoDTO);
    
    /**
     * Actualizar departamento
     */
    DepartamentoDTO actualizarDepartamento(String codigoDane, DepartamentoDTO departamentoDTO);
    
    /**
     * Desactivar departamento
     */
    void desactivarDepartamento(String codigoDane);
    
    /**
     * Activar departamento
     */
    void activarDepartamento(String codigoDane);
    
    /**
     * Verificar si existe un departamento
     */
    boolean existeDepartamento(String codigoDane);
    
    /**
     * Obtener departamentos con municipios
     */
    List<DepartamentoDTO> obtenerDepartamentosConMunicipios();
    
    /**
     * Convertir entidad a DTO
     */
    DepartamentoDTO convertirADTO(Departamento departamento);
    
    /**
     * Convertir DTO a entidad
     */
    Departamento convertirAEntidad(DepartamentoDTO departamentoDTO);
}
