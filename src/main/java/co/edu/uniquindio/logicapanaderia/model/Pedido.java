package co.edu.uniquindio.logicapanaderia.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @JsonBackReference
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "repartidor_id")
    private Repartidor repartidor;

    /**
     * Fecha de creación (o de registro) en zona 'America/Bogota'.
     * Se inicializa automáticamente en el PrePersist si es null.
     */
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoProducto> productos;

    private Double total;
    private String direccionEntrega;
    private String metodoPago;
    private String notas;

    // Getters y setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Repartidor getRepartidor() {
        return repartidor;
    }
    public void setRepartidor(Repartidor repartidor) {
        this.repartidor = repartidor;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public EstadoPedido getEstado() {
        return estado;
    }
    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public List<PedidoProducto> getProductos() {
        return productos;
    }
    public void setProductos(List<PedidoProducto> productos) {
        this.productos = productos;
    }

    public Double getTotal() {
        return total;
    }
    public void setTotal(Double total) {
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

    /**
     * Callback JPA que se ejecuta antes de persistir la entidad.
     * Si fecha es null, la inicializa con la hora actual en America/Bogota.
     */
    @PrePersist
    private void prePersist() {
        if (this.fecha == null) {
            ZoneId bogota = ZoneId.of("America/Bogota");
            this.fecha = ZonedDateTime.now(bogota).toLocalDateTime();
        }
    }
}
