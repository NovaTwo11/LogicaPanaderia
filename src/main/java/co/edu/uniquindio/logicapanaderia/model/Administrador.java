// src/main/java/com/tuempresa/tuapp/model/Administrador.java
package co.edu.uniquindio.logicapanaderia.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;


import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ADMINISTRADORES")
public class Administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Asegúrate de tener esto
    private Long id;

    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String rol;

    @Column(nullable = false)
    private String contrasena;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_CREACION")
    private Date fechaCreacion;

    private Boolean activo = true; // Usa el wrapper Boolean y así acepta null

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

    @JsonIgnore
    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo != null && activo;
    }


    public void setActivo(Boolean activo) {
        this.activo = activo;
    }


    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }


}
