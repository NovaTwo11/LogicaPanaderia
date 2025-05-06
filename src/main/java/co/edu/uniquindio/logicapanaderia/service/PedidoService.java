package co.edu.uniquindio.logicapanaderia.service;

import co.edu.uniquindio.logicapanaderia.dto.PedidoDTO;
import co.edu.uniquindio.logicapanaderia.dto.PedidoProductoDTO;
import co.edu.uniquindio.logicapanaderia.model.*;
import co.edu.uniquindio.logicapanaderia.repository.ClienteRepository;
import co.edu.uniquindio.logicapanaderia.repository.PedidoRepository;
import co.edu.uniquindio.logicapanaderia.repository.ProductoRepository;
import co.edu.uniquindio.logicapanaderia.repository.RepartidorRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PedidoService {
    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository pedidoRepo;
    private final ClienteRepository clienteRepo;
    private final RepartidorRepository repartidorRepo;
    private final ProductoRepository productoRepo;
    private final ReporteService reporteService;
    private final StreamingService streamingService;

    @Autowired
    public PedidoService(
            PedidoRepository pedidoRepo,
            ClienteRepository clienteRepo,
            RepartidorRepository repartidorRepo,
            ProductoRepository productoRepo,
            ReporteService reporteService,
            StreamingService streamingService
    ) {
        this.pedidoRepo = pedidoRepo;
        this.clienteRepo = clienteRepo;
        this.repartidorRepo = repartidorRepo;
        this.productoRepo = productoRepo;
        this.reporteService = reporteService;
        this.streamingService = streamingService;
    }

    /**
     * Mapea un PedidoDTO a la entidad Pedido completa.
     */
    public Pedido mapToEntity(PedidoDTO dto) {
        Pedido p = new Pedido();
        if (dto.getId() != null) p.setId(dto.getId());
        p.setFecha(dto.getFecha());
        p.setEstado(EstadoPedido.desdeValor(dto.getEstado()));
        p.setDireccionEntrega(dto.getDireccionEntrega());
        p.setMetodoPago(dto.getMetodoPago());
        p.setNotas(dto.getNotas());
        if (dto.getClienteId() != null) {
            Cliente c = new Cliente(); c.setId(dto.getClienteId().intValue()); p.setCliente(c);
        }
        if (dto.getRepartidorId() != null) {
            Repartidor r = new Repartidor(); r.setId(dto.getRepartidorId().intValue()); p.setRepartidor(r);
        }
        if (dto.getProductos() != null) {
            p.setProductos(
                    dto.getProductos().stream()
                            .map(ppdto -> {
                                PedidoProducto pp = new PedidoProducto();
                                pp.setProductoId(ppdto.getProductoId());
                                pp.setNombre(ppdto.getNombre());
                                pp.setCantidad(ppdto.getCantidad());
                                pp.setPrecioUnitario(ppdto.getPrecioUnitario());
                                pp.setSubtotal(ppdto.getSubtotal());
                                pp.setPedido(p);
                                return pp;
                            })
                            .collect(Collectors.toList())
            );
        }
        return p;
    }

    /**
     * Crea un pedido a partir de DTO y notifica SSE.
     */
    public PedidoDTO crearPedidoDesdeDTO(PedidoDTO dto) {
        Pedido pedido = mapToEntity(dto);
        PedidoDTO result = procesarYGuardarPedido(pedido);
        // Publicar evento SSE
        streamingService.publishOrder(pedidoRepo.getReferenceById(result.getId()));
        return result;
    }

    public PedidoDTO crearPedido(Pedido pedido) {
        PedidoDTO result = procesarYGuardarPedido(pedido);
        streamingService.publishOrder(pedidoRepo.getReferenceById(result.getId()));
        return result;
    }

    /**
     * Actualiza un pedido existente a partir de DTO y notifica SSE.
     */
    public PedidoDTO actualizarPedidoDesdeDTO(PedidoDTO dto) {
        Long id = dto.getId();
        if (id == null || pedidoRepo.findById(id).isEmpty()) {
            throw new IllegalArgumentException("No se encontró el pedido con ID: " + id);
        }
        Pedido pedido = mapToEntity(dto);
        pedido.setId(id);
        PedidoDTO result = procesarYGuardarPedido(pedido);
        streamingService.publishOrder(pedidoRepo.getReferenceById(id));
        return result;
    }

    private PedidoDTO procesarYGuardarPedido(Pedido pedido) {
        logger.info("Procesando pedido: {}", pedido);

        validarStockYActualizar(pedido);
        pedido.setTotal(calcularTotal(pedido));
        Pedido saved = pedidoRepo.save(pedido);

        if (saved.getEstado() == EstadoPedido.ENTREGADO) {
            actualizarClienteYRepartidor(saved);
            generarReportes(saved);
        }

        Pedido cargado = pedidoRepo.findByIdWithRelations(saved.getId())
                .orElseThrow(() -> new RuntimeException("Error recargando pedido"));
        return convertirAPedidoDTO(cargado);
    }

    private void validarStockYActualizar(Pedido pedido) {
        if (pedido.getProductos() == null) return;
        for (PedidoProducto pp : pedido.getProductos()) {
            Producto prod = productoRepo.findById(pp.getProductoId().longValue())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no existe: " + pp.getProductoId()));
            if (pp.getCantidad() > prod.getStock()) {
                throw new IllegalArgumentException("Stock insuficiente para: " + prod.getNombre());
            }
            prod.setStock(prod.getStock() - pp.getCantidad());
            productoRepo.save(prod);
            pp.setPedido(pedido);
        }
    }

    private double calcularTotal(Pedido pedido) {
        if (pedido.getProductos() == null) return 0;
        return pedido.getProductos().stream()
                .mapToDouble(PedidoProducto::getSubtotal)
                .sum();
    }

    private void actualizarClienteYRepartidor(Pedido pedido) {
        if (pedido.getCliente() != null) {
            clienteRepo.findById(pedido.getCliente().getId().longValue())
                    .ifPresent(c -> {
                        c.setTotalCompras(c.getTotalCompras() + pedido.getTotal());
                        clienteRepo.save(c);
                    });
        }
        if (pedido.getRepartidor() != null) {
            repartidorRepo.findById(pedido.getRepartidor().getId().longValue())
                    .ifPresent(r -> {
                        r.setPedidosEntregados(r.getPedidosEntregados() + 1);
                        repartidorRepo.save(r);
                    });
        }
    }

    private void generarReportes(Pedido pedido) {
        LocalDateTime fin = LocalDateTime.now();
        LocalDateTime inicio = fin.minusDays(7);
        reporteService.generarReporte(inicio, fin, TipoReporte.POR_PRODUCTO);
    }

    public List<PedidoDTO> listarPedidos() {
        return pedidoRepo.findAll(Sort.by(Sort.Order.desc("fecha")))
                .stream().map(this::convertirAPedidoDTO)
                .collect(Collectors.toList());
    }

    public Optional<PedidoDTO> obtenerPedido(long id) {
        return pedidoRepo.findById(id).map(this::convertirAPedidoDTO);
    }

    public void eliminarPedido(long id) {
        pedidoRepo.deleteById(id);
    }

    // ----- Conversión Entidad <-> DTO -----
    private PedidoDTO convertirAPedidoDTO(Pedido p) {
        List<PedidoProductoDTO> detalles = p.getProductos() == null ? null :
                p.getProductos().stream().map(this::convertirDetalle).collect(Collectors.toList());
        String nombreCli = obtenerNombreCliente(p.getCliente());
        String nombreRep = obtenerNombreRepartidor(p.getRepartidor());
        return new PedidoDTO(
                p.getId(),
                p.getCliente() != null ? p.getCliente().getId().longValue() : null,
                nombreCli,
                p.getFecha(),
                p.getEstado().getValor(),
                detalles,
                p.getRepartidor() != null ? p.getRepartidor().getId().longValue() : null,
                nombreRep,
                p.getTotal(),
                p.getDireccionEntrega(),
                p.getMetodoPago(),
                p.getNotas()
        );
    }

    private PedidoProductoDTO convertirDetalle(PedidoProducto pp) {
        return new PedidoProductoDTO(
                pp.getProductoId(), pp.getNombre(), pp.getCantidad(), pp.getPrecioUnitario(), pp.getSubtotal()
        );
    }

    private String obtenerNombreCliente(Cliente cliente) {
        if (cliente == null) return null;
        return clienteRepo.findById(cliente.getId().longValue())
                .map(c -> c.getNombre() + " " + c.getApellido())
                .orElse(null);
    }

    private String obtenerNombreRepartidor(Repartidor repartidor) {
        if (repartidor == null) return null;
        return repartidorRepo.findById(repartidor.getId().longValue())
                .map(r -> r.getNombre() + " " + r.getApellido())
                .orElse(null);
    }

    private Pedido convertirAPedido(PedidoDTO dto) {
        Pedido p = new Pedido();
        p.setId(dto.getId());
        p.setFecha(dto.getFecha());
        p.setEstado(EstadoPedido.desdeValor(dto.getEstado()));
        p.setDireccionEntrega(dto.getDireccionEntrega());
        p.setMetodoPago(dto.getMetodoPago());
        p.setNotas(dto.getNotas());
        p.setTotal(dto.getTotal());
        if (dto.getClienteId() != null) {
            Cliente c = new Cliente(); c.setId(dto.getClienteId().intValue()); p.setCliente(c);
        }
        if (dto.getRepartidorId() != null) {
            Repartidor r = new Repartidor(); r.setId(dto.getRepartidorId().intValue()); p.setRepartidor(r);
        }
        if (dto.getProductos() != null) {
            p.setProductos(dto.getProductos().stream().map(ppdto -> {
                PedidoProducto pp = new PedidoProducto();
                pp.setProductoId(ppdto.getProductoId());
                pp.setNombre(ppdto.getNombre());
                pp.setCantidad(ppdto.getCantidad());
                pp.setPrecioUnitario(ppdto.getPrecioUnitario());
                pp.setSubtotal(ppdto.getSubtotal());
                pp.setPedido(p);
                return pp;
            }).collect(Collectors.toList()));
        }
        return p;
    }
}
