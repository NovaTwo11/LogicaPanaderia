package co.edu.uniquindio.logicapanaderia.dto;

import java.util.List;

/**
 * DTO para solicitudes de creación de respaldo.
 */
public class BackupRequest {

    private String type;           // "full" o "partial"
    private List<String> entities; // lista de entidades (solo si partial)
    private String description;    // descripción opcional

    public BackupRequest() {
    }

    /**
     * Constructor completo.
     *
     * @param type        "full" o "partial"
     * @param entities    entidades a respaldar (si partial)
     * @param description descripción del respaldo
     */
    public BackupRequest(String type, List<String> entities, String description) {
        this.type = type;
        this.entities = entities;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getEntities() {
        return entities;
    }

    public void setEntities(List<String> entities) {
        this.entities = entities;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
