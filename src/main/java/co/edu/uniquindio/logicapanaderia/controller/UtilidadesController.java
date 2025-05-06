package co.edu.uniquindio.logicapanaderia.controller;

import co.edu.uniquindio.logicapanaderia.dto.*;
import co.edu.uniquindio.logicapanaderia.service.UtilidadesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/utilidades")
public class UtilidadesController {

    private static final Logger log = LoggerFactory.getLogger(UtilidadesController.class);

    private final UtilidadesService utilService;

    // Constructor explícito para inyectar el servicio
    public UtilidadesController(UtilidadesService utilService) {
        this.utilService = utilService;
    }
    @PostMapping("/export")
    public ResponseEntity<ByteArrayResource> export(@RequestBody ExportRequest req) throws IOException {
        log.debug("Export: {}", req);
        byte[] data = utilService.generateExport(
                req.getEntities(),
                req.getFormat(),
                req.isIncludeHeaders(),
                req.isCompress()
        );
        String filename = "export." + req.getFormat() + (req.isCompress() ? ".zip" : "");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentLength(data.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new ByteArrayResource(data));
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> importData(
            @RequestPart("request") ImportRequest req,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        log.debug("Import: {} , file={}", req, file.getOriginalFilename());
        utilService.processImport(
                req.getDataType(),
                file.getBytes(),
                file.getOriginalFilename(),
                req.isUpdateExisting(),
                req.isSkipErrors(),
                req.isHasHeaders()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/backup")
    public ResponseEntity<BackupHistoryDTO> backup(@RequestBody BackupRequest req) throws IOException {
        log.debug("Backup: {}", req);
        var model = utilService.createBackup(req);
        var dto = new BackupHistoryDTO();
        dto.setId(model.getId());
        dto.setCreatedAt(model.getCreatedAt());
        dto.setType(model.getType());
        dto.setDescription(model.getDescription());
        dto.setSizeBytes(model.getSizeBytes());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/backup-history")
    public ResponseEntity<List<BackupHistoryDTO>> listBackups() {
        var models = utilService.listBackups();
        var list = models.stream().map(m -> {
            var d = new BackupHistoryDTO();
            d.setId(m.getId());
            d.setCreatedAt(m.getCreatedAt());
            d.setType(m.getType());
            d.setDescription(m.getDescription());
            d.setSizeBytes(m.getSizeBytes());
            return d;
        }).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/backup/{id}/download")
    public ResponseEntity<ByteArrayResource> downloadBackup(@PathVariable Long id) throws IOException {
        log.debug("Download backup id={}", id);
        byte[] data = utilService.downloadBackup(id);
        String filename = "backup-" + id + ".zip";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentLength(data.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new ByteArrayResource(data));
    }

    @PostMapping("/restore")
    public ResponseEntity<Void> restore(@RequestBody RestoreRequest req) throws IOException {
        log.debug("Restore id={}", req.getBackupId());
        utilService.restoreBackupById(req.getBackupId());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/restore-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> restoreFromFile(
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        log.debug("Restore from file={}", file.getOriginalFilename());
        utilService.restoreFromFile(file.getBytes(), file.getOriginalFilename());
        return ResponseEntity.ok().build();
    }
}
