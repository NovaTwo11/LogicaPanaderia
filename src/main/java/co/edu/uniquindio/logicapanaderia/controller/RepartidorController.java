package co.edu.uniquindio.logicapanaderia.controller;

import co.edu.uniquindio.logicapanaderia.dto.RepartidorDTO;
import co.edu.uniquindio.logicapanaderia.service.RepartidorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repartidores")
@CrossOrigin(origins = "http://localhost:4200")
public class RepartidorController {

    private final RepartidorService service;

    @Autowired
    public RepartidorController(RepartidorService service) {
        this.service = service;
    }

    // Listar todos los repartidores
    @GetMapping
    public ResponseEntity<List<RepartidorDTO>> getAll() {
        return ResponseEntity.ok(service.listarRepartidoresDTO());
    }

    // Crear repartidor
    @PostMapping
    public ResponseEntity<RepartidorDTO> create(@RequestBody RepartidorDTO dto) {
        RepartidorDTO creado = service.crearRepartidorDTO(dto);
        return ResponseEntity.ok(creado);
    }

    // Obtener repartidor por ID
    @GetMapping("/{id}")
    public ResponseEntity<RepartidorDTO> getById(@PathVariable Long id) {
        try {
            RepartidorDTO dto = service.obtenerRepartidorPorId(id);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Actualizar repartidor
    @PutMapping("/{id}")
    public ResponseEntity<RepartidorDTO> update(
            @PathVariable Long id,
            @RequestBody RepartidorDTO dto
    ) {
        try {
            RepartidorDTO actualizado = service.actualizarRepartidorDTO(id, dto);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar repartidor
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.eliminarRepartidor(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
