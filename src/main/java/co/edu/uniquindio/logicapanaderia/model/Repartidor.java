package co.edu.uniquindio.logicapanaderia.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Repartidor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "repartidor_seq")
    @SequenceGenerator(name = "repartidor_seq", sequenceName = "repartidor_seq", allocationSize = 1)
    @Column(name = "id")
    private Integer id;

    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String vehiculo;
    private String licencia;
    private boolean disponible;
    private Integer pedidosEntregados;

    @OneToMany(mappedBy = "repartidor")
    private List<Pedido> pedidos;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(String vehiculo) {
        this.vehiculo = vehiculo;
    }

    public String getLicencia() {
        return licencia;
    }

    public void setLicencia(String licencia) {
        this.licencia = licencia;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public Integer getPedidosEntregados() {
        return pedidosEntregados;
    }

    public void setPedidosEntregados(Integer pedidosEntregados) {
        this.pedidosEntregados = pedidosEntregados;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }
}
