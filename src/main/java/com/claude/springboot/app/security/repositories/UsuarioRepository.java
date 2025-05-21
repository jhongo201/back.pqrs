package com.claude.springboot.app.security.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.claude.springboot.app.security.entities.Persona;
import com.claude.springboot.app.security.entities.Usuario;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

       @EntityGraph(value = "Usuario.completo")
       @Query("SELECT u FROM Usuario u")
       List<Usuario> findAllWithRelations();

       @EntityGraph(attributePaths = { "rol", "persona" })
       List<Usuario> findAll();

       Optional<Usuario> findByUsername(String username);

       boolean existsByUsername(String username);

       @Query("SELECT u FROM Usuario u WHERE u.persona.email = :email")
       Optional<Usuario> findByPersonaEmail(@Param("email") String email);

       @Query("SELECT u FROM Usuario u WHERE u.estado = true AND u.rol.idRol = :rolId")
       List<Usuario> findAllByRolId(@Param("rolId") Long rolId);

       @Query("SELECT u FROM Usuario u WHERE u.persona.area.idArea = :areaId AND u.estado = true")
       List<Usuario> findAllByAreaId(@Param("areaId") Long areaId);

       @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.tokenSesion = :token AND u.estado = true")
       boolean existsByTokenSesion(@Param("token") String token);

       /*
        * @Query("SELECT u FROM Usuario u JOIN FETCH u.rol r JOIN FETCH u.persona p " +
        * "WHERE u.username = :username AND u.estado = true")
        * Optional<Usuario> findByUsernameWithRolAndPersona(@Param("username") String
        * username);
        */

       @Query("SELECT u FROM Usuario u " +
                     "LEFT JOIN FETCH u.rol " +
                     "LEFT JOIN FETCH u.persona " +
                     "WHERE u.username = :username")
       Optional<Usuario> findByUsernameWithRolAndPersona(@Param("username") String username);

       @Query("SELECT u FROM Usuario u " +
                     "LEFT JOIN FETCH u.rol " +
                     "LEFT JOIN FETCH u.persona " +
                     "WHERE u.idUsuario = :id")
       Optional<Usuario> findByIdWithRelations(@Param("id") Long id);

       @EntityGraph(value = "Usuario.complete", type = EntityGraph.EntityGraphType.LOAD)
       Optional<Usuario> findById(Long id);

       boolean existsByPersona(Persona persona);

       @Query("SELECT u FROM Usuario u " +
           "LEFT JOIN FETCH u.persona p " +
           "LEFT JOIN FETCH u.rol " +
           "LEFT JOIN FETCH p.area a " +
           "LEFT JOIN FETCH p.empresa pe " +
           "LEFT JOIN FETCH a.direccion d " +
           "LEFT JOIN FETCH d.territorial t " +
           "LEFT JOIN FETCH t.empresa te " +
           "WHERE u.idUsuario = :id")
    Optional<Usuario> findByIdWithFullInfo(@Param("id") Long id);

    @Query("SELECT u FROM Usuario u " +
       "LEFT JOIN FETCH u.persona p " +
       "LEFT JOIN FETCH p.area a " +
       "LEFT JOIN FETCH a.direccion d " +
       "LEFT JOIN FETCH d.territorial t " +
       "WHERE a.idArea = :areaId AND u.estado = true")
       List<Usuario> findAllByAreaIdWithFullInfo(@Param("areaId") Long areaId);

       @Query("SELECT COUNT(u) FROM Usuario u WHERE u.fechaCreacion >= :startDate")
       Long countByDateAfter(@Param("startDate") LocalDateTime startDate);

       @Query("SELECT u.estado as estado, COUNT(u) as cantidad FROM Usuario u GROUP BY u.estado")
       List<Object[]> countByEstado();

    
}
