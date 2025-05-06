package co.edu.uniquindio.logicapanaderia.service;

import co.edu.uniquindio.logicapanaderia.dto.RepartidorDTO;
import co.edu.uniquindio.logicapanaderia.model.Repartidor;
import co.edu.uniquindio.logicapanaderia.repository.RepartidorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RepartidorService {

    private final RepartidorRepository repartidorRepo;

    @Autowired
    public RepartidorService(RepartidorRepository repartidorRepo) {
        this.repartidorRepo = repartidorRepo;
    }

    // Convertir de entidad a DTO
    public RepartidorDTO toDTO(Repartidor r) {
        RepartidorDTO dto = new RepartidorDTO();
        dto.setId(r.getId() != null ? r.getId().longValue() : null);
        dto.setNombre(r.getNombre());
        dto.setApellido(r.getApellido());
        dto.setTelefono(r.getTelefono());
        dto.setEmail(r.getEmail());
        dto.setVehiculo(r.getVehiculo());
        dto.setLicencia(r.getLicencia());
        dto.setDisponible(r.isDisponible());
        dto.setPedidosEntregados(r.getPedidosEntregados());
        return dto;
    }

    // Convertir de DTO a entidad
    public Repartidor fromDTO(RepartidorDTO dto) {
        Repartidor r = new Repartidor();
        if (dto.getId() != null) {
            r.setId(dto.getId().intValue());
        }
        r.setNombre(dto.getNombre());
        r.setApellido(dto.getApellido());
        r.setTelefono(dto.getTelefono());
        r.setEmail(dto.getEmail());
        r.setVehiculo(dto.getVehiculo());
        r.setLicencia(dto.getLicencia());
        r.setDisponible(dto.getDisponible());
        r.setPedidosEntregados(dto.getPedidosEntregados());
        return r;
    }

    // Crear repartidor desde DTO
    public RepartidorDTO crearRepartidorDTO(RepartidorDTO dto) {
        Repartidor r = fromDTO(dto);
        r.setPedidosEntregados(0);
        Repartidor guardado = repartidorRepo.save(r);
        return toDTO(guardado);
    }

    // Actualizar repartidor
    public RepartidorDTO actualizarRepartidorDTO(Long id, RepartidorDTO dto) {
        Optional<Repartidor> opt = repartidorRepo.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Repartidor no encontrado con ID: " + id);
        }

        Repartidor existente = opt.get();
        existente.setNombre(dto.getNombre());
        existente.setApellido(dto.getApellido());
        existente.setTelefono(dto.getTelefono());
        existente.setEmail(dto.getEmail());
        existente.setVehiculo(dto.getVehiculo());
        existente.setLicencia(dto.getLicencia());
        existente.setDisponible(dto.getDisponible());
        existente.setPedidosEntregados(dto.getPedidosEntregados());

        Repartidor actualizado = repartidorRepo.save(existente);
        return toDTO(actualizado);
    }

    // Obtener todos los repartidores
    public List<RepartidorDTO> listarRepartidoresDTO() {
        return repartidorRepo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Obtener uno por ID
    public RepartidorDTO obtenerRepartidorPorId(Long id) {
        Optional<Repartidor> opt = repartidorRepo.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Repartidor no encontrado con ID: " + id);
        }
        return toDTO(opt.get());
    }

    // Eliminar por ID
    public void eliminarRepartidor(Long id) {
        Optional<Repartidor> opt = repartidorRepo.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar, repartidor no encontrado con ID: " + id);
        }
        repartidorRepo.deleteById(id);
    }

    // Obtener entidad (opcional, si necesitas en otros servicios)
    public Repartidor obtenerEntidadRepartidor(Long id) {
        return repartidorRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repartidor no encontrado con ID: " + id));
    }
}
