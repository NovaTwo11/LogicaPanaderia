package co.edu.uniquindio.logicapanaderia.controller;

import co.edu.uniquindio.logicapanaderia.model.LoginRequest;
import co.edu.uniquindio.logicapanaderia.model.Administrador;
import co.edu.uniquindio.logicapanaderia.repository.AdministradorRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private AdministradorRepository adminRepo;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest req,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            // 1) Autenticamos
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getContrasena());
            Authentication auth = authManager.authenticate(authToken);

            // 2) Guardamos en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(auth);

            // 3) Creamos sesión y cookie (Set-Cookie enviado)
            request.getSession(true).setMaxInactiveInterval(30 * 60);

            // 4) ¡Forzamos el guardado del SecurityContext en la sesión!
            HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
            repo.saveContext(SecurityContextHolder.getContext(), request, response);

            // 5) Recuperamos la entidad Administrador y limpiamos la contraseña
            Optional<Administrador> opt = adminRepo.findByEmail(auth.getName());
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Usuario autenticado no encontrado en BD");
            }
            Administrador usuario = opt.get();
            usuario.setContrasena(null);

            return ResponseEntity.ok(usuario);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas");
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al procesar el login");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // Invalida la sesión
        request.getSession().invalidate();
        // Borra la cookie de sesión
        response.addCookie(new jakarta.servlet.http.Cookie("JSESSIONID", null));
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}
