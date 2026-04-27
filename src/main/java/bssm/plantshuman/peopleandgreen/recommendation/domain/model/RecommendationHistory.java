package bssm.plantshuman.peopleandgreen.recommendation.domain.model;

import java.time.Instant;

public record RecommendationHistory(
        Long id,
        Long userId,
        String title,
        String plantSummaryText,
        RecommendPlantsCommand requestSnapshot,
        RecommendPlantsResult resultSnapshot,
        Instant createdAt
) {
}
