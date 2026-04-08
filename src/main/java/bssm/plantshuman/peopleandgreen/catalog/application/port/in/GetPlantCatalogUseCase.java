package bssm.plantshuman.peopleandgreen.catalog.application.port.in;

import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogCursorPage;

public interface GetPlantCatalogUseCase {

    PlantCatalogCursorPage getCatalog(Long userId, String cursor, int size);
}
