package bssm.plantshuman.peopleandgreen.infrastructure.persistence.plant;

import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogFilter;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogItem;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogSortType;

import java.util.List;

public interface PlantRepositoryCustom {

    List<PlantCatalogItem> findCatalogPage(
            String cursor,
            int sizePlusOne,
            PlantCatalogSortType sortType,
            PlantCatalogFilter filter
    );
}
