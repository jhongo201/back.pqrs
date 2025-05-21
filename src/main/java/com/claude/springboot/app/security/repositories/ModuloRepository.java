package com.claude.springboot.app.security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claude.springboot.app.security.entities.Modulo;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long> {
    boolean existsByNombre(String nombre);
}
