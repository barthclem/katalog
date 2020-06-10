package com.klem.catalog.models;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Data
public class PostgresModel {
    @Id
    @Type(type = "pg-uuid")
    private UUID id;

    public PostgresModel() {
        this.id = UUID.randomUUID();
    }

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
