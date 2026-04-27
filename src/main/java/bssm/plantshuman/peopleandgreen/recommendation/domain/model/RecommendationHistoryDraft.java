package bssm.plantshuman.peopleandgreen.recommendation.domain.model;

public record RecommendationHistoryDraft(
        Long userId,
        String title,
        String plantSummaryText,
        RecommendPlantsCommand requestSnapshot,
        RecommendPlantsResult resultSnapshot
) {
}
