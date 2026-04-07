package bssm.plantshuman.peopleandgreen.recommendation.adapter.in.web.dto.request;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.CareLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.ExperienceLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.HumidityBand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlacementType;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsCommand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.SunlightLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.TemperatureBand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.VentilationLevel;
import jakarta.validation.constraints.NotNull;

public record RecommendPlantsRequest(
        @NotNull(message = "sunlight is required")
        SunlightLevel sunlight,
        @NotNull(message = "ventilation is required")
        VentilationLevel ventilation,
        @NotNull(message = "temperature is required")
        TemperatureBand temperature,
        @NotNull(message = "humidity is required")
        HumidityBand humidity,
        @NotNull(message = "careLevel is required")
        CareLevel careLevel,
        @NotNull(message = "experienceLevel is required")
        ExperienceLevel experienceLevel,
        @NotNull(message = "hasPet is required")
        Boolean hasPet,
        @NotNull(message = "placement is required")
        PlacementType placement
) {

    public RecommendPlantsCommand toCommand() {
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
}
