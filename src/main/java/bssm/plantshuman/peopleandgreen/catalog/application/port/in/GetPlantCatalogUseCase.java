package bssm.plantshuman.peopleandgreen.catalog.application.port.in;

import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogCursorPage;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogFilter;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogSortType;

public interface GetPlantCatalogUseCase {

    PlantCatalogCursorPage getCatalog(
            Long userId,
            String cursor,
            int size,
            PlantCatalogSortType sortType,
            PlantCatalogFilter filter
    );
}
