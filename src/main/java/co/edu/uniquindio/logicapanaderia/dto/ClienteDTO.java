package co.edu.uniquindio.logicapanaderia.dto;

public class ClienteDTO {
    private Integer id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private Boolean activo;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public Boolean getActivo() {
        return activo;
    }

}
