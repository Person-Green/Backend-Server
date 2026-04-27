package bssm.plantshuman.peopleandgreen.recommendation.domain.model;

import java.util.List;

public record RecommendationHistoryCursorPage(
        List<RecommendationHistorySummary> items,
        String nextCursor,
        boolean hasNext
) {

    public RecommendationHistoryCursorPage {
        items = List.copyOf(items);
    }
}
