package bssm.plantshuman.peopleandgreen.catalog.domain.model;

import bssm.plantshuman.peopleandgreen.domain.plant.AirPurification;
import bssm.plantshuman.peopleandgreen.domain.plant.ManageDifficulty;

public record PlantCatalogItem(
        String plantId,
        String plantKoreanName,
        String plantEnglishName,
        String size,
        AirPurification airPurification,
        ManageDifficulty manageDifficulty
) {
}
