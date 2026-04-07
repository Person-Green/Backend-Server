package bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence;

import bssm.plantshuman.peopleandgreen.recommendation.domain.exception.FailedLoadDataException;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.AirPurificationLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.DifficultyLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.EnvironmentType;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.FitLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PetSafetyLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlacementType;
import bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.entity.RecommendationPlantConditionEntity;
import bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.entity.RecommendationPlantEntity;
import bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.entity.RecommendationPlantEnvironmentFitEntity;
import bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.repository.RecommendationPlantRepository;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlantCatalogItem;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.SizeCategory;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.SunlightLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.VentilationLevel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Profile("!json-catalog")
public class RecommendationCatalogSeedService implements ApplicationRunner {

    private final RecommendationPlantRepository recommendationPlantRepository;
    private final ObjectMapper objectMapper;

    public RecommendationCatalogSeedService(
            RecommendationPlantRepository recommendationPlantRepository,
            ObjectMapper objectMapper
    ) {
        this.recommendationPlantRepository = recommendationPlantRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @Override
    public void run(ApplicationArguments args) {
        seedCatalog();
    }

    void seedCatalog() {
        List<PlantCatalogItem> seedData = loadSeedData();
        if (recommendationPlantRepository.existsAnyPlant()) {
            validateCatalog(seedData, recommendationPlantRepository.findAll());
            return;
        }
        recommendationPlantRepository.saveAll(seedData.stream().map(this::toEntity).toList());
    }

    private List<PlantCatalogItem> loadSeedData() {
        ClassPathResource resource = new ClassPathResource("recommendation/plant-catalog.json");
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<>() {
            });
        } catch (IOException exception) {
            throw new FailedLoadDataException();
        }
    }

    private RecommendationPlantEntity toEntity(PlantCatalogItem item) {
        RecommendationPlantEntity plant = new RecommendationPlantEntity(
                item.plantId(),
                item.nameKo(),
                item.nameEn(),
                item.difficulty(),
                item.petSafety(),
                item.airPurificationLevel(),
                item.sizeCategory(),
                item.displayWaterCycle(),
                item.displayTempRange(),
                item.displayHumidityRange(),
                item.displayLightRequirement(),
                item.oneLineDescription(),
                item.recommendedLocationText()
        );

        plant.attachCondition(new RecommendationPlantConditionEntity(
                plant,
                item.condition().sunlightLevel(),
                item.condition().ventilationNeed(),
                item.condition().tempMin(),
                item.condition().tempMax(),
                item.condition().humidityMin(),
                item.condition().humidityMax(),
                item.condition().waterCycleMinDays(),
                item.condition().waterCycleMaxDays(),
                item.condition().supportedPlacements()
        ));

        item.environmentFits().forEach(environmentFit -> plant.addEnvironmentFit(
                new RecommendationPlantEnvironmentFitEntity(
                        plant,
                        environmentFit.environmentType(),
                        environmentFit.fitLevel()
                )
        ));

        return plant;
    }

    private void validateCatalog(List<PlantCatalogItem> seedData, List<RecommendationPlantEntity> persistedPlants) {
        Map<String, CatalogSnapshot> expected = seedData.stream()
                .collect(Collectors.toMap(PlantCatalogItem::plantId, this::toSnapshot));
        Map<String, CatalogSnapshot> actual = persistedPlants.stream()
                .collect(Collectors.toMap(RecommendationPlantEntity::getPlantId, this::toSnapshot));

        if (!expected.equals(actual)) {
            throw new IllegalStateException("Recommendation catalog does not match bundled seed data");
        }
    }

    private CatalogSnapshot toSnapshot(PlantCatalogItem item) {
        return new CatalogSnapshot(
                item.nameKo(),
                item.nameEn(),
                item.difficulty(),
                item.petSafety(),
                item.airPurificationLevel(),
                item.sizeCategory(),
                item.displayWaterCycle(),
                item.displayTempRange(),
                item.displayHumidityRange(),
                item.displayLightRequirement(),
                item.oneLineDescription(),
                item.recommendedLocationText(),
                item.condition().sunlightLevel(),
                item.condition().ventilationNeed(),
                item.condition().tempMin(),
                item.condition().tempMax(),
                item.condition().humidityMin(),
                item.condition().humidityMax(),
                item.condition().waterCycleMinDays(),
                item.condition().waterCycleMaxDays(),
                item.condition().supportedPlacements(),
                item.environmentFits().stream()
                        .map(fit -> new EnvironmentFitSnapshot(fit.environmentType(), fit.fitLevel()))
                        .collect(Collectors.toSet())
        );
    }

    private CatalogSnapshot toSnapshot(RecommendationPlantEntity entity) {
        RecommendationPlantConditionEntity condition = entity.getCondition();
        return new CatalogSnapshot(
                entity.getNameKo(),
                entity.getNameEn(),
                entity.getDifficulty(),
                entity.getPetSafety(),
                entity.getAirPurificationLevel(),
                entity.getSizeCategory(),
                entity.getDisplayWaterCycle(),
                entity.getDisplayTempRange(),
                entity.getDisplayHumidityRange(),
                entity.getDisplayLightRequirement(),
                entity.getOneLineDescription(),
                entity.getRecommendedLocationText(),
                condition == null ? null : condition.getSunlightLevel(),
                condition == null ? null : condition.getVentilationNeed(),
                condition == null ? null : condition.getTempMin(),
                condition == null ? null : condition.getTempMax(),
                condition == null ? null : condition.getHumidityMin(),
                condition == null ? null : condition.getHumidityMax(),
                condition == null ? null : condition.getWaterCycleMinDays(),
                condition == null ? null : condition.getWaterCycleMaxDays(),
                condition == null ? Set.of() : condition.getSupportedPlacements(),
                entity.getEnvironmentFits().stream()
                        .map(fit -> new EnvironmentFitSnapshot(fit.getEnvironmentType(), fit.getFitLevel()))
                        .collect(Collectors.toSet())
        );
    }

    private record CatalogSnapshot(
            String nameKo,
            String nameEn,
            DifficultyLevel difficulty,
            PetSafetyLevel petSafety,
            AirPurificationLevel airPurificationLevel,
            SizeCategory sizeCategory,
            String displayWaterCycle,
            String displayTempRange,
            String displayHumidityRange,
            String displayLightRequirement,
            String oneLineDescription,
            String recommendedLocationText,
            SunlightLevel sunlightLevel,
            VentilationLevel ventilationNeed,
            Integer tempMin,
            Integer tempMax,
            Integer humidityMin,
            Integer humidityMax,
            Integer waterCycleMinDays,
            Integer waterCycleMaxDays,
            Set<PlacementType> supportedPlacements,
            Set<EnvironmentFitSnapshot> environmentFits
    ) {
    }

    private record EnvironmentFitSnapshot(
            EnvironmentType environmentType,
            FitLevel fitLevel
    ) {
    }
}
