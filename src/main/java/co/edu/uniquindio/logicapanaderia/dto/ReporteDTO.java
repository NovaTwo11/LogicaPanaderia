package co.edu.uniquindio.logicapanaderia.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

public class ReporteDTO {
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaGeneracion;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime periodoInicio;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime periodoFin;

    private String tipoReporte;
    private List<ReporteDetalleDTO> detalles;

    public ReporteDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }

    public LocalDateTime getPeriodoInicio() { return periodoInicio; }
    public void setPeriodoInicio(LocalDateTime periodoInicio) { this.periodoInicio = periodoInicio; }

    public LocalDateTime getPeriodoFin() { return periodoFin; }
    public void setPeriodoFin(LocalDateTime periodoFin) { this.periodoFin = periodoFin; }

    public String getTipoReporte() { return tipoReporte; }
    public void setTipoReporte(String tipoReporte) { this.tipoReporte = tipoReporte; }

    public List<ReporteDetalleDTO> getDetalles() { return detalles; }
    public void setDetalles(List<ReporteDetalleDTO> detalles) { this.detalles = detalles; }
}