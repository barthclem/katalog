package com.klem.catalog.controllers;

import com.klem.catalog.controllers.common.Response;
import com.klem.catalog.exceptions.ResourceNotFoundException;
import com.klem.catalog.models.Vehicle;
import com.klem.catalog.services.VehicleCatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("catalog")
public class CatalogController {

    private final VehicleCatalogService vehicleCatalogService;

    private final Logger log = LoggerFactory.getLogger(CatalogController.class);

    @Autowired
    public CatalogController(VehicleCatalogService vehicleCatalogService) {
        this.vehicleCatalogService = vehicleCatalogService;
    }

    @PostMapping("/{name}")
    public ResponseEntity<?> createVehicle(@PathVariable String name) {
        log.info("create vehicle with name [{}]", name);
        Vehicle vehicle = vehicleCatalogService.addCar(name);
        log.info("created vehicle [{}]", vehicle);
        return new ResponseEntity<>(new Response<>(true, "vehicle created", vehicle), HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<?> createVehicle(@Valid @RequestBody VehicleRequest vehicle) {
        log.info("create vehicle : [{}]", vehicle);
        Vehicle createdVehicle = vehicleCatalogService.addVehicleEntry(vehicle.getVehicle());
        return new ResponseEntity<>(new Response<>(true, "vehicle created", createdVehicle), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVehicle(@PathVariable("id") String id) {
        try {
            Optional<Vehicle> vehicleOpt = vehicleCatalogService.getVehicle(UUID.fromString(id));
            if(vehicleOpt.isPresent()) {
                return ResponseEntity.ok(new Response<>(true, "vehicle found", vehicleOpt.get()));
            }
            return new ResponseEntity<>(new Response<>(false, "vehicle not found",  null), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new Response<>(false, "bad id supplied",  null), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{vehicleId}")
    public ResponseEntity<?> updateTerminal(@PathVariable(value = "vehicleId") String vehicleId,
                                                   @RequestBody HashMap<String, Object> updates) {
        try {
            Vehicle updatedVehicle = vehicleCatalogService.updateVehicle(vehicleId, updates);
            return ResponseEntity.ok(new Response<>(true, "vehicle found", updatedVehicle));
        } catch (IllegalArgumentException | ResourceNotFoundException e) {
            return new ResponseEntity<>(new Response<>(false, "bad id supplied",  null), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<?> getVehicles(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "20") int limit) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Vehicle> vehicles = vehicleCatalogService.getVehicles(pageRequest);
        return vehicleResponse(vehicles);
    }

    @GetMapping("search")
    public ResponseEntity<?> search(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tags", required = false) String[] tags,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "20") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        if(!StringUtils.isEmpty(name)) {
            Page<Vehicle> vehicles = vehicleCatalogService.searchByName(name, pageRequest);
            return vehicleResponse(vehicles);
        }

        if(!StringUtils.isEmpty(category)) {
            Page<Vehicle> vehicles = vehicleCatalogService.searchByCategory(category, pageRequest);
            return vehicleResponse(vehicles);
        }

        if(!StringUtils.isEmpty(description)) {
            Page<Vehicle> vehicles = vehicleCatalogService.searchByDescription(description, pageRequest);
            return vehicleResponse(vehicles);
        }

        if(!Objects.isNull(tags)) {
            Page<Vehicle> vehicles = vehicleCatalogService.searchByTag(tags, pageRequest);
            return vehicleResponse(vehicles);
        }

        return new ResponseEntity<>(new Response<>(false, "no search field", null), HttpStatus.BAD_REQUEST);

    }


    public ResponseEntity<?> vehicleResponse(Page<Vehicle> vehicles) {
        if(!vehicles.hasContent()){
            return ResponseEntity.ok(new Response<>(false, "no vehicles found", null));
        }
        return ResponseEntity.ok(new Response<>(true, "vehicles found", vehicles.getContent()));
    }

}
