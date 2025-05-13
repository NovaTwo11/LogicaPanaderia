package co.edu.uniquindio.logicapanaderia.repository;

import co.edu.uniquindio.logicapanaderia.model.Bitacora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BitacoraRepository extends JpaRepository<Bitacora, Long> {

    List<Bitacora> findByTimestampBetween(LocalDateTime inicio, LocalDateTime fin);

    List<Bitacora> findByUsuarioId(Long usuarioId);

    // Métodos combinados según filtros que necesites
    List<Bitacora> findByUsuarioIdAndTimestampBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);
}
