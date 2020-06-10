package com.klem.catalog.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.time.Instant;


@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Category extends PostgresModel {

    @NotNull(message = "name of category cannot be empty")
    @Column(unique = true)
    private String name;

    public Category (String name) { this.name = name; }

    @Override
    @JsonIgnore
    public Instant getCreatedAt() {
        return super.getCreatedAt();
    }

    @Override
    @JsonIgnore
    public Instant getUpdatedAt() {
        return super.getUpdatedAt();
    }
}
