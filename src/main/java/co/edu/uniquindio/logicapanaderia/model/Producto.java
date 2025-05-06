// src/main/java/co/edu/uniquindio/logicapanaderia/model/Producto.java
package co.edu.uniquindio.logicapanaderia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "PRODUCTO")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String descripcion;

    private Double precio;

    private String categoria;

    private Integer stock;

    private Boolean disponible;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "imagen", nullable = true)
    private byte[] imagen;

    @Column(name = "imagen_tipo", length = 100, nullable = true)
    private String imagenTipo;


    // Getters y setters

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
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public Double getPrecio() {
        return precio;
    }
    public void setPrecio(Double precio) {
        this.precio = precio;
    }
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public Integer getStock() {
        return stock;
    }
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    public Boolean getDisponible() {
        return disponible;
    }
    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }
    public byte[] getImagen() { return imagen; }
    public void setImagen(byte[] imagen) { this.imagen = imagen; }

    public String getImagenTipo() { return imagenTipo; }
    public void setImagenTipo(String imagenTipo) { this.imagenTipo = imagenTipo; }
}
