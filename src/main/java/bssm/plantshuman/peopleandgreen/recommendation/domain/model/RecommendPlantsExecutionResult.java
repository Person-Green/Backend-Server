package bssm.plantshuman.peopleandgreen.recommendation.domain.model;

public record RecommendPlantsExecutionResult(
        Long historyId,
        boolean saved,
        RecommendPlantsResult result
) {
}
