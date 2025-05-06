package co.edu.uniquindio.logicapanaderia.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime fechaGeneracion;
    private LocalDateTime periodoInicio;
    private LocalDateTime periodoFin;

    @Enumerated(EnumType.STRING)
    private TipoReporte tipoReporte;

    @OneToMany(mappedBy = "reporte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReporteDetalle> detalles;

    public Reporte() {}
    public Reporte(LocalDateTime periodoInicio, LocalDateTime periodoFin, TipoReporte tipoReporte) {
        this.fechaGeneracion = LocalDateTime.now();
        this.periodoInicio = periodoInicio;
        this.periodoFin = periodoFin;
        this.tipoReporte = tipoReporte;
    }
    // getters y setters...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public LocalDateTime getPeriodoInicio() {
        return periodoInicio;
    }

    public void setPeriodoInicio(LocalDateTime periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    public LocalDateTime getPeriodoFin() {
        return periodoFin;
    }

    public void setPeriodoFin(LocalDateTime periodoFin) {
        this.periodoFin = periodoFin;
    }

    public TipoReporte getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(TipoReporte tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public List<ReporteDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<ReporteDetalle> detalles) {
        this.detalles = detalles;
    }
}