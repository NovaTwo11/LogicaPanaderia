package co.edu.uniquindio.logicapanaderia.repository;

import co.edu.uniquindio.logicapanaderia.model.BackupHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BackupHistoryRepository extends JpaRepository<BackupHistory, Long> {
    Optional<BackupHistory> findFirstByTypeOrderByCreatedAtDesc(String type);
}
