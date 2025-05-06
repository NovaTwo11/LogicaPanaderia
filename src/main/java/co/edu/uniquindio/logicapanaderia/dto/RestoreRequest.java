// src/main/java/co/edu/uniquindio/logicapanaderia/dto/RestoreRequest.java
package co.edu.uniquindio.logicapanaderia.dto;

/**
 * DTO para solicitar la restauración de un respaldo existente por ID.
 */
public class RestoreRequest {

    private Long backupId;

    public RestoreRequest() {
    }

    /**
     * @param backupId ID del BackupHistory a restaurar
     */
    public RestoreRequest(Long backupId) {
        this.backupId = backupId;
    }

    public Long getBackupId() {
        return backupId;
    }

    public void setBackupId(Long backupId) {
        this.backupId = backupId;
    }
}
