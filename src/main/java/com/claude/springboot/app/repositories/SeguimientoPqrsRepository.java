package com.claude.springboot.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claude.springboot.app.entities.Pqrs;
import com.claude.springboot.app.entities.SeguimientoPqrs;

@Repository
public interface SeguimientoPqrsRepository extends JpaRepository<SeguimientoPqrs, Long> {
    List<SeguimientoPqrs> findByPqrsOrderByFechaCreacionDesc(Pqrs pqrs);
}
