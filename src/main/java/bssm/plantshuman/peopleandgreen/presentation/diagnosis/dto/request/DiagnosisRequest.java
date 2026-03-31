package bssm.plantshuman.peopleandgreen.presentation.diagnosis.dto.request;

public record DiagnosisRequest(
        String sunlight,
        String temperature,
        String humidity,
        String ventilation
) {
}
