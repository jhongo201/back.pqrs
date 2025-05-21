package com.claude.springboot.app.security.service;

import java.util.List;

import com.claude.springboot.app.security.dto.AsignacionPermisosDTO;
import com.claude.springboot.app.security.dto.PermisoRolDTO;

public interface PermisoRolService {
    List<PermisoRolDTO> listarPermisosPorRol(Long idRol);
    PermisoRolDTO obtenerPermiso(Long idPermiso);
    void asignarPermisosARol(AsignacionPermisosDTO asignacionDTO);
    PermisoRolDTO actualizarPermiso(Long idPermiso, PermisoRolDTO permisoDTO);
    void eliminarPermiso(Long idPermiso);
}
