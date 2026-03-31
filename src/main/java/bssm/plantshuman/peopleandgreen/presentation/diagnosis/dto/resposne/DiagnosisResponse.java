package bssm.plantshuman.peopleandgreen.presentation.diagnosis.dto.resposne;

import bssm.plantshuman.peopleandgreen.domain.diagnosis.PlantEnvironment;

public record DiagnosisResponse(
        String typeId,
        String typeName,
        String description,
        String sunlight,
        String ventilation,
        String temperature,
        String humidity
) {
    public static DiagnosisResponse from(PlantEnvironment env) {
        return new DiagnosisResponse(
                env.getTypeId(),
                env.getTypeName(),
                env.getDescription(),
                env.getSunlight(),
                env.getVentilation(),
                env.getTemperature(),
                env.getHumidity()
        );
    }
}
