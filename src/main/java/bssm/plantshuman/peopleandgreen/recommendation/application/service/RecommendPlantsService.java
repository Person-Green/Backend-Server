package bssm.plantshuman.peopleandgreen.recommendation.application.service;

import bssm.plantshuman.peopleandgreen.recommendation.application.port.in.RecommendPlantsUseCase;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.LoadPlantCatalogPort;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationPolicy;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsCommand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsResult;
import bssm.plantshuman.peopleandgreen.recommendation.domain.service.PlantRecommendationEngine;
import bssm.plantshuman.peopleandgreen.recommendation.domain.service.RepresentativeEnvironmentResolver;
import bssm.plantshuman.peopleandgreen.recommendation.domain.service.SecondaryEnvironmentTagResolver;
import org.springframework.stereotype.Service;

@Service
public class RecommendPlantsService implements RecommendPlantsUseCase {

    private final LoadPlantCatalogPort loadPlantCatalogPort;
    private final RecommendationPolicyProperties recommendationPolicyProperties;
    private final PlantRecommendationEngine plantRecommendationEngine;

    public RecommendPlantsService(LoadPlantCatalogPort loadPlantCatalogPort, RecommendationPolicyProperties recommendationPolicyProperties) {
        this.loadPlantCatalogPort = loadPlantCatalogPort;
        this.recommendationPolicyProperties = recommendationPolicyProperties;
        RecommendationPolicy recommendationPolicy = recommendationPolicyProperties.toPolicy();
        this.plantRecommendationEngine = new PlantRecommendationEngine(
                new RepresentativeEnvironmentResolver(),
                new SecondaryEnvironmentTagResolver(),
                recommendationPolicy
        );
    }

    @Override
    public RecommendPlantsResult recommend(RecommendPlantsCommand command) {
        RecommendPlantsResult recommendationResult = plantRecommendationEngine.recommendAll(
                command.toUserProfile(),
                loadPlantCatalogPort.loadCatalog()
        );

        return new RecommendPlantsResult(
                recommendationResult.representativeEnvironment(),
                recommendationResult.secondaryEnvironmentTags(),
                recommendationResult.plants().stream()
                        .limit(recommendationPolicyProperties.maxRecommendations())
                        .toList()
        );
    }
}
