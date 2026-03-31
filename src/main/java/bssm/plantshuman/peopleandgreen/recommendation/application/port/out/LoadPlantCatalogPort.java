package bssm.plantshuman.peopleandgreen.recommendation.application.port.out;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlantCatalogItem;

import java.util.List;

public interface LoadPlantCatalogPort {

    List<PlantCatalogItem> loadCatalog();
}
