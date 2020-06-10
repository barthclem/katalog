package com.klem.catalog.services;

import com.google.common.collect.Sets;
import com.klem.catalog.exceptions.ResourceConflictException;
import com.klem.catalog.exceptions.ResourceNotFoundException;
import com.klem.catalog.models.Vehicle;
import com.klem.catalog.models.Category;
import com.klem.catalog.repositories.VehicleCategoryRepository;
import com.klem.catalog.repositories.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class CatalogServiceImpl  implements VehicleCatalogService {

    private final Logger log = LoggerFactory.getLogger(VehicleCatalogService.class);

    private final VehicleRepository vehicleRepository;
    private final VehicleCategoryRepository categoryRepository;

    @Value("${default_category}")
    private String defaultCategory;


    @Autowired
    public CatalogServiceImpl(
            VehicleRepository vehicleRepository, VehicleCategoryRepository categoryRepository) {
        this.vehicleRepository = vehicleRepository;
        this.categoryRepository = categoryRepository;
    }

    @PostConstruct
    @Transactional
    public void fillData() {
        Category category = categoryRepository.save(new Category("Lorry"));

        log.info("Saved Category successfully [{}]", category);
    }


    @Transactional
    @Override
    public Vehicle addCar(String name) {
        String description = String.format("%s %s", name, defaultCategory);
        Long engineNumber = generateNewEngineNumber();
        Category category = categoryRepository.findByName(defaultCategory)
                .orElse(categoryRepository.save(new Category(defaultCategory)));

        if (vehicleRepository.existsByName(name)) {
            throw new ResourceConflictException(String.format("Name [%s] exists. Please choose another name", name));
        }

        return vehicleRepository.save(new Vehicle(name, description, engineNumber, Collections.singleton(category)));
    }

    @Override
    public Vehicle addVehicleEntry(Vehicle vehicle) {

        vehicle.getCategories().forEach(category -> {
            Category cat = categoryRepository.findByName(category.getName())
                    .orElse(categoryRepository.save(category));
            vehicle.getCategories().remove(category);
            vehicle.getCategories().add(cat);
        });

        vehicle.setEngineNumber(generateNewEngineNumber());
        log.info("Vehicle to be saved :: [{}]", vehicle);
        return vehicleRepository.save(vehicle);
    }


    @Override
    public Vehicle updateVehicle(String carId, HashMap<String, Object> updates) {
         return vehicleRepository.findById(UUID.fromString(carId))
                 .map(persistedVehicle -> {
                     if(updates.containsKey("description")) {
                         persistedVehicle.setDescription(updates.get("description").toString());
                     }

                     if(updates.containsKey("images")) {
                         String[] imageList = ((List<String>) updates.get("images")).toArray(new String[0]);
                         String[] images = Stream.of(persistedVehicle.getImages(), imageList).flatMap(Stream::of).distinct().toArray(String[]::new);
                         persistedVehicle.setImages(images);
                     }

                     if(updates.containsKey("tags")) {
                         String[] tagsList = ((List<String>) updates.get("tags")).toArray(new String[0]);

                         String[] tags = Stream.of(persistedVehicle.getTags(), tagsList).flatMap(Stream::of).distinct().toArray(String[]::new);
                         persistedVehicle.setTags(tags);
                     }

                     return vehicleRepository.save(persistedVehicle);
                 })
                 .orElseThrow(() -> new ResourceNotFoundException(String.format("Car with id %s does not exist", carId)));
    }

    @Override
    public Page<Vehicle> getVehicles(Pageable pageable) {
        return vehicleRepository.findAll(pageable);
    }

    @Override
    public Optional<Vehicle> getVehicle(UUID carId) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findVehicleById(carId);
        return vehicleOpt;
    }

    @Override
    public Page<Vehicle> searchByName(String name, Pageable pageable) {
        return vehicleRepository.findByName(name, pageable);
    }

    @Override
    public Page<Vehicle> searchByCategory(String category, Pageable pageable) {
        return vehicleRepository.findCarAllByCategories_Name(category, pageable);
    }

    @Override
    public Page<Vehicle> searchByDescription(String description, Pageable pageable) {
        return vehicleRepository.findAllByDescriptionContaining(description, pageable);
    }

    @Override
    public Page<Vehicle> searchByTag(String[] tags, Pageable pageable) {
        String tagString = Stream.of(tags).collect(Collectors.joining(",","{","}"));
        return vehicleRepository.findCarsByTags(tagString, pageable);
    }


    public Long generateNewEngineNumber() {
        Long lastEngineNumber =  vehicleRepository.findAll(PageRequest.of(0, 1, Sort.by("createdAt").descending()))
                .get()
                .findFirst()
                .map(Vehicle::getEngineNumber)
                .orElse(10000L);

       return lastEngineNumber + 1;
    }

    public void removeTags(String carId, String[] tags) {
        Vehicle vehicle = vehicleRepository.findById(UUID.fromString(carId))
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Vehicle with id %s does not exist", carId)));

        String[] newTagList = Sets.difference(Sets.newHashSet(Arrays.asList(tags)), Sets.newHashSet(Arrays.asList(vehicle.getTags()))).toArray(new String[0]);
        vehicle.setTags(newTagList);
        vehicleRepository.save(vehicle);
    }

    public void removeImage(String carId, String[] images) {
        Vehicle vehicle = vehicleRepository.findById(UUID.fromString(carId))
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Vehicle with id %s does not exist", carId)));

        String[] newImages = Sets.difference(Sets.newHashSet(Arrays.asList(images)), Sets.newHashSet(Arrays.asList(vehicle.getImages()))).toArray(new String[0]);
        vehicle.setTags(newImages);
        vehicleRepository.save(vehicle);
    }
}
