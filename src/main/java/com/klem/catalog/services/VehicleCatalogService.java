package com.klem.catalog.services;

import com.klem.catalog.models.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Service
public interface VehicleCatalogService {
    Vehicle addCar(String name);
    Vehicle addVehicleEntry(Vehicle vehicle);
    Vehicle updateVehicle(String carId, HashMap<String, Object> updates);
    Page<Vehicle> getVehicles(Pageable pageable);
    Optional<Vehicle> getVehicle(UUID carId);
    Page<Vehicle> searchByName(String name, Pageable pageable);
    Page<Vehicle> searchByCategory(String category, Pageable pageable);
    Page<Vehicle> searchByDescription(String description, Pageable pageable);
    Page<Vehicle> searchByTag(String[] tags, Pageable pageable);
}
