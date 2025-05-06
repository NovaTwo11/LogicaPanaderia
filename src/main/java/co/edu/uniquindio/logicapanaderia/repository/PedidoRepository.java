package co.edu.uniquindio.logicapanaderia.repository;

import co.edu.uniquindio.logicapanaderia.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT p FROM Pedido p " +
            "LEFT JOIN FETCH p.cliente c " +
            "LEFT JOIN FETCH p.repartidor r " +
            "WHERE p.id = :id")
    Optional<Pedido> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.productos WHERE p.estado = 'ENTREGADO'")
    List<Pedido> obtenerPedidosEntregadosConProductos();

    long countByFechaBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.fecha >= :start AND p.fecha < :end")
    double sumTotalByFechaBetween(LocalDateTime start, LocalDateTime end);

}
