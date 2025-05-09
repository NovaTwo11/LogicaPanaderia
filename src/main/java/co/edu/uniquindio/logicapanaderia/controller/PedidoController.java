package co.edu.uniquindio.logicapanaderia.controller;

import co.edu.uniquindio.logicapanaderia.dto.PedidoDTO;
import co.edu.uniquindio.logicapanaderia.model.Pedido;
import co.edu.uniquindio.logicapanaderia.repository.PedidoRepository;
import co.edu.uniquindio.logicapanaderia.service.PedidoService;
import co.edu.uniquindio.logicapanaderia.service.StreamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:4200")
public class PedidoController {

    @Autowired private PedidoService pedidoService;
    @Autowired private PedidoRepository repo;
    @Autowired private StreamingService streamingService;

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
            PedidoDTO nuevoPedido = pedidoService.crearPedidoDesdeDTO(pedidoDTO);
            streamingService.publishOrder(
                    pedidoService.mapToEntity(nuevoPedido)
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPedido(
            @PathVariable Long id,
            @RequestBody PedidoDTO pedidoDTO
    ) {
        // 1. Validar ID
        if (pedidoDTO.getId() != null && !pedidoDTO.getId().equals(id)) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "El ID del body no coincide con el path"));
        }
        pedidoDTO.setId(id);

        // 2. Conservar fecha y total
        Optional<PedidoDTO> existenteOpt = pedidoService.obtenerPedido(id);
        if (existenteOpt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Pedido con ID " + id + " no existe"));
        }
        PedidoDTO existente = existenteOpt.get();
        pedidoDTO.setFecha(existente.getFecha());
        pedidoDTO.setTotal(existente.getTotal());

        try {
            // 3. Actualización
            PedidoDTO actualizado = pedidoService.actualizarPedidoDesdeDTO(pedidoDTO);
            return ResponseEntity.ok(actualizado);

        } catch (NoSuchElementException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));

        } catch (IllegalArgumentException | IllegalStateException ex) {
            // Aquí devolvemos el mensaje de la excepción para que el cliente lo reciba
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));

        } catch (Exception ex) {
            // Cualquier otro error inesperado
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        try {
            pedidoService.eliminarPedido(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/count/today")
    public ResponseEntity<Long> countToday() {
        LocalDate today = LocalDate.now();
        long count = repo.countByFechaBetween(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );
        return ResponseEntity.ok(count);
    }

    @GetMapping("/sum/monthly")
    public ResponseEntity<Double> sumMonthlySales() {
        YearMonth ym = YearMonth.now();
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.plusMonths(1).atDay(1);
        double total = repo.sumTotalByFechaBetween(
                start.atStartOfDay(),
                end.atStartOfDay()
        );
        return ResponseEntity.ok(total);
    }

    @GetMapping("/stream")
    public SseEmitter stream() {
        return streamingService.createEmitter("pedido");
    }
}
