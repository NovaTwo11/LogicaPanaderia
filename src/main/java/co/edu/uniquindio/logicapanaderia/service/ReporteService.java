package co.edu.uniquindio.logicapanaderia.service;

import co.edu.uniquindio.logicapanaderia.dto.ReporteDTO;
import co.edu.uniquindio.logicapanaderia.dto.ReporteDetalleDTO;
import co.edu.uniquindio.logicapanaderia.model.Cliente;
import co.edu.uniquindio.logicapanaderia.model.Pedido;
import co.edu.uniquindio.logicapanaderia.model.PedidoProducto;
import co.edu.uniquindio.logicapanaderia.model.Producto;
import co.edu.uniquindio.logicapanaderia.model.Reporte;
import co.edu.uniquindio.logicapanaderia.model.ReporteDetalle;
import co.edu.uniquindio.logicapanaderia.model.TipoReporte;
import co.edu.uniquindio.logicapanaderia.repository.PedidoRepository;
import co.edu.uniquindio.logicapanaderia.repository.ReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private final ReporteRepository reporteRepo;
    private final PedidoRepository pedidoRepo;
    private final ProductoService productoService;
    private final ClienteService clienteService;

    @Autowired
    public ReporteService(ReporteRepository reporteRepo,
                          PedidoRepository pedidoRepo,
                          ProductoService productoService,
                          ClienteService clienteService) {
        this.reporteRepo      = reporteRepo;
        this.pedidoRepo       = pedidoRepo;
        this.productoService  = productoService;
        this.clienteService   = clienteService;
    }

    /**
     * Genera y persiste un Reporte completo, devolviendo su DTO padre.
     * Si inicio o fin son null, trae todos los pedidos entregados.
     */
    public ReporteDTO generarReporte(LocalDateTime inicio, LocalDateTime fin, TipoReporte tipoReporte) {
        Reporte reporte = new Reporte(inicio, fin, tipoReporte);

        List<Pedido> pedidos = pedidoRepo.obtenerPedidosEntregadosConProductos();

        if (inicio != null && fin != null) {
            pedidos = pedidos.stream()
                    // <-- filtramos por fechaEntrega, no por fecha:
                    .filter(p -> {
                        LocalDateTime fe = p.getFecha();
                        return fe != null
                                && !fe.isBefore(inicio)
                                && !fe.isAfter(fin);
                    })
                    .collect(Collectors.toList());
        }

        List<ReporteDetalle> detallesEntidad =
                (tipoReporte == TipoReporte.POR_PRODUCTO || tipoReporte == TipoReporte.GENERAL)
                        ? generarDetallesPorProductoEntidad(reporte, pedidos)
                        : generarDetallesPorClienteEntidad(reporte, pedidos);

        reporte.setDetalles(detallesEntidad);
        Reporte saved = reporteRepo.save(reporte);

        return convertirAReporteDTO(saved);
    }


    // ------------ VENTAS ------------

    /**
     * Genera datos de reporte de VENTAS (sin persistir), con filtro opcional de fechas y método de pago.
     */
    public List<ReporteDetalleDTO> generarReporteVentas(
            LocalDateTime inicio,
            LocalDateTime fin,
            TipoReporte tipo,
            String metodoPago
    ) {
        List<Pedido> pedidos = pedidoRepo.obtenerPedidosEntregadosConProductos();

        if (inicio != null && fin != null) {
            pedidos = pedidos.stream()
                    .filter(p -> {
                        LocalDateTime fe = p.getFecha();
                        return fe != null
                                && !fe.isBefore(inicio)
                                && !fe.isAfter(fin);
                    })
                    .collect(Collectors.toList());
        }

        if (metodoPago != null && !metodoPago.equalsIgnoreCase("TODOS")) {
            pedidos = pedidos.stream()
                    .filter(p -> metodoPago.equalsIgnoreCase(p.getMetodoPago()))
                    .collect(Collectors.toList());
        }

        // 4) Agrupar según tipo de reporte
        if (tipo == TipoReporte.GENERAL) {
            return pedidos.stream()
                    .collect(Collectors.groupingBy(Pedido::getMetodoPago))
                    .entrySet().stream()
                    .map(e -> {
                        String met = e.getKey();
                        List<Pedido> list = e.getValue();
                        long numPed = list.size();
                        double total = list.stream()
                                .flatMap(p -> p.getProductos().stream())
                                .mapToDouble(PedidoProducto::getSubtotal)
                                .sum();
                        double ticket = numPed > 0 ? total / numPed : 0.0;
                        return new ReporteDetalleDTO(
                                null, null, null,
                                total,
                                null, null,
                                met,
                                null, null, null,
                                numPed, ticket,
                                null, null
                        );
                    })
                    .collect(Collectors.toList());

        } else if (tipo == TipoReporte.POR_PRODUCTO) {
            Map<Integer, Long> byProd = pedidos.stream()
                    .flatMap(p -> p.getProductos().stream())
                    .collect(Collectors.groupingBy(
                            PedidoProducto::getProductoId,
                            Collectors.summingLong(PedidoProducto::getCantidad)
                    ));

            return byProd.entrySet().stream()
                    .map(en -> {
                        Producto pr = productoService.obtenerProducto(en.getKey().longValue())
                                .orElseThrow();
                        return new ReporteDetalleDTO(
                                pr.getId(), pr.getNombre(), en.getValue(), null,
                                null, null, null,
                                pr.getStock(), pr.getPrecio(), null,
                                null, null,
                                pr.getCategoria(), null
                        );
                    })
                    .collect(Collectors.toList());

        } else { // POR_CLIENTE
            Map<Long, List<PedidoProducto>> byCliente = pedidos.stream()
                    .collect(Collectors.groupingBy(
                            p -> p.getCliente().getId().longValue(),
                            Collectors.flatMapping(p -> p.getProductos().stream(), Collectors.toList())
                    ));

            return byCliente.entrySet().stream()
                    .map(e -> {
                        Long cliId = e.getKey();
                        Cliente cli = clienteService.obtenerCliente(cliId)
                                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + cliId));
                        List<PedidoProducto> items = e.getValue();
                        long totalUnidades = items.stream()
                                .mapToLong(PedidoProducto::getCantidad).sum();
                        Map<String, Long> sumByProduct = items.stream()
                                .collect(Collectors.groupingBy(
                                        PedidoProducto::getNombre,
                                        Collectors.summingLong(PedidoProducto::getCantidad)
                                ));
                        List<ReporteDetalleDTO.ProductoComprado> detalleList = sumByProduct.entrySet().stream()
                                .map(en2 -> new ReporteDetalleDTO.ProductoComprado(en2.getKey(), en2.getValue().intValue()))
                                .collect(Collectors.toList());
                        return new ReporteDetalleDTO(
                                null, null, totalUnidades, null,
                                cli.getId().intValue(), cli.getNombre(),
                                null, null, null, detalleList,
                                null, null, null, null
                        );
                    })
                    .collect(Collectors.toList());
        }
    }

    // ------------ PRODUCTOS ------------

    /**
     * Genera datos de reporte de PRODUCTOS (sin persistir), con filtro opcional de fechas.
     */
    public List<ReporteDetalleDTO> generarReporteProductos(
            LocalDateTime inicio,
            LocalDateTime fin,
            TipoReporte tipo
    ) {
        List<Pedido> pedidos = pedidoRepo.obtenerPedidosEntregadosConProductos();

        if (inicio != null && fin != null) {
            pedidos = pedidos.stream()
                    .filter(p -> {
                        LocalDateTime fe = p.getFecha();
                        return fe != null
                                && !fe.isBefore(inicio)
                                && !fe.isAfter(fin);
                    })
                    .collect(Collectors.toList());
        }


        if (tipo == TipoReporte.GENERAL) {
            Map<String, Long> byCat = pedidos.stream()
                    .flatMap(p -> p.getProductos().stream())
                    .collect(Collectors.groupingBy(
                            pp -> productoService.obtenerProducto(pp.getProductoId().longValue())
                                    .orElseThrow()
                                    .getCategoria(),
                            Collectors.summingLong(PedidoProducto::getCantidad)
                    ));
            return byCat.entrySet().stream()
                    .map(e -> new ReporteDetalleDTO(
                            null, null, null, null,
                            null, null, null,
                            null, null, null,
                            null, null,
                            e.getKey(), e.getValue()
                    ))
                    .collect(Collectors.toList());

        } else if (tipo == TipoReporte.POR_PRODUCTO) {
            Map<Integer, Long> byProd = pedidos.stream()
                    .flatMap(p -> p.getProductos().stream())
                    .collect(Collectors.groupingBy(
                            PedidoProducto::getProductoId,
                            Collectors.summingLong(PedidoProducto::getCantidad)
                    ));
            return byProd.entrySet().stream()
                    .map(e -> {
                        Producto pr = productoService.obtenerProducto(e.getKey().longValue())
                                .orElseThrow();
                        return new ReporteDetalleDTO(
                                pr.getId(), pr.getNombre(), e.getValue(), null,
                                null, null, null,
                                pr.getStock(), pr.getPrecio(), null,
                                null, null,
                                pr.getCategoria(), null
                        );
                    })
                    .collect(Collectors.toList());

        } else { // POR_CLIENTE
            Map<Long, List<PedidoProducto>> byCliente = pedidos.stream()
                    .collect(Collectors.groupingBy(
                            p -> p.getCliente().getId().longValue(),
                            Collectors.flatMapping(p -> p.getProductos().stream(), Collectors.toList())
                    ));
            return byCliente.entrySet().stream()
                    .map(e -> {
                        Long cliId = e.getKey();
                        Cliente cli = clienteService.obtenerCliente(cliId)
                                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + cliId));
                        List<PedidoProducto> items = e.getValue();
                        long totalUnidades = items.stream()
                                .mapToLong(PedidoProducto::getCantidad).sum();
                        Map<String, Long> sumByProduct = items.stream()
                                .collect(Collectors.groupingBy(
                                        PedidoProducto::getNombre,
                                        Collectors.summingLong(PedidoProducto::getCantidad)
                                ));
                        List<ReporteDetalleDTO.ProductoComprado> detalleList = sumByProduct.entrySet().stream()
                                .map(en2 -> new ReporteDetalleDTO.ProductoComprado(en2.getKey(), en2.getValue().intValue()))
                                .collect(Collectors.toList());
                        return new ReporteDetalleDTO(
                                null, null, totalUnidades, null,
                                cli.getId().intValue(), cli.getNombre(),
                                null, null, null, detalleList,
                                null, null, null, null
                        );
                    })
                    .collect(Collectors.toList());
        }
    }

    /**
     * Genera la lista de ReporteDetalle para un Reporte,
     * agrupando los pedidos por producto.
     */
    private List<ReporteDetalle> generarDetallesPorProductoEntidad(Reporte reporte, List<Pedido> pedidos) {
        // Mapas para acumular cantidad vendida y total generado por productoId
        Map<Long, Long> cantidadMap = new HashMap<>();
        Map<Long, Double> totalMap = new HashMap<>();

        // Recorremos cada pedido y sus productos para sumar
        for (Pedido pedido : pedidos) {
            for (PedidoProducto pp : pedido.getProductos()) {
                Long prodId = pp.getProductoId().longValue();
                cantidadMap.merge(prodId, pp.getCantidad().longValue(), Long::sum);
                totalMap.merge(prodId, pp.getSubtotal(), Double::sum);
            }
        }

        // Convertimos cada entrada en un ReporteDetalle
        return cantidadMap.entrySet().stream()
                .map(entry -> {
                    Long prodId = entry.getKey();
                    Long cantidadVendida = entry.getValue();
                    Double totalGenerado = totalMap.get(prodId);

                    // Obtenemos la entidad Producto
                    Producto producto = productoService.obtenerProducto(prodId)
                            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + prodId));

                    // Creamos el ReporteDetalle: no lleva cliente
                    return new ReporteDetalle(
                            reporte,          // el reporte padre
                            producto,         // la entidad Producto
                            cantidadVendida,  // cantidad total vendida
                            totalGenerado,    // total monetario generado
                            null              // sin cliente
                    );
                })
                .collect(Collectors.toList());
    }

    private List<ReporteDetalle> generarDetallesPorClienteEntidad(Reporte reporte, List<Pedido> pedidos) {
        Map<Long, Long> cantidadMap = new HashMap<>();
        Map<Long, Double> totalMap = new HashMap<>();

        for (Pedido pedido : pedidos) {
            Long cliId = pedido.getCliente().getId().longValue();
            for (PedidoProducto pp : pedido.getProductos()) {
                cantidadMap.merge(cliId, pp.getCantidad().longValue(), Long::sum);
                totalMap.merge(cliId, pp.getSubtotal(), Double::sum);
            }
        }

        return cantidadMap.keySet().stream()
                .map(cliId -> {
                    Cliente cliente = clienteService.obtenerCliente(cliId)
                            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + cliId));
                    return new ReporteDetalle(
                            reporte,
                            null,
                            cantidadMap.get(cliId),
                            totalMap.get(cliId),
                            cliente
                    );
                })
                .collect(Collectors.toList());
    }

    private ReporteDTO convertirAReporteDTO(Reporte reporte) {
        List<ReporteDetalleDTO> dtoDetalles = reporte.getDetalles().stream()
                .map(d -> new ReporteDetalleDTO(
                        d.getProducto() != null ? d.getProducto().getId() : null,
                        d.getProducto() != null ? d.getProducto().getNombre() : null,
                        d.getCantidadVendida(),
                        d.getTotalGenerado(),
                        d.getCliente() != null ? d.getCliente().getId() : null,
                        d.getCliente() != null ? d.getCliente().getNombre() : null,
                        null,       // metodoPago
                        null,       // stock
                        null,       // precioUnitario
                        null,       // productosComprados
                        null,       // numeroPedidos
                        null,       // ticketPromedio
                        null,       // categoria
                        null        // unidadesVendidasTotales
                ))
                .collect(Collectors.toList());

        ReporteDTO dto = new ReporteDTO();
        dto.setId(reporte.getId());
        dto.setFechaGeneracion(reporte.getFechaGeneracion());
        dto.setPeriodoInicio(reporte.getPeriodoInicio());
        dto.setPeriodoFin(reporte.getPeriodoFin());
        dto.setTipoReporte(reporte.getTipoReporte().name());
        dto.setDetalles(dtoDetalles);
        return dto;
    }
}
