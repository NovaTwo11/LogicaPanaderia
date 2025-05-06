package co.edu.uniquindio.logicapanaderia.dto;

public class RepartidorDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String vehiculo;
    private String licencia;
    private Boolean disponible;
    private Integer pedidosEntregados;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public Integer getPedidosEntregados() {
        return pedidosEntregados;
    }

    public void setPedidosEntregados(Integer pedidosEntregados) {
        this.pedidosEntregados = pedidosEntregados;
    }
}
