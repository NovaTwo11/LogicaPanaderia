package co.edu.uniquindio.logicapanaderia.model;

import jakarta.persistence.*;

@Entity
public class ReporteDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporte_id")
    private Reporte reporte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private Long cantidadVendida;
    private Double totalGenerado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    public ReporteDetalle() {}
    public ReporteDetalle(Reporte reporte, Producto producto, Long cantidadVendida, Double totalGenerado, Cliente cliente) {
        this.reporte = reporte; this.producto = producto;
        this.cantidadVendida = cantidadVendida; this.totalGenerado = totalGenerado;
        this.cliente = cliente;
    }
    // getters y setters...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Reporte getReporte() {
        return reporte;
    }

    public void setReporte(Reporte reporte) {
        this.reporte = reporte;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Long getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(Long cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public Double getTotalGenerado() {
        return totalGenerado;
    }

    public void setTotalGenerado(Double totalGenerado) {
        this.totalGenerado = totalGenerado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}