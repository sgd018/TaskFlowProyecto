package com.taskflow.web.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. Obtenemos la sesión (pero no la creamos si no existe)
        HttpSession session = request.getSession(false);

        // 2. Verificamos si hay un usuario logueado
        if (session != null && session.getAttribute("usuarioLogueado") != null) {
            return true; // TIENE PASE: Dejamos que la petición continúe hacia el Controlador
        }

        // 3. Si no hay usuario, redirigimos al Login
        response.sendRedirect("/");
        return false; // BLOQUEADO: La petición muere aquí
    }
}