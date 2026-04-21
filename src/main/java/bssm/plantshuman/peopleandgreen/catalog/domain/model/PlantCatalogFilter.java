package bssm.plantshuman.peopleandgreen.catalog.domain.model;

import bssm.plantshuman.peopleandgreen.domain.plant.AirPurification;
import bssm.plantshuman.peopleandgreen.domain.plant.ManageDifficulty;

import java.util.Set;

public record PlantCatalogFilter(
        String keyword,
        Set<ManageDifficulty> manageDifficulties,
        Set<AirPurification> airPurifications,
        Set<String> sizes,
        Set<String> environmentTypeIds
) {

    public PlantCatalogFilter {
        keyword = normalizeKeyword(keyword);
        manageDifficulties = manageDifficulties == null ? Set.of() : Set.copyOf(manageDifficulties);
        airPurifications = airPurifications == null ? Set.of() : Set.copyOf(airPurifications);
        sizes = sizes == null ? Set.of() : Set.copyOf(sizes);
        environmentTypeIds = environmentTypeIds == null ? Set.of() : Set.copyOf(environmentTypeIds);
    }

    public static PlantCatalogFilter of(
            String keyword,
            Set<ManageDifficulty> manageDifficulties,
            Set<AirPurification> airPurifications,
            Set<String> sizes,
            Set<String> environmentTypeIds
    ) {
        return new PlantCatalogFilter(keyword, manageDifficulties, airPurifications, sizes, environmentTypeIds);
    }

    public static PlantCatalogFilter empty() {
        return new PlantCatalogFilter(null, Set.of(), Set.of(), Set.of(), Set.of());
    }

    private static String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        String trimmed = keyword.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
