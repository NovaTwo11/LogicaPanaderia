package co.edu.uniquindio.logicapanaderia.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name="BITACORA")
public class Bitacora {
    @Id
    @GeneratedValue
    private Long id;
    private LocalDateTime timestamp;
    private Long usuarioId;
    private String evento;
    private String detalle;

    // Constructores
    public Bitacora() {}

    public Bitacora(Long usuarioId, String evento, String detalle) {
        this.timestamp = LocalDateTime.now();
        this.usuarioId = usuarioId;
        this.evento = evento;
        this.detalle = detalle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
}