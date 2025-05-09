package co.edu.uniquindio.logicapanaderia.controller;

import co.edu.uniquindio.logicapanaderia.dto.ReporteDetalleDTO;
import co.edu.uniquindio.logicapanaderia.model.TipoReporte;
import co.edu.uniquindio.logicapanaderia.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    // --- Productos ---
    @GetMapping("/productos")
    public ResponseEntity<List<ReporteDetalleDTO>> getReporteProductos(
            @RequestParam(name = "periodoInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime periodoInicio,

            @RequestParam(name = "periodoFin", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime periodoFin,

            @RequestParam(name = "tipoReporte", required = true)
            TipoReporte tipoReporte
    ) {
        List<ReporteDetalleDTO> detalles =
                reporteService.generarReporteProductos(periodoInicio, periodoFin, tipoReporte);
        return ResponseEntity.ok(detalles);
    }

    // --- Ventas ---
    @GetMapping("/ventas")
    public ResponseEntity<List<ReporteDetalleDTO>> getReporteVentas(
            @RequestParam(name = "periodoInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime periodoInicio,

            @RequestParam(name = "periodoFin", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime periodoFin,

            @RequestParam(name = "tipoReporte", required = true)
            TipoReporte tipoReporte,

            @RequestParam(name = "metodoPago", required = false)
            String metodoPago
    ) {
        List<ReporteDetalleDTO> detalles =
                reporteService.generarReporteVentas(periodoInicio, periodoFin, tipoReporte, metodoPago);
        return ResponseEntity.ok(detalles);
    }
}