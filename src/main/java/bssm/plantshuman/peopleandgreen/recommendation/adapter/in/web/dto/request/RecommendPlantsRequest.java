package bssm.plantshuman.peopleandgreen.recommendation.adapter.in.web.dto.request;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.CareLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.ExperienceLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.HumidityBand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlacementType;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsCommand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.SunlightLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.TemperatureBand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.VentilationLevel;

public record RecommendPlantsRequest(
        SunlightLevel sunlight,
        VentilationLevel ventilation,
        TemperatureBand temperature,
        HumidityBand humidity,
        CareLevel careLevel,
        ExperienceLevel experienceLevel,
        boolean hasPet,
        PlacementType placement
) {

    public RecommendPlantsCommand toCommand() {
        validateRequiredFields();
        return new RecommendPlantsCommand(
                sunlight,
                ventilation,
                temperature,
                humidity,
                careLevel,
                experienceLevel,
                hasPet,
                placement
        );
    }

    private void validateRequiredFields() {
        if (sunlight == null) {
            throw new IllegalArgumentException("sunlight is required");
        }
        if (ventilation == null) {
            throw new IllegalArgumentException("ventilation is required");
        }
        if (temperature == null) {
            throw new IllegalArgumentException("temperature is required");
        }
        if (humidity == null) {
            throw new IllegalArgumentException("humidity is required");
        }
        if (careLevel == null) {
            throw new IllegalArgumentException("careLevel is required");
        }
        if (experienceLevel == null) {
            throw new IllegalArgumentException("experienceLevel is required");
        }
        if (placement == null) {
            throw new IllegalArgumentException("placement is required");
        }
    }
}
