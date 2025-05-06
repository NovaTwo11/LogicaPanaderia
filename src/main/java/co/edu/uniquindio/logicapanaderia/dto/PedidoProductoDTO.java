package co.edu.uniquindio.logicapanaderia.dto;

public class PedidoProductoDTO {

    private Integer productoId;
    private String nombre;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;

    public PedidoProductoDTO() {}

    public PedidoProductoDTO(Integer productoId, String nombre, Integer cantidad, Double precioUnitario, Double subtotal) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "PedidoProductoDTO{" +
                "productoId=" + productoId +
                ", nombre='" + nombre + '\'' +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                '}';
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
