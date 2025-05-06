package co.edu.uniquindio.logicapanaderia.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/api/archivos")
@CrossOrigin(origins = "http://localhost:4200")
public class ArchivoController {

    private static final String RUTA_BASE = "src/main/resources/static/uploads/";

    @PostMapping("/upload")
    public ResponseEntity<String> subirArchivo(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo está vacío");
        }

        try {
            Path rutaDirectorio = Paths.get(RUTA_BASE);
            if (Files.notExists(rutaDirectorio)) {
                Files.createDirectories(rutaDirectorio);
            }

            String nombreArchivo = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path rutaArchivo = rutaDirectorio.resolve(nombreArchivo);

            Files.copy(file.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

            // Devuelve la ruta relativa que se puede usar luego para mostrar la imagen
            String url = "/static/uploads/" + nombreArchivo;
            return ResponseEntity.ok(url);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al guardar el archivo");
        }
    }
}
