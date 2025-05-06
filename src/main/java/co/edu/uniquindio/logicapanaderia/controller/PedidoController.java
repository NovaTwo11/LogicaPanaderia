package co.edu.uniquindio.logicapanaderia.controller;

import co.edu.uniquindio.logicapanaderia.dto.PedidoDTO;
import co.edu.uniquindio.logicapanaderia.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:4200")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> obtenerPedidos() {
        List<PedidoDTO> lista = pedidoService.listarPedidos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> obtenerPedido(@PathVariable Long id) {
        Optional<PedidoDTO> pedido = pedidoService.obtenerPedido(id);
        return pedido.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> crearPedido(@RequestBody PedidoDTO pedidoDTO) {
        try {
            PedidoDTO nuevoPedido = pedidoService.crearPedidoDesdeDTO(pedidoDTO); // ✅ cambio aquí
            return ResponseEntity.ok(nuevoPedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // src/main/java/co/edu/uniquindio/logicapanaderia/controller/PedidoController.java
    @PutMapping("/{id}")
    public ResponseEntity<PedidoDTO> actualizarPedido(
            @PathVariable Long id,
            @RequestBody PedidoDTO pedidoDTO
    ) {
        // 1) Asegurarnos de que body.id coincide con path id (o sea null en body → path id)
        if (pedidoDTO.getId() != null && !pedidoDTO.getId().equals(id)) {
            return ResponseEntity
                    .badRequest()
                    .body(null);
        }
        pedidoDTO.setId(id);

        try {
            // 2) Llamada al service
            PedidoDTO actualizado = pedidoService.actualizarPedidoDesdeDTO(pedidoDTO);
            return ResponseEntity.ok(actualizado);
        } catch (NoSuchElementException ex) {
            // Cuando el service no encuentra el pedido
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            // Validaciones de negocio (stock, estado no modificable…)
            return ResponseEntity
                    .badRequest()
                    .body(null);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminarPedido(@PathVariable Long id) {
        try {
            pedidoService.eliminarPedido(id);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

}
