package co.edu.uniquindio.logicapanaderia.dto;

import java.util.List;

/**
 * DTO para los distintos tipos de reporte de productos.
 * Se rellena sólo con los campos relevantes según TipoReporte:
 * - GENERAL       → categoría, unidadesVendidasTotales, totalGenerado, numeroPedidos, ticketPromedio, metodoPago
 * - POR_PRODUCTO  → productoId, nombreProducto, cantidadVendida, stock, precioUnitario, categoria
 * - POR_CLIENTE   → clienteId, nombreCliente, totalUnidades, productosComprados
 */
public class ReporteDetalleDTO {

    // Campos COMUNES
    // (ninguno declarado aquí, los siguientes son específicos)

    // --- Para POR_CLIENTE ---
    private Long clienteId;
    private String nombreCliente;
    private Long totalUnidades;
    private List<ProductoComprado> productosComprados;

    // --- Para POR_PRODUCTO ---
    private Integer productoId;
    private String nombreProducto;
    private Long cantidadVendida;
    private Integer stock;
    private Double precioUnitario;
    private String categoria;

    // --- Para GENERAL ---
    private Long unidadesVendidasTotales;
    private Double totalGenerado;
    private Long numeroPedidos;
    private Double ticketPromedio;
    private String metodoPago;

    /** 0) Constructor vacío para Jackson */
    public ReporteDetalleDTO() { }

    /** 1) Constructor para ventas agrupadas por producto */
    public ReporteDetalleDTO(
            Integer productoId,
            String nombreProducto,
            Long cantidadVendida,
            Double totalGenerado
    ) {
        this.productoId      = productoId;
        this.nombreProducto  = nombreProducto;
        this.cantidadVendida = cantidadVendida;
        this.totalGenerado   = totalGenerado;
    }

    /** 2) Constructor para ventas agrupadas por cliente */
    public ReporteDetalleDTO(
            Long clienteId,
            String nombreCliente,
            Long totalUnidades,
            Double totalGenerado
    ) {
        this.clienteId    = clienteId;
        this.nombreCliente= nombreCliente;
        this.totalUnidades= totalUnidades;
        this.totalGenerado= totalGenerado;
    }

    /** 3) Constructor para ventas generales por método de pago */
    public ReporteDetalleDTO(
            Integer ignored1,
            String ignored2,
            Long cantidadVendida,
            Double totalGenerado,
            Long ignored3,
            String metodoPago
    ) {
        // los primeros dos son null en tu query
        this.cantidadVendida = cantidadVendida;
        this.totalGenerado   = totalGenerado;
        this.metodoPago      = metodoPago;
    }

    // --- Setters y Getters ---

    // POR_CLIENTE
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public Long getTotalUnidades() { return totalUnidades; }
    public void setTotalUnidades(Long totalUnidades) { this.totalUnidades = totalUnidades; }

    public List<ProductoComprado> getProductosComprados() { return productosComprados; }
    public void setProductosComprados(List<ProductoComprado> productosComprados) {
        this.productosComprados = productosComprados;
    }

    // POR_PRODUCTO
    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public Long getCantidadVendida() { return cantidadVendida; }
    public void setCantidadVendida(Long cantidadVendida) { this.cantidadVendida = cantidadVendida; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    // GENERAL
    public Long getUnidadesVendidasTotales() { return unidadesVendidasTotales; }
    public void setUnidadesVendidasTotales(Long unidadesVendidasTotales) {
        this.unidadesVendidasTotales = unidadesVendidasTotales;
    }

    public Double getTotalGenerado() { return totalGenerado; }
    public void setTotalGenerado(Double totalGenerado) { this.totalGenerado = totalGenerado; }

    public Long getNumeroPedidos() { return numeroPedidos; }
    public void setNumeroPedidos(Long numeroPedidos) { this.numeroPedidos = numeroPedidos; }

    public Double getTicketPromedio() { return ticketPromedio; }
    public void setTicketPromedio(Double ticketPromedio) { this.ticketPromedio = ticketPromedio; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    // --- Clase interna para detalle de productos comprados (POR_CLIENTE) ---
    public static class ProductoComprado {
        private String nombre;
        private Integer cantidad;

        public ProductoComprado() { }

        public ProductoComprado(String nombre, Integer cantidad) {
            this.nombre   = nombre;
            this.cantidad = cantidad;
        }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }
}
