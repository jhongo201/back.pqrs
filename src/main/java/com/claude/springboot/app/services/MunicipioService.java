package com.claude.springboot.app.services;

import com.claude.springboot.app.dto.MunicipioDTO;
import com.claude.springboot.app.entities.Municipio;

import java.util.List;
import java.util.Optional;

public interface MunicipioService {
    
    /**
     * Obtener todos los municipios activos
     */
    List<MunicipioDTO> obtenerTodosLosMunicipios();
    
    /**
     * Obtener municipio por código DANE
     */
    Optional<MunicipioDTO> obtenerMunicipioPorCodigo(String codigoDane);
    
    /**
     * Obtener municipios por departamento
     */
    List<MunicipioDTO> obtenerMunicipiosPorDepartamento(String codigoDepartamento);
    
    /**
     * Buscar municipios por nombre
     */
    List<MunicipioDTO> buscarMunicipiosPorNombre(String nombre);
    
    /**
     * Obtener municipios por categoría
     */
    List<MunicipioDTO> obtenerMunicipiosPorCategoria(String categoria);
    
    /**
     * Obtener municipios por población mínima
     */
    List<MunicipioDTO> obtenerMunicipiosPorPoblacionMinima(Integer poblacionMinima);
    
    /**
     * Buscar municipios por nombre y departamento
     */
    List<MunicipioDTO> buscarMunicipiosPorNombreYDepartamento(String nombreMunicipio, String nombreDepartamento);
    
    /**
     * Obtener municipios con información completa del departamento
     */
    List<MunicipioDTO> obtenerMunicipiosConDepartamento();
    
    /**
     * Obtener todas las categorías de municipios
     */
    List<String> obtenerCategorias();
    
    /**
     * Obtener municipios por región
     */
    List<MunicipioDTO> obtenerMunicipiosPorRegion(String region);
    
    /**
     * Obtener municipios más poblados
     */
    List<MunicipioDTO> obtenerMunicipiosMasPoblados(int limite);
    
    /**
     * Crear nuevo municipio
     */
    MunicipioDTO crearMunicipio(MunicipioDTO municipioDTO);
    
    /**
     * Actualizar municipio
     */
    MunicipioDTO actualizarMunicipio(String codigoDane, MunicipioDTO municipioDTO);
    
    /**
     * Desactivar municipio
     */
    void desactivarMunicipio(String codigoDane);
    
    /**
     * Activar municipio
     */
    void activarMunicipio(String codigoDane);
    
    /**
     * Verificar si existe un municipio
     */
    boolean existeMunicipio(String codigoDane);
    
    /**
     * Validar código DANE del municipio
     */
    boolean validarCodigoDaneMunicipio(String codigoMunicipio, String codigoDepartamento);
    
    /**
     * Obtener estadísticas de municipios por departamento
     */
    List<Object[]> obtenerEstadisticasPorDepartamento();
    
    /**
     * Obtener estadísticas de municipios por categoría
     */
    List<Object[]> obtenerEstadisticasPorCategoria();
    
    /**
     * Convertir entidad a DTO
     */
    MunicipioDTO convertirADTO(Municipio municipio);
    
    /**
     * Convertir DTO a entidad
     */
    Municipio convertirAEntidad(MunicipioDTO municipioDTO);
}
