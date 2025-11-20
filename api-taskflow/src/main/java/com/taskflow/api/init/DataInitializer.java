package com.taskflow.api.init;

import com.taskflow.api.model.Rol;
import com.taskflow.api.model.Usuario;
import com.taskflow.api.repository.RolRepository;
import com.taskflow.api.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepo, RolRepository rolRepo, PasswordEncoder encoder) {
        return args -> {
            // 1. Asegurar Roles
            crearRolSiNoExiste(rolRepo, "Administrador");
            crearRolSiNoExiste(rolRepo, "Miembro");

            // 2. Crear Usuario Admin
            if (usuarioRepo.findByEmail("admin@taskflow.com").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNombre("Super Admin");
                admin.setEmail("admin@taskflow.com");
                // Contraseña Admin$9 encriptada
                admin.setContrasenaHash(encoder.encode("Admin$9")); 
                
                // Buscamos el rol y lo asignamos
                Optional<Rol> rolAdmin = rolRepo.findByNombreRol("Administrador");
                if (rolAdmin.isPresent()) {
                    admin.setRol(rolAdmin.get());
                    usuarioRepo.save(admin);
                    System.out.println(">>> Usuario Administrador creado correctamente.");
                }
            }
        };
    }

    private void crearRolSiNoExiste(RolRepository repo, String nombre) {
        if (repo.findByNombreRol(nombre).isEmpty()) {
            Rol rol = new Rol();
            rol.setNombreRol(nombre);
            repo.save(rol);
        }
    }
}