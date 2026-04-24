package bssm.plantshuman.peopleandgreen.catalog.application.port.out;

import bssm.plantshuman.peopleandgreen.domain.plant.Plant;

import java.util.Optional;

public interface LoadPlantDetailPort {

    Optional<Plant> loadById(String plantId);
}
