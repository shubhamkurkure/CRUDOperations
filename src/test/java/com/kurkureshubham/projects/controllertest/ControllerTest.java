package com.kurkureshubham.projects.controllertest;

import com.kurkureshubham.projects.CrudOperationsApplication;
import com.kurkureshubham.projects.models.Vehicle;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Objects;

/**
 * A test class for testing the Controller.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrudOperationsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String getRootUrl()  {
        return "http://localhost:" + port;
    }

    @Before
    public void beforeAll() {
        String[] makes = {"Acura", "Audi", "BMW", "Lexus", "Bentley", "Cadillac", "Chevrolet", "MercedesBenz",
                "Volkswagen", "Tesla"};
        String[] models = {"AcuraModel", "AudiModel", "BMWModel", "LexusModel", "BentleyModel",
                "CadillacModel", "ChevroletModel", "MercedesBenzModel", "VolkswagenModel", "TeslaModel"};
        int[] years = {2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2019, 2020};
        for (int i = 0; i < makes.length; i++) {
            Vehicle vehicle = new Vehicle();
            vehicle.setYear(years[i]);
            vehicle.setModel(models[i]);
            vehicle.setMake(makes[i]);
            restTemplate.postForEntity(getRootUrl() + "/vehicles", vehicle, String.class);
        }
    }

    @Test
    public void checkAllVehiclesInDatabase() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        String[] makes = {"Acura", "Audi", "BMW", "Lexus", "Bentley",
                "Cadillac", "Chevrolet", "MercedesBenz", "Volkswagen", "Tesla"};
        String[] models = {"AcuraModel", "AudiModel", "BMWModel", "LexusModel", "BentleyModel",
                "CadillacModel", "ChevroletModel", "MercedesBenzModel", "VolkswagenModel", "TeslaModel"};
        int[] years = {2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2019, 2020};
        ResponseEntity<List<Vehicle>> response = restTemplate.exchange(getRootUrl() + "/vehicles",
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Vehicle>>() {});
        for(int i = 0; i < makes.length; i++) {
            Assert.assertEquals(Objects.requireNonNull(response.getBody()).get(i).getYear(), years[i]);
            Assert.assertEquals(response.getBody().get(i).getModel(), models[i]);
            Assert.assertEquals(response.getBody().get(i).getMake(), makes[i]);
        }
        Assert.assertNotNull(response.getBody());
    }

    @Test
    public void testGetAllVehicles() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/vehicles",
                HttpMethod.GET, entity, String.class);
        Assert.assertNotNull(response.getBody());
    }

    @Test
    public void testDeleteVehicleById()  {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        Vehicle vehicle = new Vehicle();
        vehicle.setYear(2001);
        vehicle.setModel("AcuraModel");
        vehicle.setMake("Acura");

        ResponseEntity<List<Vehicle>> list = restTemplate.exchange(getRootUrl() +
                        "/vehicles?yearBegin=2001&yearEnd=2001&make=Acura&model=AcuraModel",
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Vehicle>>() {});
        Assert.assertEquals(1, Objects.requireNonNull(list.getBody()).size());
    }

    @Test
    public void testGetVehicleById() {
        Vehicle vehicle = restTemplate.getForObject(getRootUrl() + "/vehicles/1", Vehicle.class);
        Assert.assertEquals(2001, vehicle.getYear());
        Assert.assertEquals("AcuraModel", vehicle.getModel());
        Assert.assertEquals( "Acura", vehicle.getMake());
    }

    @Test
    public void testUpdateVehicle() {
        HttpHeaders headers = new HttpHeaders();
        Vehicle vehicle = restTemplate.getForObject(getRootUrl() + "/vehicles/4", Vehicle.class);
        vehicle.setModel("LexusModel");
        System.out.println(vehicle.getId());
        HttpEntity<Vehicle> entity = new HttpEntity<>(vehicle, headers);
        ResponseEntity<Vehicle> updatedVehicle = restTemplate.exchange(getRootUrl() + "/vehicles",
                HttpMethod.PUT, entity, Vehicle.class);
        Assert.assertEquals(vehicle.getId(), Objects.requireNonNull(updatedVehicle.getBody()).getId());
        Assert.assertEquals(2004, updatedVehicle.getBody().getYear());
        Assert.assertEquals("LexusModel", updatedVehicle.getBody().getModel());
        Assert.assertEquals( "Lexus", updatedVehicle.getBody().getMake());
    }


    @Test
    public void testCreateVehicle() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        Vehicle vehicle = new Vehicle();
        vehicle.setYear(2021);
        vehicle.setModel("PorscheModel");
        vehicle.setMake("Porsche");
        ResponseEntity<Vehicle> vehicleCreated = restTemplate.postForEntity(getRootUrl() + "/vehicles", vehicle,
                Vehicle.class);
        ResponseEntity<Vehicle> vehicleGet = restTemplate.exchange(getRootUrl() + "/vehicles/" +
                Objects.requireNonNull(vehicleCreated.getBody()).getId(), HttpMethod.GET, entity, Vehicle.class);
        Assert.assertEquals(2021, Objects.requireNonNull(vehicleGet.getBody()).getYear());
        Assert.assertEquals("Porsche", vehicleGet.getBody().getMake());
        Assert.assertEquals("PorscheModel", vehicleGet.getBody().getModel());
    }

    @Test
    public void testFilterVehicleByYear() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        String[] makes = {"Acura", "Audi", "BMW", "Lexus", "Bentley",
                "Cadillac", "Chevrolet", "MercedesBenz", "Volkswagen", "Tesla"};
        String[] models = {"AcuraModel", "AudiModel", "BMWModel", "LexusModel", "BentleyModel",
                "CadillacModel", "ChevroletModel", "MercedesBenzModel", "VolkswagenModel", "TeslaModel"};
        int[] years = {2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2019, 2020};
        ResponseEntity<List<Vehicle>> response = restTemplate.exchange(getRootUrl() +
                        "/vehicles?yearBegin=2001&yearEnd=2020",
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Vehicle>>() {});

        for(int i = 0; i < 8; i++) {
            Assert.assertEquals(years[i], Objects.requireNonNull(response.getBody()).get(i).getYear());
            Assert.assertEquals(models[i], response.getBody().get(i).getModel());
            Assert.assertEquals(makes[i], response.getBody().get(i).getMake());
        }
    }

    @Test
    public void testFilterVehicleByMake() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        String[] makes = {"Acura", "Audi", "BMW", "Lexus", "Bentley",
                "Cadillac", "Chevrolet", "MercedesBenz", "Volkswagen", "Tesla"};
        String[] models = {"AcuraModel", "AudiModel", "BMWModel", "LexusModel", "BentleyModel",
                "CadillacModel", "ChevroletModel", "MercedesBenzModel", "VolkswagenModel", "TeslaModel"};
        int[] years = {2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2019, 2020};
        ResponseEntity<List<Vehicle>> response = restTemplate.exchange(getRootUrl() +
                        "/vehicles?make=Acura,Audi,BMW",
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Vehicle>>() {});
        for(int i = 0; i < 3; i++) {
            Assert.assertEquals(years[i], response.getBody().get(i).getYear());
            Assert.assertEquals(models[i], response.getBody().get(i).getModel());
            Assert.assertEquals(makes[i], response.getBody().get(i).getMake());
        }
    }

    @Test
    public void testFilterVehicleByModel() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        String[] makes = {"Acura", "Audi", "BMW", "Lexus", "Bentley",
                "Cadillac", "Chevrolet", "MercedesBenz", "Volkswagen", "Tesla"};
        String[] models = {"AcuraModel", "AudiModel", "BMWModel", "LexusModel", "BentleyModel",
                "CadillacModel", "ChevroletModel", "MercedesBenzModel", "VolkswagenModel", "TeslaModel"};
        int[] years = {2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2019, 2020};
        ResponseEntity<List<Vehicle>> response = restTemplate.exchange(getRootUrl() +
                        "/vehicles?model=AcuraModel,AudiModel,BMWModel",
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Vehicle>>() {});
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(years[i], response.getBody().get(i).getYear());
            Assert.assertEquals(models[i], response.getBody().get(i).getModel());
            Assert.assertEquals(makes[i], response.getBody().get(i).getMake());
        }
    }

    @Test
    public void testFilterVehicleByYearMakeModel() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        ResponseEntity<List<Vehicle>> response = restTemplate.exchange(getRootUrl() +
                        "/vehicles?model=ChevroletModel&yearBegin=2001&yearEnd=2019&make=Chevrolet",
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Vehicle>>() {});
        for(int i = 0; i < Objects.requireNonNull(response.getBody()).size(); i++) {
            Assert.assertEquals(2007, response.getBody().get(i).getYear());
            Assert.assertEquals("ChevroletModel", response.getBody().get(i).getModel());
            Assert.assertEquals("Chevrolet", response.getBody().get(i).getMake());
        }
    }

    @Test
    public void testFilterVehicleByMakeModel() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        ResponseEntity<List<Vehicle>> response = restTemplate.exchange(getRootUrl() +
                        "/vehicles?model=MercedesBenzModel&make=MercedesBenz",
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Vehicle>>() {});
        for(int i = 0; i < Objects.requireNonNull(response.getBody()).size(); i++) {
            Assert.assertEquals(2008, response.getBody().get(i).getYear());
            Assert.assertEquals("MercedesBenzModel", response.getBody().get(i).getModel());
            Assert.assertEquals("MercedesBenz", response.getBody().get(i).getMake());
        }
    }

    @Test
    public void testFilterVehicleByYearMake() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        ResponseEntity<List<Vehicle>> response = restTemplate.exchange(getRootUrl() +
                        "/vehicles?yearBegin=2002&yearEnd=2020&make=Audi",
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Vehicle>>() {});
        for(int i = 0; i < Objects.requireNonNull(response.getBody()).size(); i++) {
            Assert.assertEquals(2002, response.getBody().get(i).getYear());
            Assert.assertEquals("AudiModel", response.getBody().get(i).getModel());
            Assert.assertEquals("Audi", response.getBody().get(i).getMake());
        }
    }

    @Test
    public void testFilterVehicleByYearModel() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        ResponseEntity<List<Vehicle>> response = restTemplate.exchange(getRootUrl() +
                        "/vehicles?yearBegin=2006&yearEnd=2018&model=CadillacModel",
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Vehicle>>() {});

        for(int i = 0; i < Objects.requireNonNull(response.getBody()).size(); i++) {
            Assert.assertEquals(2006, response.getBody().get(i).getYear());
            Assert.assertEquals("CadillacModel", response.getBody().get(i).getModel());
            Assert.assertEquals("Cadillac", response.getBody().get(i).getMake());
        }
    }

}
