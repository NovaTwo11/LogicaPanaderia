package co.edu.uniquindio.logicapanaderia.service;

import co.edu.uniquindio.logicapanaderia.model.Administrador;
import co.edu.uniquindio.logicapanaderia.repository.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdministradorRepository adminRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Administrador admin = adminRepo.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado: " + email)
                );

        // Construimos los roles/authorities según tu campo 'rol'
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + admin.getRol().toUpperCase())
        );

        // Retornamos un User (Spring) con email, password y authorities
        return new User(
                admin.getEmail(),
                admin.getContrasena(),  // tu campo cifrado
                admin.isActivo(),       // enabled
                true,  // accountNonExpired
                true,  // credentialsNonExpired
                true,  // accountNonLocked
                authorities
        );
    }
}
