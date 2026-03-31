package bssm.plantshuman.peopleandgreen.recommendation.application.service;

import bssm.plantshuman.peopleandgreen.recommendation.application.port.in.RecommendPlantsUseCase;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.LoadPlantCatalogPort;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.EnvironmentType;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlantRecommendation;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationPolicy;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsCommand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsResult;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.UserProfile;
import bssm.plantshuman.peopleandgreen.recommendation.domain.service.PlantRecommendationEngine;
import bssm.plantshuman.peopleandgreen.recommendation.domain.service.RepresentativeEnvironmentResolver;
import bssm.plantshuman.peopleandgreen.recommendation.domain.service.SecondaryEnvironmentTagResolver;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendPlantsService implements RecommendPlantsUseCase {

    private final LoadPlantCatalogPort loadPlantCatalogPort;
    private final RepresentativeEnvironmentResolver representativeEnvironmentResolver;
    private final SecondaryEnvironmentTagResolver secondaryEnvironmentTagResolver;
    private final PlantRecommendationEngine plantRecommendationEngine;

    public RecommendPlantsService(
            LoadPlantCatalogPort loadPlantCatalogPort,
            RecommendationPolicyProperties recommendationPolicyProperties
    ) {
        this.loadPlantCatalogPort = loadPlantCatalogPort;
        this.representativeEnvironmentResolver = new RepresentativeEnvironmentResolver();
        this.secondaryEnvironmentTagResolver = new SecondaryEnvironmentTagResolver();
        RecommendationPolicy recommendationPolicy = recommendationPolicyProperties.toPolicy();
        this.plantRecommendationEngine = new PlantRecommendationEngine(
                representativeEnvironmentResolver,
                secondaryEnvironmentTagResolver,
                recommendationPolicy
        );
    }

    @Override
    public RecommendPlantsResult recommend(RecommendPlantsCommand command) {
        UserProfile userProfile = command.toUserProfile();
        EnvironmentType representativeEnvironment =
                representativeEnvironmentResolver.resolve(userProfile.environment());
        List<EnvironmentType> secondaryTags =
                secondaryEnvironmentTagResolver.resolve(userProfile.environment(), representativeEnvironment);
        List<PlantRecommendation> recommendations = plantRecommendationEngine.recommend(
                userProfile,
                loadPlantCatalogPort.loadCatalog()
        );

        return new RecommendPlantsResult(
                representativeEnvironment,
                secondaryTags,
                recommendations.stream().limit(10).toList()
        );
    }
}
