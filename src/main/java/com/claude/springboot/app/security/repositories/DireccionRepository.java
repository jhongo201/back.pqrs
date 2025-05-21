package com.claude.springboot.app.security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claude.springboot.app.security.entities.Direccion;
import com.claude.springboot.app.security.entities.Territorial;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Long> {
   boolean existsByNombreAndTerritorial(String nombre, Territorial territorial);
}
