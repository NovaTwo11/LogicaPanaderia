package co.edu.uniquindio.logicapanaderia.controller;

import co.edu.uniquindio.logicapanaderia.dto.AdministradorDTO;
import co.edu.uniquindio.logicapanaderia.dto.LoginDTO;
import co.edu.uniquindio.logicapanaderia.model.Administrador;
import co.edu.uniquindio.logicapanaderia.service.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/administradores")
@CrossOrigin(origins = "http://localhost:4200") // Ajusta el origen según tus necesidades
public class AdministradorController {

    @Autowired
    private AdministradorService administradorService;

    // Método auxiliar para mapear Administrador a AdministradorDTO
    private AdministradorDTO convertToDTO(Administrador admin) {
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

    @GetMapping
    public ResponseEntity<List<AdministradorDTO>> obtenerAdministradores() {
        List<Administrador> lista = administradorService.listarAdministradores();
        List<AdministradorDTO> dtos = lista.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/crear")
    public ResponseEntity<AdministradorDTO> crearAdministrador(@RequestBody AdministradorDTO dto) {
        AdministradorDTO adminGuardado = administradorService.crearAdministrador(dto);
        return ResponseEntity.ok(adminGuardado);
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            AdministradorDTO dto = administradorService.login(loginDTO.getEmail(), loginDTO.getPassword());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<AdministradorDTO> actualizarAdministrador(@PathVariable Long id, @RequestBody Administrador admin) {
        admin.setId(id); // Establecer el ID a partir de la URL
        Administrador actualizado = administradorService.actualizarAdministrador(admin);
        AdministradorDTO dto = convertToDTO(actualizado);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAdministrador(@PathVariable Long id) {
        try {
            administradorService.eliminarAdministrador(id);
            return ResponseEntity.noContent().build(); // 204 No Content si todo sale bien
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Administrador no encontrado con ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el administrador");
        }
    }
}
