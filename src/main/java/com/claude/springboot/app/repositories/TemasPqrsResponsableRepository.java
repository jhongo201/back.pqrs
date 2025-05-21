package com.claude.springboot.app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claude.springboot.app.entities.TemasPqrs;
import com.claude.springboot.app.entities.TemasPqrsResponsable;
import com.claude.springboot.app.security.entities.Usuario;

@Repository
public interface TemasPqrsResponsableRepository extends JpaRepository<TemasPqrsResponsable, Long> {
    List<TemasPqrsResponsable> findByTemaAndEstadoTrue(TemasPqrs tema);
    Optional<TemasPqrsResponsable> findByTemaAndUsuario(TemasPqrs tema, Usuario usuario);
    Optional<TemasPqrsResponsable> findByTemaAndUsuarioAndEstadoTrue(TemasPqrs tema, Usuario usuario);
    boolean existsByTemaAndUsuarioAndEstadoTrue(TemasPqrs tema, Usuario usuario);
}
