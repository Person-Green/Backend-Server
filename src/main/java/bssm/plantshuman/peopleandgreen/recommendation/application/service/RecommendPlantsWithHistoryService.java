package bssm.plantshuman.peopleandgreen.recommendation.application.service;

import bssm.plantshuman.peopleandgreen.recommendation.application.port.in.RecommendPlantsUseCase;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.in.RecommendPlantsWithHistoryUseCase;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.LoadRecommendationFavoriteMetadataPort;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.RecommendationHistoryCommandPort;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.EnvironmentType;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsCommand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsExecutionResult;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsResult;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationFavoriteMetadata;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationHistoryDraft;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendPlantsWithHistoryService implements RecommendPlantsWithHistoryUseCase {

    private final RecommendPlantsUseCase recommendPlantsUseCase;
    private final LoadRecommendationFavoriteMetadataPort loadRecommendationFavoriteMetadataPort;
    private final RecommendationHistoryCommandPort recommendationHistoryCommandPort;

    @Override
    public RecommendPlantsExecutionResult recommend(Long userId, RecommendPlantsCommand command) {
        RecommendPlantsResult result = withFavoriteMetadata(userId, recommendPlantsUseCase.recommend(command));
        RecommendationHistoryDraft draft = new RecommendationHistoryDraft(
                userId,
                titleFor(result.representativeEnvironment()),
                summarizePlants(result),
                command,
                result.withoutFavoriteMetadata()
        );
        Long historyId = recommendationHistoryCommandPort.save(draft);
        return new RecommendPlantsExecutionResult(historyId, true, result);
    }

    private RecommendPlantsResult withFavoriteMetadata(Long userId, RecommendPlantsResult result) {
        RecommendationFavoriteMetadata favoriteMetadata = loadRecommendationFavoriteMetadataPort.load(userId, result.plantIds());
        return result.withFavoriteMetadata(favoriteMetadata);
    }

    private String summarizePlants(RecommendPlantsResult result) {
        return result.plants().stream()
                .limit(3)
                .map(plant -> plant.plantName())
                .toList()
                .stream()
                .reduce((first, second) -> first + ", " + second)
                .orElse("");
    }

    private String titleFor(EnvironmentType environmentType) {
        return switch (environmentType) {
            case ENV_01_SUNNY -> "햇빛이 잘드는 공간";
            case ENV_02_DARK -> "빛이 적은 공간";
            case ENV_03_BRIGHT_INDIRECT -> "은은한 빛이 드는 공간";
            case ENV_04_HOT -> "따뜻한 공간";
            case ENV_05_COLD -> "서늘한 공간";
            case ENV_06_DRY -> "건조한 공간";
            case ENV_07_HUMID -> "습한 공간";
            case ENV_08_WELL_VENTILATED -> "환기가 잘되는 공간";
            case ENV_09_CLOSED -> "바람이 적은 공간";
        };
    }
}
