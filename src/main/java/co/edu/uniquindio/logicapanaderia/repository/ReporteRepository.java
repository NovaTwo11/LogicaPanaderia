package co.edu.uniquindio.logicapanaderia.repository;

import co.edu.uniquindio.logicapanaderia.dto.ReporteDetalleDTO;
import co.edu.uniquindio.logicapanaderia.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    // Ventas agrupadas por producto (usa los campos productoId y nombre de PedidoProducto)
    @Query("""
      SELECT new co.edu.uniquindio.logicapanaderia.dto.ReporteDetalleDTO(
        pp.productoId,
        pp.nombre,
        SUM(pp.cantidad),
        SUM(pp.subtotal)
      )
      FROM PedidoProducto pp
      WHERE pp.pedido.estado = 'ENTREGADO'
        AND pp.pedido.fecha BETWEEN :inicio AND :fin
      GROUP BY pp.productoId, pp.nombre
    """)
    List<ReporteDetalleDTO> findDetallesProductos(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin")    LocalDateTime fin
    );

    // Ventas agrupadas por cliente
    @Query("""
      SELECT new co.edu.uniquindio.logicapanaderia.dto.ReporteDetalleDTO(
        c.id,
        c.nombre,
        SUM(pp.cantidad),
        SUM(pp.subtotal)
      )
      FROM PedidoProducto pp
           JOIN pp.pedido p
           JOIN p.cliente c
      WHERE p.estado = 'ENTREGADO'
        AND p.fecha BETWEEN :inicio AND :fin
      GROUP BY c.id, c.nombre
    """)
    List<ReporteDetalleDTO> findDetallesClientes(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin")    LocalDateTime fin
    );


    // Ventas GENERALES agrupadas por método de pago
    @Query("""
      SELECT new co.edu.uniquindio.logicapanaderia.dto.ReporteDetalleDTO(
        null,
        null,
        SUM(pp.cantidad),
        SUM(pp.subtotal),
        null,
        p.metodoPago
      )
      FROM PedidoProducto pp
           JOIN pp.pedido p
      WHERE p.estado = 'ENTREGADO'
        AND p.fecha BETWEEN :inicio AND :fin
      GROUP BY p.metodoPago
    """)
    List<ReporteDetalleDTO> findDetallesPorMetodoPago(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin")    LocalDateTime fin
    );
}
