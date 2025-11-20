package com.taskflow.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Roles\"")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Integer rolId;

    @Column(name = "nombre_rol", unique = true, nullable = false)
    private String nombreRol;

    // --- Getters y Setters Manuales ---
    public Integer getRolId() { return rolId; }
    public void setRolId(Integer rolId) { this.rolId = rolId; }
    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }
}