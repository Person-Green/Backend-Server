package bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence;

import bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.entity.RecommendationPlantConditionEntity;
import bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.entity.RecommendationPlantEntity;
import bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.entity.RecommendationPlantEnvironmentFitEntity;
import bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.repository.RecommendationPlantRepository;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlantCatalogItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

@Component
@Profile("!json-catalog")
public class RecommendationCatalogSeedService {

    private final RecommendationPlantRepository recommendationPlantRepository;
    private final ObjectMapper objectMapper;

    public RecommendationCatalogSeedService(
            RecommendationPlantRepository recommendationPlantRepository,
            ObjectMapper objectMapper
    ) {
        this.recommendationPlantRepository = recommendationPlantRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    @Transactional
    void seedIfEmpty() {
        if (recommendationPlantRepository.count() > 0) {
            return;
        }
        recommendationPlantRepository.saveAll(loadSeedData().stream().map(this::toEntity).toList());
    }

    private List<PlantCatalogItem> loadSeedData() {
        ClassPathResource resource = new ClassPathResource("recommendation/plant-catalog.json");
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<>() {
            });
        } catch (IOException exception) {
            throw new UncheckedIOException("Failed to load recommendation seed data", exception);
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
}
