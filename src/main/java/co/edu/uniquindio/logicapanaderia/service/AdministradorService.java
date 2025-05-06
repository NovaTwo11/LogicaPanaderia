package co.edu.uniquindio.logicapanaderia.service;

import co.edu.uniquindio.logicapanaderia.dto.AdministradorDTO;
import co.edu.uniquindio.logicapanaderia.model.Administrador;
import co.edu.uniquindio.logicapanaderia.repository.AdministradorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdministradorService {

    private final AdministradorRepository administradorRepo;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public AdministradorService(AdministradorRepository administradorRepo, BCryptPasswordEncoder encoder) {
        this.administradorRepo = administradorRepo;
        this.encoder = encoder;
    }
    public AdministradorDTO crearAdministrador(AdministradorDTO dto) {
        Administrador admin = new Administrador();

        admin.setNombre(dto.getNombre());
        admin.setApellido(dto.getApellido());
        admin.setEmail(dto.getEmail());
        admin.setTelefono(dto.getTelefono());
        admin.setRol(dto.getRol());
        admin.setActivo(true);
        admin.setFechaCreacion(new Date());
        admin.setContrasena(encoder.encode(dto.getPassword())); // Asegúrate de tener este campo en el DTO

        Administrador adminGuardado = administradorRepo.save(admin);

        return convertirADTO(adminGuardado);
    }
    private AdministradorDTO convertirADTO(Administrador admin) {
        AdministradorDTO dto = new AdministradorDTO();

        dto.setId(admin.getId());
        dto.setNombre(admin.getNombre());
        dto.setApellido(admin.getApellido());
        dto.setEmail(admin.getEmail());
        dto.setTelefono(admin.getTelefono());
        dto.setRol(admin.getRol());
        dto.setActivo(admin.isActivo());
        dto.setFechaCreacion(admin.getFechaCreacion());

        return dto;
    }
    public List<Administrador> listarAdministradores() {
        return administradorRepo.findAll();
    }

    public Optional<Administrador> obtenerAdministrador(long id) {
        return administradorRepo.findById(id);
    }

    public AdministradorDTO login(String email, String contrasena) throws Exception {
        Optional<Administrador> optionalAdmin = administradorRepo.findByEmail(email);

        if (optionalAdmin.isEmpty()) {
            throw new Exception("Correo no registrado");
        }

        Administrador admin = optionalAdmin.get();

        if (!encoder.matches(contrasena, admin.getContrasena())) {
            throw new Exception("Contraseña incorrecta");
        }

        return convertirADTO(admin);
    }


    public Administrador actualizarAdministrador(Administrador admin) {
        Administrador existente = administradorRepo.findById(admin.getId())
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado con ID: " + admin.getId()));

        existente.setNombre(admin.getNombre());
        existente.setApellido(admin.getApellido());
        existente.setEmail(admin.getEmail());
        existente.setTelefono(admin.getTelefono());
        existente.setRol(admin.getRol());
        existente.setActivo(admin.isActivo());

        if (admin.getContrasena() != null && !admin.getContrasena().isBlank()) {
            existente.setContrasena(encoder.encode(admin.getContrasena()));
        }

        return administradorRepo.save(existente);
    }

    public void eliminarAdministrador(Long id) throws Exception {
        // Puedes verificar que el registro exista si lo considerás necesario.
        if (!administradorRepo.existsById(id)) {
            throw new Exception("No existe un administrador con ese ID");
        }
        administradorRepo.eliminarPorId(id);
    }


}
