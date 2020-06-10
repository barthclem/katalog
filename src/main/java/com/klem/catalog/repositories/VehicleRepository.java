package com.klem.catalog.repositories;

import com.klem.catalog.models.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    Optional<Vehicle> findVehicleById(UUID id);
    boolean existsByName(String name);
    Page<Vehicle> findAllByDescriptionContaining(String description, Pageable pageable);
    Page<Vehicle> findByName(String name, Pageable pageable);
    Page<Vehicle> findCarAllByCategories_Name(String name, Pageable pageable);
    @Query(value = "SELECT * FROM vehicles WHERE tags @>  cast(:tag as text[]) ", nativeQuery = true)
    Page<Vehicle> findCarsByTags(@Param("tag") String tags, Pageable pageable);
}
