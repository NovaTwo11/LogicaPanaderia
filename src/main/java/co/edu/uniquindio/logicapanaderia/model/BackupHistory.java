package co.edu.uniquindio.logicapanaderia.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "backup_history")
public class BackupHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "backup_history_seq")
    @SequenceGenerator(
            name = "backup_history_seq",
            sequenceName = "backup_history_seq",  // Debes crear esta secuencia en tu BD:
            //   CREATE SEQUENCE backup_history_seq START WITH 1 INCREMENT BY 1;
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "type", nullable = false)
    private String type;   // "full" o "partial"

    @Column(name = "description")
    private String description;

    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes;

    @Lob
    @Column(name = "data", nullable = false)
    private byte[] data;

    public BackupHistory() {}

    public BackupHistory(LocalDateTime createdAt, String type, String description, Long sizeBytes, byte[] data) {
        this.createdAt  = createdAt;
        this.type       = type;
        this.description= description;
        this.sizeBytes  = sizeBytes;
        this.data       = data;
    }

    // getters y setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; }

    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }
}
