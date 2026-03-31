package bssm.plantshuman.peopleandgreen.presentation.diagnosis.dto.resposne;

import java.util.List;

public record DiagnosisResultResponse(
        DiagnosisResponse environment,
        List<PlantSummaryResponse> plants
) {
}
