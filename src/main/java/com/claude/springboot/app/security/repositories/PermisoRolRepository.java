package com.claude.springboot.app.security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.claude.springboot.app.security.entities.PermisoRol;
import com.claude.springboot.app.security.entities.Rol;
import com.claude.springboot.app.security.entities.Ruta;

import java.util.List;

@Repository
public interface PermisoRolRepository extends JpaRepository<PermisoRol, Long> {

       @Query("SELECT p FROM PermisoRol p WHERE p.rol.idRol = :rolId AND p.estado = true")
       List<PermisoRol> findAllByRolId(@Param("rolId") Long rolId);

       @Query("SELECT p FROM PermisoRol p WHERE p.ruta.idRuta = :rutaId AND p.estado = true")
       List<PermisoRol> findAllByRutaId(@Param("rutaId") Long rutaId);

       // Método para verificar permiso de lectura por ID de rol
       @Query("SELECT COUNT(p) > 0 FROM PermisoRol p " +
                     "WHERE p.rol.idRol = :rolId " +
                     "AND p.ruta.ruta = :rutaPath " +
                     "AND p.puedeLeer = true " +
                     "AND p.estado = true")
       boolean tienePermisoLectura(@Param("rolId") Long rolId, @Param("rutaPath") String rutaPath);

       // Método para verificar permiso de escritura por ID de rol
       @Query("SELECT COUNT(p) > 0 FROM PermisoRol p " +
                     "WHERE p.rol.idRol = :rolId " +
                     "AND p.ruta.ruta = :rutaPath " +
                     "AND p.puedeEscribir = true " +
                     "AND p.estado = true")
       boolean tienePermisoEscritura(@Param("rolId") Long rolId, @Param("rutaPath") String rutaPath);

       // Métodos adicionales para búsqueda por nombre de rol si los necesitas
       @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
                     "FROM PermisoRol p " +
                     "JOIN p.rol r " +
                     "JOIN p.ruta rt " +
                     "WHERE r.nombre = :rolNombre " +
                     "AND rt.ruta = :rutaPath " +
                     "AND p.puedeLeer = true " +
                     "AND p.estado = true")
       boolean tienePermisoLecturaPorNombre(@Param("rolNombre") String rolNombre,
                     @Param("rutaPath") String rutaPath);

       @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
                     "FROM PermisoRol p " +
                     "JOIN p.rol r " +
                     "JOIN p.ruta rt " +
                     "WHERE r.nombre = :rolNombre " +
                     "AND rt.ruta = :rutaPath " +
                     "AND p.puedeEscribir = true " +
                     "AND p.estado = true")
       boolean tienePermisoEscrituraPorNombre(@Param("rolNombre") String rolNombre,
                     @Param("rutaPath") String rutaPath);

       @Query("SELECT CASE WHEN COUNT(pr) > 0 THEN true ELSE false END " +
                     "FROM PermisoRol pr " +
                     "JOIN pr.rol r " +
                     "JOIN pr.ruta rt " +
                     "WHERE r.nombre = :rolNombre " +
                     "AND rt.ruta = :ruta " +
                     "AND pr.puedeActualizar = true " +
                     "AND pr.estado = true")
       boolean tienePermisoActualizarPorNombre(@Param("rolNombre") String rolNombre, @Param("ruta") String ruta);

       @Query("SELECT CASE WHEN COUNT(pr) > 0 THEN true ELSE false END " +
                     "FROM PermisoRol pr " +
                     "JOIN pr.rol r " +
                     "JOIN pr.ruta rt " +
                     "WHERE r.nombre = :rolNombre " +
                     "AND rt.ruta = :ruta " +
                     "AND pr.puedeEliminar = true " +
                     "AND pr.estado = true")
       boolean tienePermisoEliminarPorNombre(@Param("rolNombre") String rolNombre, @Param("ruta") String ruta);

       @Query("SELECT pr.puedeActualizar FROM PermisoRol pr " +
                     "WHERE pr.rol.idRol = :idRol AND pr.ruta.ruta = :ruta AND pr.estado = true")
       boolean tienePermisoActualizacion(@Param("idRol") Long idRol, @Param("ruta") String ruta);

       @Query("SELECT pr.puedeEliminar FROM PermisoRol pr " +
                     "WHERE pr.rol.idRol = :idRol AND pr.ruta.ruta = :ruta AND pr.estado = true")
       boolean tienePermisoEliminacion(@Param("idRol") Long idRol, @Param("ruta") String ruta);

       List<PermisoRol> findByRolIdRol(Long idRol);

       @Modifying
       @Query("UPDATE PermisoRol pr SET pr.estado = false WHERE pr.rol.idRol = :idRol")
       void desactivarPermisosPorRol(@Param("idRol") Long idRol);

       List<PermisoRol> findByRuta(Ruta ruta);

       void deleteByRuta(Ruta ruta);

       boolean existsByRolAndRuta(Rol rol, Ruta ruta);

}
