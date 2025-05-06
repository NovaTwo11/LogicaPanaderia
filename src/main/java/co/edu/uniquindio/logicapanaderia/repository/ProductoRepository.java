// src/main/java/co/edu/uniquindio/logicapanaderia/repository/ProductoRepository.java
package co.edu.uniquindio.logicapanaderia.repository;

import co.edu.uniquindio.logicapanaderia.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
