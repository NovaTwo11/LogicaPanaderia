// src/main/java/co/edu/uniquindio/logicapanaderia/service/ProductoService.java
package co.edu.uniquindio.logicapanaderia.service;

import co.edu.uniquindio.logicapanaderia.model.Producto;
import co.edu.uniquindio.logicapanaderia.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepo;

    public List<Producto> listarProductos() {
        return productoRepo.findAll();
    }
    public Optional<Producto> obtenerProducto(Long id) {
        return productoRepo.findById(id);
    }

    @Transactional
    public Producto crearProductoConImagen(Producto producto, MultipartFile imagenFile) throws IOException {
        if (imagenFile != null && !imagenFile.isEmpty()) {
            producto.setImagen(imagenFile.getBytes());
            producto.setImagenTipo(imagenFile.getContentType()); // Guarda el tipo MIME
        }
        return productoRepo.save(producto);
    }


    @Transactional
    public Producto actualizarProductoConImagen(Long id, Producto productoActualizado, MultipartFile imagenFile) throws IOException {
        Producto productoExistente = productoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));

        productoExistente.setNombre(productoActualizado.getNombre());
        productoExistente.setPrecio(productoActualizado.getPrecio());
        productoExistente.setDescripcion(productoActualizado.getDescripcion());
        productoExistente.setStock(productoActualizado.getStock());
        productoExistente.setCategoria(productoActualizado.getCategoria());

        if (imagenFile != null && !imagenFile.isEmpty()) {
            productoExistente.setImagen(imagenFile.getBytes());
            productoExistente.setImagenTipo(imagenFile.getContentType()); // Guarda el tipo MIME
        }

        return productoRepo.save(productoExistente);
    }

    public byte[] obtenerImagenProducto(Long id) {
        Producto producto = productoRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
        return producto.getImagen();
    }

    public void eliminarProducto(Long id) {
        productoRepo.deleteById(id);
    }
}
