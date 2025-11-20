package com.taskflow.api.controller;

import com.taskflow.api.dto.UsuarioDTO;
import com.taskflow.api.model.Rol;
import com.taskflow.api.model.Usuario;
import com.taskflow.api.repository.RolRepository;
import com.taskflow.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.crypto.password.PasswordEncoder; <-- YA NO LO USAMOS AQUÍ PARA GUARDAR
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;

    // --- NUEVO ENDPOINT: Buscar por email ---
    @GetMapping("/buscar")
    public ResponseEntity<UsuarioDTO> buscarPorEmail(@RequestParam String email) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(convertirADTO(usuario.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> crearUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        if (usuarioRepository.findByEmail(usuarioDTO.getEmail()).isPresent()) {
            // Aquí también devolvemos un JSON de error para ser consistentes (opcional, pero recomendado)
            // Por simplicidad, podemos dejar el badRequest con texto o usar un Map
             return ResponseEntity.badRequest().body("{\"error\": \"El email ya está registrado\"}");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(usuarioDTO.getNombre());
        nuevoUsuario.setEmail(usuarioDTO.getEmail());
        
        // Usamos el hash que viene del frontend (MVC)
        nuevoUsuario.setContrasenaHash(usuarioDTO.getContrasenaHash());

        Optional<Rol> rol = rolRepository.findByNombreRol(usuarioDTO.getRolNombre());
        if (rol.isPresent()) {
            nuevoUsuario.setRol(rol.get());
        } else {
            nuevoUsuario.setRol(rolRepository.findByNombreRol("Miembro").orElse(null));
        }

        // Guardamos y capturamos el objeto guardado (que ya tiene ID)
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
        
        // DEVOLVEMOS EL DTO (Esto genera el JSON { "id": 1, "nombre": "...", ... })
        return ResponseEntity.ok(convertirADTO(usuarioGuardado));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Integer id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return ResponseEntity.ok("Usuario eliminado");
        }
        return ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> editarUsuario(@PathVariable Integer id, @RequestBody UsuarioDTO usuarioDTO) {
        return usuarioRepository.findById(id).map(usuario -> {
            // Actualizamos datos básicos
            usuario.setNombre(usuarioDTO.getNombre());
            usuario.setEmail(usuarioDTO.getEmail());

            // Actualizamos el Rol si viene en el JSON
            if (usuarioDTO.getRolNombre() != null) {
                rolRepository.findByNombreRol(usuarioDTO.getRolNombre())
                             .ifPresent(usuario::setRol);
            }

            // Actualizamos la Contraseña SOLO si viene en el JSON
            // (Así evitas borrar la contraseña si solo editas el nombre)
            if (usuarioDTO.getContrasenaHash() != null && !usuarioDTO.getContrasenaHash().isEmpty()) {
                usuario.setContrasenaHash(usuarioDTO.getContrasenaHash());
            }

            usuarioRepository.save(usuario);
            return ResponseEntity.ok(convertirADTO(usuario));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Convertidor
    private UsuarioDTO convertirADTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setUsuarioId(u.getUsuarioId());
        dto.setNombre(u.getNombre());
        dto.setEmail(u.getEmail());
        dto.setContrasenaHash(u.getContrasenaHash()); // Devolvemos el hash para que el login lo pueda comparar
        if (u.getRol() != null) {
            dto.setRolNombre(u.getRol().getNombreRol());
        }
        return dto;
    }
}