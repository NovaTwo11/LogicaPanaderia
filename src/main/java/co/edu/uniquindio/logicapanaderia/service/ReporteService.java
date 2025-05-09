package co.edu.uniquindio.logicapanaderia.service;

import co.edu.uniquindio.logicapanaderia.dto.ReporteDTO;
import co.edu.uniquindio.logicapanaderia.dto.ReporteDetalleDTO;
import co.edu.uniquindio.logicapanaderia.model.*;
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
        this.reporteRepo     = reporteRepo;
        this.pedidoRepo      = pedidoRepo;
        this.productoService = productoService;
        this.clienteService  = clienteService;
    }

    /**
     * Genera y persiste un Reporte completo, devolviendo su DTO padre.
     */
    public ReporteDTO generarReporte(LocalDateTime inicio, LocalDateTime fin, TipoReporte tipoReporte) {
        // Persistir reporte
        Reporte reporte = new Reporte(inicio, fin, tipoReporte);
        // Obtener pedidos entregados
        List<Pedido> pedidos = pedidoRepo.obtenerPedidosEntregadosConProductos();
        // Filtrar rango
        if (inicio != null && fin != null) {
            pedidos = filterByDate(pedidos, inicio, fin);
        }
        // Generar detalles en entidad
        List<ReporteDetalle> detallesEntidad;
        if (tipoReporte == TipoReporte.POR_CLIENTE) {
            detallesEntidad = generarDetallesPorClienteEntidad(reporte, pedidos);
        } else {
            detallesEntidad = generarDetallesPorProductoEntidad(reporte, pedidos);
        }
        reporte.setDetalles(detallesEntidad);
        Reporte saved = reporteRepo.save(reporte);
        return convertirAReporteDTO(saved);
    }

    /**
     * Genera la lista de ReporteDetalle para un Reporte,
     * agrupando los pedidos por producto.
     */
    private List<ReporteDetalle> generarDetallesPorProductoEntidad(Reporte reporte, List<Pedido> pedidos) {
        // Mapas para acumular cantidad vendida y total generado por productoId
        Map<Long, Long> cantidadMap = new HashMap<>();
        Map<Long, Double> totalMap    = new HashMap<>();

        // Sumar cantidades y subtotales
        for (Pedido pedido : pedidos) {
            for (PedidoProducto pp : pedido.getProductos()) {
                Long prodId = pp.getProductoId().longValue();
                cantidadMap.merge(prodId, pp.getCantidad().longValue(), Long::sum);
                totalMap.merge(prodId, pp.getSubtotal(), Double::sum);
            }
        }

        // Crear un ReporteDetalle por cada producto
        return cantidadMap.entrySet().stream()
                .map(entry -> {
                    Long prodId         = entry.getKey();
                    Long cantidadVendida = entry.getValue();
                    Double totalGen      = totalMap.get(prodId);

                    Producto prod = productoService.obtenerProducto(prodId)
                            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + prodId));

                    // reporte, producto, cantidadVendida, totalGenerado, cliente=null
                    return new ReporteDetalle(reporte, prod, cantidadVendida, totalGen, null);
                })
                .collect(Collectors.toList());
    }

    /**
     * Genera la lista de ReporteDetalle para un Reporte,
     * agrupando los pedidos por cliente.
     */
    private List<ReporteDetalle> generarDetallesPorClienteEntidad(Reporte reporte, List<Pedido> pedidos) {
        Map<Long, Long> cantidadMap = new HashMap<>();
        Map<Long, Double> totalMap  = new HashMap<>();

        // Sumar por cliente
        for (Pedido pedido : pedidos) {
            Long cliId = pedido.getCliente().getId().longValue();
            for (PedidoProducto pp : pedido.getProductos()) {
                cantidadMap.merge(cliId, pp.getCantidad().longValue(), Long::sum);
                totalMap.merge(cliId, pp.getSubtotal(), Double::sum);
            }
        }

        // Crear un ReporteDetalle por cada cliente
        return cantidadMap.entrySet().stream()
                .map(entry -> {
                    Long cliId           = entry.getKey();
                    Long totalUnidades   = entry.getValue();
                    Double totalGen      = totalMap.get(cliId);

                    Cliente cli = clienteService.obtenerCliente(cliId)
                            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + cliId));

                    // reporte, producto=null, cantidadVendida=totalUnidades, totalGenerado, cliente
                    return new ReporteDetalle(reporte, null, totalUnidades, totalGen, cli);
                })
                .collect(Collectors.toList());
    }

    /**
     * Genera datos de reporte de VENTAS (sin persistir).
     */
    public List<ReporteDetalleDTO> generarReporteVentas(LocalDateTime inicio,
                                                        LocalDateTime fin,
                                                        TipoReporte tipo,
                                                        String metodoPago) {
        List<Pedido> pedidos = pedidoRepo.obtenerPedidosEntregadosConProductos();
        if (inicio != null && fin != null) {
            pedidos = filterByDate(pedidos, inicio, fin);
        }
        if (metodoPago != null && !metodoPago.equalsIgnoreCase("TODOS")) {
            pedidos = pedidos.stream()
                    .filter(p -> metodoPago.equalsIgnoreCase(p.getMetodoPago()))
                    .collect(Collectors.toList());
        }
        if (tipo == TipoReporte.GENERAL) {
            // Agrupar por método de pago
            return pedidos.stream()
                    .collect(Collectors.groupingBy(Pedido::getMetodoPago))
                    .entrySet().stream()
                    .map(e -> {
                        String met = e.getKey();
                        List<Pedido> list = e.getValue();
                        long numPed = list.size();
                        double totalGen = list.stream()
                                .flatMap(p -> p.getProductos().stream())
                                .mapToDouble(PedidoProducto::getSubtotal)
                                .sum();
                        double ticket = numPed > 0 ? totalGen / numPed : 0.0;
                        ReporteDetalleDTO dto = new ReporteDetalleDTO();
                        dto.setMetodoPago(met);
                        dto.setTotalGenerado(totalGen);
                        dto.setNumeroPedidos(numPed);
                        dto.setTicketPromedio(ticket);
                        return dto;
                    })
                    .collect(Collectors.toList());
        } else if (tipo == TipoReporte.POR_PRODUCTO) {
            // Reutilizar lógica de productos
            return generarReporteProductos(inicio, fin, tipo);
        } else {
            // POR_CLIENTE
            return generarReporteProductos(inicio, fin, tipo);
        }
    }

    /**
     * Genera datos de reporte de PRODUCTOS (sin persistir).
     */
    public List<ReporteDetalleDTO> generarReporteProductos(LocalDateTime inicio,
                                                           LocalDateTime fin,
                                                           TipoReporte tipo) {
        List<Pedido> pedidos = pedidoRepo.obtenerPedidosEntregadosConProductos();
        if (inicio != null && fin != null) {
            pedidos = filterByDate(pedidos, inicio, fin);
        }
        if (tipo == TipoReporte.GENERAL) {
            return buildGeneralByCategory(pedidos);
        } else if (tipo == TipoReporte.POR_PRODUCTO) {
            return buildByProduct(pedidos);
        } else {
            return buildByClient(pedidos);
        }
    }

    // ---------- Helpers para PRODUCTOS ----------
    private List<ReporteDetalleDTO> buildGeneralByCategory(List<Pedido> pedidos) {
        Map<String, List<Pedido>> pedidosPorCat = new HashMap<>();
        pedidos.forEach(p -> {
            Set<String> cats = p.getProductos().stream()
                    .map(pp -> productoService.obtenerProducto(pp.getProductoId().longValue())
                            .map(Producto::getCategoria).orElse("Sin categoría"))
                    .collect(Collectors.toSet());
            cats.forEach(cat -> pedidosPorCat
                    .computeIfAbsent(cat, k -> new ArrayList<>()).add(p));
        });
        return pedidosPorCat.entrySet().stream().map(entry -> {
            String cat = entry.getKey();
            List<Pedido> list = entry.getValue();
            long unidades = list.stream()
                    .flatMap(p -> p.getProductos().stream())
                    .filter(pp -> productoService.obtenerProducto(pp.getProductoId().longValue())
                            .map(Producto::getCategoria).orElse("").equals(cat))
                    .mapToLong(PedidoProducto::getCantidad).sum();
            double total = list.stream()
                    .flatMap(p -> p.getProductos().stream())
                    .filter(pp -> productoService.obtenerProducto(pp.getProductoId().longValue())
                            .map(Producto::getCategoria).orElse("").equals(cat))
                    .mapToDouble(PedidoProducto::getSubtotal).sum();
            long numPed = list.size();
            double ticket = numPed > 0 ? total / numPed : 0.0;
            String met = list.stream()
                    .collect(Collectors.groupingBy(Pedido::getMetodoPago, Collectors.counting()))
                    .entrySet().stream().max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey).orElse("—");
            ReporteDetalleDTO dto = new ReporteDetalleDTO();
            dto.setCategoria(cat);
            dto.setUnidadesVendidasTotales(unidades);
            dto.setTotalGenerado(total);
            dto.setNumeroPedidos(numPed);
            dto.setTicketPromedio(ticket);
            dto.setMetodoPago(met);
            return dto;
        }).collect(Collectors.toList());
    }

    private List<ReporteDetalleDTO> buildByProduct(List<Pedido> pedidos) {
        Map<Integer, Long> byProd = pedidos.stream()
                .flatMap(p -> p.getProductos().stream())
                .collect(Collectors.groupingBy(
                        PedidoProducto::getProductoId,
                        Collectors.summingLong(PedidoProducto::getCantidad)
                ));
        return byProd.entrySet().stream().map(en -> {
            Producto pr = productoService.obtenerProducto(en.getKey().longValue()).orElseThrow();
            ReporteDetalleDTO dto = new ReporteDetalleDTO();
            dto.setProductoId(pr.getId().intValue());
            dto.setNombreProducto(pr.getNombre());
            dto.setCantidadVendida(en.getValue());
            dto.setStock(pr.getStock());
            dto.setPrecioUnitario(pr.getPrecio());
            dto.setCategoria(pr.getCategoria());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<ReporteDetalleDTO> buildByClient(List<Pedido> pedidos) {
        Map<Long, List<PedidoProducto>> byClient = pedidos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCliente().getId().longValue(),
                        Collectors.flatMapping(p -> p.getProductos().stream(), Collectors.toList())
                ));
        return byClient.entrySet().stream().map(e -> {
            Cliente c = clienteService.obtenerCliente(e.getKey()).orElseThrow();
            List<PedidoProducto> items = e.getValue();
            long totalUnits = items.stream().mapToLong(PedidoProducto::getCantidad).sum();
            Map<String, Long> sumBy = items.stream()
                    .collect(Collectors.groupingBy(PedidoProducto::getNombre, Collectors.summingLong(PedidoProducto::getCantidad)));
            List<ReporteDetalleDTO.ProductoComprado> detail = sumBy.entrySet().stream()
                    .map(en -> new ReporteDetalleDTO.ProductoComprado(en.getKey(), en.getValue().intValue()))
                    .collect(Collectors.toList());
            ReporteDetalleDTO dto = new ReporteDetalleDTO();
            dto.setClienteId(c.getId().longValue());
            dto.setNombreCliente(c.getNombre() + " " + c.getApellido());
            dto.setTotalUnidades(totalUnits);
            dto.setProductosComprados(detail);
            return dto;
        }).collect(Collectors.toList());
    }

    // Helpers comunes
    private List<Pedido> filterByDate(List<Pedido> pedidos, LocalDateTime inicio, LocalDateTime fin) {
        return pedidos.stream()
                .filter(p -> {
                    LocalDateTime fe = p.getFecha();
                    return fe != null && !fe.isBefore(inicio) && !fe.isAfter(fin);
                })
                .collect(Collectors.toList());
    }

    /**
     * Convierte entidad Reporte (junto con detalles) a ReporteDTO.
     */
    private ReporteDTO convertirAReporteDTO(Reporte reporte) {
        ReporteDTO dto = new ReporteDTO();
        dto.setId(reporte.getId());
        dto.setFechaGeneracion(reporte.getFechaGeneracion());
        dto.setPeriodoInicio(reporte.getPeriodoInicio());
        dto.setPeriodoFin(reporte.getPeriodoFin());
        dto.setTipoReporte(reporte.getTipoReporte().name());
        List<ReporteDetalleDTO> lista = reporte.getDetalles().stream().map(d -> {
            ReporteDetalleDTO rdto = new ReporteDetalleDTO();
            if (d.getProducto() != null) {
                Producto p = d.getProducto();
                rdto.setProductoId(p.getId().intValue());
                rdto.setNombreProducto(p.getNombre());
                rdto.setCantidadVendida(d.getCantidadVendida());
                rdto.setTotalGenerado(d.getTotalGenerado());
            } else if (d.getCliente() != null) {
                Cliente c = d.getCliente();
                rdto.setClienteId(c.getId().longValue());
                rdto.setNombreCliente(c.getNombre() + " " + c.getApellido());
                rdto.setTotalUnidades(d.getCantidadVendida());
            }
            return rdto;
        }).collect(Collectors.toList());
        dto.setDetalles(lista);
        return dto;
    }
}
