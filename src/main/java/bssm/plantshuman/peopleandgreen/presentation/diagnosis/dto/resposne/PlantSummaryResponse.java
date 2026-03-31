package bssm.plantshuman.peopleandgreen.presentation.diagnosis.dto.resposne;

import bssm.plantshuman.peopleandgreen.domain.diagnosis.FitType;
import bssm.plantshuman.peopleandgreen.domain.plant.Plant;

public record PlantSummaryResponse(
        String plantId,
        String plantKoreanName,
        String plantEnglishName,
        String manageDifficulty,
        String airPurification,
        String size,
        String description,
        String fit
) {
    public static PlantSummaryResponse from(Plant plant, FitType fit) {
        return new PlantSummaryResponse(
                plant.getPlantId(),
                plant.getPlantKoreanName(),
                plant.getPlantEnglishName(),
                plant.getManageDifficulty().name(),
                plant.getAirPurification().getDescription(),
                plant.getSize(),
                plant.getDescription(),
                fit.name()
        );
    }
}
