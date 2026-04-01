package bssm.plantshuman.peopleandgreen.recommendation.domain.model;

public record UserEnvironment(
        SunlightLevel sunlight,
        VentilationLevel ventilation,
        TemperatureBand temperature,
        HumidityBand humidity
) {
}
