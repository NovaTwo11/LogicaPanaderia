package co.edu.uniquindio.logicapanaderia.controller;

import co.edu.uniquindio.logicapanaderia.model.LoginRequest;
import co.edu.uniquindio.logicapanaderia.model.Administrador;
import co.edu.uniquindio.logicapanaderia.repository.AdministradorRepository;
import co.edu.uniquindio.logicapanaderia.service.BitacoraService;
import jakarta.servlet.http.Cookie;
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

    @Autowired
    private BitacoraService bitacoraService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest req,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            // 1) Autenticación (aquí Spring validará el flag 'enabled' internamente)
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getContrasena());
            Authentication auth = authManager.authenticate(authToken);

            // 2) Guardar en SecurityContext y crear sesión
            SecurityContextHolder.getContext().setAuthentication(auth);
            request.getSession(true).setMaxInactiveInterval(30 * 60);
            new HttpSessionSecurityContextRepository()
                    .saveContext(SecurityContextHolder.getContext(), request, response);

            // 3) Recuperar Administrador de BD
            Administrador usuario = adminRepo.findByEmail(auth.getName())
                    .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

            // 4) Limpiar contraseña antes de devolver
            usuario.setContrasena(null);

            // 5) Registrar LOGIN en bitácora
            String detalleLogin = String.format(
                    "{\"nombre\":\"%s\",\"apellido\":\"%s\",\"rol\":\"%s\"}",
                    usuario.getNombre(), usuario.getApellido(), usuario.getRol()
            );
            bitacoraService.registrarEvento(usuario.getId(), "LOGIN", detalleLogin);

            // 6) Devolver el Administrador al frontend
            return ResponseEntity.ok(usuario);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas");
        } catch (DisabledException ex) {
            // opcional: mensaje específico si la cuenta está deshabilitada en BD
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Cuenta deshabilitada");
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            // 1) Registrar LOGOUT en bitácora
            Optional<Administrador> opt = adminRepo.findByEmail(auth.getName());
            if (opt.isPresent()) {
                Administrador admin = opt.get();
                String detalleLogout = String.format(
                        "{\"nombre\":\"%s\",\"apellido\":\"%s\",\"rol\":\"%s\"}",
                        admin.getNombre(), admin.getApellido(), admin.getRol()
                );
                bitacoraService.registrarEvento(admin.getId(), "LOGOUT", detalleLogout);
            }
        }

        // 2) Invalidar sesión y borrar la cookie
        request.getSession().invalidate();
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        // 3) Limpiar SecurityContext
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}
