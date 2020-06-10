package com.klem.catalog.controllers;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.klem.catalog.models.Category;
import com.klem.catalog.models.Vehicle;
import com.klem.catalog.validations.ListNotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleRequest {

    @NotBlank(message = "name of vehicle must be supplied")
    private String name;

    @NotBlank(message = "description of vehicle must be supplied")
    private String description;

    @ListNotEmpty(message = "at least one non-blank categories must be supplied")
    private List<String > categories;

    public Vehicle getVehicle() {
        return new Vehicle(name, description, categories.stream().map(Category::new).collect(Collectors.toSet()));
    }
}
