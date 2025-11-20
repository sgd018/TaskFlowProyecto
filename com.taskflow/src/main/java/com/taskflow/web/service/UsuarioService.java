package com.taskflow.web.service;

import com.taskflow.web.dto.UsuarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inyectamos el encriptador

    @Value("${api.url}")
    private String apiUrl;

    public List<UsuarioDTO> obtenerTodos() {
        UsuarioDTO[] usuarios = restTemplate.getForObject(apiUrl + "/usuarios", UsuarioDTO[].class);
        return Arrays.asList(usuarios);
    }

    // --- NUEVO: Buscar por email para el Login ---
    public UsuarioDTO buscarPorEmail(String email) {
        try {
            // Llamamos a un endpoint de la API que busca por email
            // Nota: Usamos postForObject para enviar el email en el cuerpo o get con param
            // Vamos a asumir un endpoint GET de búsqueda en la API
            String url = apiUrl + "/usuarios/buscar?email=" + email;
            return restTemplate.getForObject(url, UsuarioDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null; // Usuario no encontrado
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void guardarUsuario(UsuarioDTO usuario) {
        // 1. Encriptación (Se mantiene igual)
        if (usuario.getContrasenaHash() != null && !usuario.getContrasenaHash().isEmpty()) {
            String hash = passwordEncoder.encode(usuario.getContrasenaHash());
            usuario.setContrasenaHash(hash);
        } else {
            usuario.setContrasenaHash(passwordEncoder.encode("123456"));
        }

        // 2. Enviar a la API
        // CORRECTO: Ahora esperamos un UsuarioDTO.class de vuelta porque la API devuelve JSON
        restTemplate.postForObject(apiUrl + "/usuarios", usuario, UsuarioDTO.class);
    }

    public void eliminarUsuario(Integer id) {
        restTemplate.delete(apiUrl + "/usuarios/" + id);
    }
}