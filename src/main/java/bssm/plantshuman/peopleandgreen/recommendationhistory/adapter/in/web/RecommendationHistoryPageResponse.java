package bssm.plantshuman.peopleandgreen.recommendationhistory.adapter.in.web;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationHistoryCursorPage;

import java.time.Instant;
import java.util.List;

public record RecommendationHistoryPageResponse(
        List<Item> items,
        Long nextCursor,
        boolean hasNext
) {

    public static RecommendationHistoryPageResponse from(RecommendationHistoryCursorPage page) {
        return new RecommendationHistoryPageResponse(
                page.items().stream().map(item -> new Item(
                        item.historyId(),
                        item.title(),
                        item.plantSummaryText(),
                        item.createdAt()
                )).toList(),
                page.nextCursor(),
                page.hasNext()
        );
    }

    public record Item(
            Long historyId,
            String title,
            String plantSummaryText,
            Instant createdAt
    ) {
    }
}
