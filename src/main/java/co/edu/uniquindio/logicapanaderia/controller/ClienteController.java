package co.edu.uniquindio.logicapanaderia.controller;

import co.edu.uniquindio.logicapanaderia.model.Cliente;
import co.edu.uniquindio.logicapanaderia.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<Cliente>> obtenerClientes() {
        List<Cliente> lista = clienteService.listarClientes();
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente) {
        Cliente nuevo = clienteService.crearCliente(cliente);
        return ResponseEntity.ok(nuevo);
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

}
