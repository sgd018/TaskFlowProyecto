package com.taskflow.api.repository;

import com.taskflow.api.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    // Spring Data JPA crea la consulta automáticamente por el nombre del método
    Optional<Rol> findByNombreRol(String nombreRol);
}