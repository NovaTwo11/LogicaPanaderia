package co.edu.uniquindio.logicapanaderia.controller;

import co.edu.uniquindio.logicapanaderia.model.Producto;
import co.edu.uniquindio.logicapanaderia.service.ProductoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public List<Producto> listarProductos() {
        return productoService.listarProductos();
    }

    @GetMapping("/{id}")
    public Producto obtenerProducto(@PathVariable Long id) {
        return productoService.obtenerProducto(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Producto crearProducto(
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam Double precio,
            @RequestParam String categoria,
            @RequestParam Integer stock,
            @RequestParam Boolean disponible,
            @RequestParam(required = false) MultipartFile imagen
    ) throws IOException {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecio(precio);
        p.setCategoria(categoria);
        p.setStock(stock);
        p.setDisponible(disponible);
        return productoService.crearProductoConImagen(p, imagen);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Producto actualizarProducto(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam Double precio,
            @RequestParam String categoria,
            @RequestParam Integer stock,
            @RequestParam Boolean disponible,
            @RequestParam(required = false) MultipartFile imagen
    ) throws IOException {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecio(precio);
        p.setCategoria(categoria);
        p.setStock(stock);
        p.setDisponible(disponible);
        return productoService.actualizarProductoConImagen(id, p, imagen);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
    }

    @GetMapping(value = "/{id}/imagen")
    public ResponseEntity<byte[]> descargarImagen(@PathVariable Long id) {
        Producto p = productoService.obtenerProducto(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Producto no encontrado"));
        byte[] img = p.getImagen();
        if (img == null || img.length == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sin imagen");
        }
        String tipo = p.getImagenTipo();
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (tipo != null && MediaType.parseMediaType(tipo) != null) {
            mediaType = MediaType.parseMediaType(tipo);
        }
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(img);
    }
}
