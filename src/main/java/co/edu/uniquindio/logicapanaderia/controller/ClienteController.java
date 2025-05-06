package co.edu.uniquindio.logicapanaderia.controller;

import co.edu.uniquindio.logicapanaderia.model.Cliente;
import co.edu.uniquindio.logicapanaderia.repository.ClienteRepository;
import co.edu.uniquindio.logicapanaderia.service.ClienteService;
import co.edu.uniquindio.logicapanaderia.service.StreamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    @Autowired private ClienteService clienteService;
    @Autowired private ClienteRepository repo;
    @Autowired private StreamingService streamingService;

    @GetMapping
    public ResponseEntity<List<Cliente>> obtenerClientes() {
        List<Cliente> lista = clienteService.listarClientes();
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable Long id, @RequestBody Cliente cliente) {
        cliente.setId(Math.toIntExact(id));
        Cliente actualizado = clienteService.actualizarCliente(cliente);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(Math.toIntExact(id));
        return ResponseEntity.ok(true);
    }

    @GetMapping("/count")
    public long countAll() {
        return repo.count();
    }

    @GetMapping("/stream")
    public SseEmitter stream() {
        return streamingService.createEmitter("cliente");
    }

    @PostMapping
    public Cliente create(@RequestBody Cliente c) {
        Cliente saved = repo.save(c);
        streamingService.publishClient(saved);
        return saved;
    }

}
