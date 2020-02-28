package com.kurkureshubham.projects.repositories;

import com.kurkureshubham.projects.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

}
