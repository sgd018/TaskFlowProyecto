package com.taskflow.web.controller;

import com.taskflow.web.dto.UsuarioDTO;
import com.taskflow.web.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AppController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- Login: Página de entrada ---
    @GetMapping("/")
    public String login() {
        return "login";
    }

    // --- Login: Procesar credenciales ---
    @PostMapping("/procesar-login")
    public String procesarLogin(@RequestParam String email,
                                @RequestParam String password,
                                HttpSession session,
                                Model model) {
        // 1. Buscar usuario en la API
        UsuarioDTO usuario = usuarioService.buscarPorEmail(email);

        // 2. Comprobar contraseña (encriptada)
        if (usuario != null && passwordEncoder.matches(password, usuario.getContrasenaHash())) {
            session.setAttribute("usuarioLogueado", usuario);
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Credenciales incorrectas");
            return "login";
        }
    }

    // --- Dashboard (SOLO ESTE, borra cualquier otro que tengas) ---
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        // 1. Recuperamos el usuario que guardamos en sesión durante el login
        UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuarioLogueado");
        
        // 2. Lo pasamos a la vista con el nombre "usuario"
        model.addAttribute("usuario", usuario);
        
        return "dashboard";
    }

    // --- Listar Usuarios ---
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/";
        model.addAttribute("listaUsuarios", usuarioService.obtenerTodos());
        return "usuarios";
    }

    // --- Guardar Usuario (Crea y Encripta) ---
    @PostMapping("/guardar-usuario")
    public String guardarUsuario(@ModelAttribute UsuarioDTO usuario, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        // Asignar rol por defecto si no viene
        if (usuario.getRolNombre() == null) usuario.setRolNombre("Miembro");
        
        try {
            // Intentamos guardar llamando a la API
            usuarioService.guardarUsuario(usuario);
            redirectAttrs.addFlashAttribute("mensaje", "Usuario invitado correctamente.");
            
        } catch (Exception e) {
            // SI FALLA (ej: email duplicado o API caída):
            e.printStackTrace(); // Imprimimos el error en la consola de Eclipse/Docker para que tú lo veas
            
            // Guardamos el mensaje de error para mostrarlo en la web (opcional)
            redirectAttrs.addFlashAttribute("error", "No se pudo guardar el usuario. Verifique que el email no exista ya.");
            
            // IMPORTANTE: En vez de la pantalla blanca, volvemos a la gestión de usuarios
            return "redirect:/usuarios"; 
            // O si prefieres que vuelva al formulario: return "redirect:/invitar-usuario";
        }
        
        return "redirect:/usuarios";
    }

    // --- Formulario Invitar ---
    @GetMapping("/invitar-usuario")
    public String formInvitar(Model model) {
        model.addAttribute("usuario", new UsuarioDTO());
        return "invitar-usuario";
    }

    // --- Eliminar Usuario ---
    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Integer id) {
        usuarioService.eliminarUsuario(id);
        return "redirect:/usuarios";
    }
    
    // --- Mostrar Formulario de Edición ---
    @GetMapping("/usuarios/editar/{id}")
    public String formEditar(@PathVariable Integer id, Model model) {
        // 1. Pedimos el usuario actual a la API
        UsuarioDTO usuario = usuarioService.obtenerPorId(id);
        
        // 2. Lo pasamos a la vista para que los inputs salgan rellenos
        model.addAttribute("usuario", usuario);
        
        return "editar-usuario"; // Crea este HTML en el siguiente paso
    }

    // --- Procesar la Edición ---
    @PostMapping("/actualizar-usuario")
    public String actualizarUsuario(@ModelAttribute UsuarioDTO usuario, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        try {
            // Enviamos los cambios a la API
            usuarioService.actualizarUsuario(usuario);
            redirectAttrs.addFlashAttribute("mensaje", "Usuario actualizado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttrs.addFlashAttribute("error", "Error al actualizar el usuario.");
        }
        return "redirect:/usuarios";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Esto borra todos los datos de la sesión (el usuario logueado)
        session.invalidate(); 
        return "redirect:/"; // Nos devuelve a la pantalla de login
    }
}