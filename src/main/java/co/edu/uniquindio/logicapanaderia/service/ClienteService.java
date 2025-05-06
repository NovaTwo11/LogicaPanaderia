package co.edu.uniquindio.logicapanaderia.service;

import co.edu.uniquindio.logicapanaderia.model.Cliente;
import co.edu.uniquindio.logicapanaderia.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepo;

    @Autowired
    public ClienteService(ClienteRepository clienteRepo) {
        this.clienteRepo = clienteRepo;
    }

    public Cliente crearCliente(Cliente cliente) {
        cliente.setFechaRegistro(LocalDate.now());
        cliente.setTotalCompras(0.0);
        return clienteRepo.save(cliente);
    }

    public List<Cliente> listarClientes() {
        return clienteRepo.findAll();
    }

    public Optional<Cliente> obtenerCliente(long id) {
        return clienteRepo.findById((long) id);
    }

    public Cliente actualizarCliente(Cliente cliente) {
        return clienteRepo.save(cliente);
    }

    @Transactional
    public void eliminarCliente(long id) {
        Cliente cliente = clienteRepo.findByIdWithPedidos(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        clienteRepo.delete(cliente);
    }


}