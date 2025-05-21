package com.claude.springboot.app.security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claude.springboot.app.security.entities.Empresa;
import com.claude.springboot.app.security.entities.Territorial;

@Repository
public interface TerritorialRepository extends JpaRepository<Territorial, Long> {
    boolean existsByCodigoAndEmpresa(String codigo, Empresa empresa);
}
