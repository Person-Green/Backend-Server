package bssm.plantshuman.peopleandgreen.recommendationhistory.adapter.in.web;

import bssm.plantshuman.peopleandgreen.recommendation.adapter.in.web.dto.response.PlantRecommendationResponse;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsCommand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsResult;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationHistory;

import java.time.Instant;
import java.util.List;

public record RecommendationHistoryDetailResponse(
        Long historyId,
        String title,
        String plantSummaryText,
        Instant createdAt,
        RequestSnapshot request,
        ResultSnapshot result
) {

    public static RecommendationHistoryDetailResponse from(RecommendationHistory history) {
        return new RecommendationHistoryDetailResponse(
                history.id(),
                history.title(),
                history.plantSummaryText(),
                history.createdAt(),
                RequestSnapshot.from(history.requestSnapshot()),
                ResultSnapshot.from(history.resultSnapshot())
        );
    }

    public record RequestSnapshot(
            String sunlight,
            String ventilation,
            String temperature,
            String humidity,
            String careLevel,
            String experienceLevel,
            boolean hasPet,
            String placement
    ) {
        static RequestSnapshot from(RecommendPlantsCommand command) {
            return new RequestSnapshot(
                    command.sunlight().name(),
                    command.ventilation().name(),
                    command.temperature().name(),
                    command.humidity().name(),
                    command.careLevel().name(),
                    command.experienceLevel().name(),
                    command.hasPet(),
                    command.placement().name()
            );
        }
    }

    public record ResultSnapshot(
            String representativeEnvironment,
            List<String> secondaryEnvironmentTags,
            List<PlantRecommendationResponse> plants
    ) {
        static ResultSnapshot from(RecommendPlantsResult result) {
            return new ResultSnapshot(
                    result.representativeEnvironment().name(),
                    result.secondaryEnvironmentTags().stream().map(Enum::name).toList(),
                    result.plants().stream().map(PlantRecommendationResponse::from).toList()
            );
        }
    }
}
