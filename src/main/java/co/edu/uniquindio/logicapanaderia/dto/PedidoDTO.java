package co.edu.uniquindio.logicapanaderia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class PedidoDTO {

    private Long id;
    private Long clienteId;
    private String clienteNombre;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime fecha;
    private String estado;
    private List<PedidoProductoDTO> productos;
    private Long repartidorId;
    private String repartidorNombre;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private double total;
    private String direccionEntrega;
    private String metodoPago;
    private String notas;

    // Constructor vacío necesario para Jackson
    public PedidoDTO() {
    }

    public PedidoDTO(Long id, Long clienteId, String clienteNombre, LocalDateTime fecha, String estado,
                     List<PedidoProductoDTO> productos, Long repartidorId, String repartidorNombre,
                     double total, String direccionEntrega, String metodoPago, String notas) {
        this.id = id;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.fecha = fecha;
        this.estado = estado;
        this.productos = productos;
        this.repartidorId = repartidorId;
        this.repartidorNombre = repartidorNombre;
        this.total = total;
        this.direccionEntrega = direccionEntrega;
        this.metodoPago = metodoPago;
        this.notas = notas;
    }

    @Override
    public String toString() {
        return "PedidoDTO{" +
                "id=" + id +
                ", clienteId=" + clienteId +
                ", clienteNombre='" + clienteNombre + '\'' +
                ", fecha=" + fecha +
                ", estado='" + estado + '\'' +
                ", productos=" + productos +
                ", repartidorId=" + repartidorId +
                ", repartidorNombre='" + repartidorNombre + '\'' +
                ", total=" + total +
                ", direccionEntrega='" + direccionEntrega + '\'' +
                ", metodoPago='" + metodoPago + '\'' +
                ", notas='" + notas + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<PedidoProductoDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<PedidoProductoDTO> productos) {
        this.productos = productos;
    }

    public Long getRepartidorId() {
        return repartidorId;
    }

    public void setRepartidorId(Long repartidorId) {
        this.repartidorId = repartidorId;
    }

    public String getRepartidorNombre() {
        return repartidorNombre;
    }

    public void setRepartidorNombre(String repartidorNombre) {
        this.repartidorNombre = repartidorNombre;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
