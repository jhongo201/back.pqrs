package com.claude.springboot.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claude.springboot.app.entities.TemasPqrs;

@Repository
public interface TemasPqrsRepository extends JpaRepository<TemasPqrs, Long> {
    boolean existsByNombreAndAreaIdArea(String nombre, Long idArea);
}
