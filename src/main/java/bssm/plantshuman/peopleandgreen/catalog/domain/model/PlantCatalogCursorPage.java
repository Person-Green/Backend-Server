package bssm.plantshuman.peopleandgreen.catalog.domain.model;

import java.util.List;

public record PlantCatalogCursorPage(
        List<PlantCatalogView> plants,
        String nextCursor,
        boolean hasNext
) {
}
