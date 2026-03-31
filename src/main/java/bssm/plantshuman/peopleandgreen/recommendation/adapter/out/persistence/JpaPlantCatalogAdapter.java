package bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence;

import bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.entity.RecommendationPlantEntity;
import bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.repository.RecommendationPlantRepository;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.LoadPlantCatalogPort;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.EnvironmentFit;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlantCatalogItem;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlantCondition;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("!json-catalog")
public class JpaPlantCatalogAdapter implements LoadPlantCatalogPort {

    private final RecommendationPlantRepository recommendationPlantRepository;

    public JpaPlantCatalogAdapter(RecommendationPlantRepository recommendationPlantRepository) {
        this.recommendationPlantRepository = recommendationPlantRepository;
    }

    @Override
    public List<PlantCatalogItem> loadCatalog() {
        return recommendationPlantRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private PlantCatalogItem toDomain(RecommendationPlantEntity entity) {
        PlantCondition condition = new PlantCondition(
                entity.getCondition().getSunlightLevel(),
                entity.getCondition().getVentilationNeed(),
                entity.getCondition().getTempMin(),
                entity.getCondition().getTempMax(),
                entity.getCondition().getHumidityMin(),
                entity.getCondition().getHumidityMax(),
                entity.getCondition().getWaterCycleMinDays(),
                entity.getCondition().getWaterCycleMaxDays(),
                entity.getCondition().getSupportedPlacements()
        );

        List<EnvironmentFit> environmentFits = entity.getEnvironmentFits().stream()
                .map(fit -> new EnvironmentFit(fit.getEnvironmentType(), fit.getFitLevel()))
                .toList();

        return new PlantCatalogItem(
                entity.getPlantId(),
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
                condition,
                environmentFits
        );
    }
}
