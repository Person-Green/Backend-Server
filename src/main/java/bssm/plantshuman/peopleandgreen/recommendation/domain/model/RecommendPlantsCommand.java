package bssm.plantshuman.peopleandgreen.recommendation.domain.model;

public record RecommendPlantsCommand(
        SunlightLevel sunlight,
        VentilationLevel ventilation,
        TemperatureBand temperature,
        HumidityBand humidity,
        CareLevel careLevel,
        ExperienceLevel experienceLevel,
        boolean hasPet,
        PlacementType placement
) {

    public UserProfile toUserProfile() {
        return new UserProfile(
                new UserEnvironment(sunlight, ventilation, temperature, humidity),
                careLevel,
                experienceLevel,
                hasPet,
                placement
        );
    }
}
