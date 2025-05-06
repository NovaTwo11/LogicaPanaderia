package co.edu.uniquindio.logicapanaderia.repository;

import co.edu.uniquindio.logicapanaderia.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {

    Optional<Administrador> findByEmail(String email);

    @Modifying
    @Query("DELETE FROM Administrador a WHERE a.id = :id")
    void eliminarPorId(@Param("id") Long id);
}
