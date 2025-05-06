package co.edu.uniquindio.logicapanaderia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ReporteDetalleDTO {

    // — Campos básicos —
    private Long productoId;
    private String nombreProducto;
    private Long cantidadVendida;
    private Double totalGenerado;
    private Integer clienteId;
    private String nombreCliente;
    private String metodoPago;
    private Integer stock;              // equivale a “stockActual”
    private Double precioUnitario;
    private List<ProductoComprado> productosComprados;  // para detalle de cliente

    // — Campos para “Ventas | GENERAL” —
    private Long numeroPedidos;
    private Double ticketPromedio;

    // — Campos para “Productos | GENERAL” —
    private String categoria;
    private Long unidadesVendidasTotales;

    public ReporteDetalleDTO() {
        // constructor vacío para Jackson
    }

    /** Constructor completo */
    public ReporteDetalleDTO(
            Long productoId,
            String nombreProducto,
            Long cantidadVendida,
            Double totalGenerado,
            Integer clienteId,
            String nombreCliente,
            String metodoPago,
            Integer stock,
            Double precioUnitario,
            List<ProductoComprado> productosComprados,
            Long numeroPedidos,
            Double ticketPromedio,
            String categoria,
            Long unidadesVendidasTotales
    ) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidadVendida = cantidadVendida;
        this.totalGenerado = totalGenerado;
        this.clienteId = clienteId;
        this.nombreCliente = nombreCliente;
        this.metodoPago = metodoPago;
        this.stock = stock;
        this.precioUnitario = precioUnitario;
        this.productosComprados = productosComprados;
        this.numeroPedidos = numeroPedidos;
        this.ticketPromedio = ticketPromedio;
        this.categoria = categoria;
        this.unidadesVendidasTotales = unidadesVendidasTotales;
    }

    /** Sobrecarga: reporte general/por cliente con clienteId */
    public ReporteDetalleDTO(
            Integer clienteId,
            String nombreCliente,
            Long cantidadVendida,
            Double totalGenerado
    ) {
        this(
                null, null, cantidadVendida, totalGenerado,
                clienteId, nombreCliente,
                null, null, null,
                null, null, null,
                null, null
        );
    }

    /** Sobrecarga: reporte general/por producto con productoId */
    public ReporteDetalleDTO(
            Long productoId,
            String nombreProducto,
            Long cantidadVendida,
            Double totalGenerado
    ) {
        this(
                productoId, nombreProducto, cantidadVendida, totalGenerado,
                null, null,
                null, null, null,
                null, null, null,
                null, null
        );
    }

    /** Sobrecarga original de 6 args */
    public ReporteDetalleDTO(
            Long productoId,
            String nombreProducto,
            Long cantidadVendida,
            Double totalGenerado,
            Integer clienteId,
            String nombreCliente
    ) {
        this(
                productoId, nombreProducto, cantidadVendida, totalGenerado,
                clienteId, nombreCliente,
                null, null, null,
                null, null, null,
                null, null
        );
    }

    // — Getters & Setters —

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public Long getCantidadVendida() { return cantidadVendida; }
    public void setCantidadVendida(Long cantidadVendida) { this.cantidadVendida = cantidadVendida; }

    public Double getTotalGenerado() { return totalGenerado; }
    public void setTotalGenerado(Double totalGenerado) { this.totalGenerado = totalGenerado; }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

    public List<ProductoComprado> getProductosComprados() { return productosComprados; }
    public void setProductosComprados(List<ProductoComprado> productosComprados) { this.productosComprados = productosComprados; }

    public Long getNumeroPedidos() { return numeroPedidos; }
    public void setNumeroPedidos(Long numeroPedidos) { this.numeroPedidos = numeroPedidos; }

    public Double getTicketPromedio() { return ticketPromedio; }
    public void setTicketPromedio(Double ticketPromedio) { this.ticketPromedio = ticketPromedio; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Long getUnidadesVendidasTotales() { return unidadesVendidasTotales; }
    public void setUnidadesVendidasTotales(Long unidadesVendidasTotales) { this.unidadesVendidasTotales = unidadesVendidasTotales; }

    /** Clase interna para detallar productos comprados en reporte por cliente */
    public static class ProductoComprado {
        private String nombre;
        private Integer cantidad;

        public ProductoComprado() { }

        public ProductoComprado(String nombre, Integer cantidad) {
            this.nombre = nombre;
            this.cantidad = cantidad;
        }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }
}
