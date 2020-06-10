package com.klem.catalog.models;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Transactional
@TypeDef(name = "string-array", typeClass = StringArrayType.class, defaultForType = String[].class)
@Table(name = "vehicles")
public class Vehicle extends PostgresModel {

    @NotBlank(message = "name of vehicle must be supplied")
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank(message = "description of vehicle must be supplied")
    @Column(nullable = false)
    private String description;

    @NotNull(message = "engine number is not allowed to be empty")
    @Min( value = 10000, message =  "Engine must be 5 digits in length")
    @Max( value = 99999, message =  "Engine must be 5 digits in length")
    @Column(name = "engine_number", unique = true, nullable = false)
    private Long engineNumber;

    @Type(type = "string-array")
    @Column(columnDefinition = "text[]")
    @Basic(fetch = FetchType.LAZY)
    private String[] tags = new String[]{};

    @Type(type = "string-array")
    @Column(columnDefinition = "text[]")
    @Basic(fetch = FetchType.LAZY)
    private String[] images = new String[]{};

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vehicle_categories",
            joinColumns = {
            @JoinColumn(name = "vehicle_id", referencedColumnName = "id", nullable = false, updatable = false)
            },
            inverseJoinColumns = {
            @JoinColumn(name="category_id", referencedColumnName = "id", nullable = false, updatable = false)
            }
            )
    private Set<@NotNull Category> categories;

    public Vehicle(String name, String description, Set<Category> categories) {
        this.name = name;
        this.description = description;
        this.categories = categories;
    }

    public Vehicle(String name, String description, Long engineNumber, Set<Category> categories) {
        this.name = name;
        this.description = description;
        this.engineNumber = engineNumber;
        this.categories = categories;
    }

}
