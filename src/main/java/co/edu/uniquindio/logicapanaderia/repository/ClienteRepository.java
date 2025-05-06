package co.edu.uniquindio.logicapanaderia.repository;

import co.edu.uniquindio.logicapanaderia.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.pedidos WHERE c.id = :id")
    Optional<Cliente> findByIdWithPedidos(@Param("id") Long id);

}
