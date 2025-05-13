package co.edu.uniquindio.logicapanaderia.controller;

import co.edu.uniquindio.logicapanaderia.model.Bitacora;
import co.edu.uniquindio.logicapanaderia.service.BitacoraService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bitacora")
public class BitacoraController {

    private final BitacoraService service;

    public BitacoraController(BitacoraService service) {
        this.service = service;
    }

    /**
     * Registra un evento en la bitácora.
     * Body: { usuarioId, evento, detalle }
     */
    @PostMapping
    public ResponseEntity<Void> registrar(@RequestBody Bitacora payload) {
        service.registrarEvento(
                payload.getUsuarioId(),
                payload.getEvento(),
                payload.getDetalle()
        );
        return ResponseEntity.ok().build();
    }

    /**
     * Lista bitácoras entre dos fechas, opcionalmente filtrando por usuario.
     * Si no vienen 'desde'/'hasta', se consideran últimos 30 días.
     */
    @GetMapping
    public ResponseEntity<List<Bitacora>> listar(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta
    ) {
        // Defaults: últimos 30 días
        LocalDateTime now = LocalDateTime.now();
        if (hasta == null) {
            hasta = now;
        }
        if (desde == null) {
            desde = now.minusDays(30);
        }

        List<Bitacora> logs;
        if (usuarioId != null) {
            logs = service.listarPorUsuarioYFechas(usuarioId, desde, hasta);
        } else {
            logs = service.listarEntre(desde, hasta);
        }
        return ResponseEntity.ok(logs);
    }
}
