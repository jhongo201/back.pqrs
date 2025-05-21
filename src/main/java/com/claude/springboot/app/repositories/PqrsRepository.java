package com.claude.springboot.app.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.claude.springboot.app.entities.Pqrs;
import com.claude.springboot.app.security.entities.Usuario;

@Repository
public interface PqrsRepository extends JpaRepository<Pqrs, Long> {
    List<Pqrs> findByUsuarioAsignado(Usuario usuario);
    List<Pqrs> findByEstadoPqrs(String estado);
    Optional<Pqrs> findByNumeroRadicado(String numeroRadicado);
    Optional<Pqrs> findByNumeroRadicadoAndTokenConsulta(String numeroRadicado, String tokenConsulta);
    List<Pqrs> findByUsuarioCreador(String usuarioCreador);
    List<Pqrs> findByUsuarioAsignadoIsNull();
    
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(p.numeroRadicado, 12) AS LONG)), 0) FROM Pqrs p " +
           "WHERE SUBSTRING(p.numeroRadicado, 1, 11) = :prefijo")
    Long findLastSecuencial(@Param("prefijo") String prefijo);


    boolean existsByNumeroRadicado(String numeroRadicado);
    
    @Query("SELECT MAX(CAST(SUBSTRING(p.numeroRadicado, LENGTH(:prefijo) + 2) AS int)) " +
           "FROM Pqrs p WHERE p.numeroRadicado LIKE :prefijo%")
    Integer findUltimoSecuencial(@Param("prefijo") String prefijo);

    @Query("SELECT p FROM Pqrs p LEFT JOIN FETCH p.seguimientos WHERE p.idPqrs = :id")
    Optional<Pqrs> findByIdWithSeguimientos(@Param("id") Long id);

    List<Pqrs> findAllByOrderByIdPqrsDesc();
    List<Pqrs> findByUsuarioAsignadoIsNullOrderByIdPqrsDesc();
    
    List<Pqrs> findByUsuarioCreadorOrderByIdPqrsDesc(String usuarioCreador);

    boolean existsByNumeroDocumentoSolicitanteAndTemaAreaIdAreaAndEstadoPqrsNotIn(
        String numeroDocumento, 
        Long idArea, 
        List<String> estados
    );

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pqrs p " +
           "WHERE p.numeroDocumentoSolicitante = :numeroDocumento " +
           "AND p.tema.area.idArea = :idArea " +
           "AND p.estadoPqrs NOT IN :estados")
    boolean hasActivePqrs(
        @Param("numeroDocumento") String numeroDocumento,
        @Param("idArea") Long idArea,
        @Param("estados") List<String> estados
    );

    @Query("SELECT p FROM Pqrs p " +
           "WHERE p.numeroDocumentoSolicitante = :numeroDocumento " +
           "AND p.tema.area.idArea = :idArea " +
           "AND p.estadoPqrs NOT IN :estados")
    List<Pqrs> findActivePqrsByDocumentoAndArea(
        @Param("numeroDocumento") String numeroDocumento,
        @Param("idArea") Long idArea,
        @Param("estados") List<String> estados
    );

    Optional<Pqrs> findByTokenConsulta(String tokenConsulta);

    // metodos para estdisticas del dashboard
    @Query("SELECT COUNT(p) FROM Pqrs p WHERE p.fechaCreacion >= :startDate")
    Long countByDateAfter(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT p.estadoPqrs as estado, COUNT(p) as cantidad FROM Pqrs p GROUP BY p.estadoPqrs")
    List<Object[]> countByEstado();

    @Query("SELECT p.prioridad as prioridad, COUNT(p) as cantidad FROM Pqrs p GROUP BY p.prioridad")
    List<Object[]> countByPrioridad();

    // PqrsRepository.java
   /* @Query("SELECT p.estadoPqrs as estado, COUNT(p) as cantidad FROM Pqrs p " +
    "WHERE p.fechaCreacion >= :startDate GROUP BY p.estadoPqrs")
    List<Object[]> countByEstadoAndDateAfter(@Param("startDate") LocalDateTime startDate);*/

    @Query("SELECT p.estadoPqrs as estado, COUNT(p) as cantidad FROM Pqrs p " +
    "WHERE p.fechaUltimaActualizacion >= :startDate GROUP BY p.estadoPqrs")
    List<Object[]> countByEstadoAndDateAfter(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT p.prioridad as prioridad, COUNT(p) as cantidad FROM Pqrs p " +
    "WHERE p.fechaCreacion >= :startDate GROUP BY p.prioridad")
    List<Object[]> countByPrioridadAndDateAfter(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT p.estadoPqrs as estado, COUNT(p) as cantidad FROM Pqrs p " +
       "WHERE p.fechaUltimaActualizacion >= :startDate " +
       "AND p.fechaUltimaActualizacion < :endDate " +
       "GROUP BY p.estadoPqrs")
       List<Object[]> countByEstadoAndDateBetween(
       @Param("startDate") LocalDateTime startDate, 
       @Param("endDate") LocalDateTime endDate
       );


       @Query("SELECT p.prioridad as prioridad, COUNT(p) as cantidad FROM Pqrs p " +
       "WHERE p.fechaUltimaActualizacion BETWEEN :startDate AND :endDate " +
       "GROUP BY p.prioridad")
       List<Object[]> countByPrioridadAndDateBetween(
       @Param("startDate") LocalDateTime startDate, 
       @Param("endDate") LocalDateTime endDate
       );


       //reportes
       @Query("SELECT COUNT(p) FROM Pqrs p WHERE p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin")
       Long countByFechaCreacionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

       @Query("SELECT p.estadoPqrs as estado, COUNT(p) as cantidad FROM Pqrs p " +
      "WHERE p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin GROUP BY p.estadoPqrs")
       List<Object[]> countByEstadoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);

       @Query("SELECT p.prioridad as prioridad, COUNT(p) as cantidad FROM Pqrs p " +
       "WHERE p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin GROUP BY p.prioridad")
       List<Object[]> countByPrioridadFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);

       @Query("SELECT p.tema.nombre as tema, COUNT(p) as cantidad FROM Pqrs p " +
       "WHERE p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin GROUP BY p.tema.nombre")
       List<Object[]> countByTemaFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);

       @Query("SELECT AVG(DATEDIFF(day, p.fechaCreacion, p.fechaUltimaActualizacion)) " +
              "FROM Pqrs p WHERE p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin")
       Double getAverageResponseTime(LocalDateTime fechaInicio, LocalDateTime fechaFin);

       @Query("SELECT FORMAT(p.fechaCreacion, 'yyyy-MM') as mes, COUNT(p) as cantidad " +
              "FROM Pqrs p WHERE p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin " +
              "GROUP BY FORMAT(p.fechaCreacion, 'yyyy-MM') ORDER BY mes")
       List<Object[]> getTendenciaMensual(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    

}
