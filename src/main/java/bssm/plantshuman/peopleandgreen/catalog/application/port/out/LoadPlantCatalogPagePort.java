package bssm.plantshuman.peopleandgreen.catalog.application.port.out;

import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogItem;

import java.util.List;
import java.util.Set;

public interface LoadPlantCatalogPagePort {

    List<PlantCatalogItem> loadPage(String cursor, int sizePlusOne);

    Set<String> loadFavoritePlantIds(Long userId, Set<String> plantIds);
}
