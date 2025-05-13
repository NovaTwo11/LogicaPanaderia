package co.edu.uniquindio.logicapanaderia.config;

import java.io.IOException;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import co.edu.uniquindio.logicapanaderia.service.BitacoraService;
import co.edu.uniquindio.logicapanaderia.model.Administrador;

@Component
public class LogBitacoraSuccessHandler implements AuthenticationSuccessHandler {

    private final BitacoraService bitacoraService;

    public LogBitacoraSuccessHandler(BitacoraService bitacoraService) {
        this.bitacoraService = bitacoraService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication auth) throws IOException, ServletException {

        // Suponemos que tu UserDetails devuelve un Administrador
        Administrador admin = (Administrador) auth.getPrincipal();
        // Construye detalle JSON con nombre, apellido, rol
        String detalle = String.format(
                "{\"nombre\":\"%s\",\"apellido\":\"%s\",\"rol\":\"%s\"}",
                admin.getNombre(), admin.getApellido(), admin.getRol()
        );
        // Guarda evento LOGIN
        bitacoraService.registrarEvento(admin.getId(), "LOGIN", detalle);

        // Continúa con el flujo normal
        response.sendRedirect("/dashboard");
    }
}
