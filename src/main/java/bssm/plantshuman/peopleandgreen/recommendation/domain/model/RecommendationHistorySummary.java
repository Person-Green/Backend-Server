package bssm.plantshuman.peopleandgreen.recommendation.domain.model;

import java.time.Instant;

public record RecommendationHistorySummary(
        Long historyId,
        String title,
        String plantSummaryText,
        Instant createdAt
) {
}
