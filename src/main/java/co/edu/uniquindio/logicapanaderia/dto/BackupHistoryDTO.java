// src/main/java/co/edu/uniquindio/logicapanaderia/dto/BackupHistoryDto.java
package co.edu.uniquindio.logicapanaderia.dto;

import java.time.LocalDateTime;

/**
 * DTO que representa un registro de historial de respaldos.
 */
public class BackupHistoryDTO {

    private Long id;
    private LocalDateTime createdAt;
    private String type;
    private String description;
    private Long sizeBytes;

    public BackupHistoryDTO() {
    }

    public BackupHistoryDTO(Long id,
                            LocalDateTime createdAt,
                            String type,
                            String description,
                            Long sizeBytes) {
        this.id = id;
        this.createdAt = createdAt;
        this.type = type;
        this.description = description;
        this.sizeBytes = sizeBytes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }
}
