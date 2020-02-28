package com.kurkureshubham.projects.controller;

import com.kurkureshubham.projects.repositories.VehicleRepository;
import com.kurkureshubham.projects.models.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A Controller Class for Vehicle Data Model. This contains the functionality of retrieving, creating, deleting and
 * updating vehicles.
 */
@RestController
public class Controller {
    @Autowired
    private VehicleRepository vehicleRepository;
    private final int beginningYear = 1950;
    private final int endingYear = 2050;

    @GetMapping("/vehicles")
    public ResponseEntity<List<Vehicle>> getVehicles(
            @RequestParam(value = "beginningYear", required = false) Integer beginningYear,
            @RequestParam(value = "endingYear", required = false) Integer endingYear,
            @RequestParam(value = "model", required = false) String model,
            @RequestParam(value = "make", required = false) String make) {

        List<Vehicle> allVehicles = vehicleRepository.findAll();
        Stream<Vehicle> vehiclesStream = allVehicles.stream();

        if (make != null) {
            List<String> makes = Arrays.asList(make.split(","));
            vehiclesStream = vehiclesStream.filter(v -> makes.contains(v.getMake()));
        }
        if (model != null) {
            List<String> models = Arrays.asList(model.split(","));
            vehiclesStream = vehiclesStream.filter(v -> models.contains(v.getModel()));
        }
        if (beginningYear == null && endingYear == null && model == null && make == null) {
            return ResponseEntity.ok().body(allVehicles);
        }
        if (beginningYear != null && endingYear != null && (beginningYear <= endingYear)) {
            vehiclesStream = vehiclesStream.filter(v -> (v.getYear() >= beginningYear && v.getYear() <= endingYear));
        }
        return ResponseEntity.ok().body(vehiclesStream.collect(Collectors.toList()));
    }

    @GetMapping("/vehicles/{id}")
    public ResponseEntity<Vehicle> getVehiclesById(@PathVariable(value = "id") int vehicleId)
            throws ResourceNotFoundException {
        Vehicle vehicle = vehicleRepository
                .findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("VEHICLE NOT FOUND AT: " + vehicleId));
        return ResponseEntity.ok().body(vehicle);
    }

    @PostMapping("/vehicles")
    public ResponseEntity createVehicle(@RequestBody Vehicle vehicle) {

        if (vehicle.getId() != 0) {
            vehicle.setId(0);
        }
        if (vehicle.getMake() == null || vehicle.getMake().length() == 0) {
            return ResponseEntity.badRequest().body("NOT SAVED!, MAKE NOT SPECIFIED");
        }
        if (vehicle.getModel() == null || vehicle.getModel().length() == 0) {
            return ResponseEntity.badRequest().body("NOT SAVED!, MODEL NOT SPECIFIED");
        }
        if (vehicle.getYear() > endingYear || vehicle.getYear() < beginningYear) {
            return ResponseEntity.badRequest().body("NOT SAVED!, INVALID YEAR");
        }

        vehicleRepository.save(vehicle);
        return ResponseEntity.ok().body(vehicle);
    }

    @PutMapping("/vehicles")
    public ResponseEntity updateVehicle(@RequestBody Vehicle vehicleDetails) throws ResourceNotFoundException {
        Vehicle vehicle =
                vehicleRepository
                        .findById(vehicleDetails.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("VEHICLE NOT FOUND AT: "
                                + vehicleDetails.getId()));

        if (vehicleDetails.getYear() < endingYear && vehicleDetails.getYear() > beginningYear) {
            vehicle.setYear(vehicleDetails.getYear());
        } else {
            return ResponseEntity.badRequest().body("NOT SAVED!, INVALID YEAR");
        }

        if (vehicleDetails.getMake() != null && vehicleDetails.getMake().length() != 0) {
            vehicle.setMake(vehicleDetails.getMake());
        } else {
            return ResponseEntity.badRequest().body("NOT SAVED!, MAKE NOT SPECIFIED");
        }

        if (vehicleDetails.getModel() != null && vehicleDetails.getModel().length() != 0) {
            vehicle.setModel(vehicleDetails.getModel());
        } else {
            return ResponseEntity.badRequest().body("NOT SAVED!, MODEL NOT SPECIFIED");
        }
        vehicleRepository.save(vehicle);
        return ResponseEntity.ok().body(vehicle);
    }

    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable(value = "id") int vehicleId) throws ResourceNotFoundException {
        Vehicle vehicle =
                vehicleRepository
                        .findById(vehicleId)
                        .orElseThrow(() -> new ResourceNotFoundException("VEHICLE NOT FOUND AT: " + vehicleId));
        vehicleRepository.delete(vehicle);
        return ResponseEntity.ok().body("VEHICLE DELETED SUCCESSFULLY!");
    }
}

