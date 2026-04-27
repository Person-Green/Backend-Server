package bssm.plantshuman.peopleandgreen.recommendationhistory.adapter.in.web;

import bssm.plantshuman.peopleandgreen.auth.adapter.out.security.AuthenticatedUser;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsCommand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsResult;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.in.DeleteRecommendationHistoryUseCase;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.in.GetRecommendationHistoryUseCase;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.in.GetRecommendationHistoriesUseCase;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationHistory;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationHistoryCursorPage;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationHistorySummary;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecommendationHistoryControllerTest {

    @Test
    void returnsHistoryPageForAuthenticatedUser() {
        RecommendationHistoryController controller = new RecommendationHistoryController(
                (userId, cursor, size) -> new RecommendationHistoryCursorPage(
                        List.of(new RecommendationHistorySummary(10L, "햇빛이 잘드는 공간", "스투키, 몬스테라", Instant.parse("2026-04-14T10:00:00Z"))),
                        "10",
                        false
                ),
                (userId, historyId) -> history(historyId),
                (userId, historyId) -> {}
        );

        ResponseEntity<RecommendationHistoryPageResponse> response = controller.getHistories(new AuthenticatedUser(1L), null, 20);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(10L, response.getBody().items().getFirst().historyId());
    }

    @Test
    void returnsHistoryDetailForAuthenticatedUser() {
        RecommendationHistoryController controller = new RecommendationHistoryController(
                (userId, cursor, size) -> new RecommendationHistoryCursorPage(List.of(), null, false),
                (userId, historyId) -> history(historyId),
                (userId, historyId) -> {}
        );

        ResponseEntity<RecommendationHistoryDetailResponse> response = controller.getHistory(new AuthenticatedUser(1L), 10L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(10L, response.getBody().historyId());
        assertEquals("햇빛이 잘드는 공간", response.getBody().title());
    }

    @Test
    void deletesHistoryForAuthenticatedUser() {
        RecordingDeleteRecommendationHistoryUseCase useCase = new RecordingDeleteRecommendationHistoryUseCase();
        RecommendationHistoryController controller = new RecommendationHistoryController(
                (userId, cursor, size) -> new RecommendationHistoryCursorPage(List.of(), null, false),
                (userId, historyId) -> history(historyId),
                useCase
        );

        ResponseEntity<Void> response = controller.deleteHistory(new AuthenticatedUser(1L), 10L);

        assertEquals(204, response.getStatusCode().value());
        assertEquals(1L, useCase.userId);
        assertEquals(10L, useCase.historyId);
    }

    private static RecommendationHistory history(Long historyId) {
        return new RecommendationHistory(
                historyId,
                1L,
                "햇빛이 잘드는 공간",
                "스투키, 몬스테라",
                new RecommendPlantsCommand(
                        bssm.plantshuman.peopleandgreen.recommendation.domain.model.SunlightLevel.HIGH,
                        bssm.plantshuman.peopleandgreen.recommendation.domain.model.VentilationLevel.NORMAL,
                        bssm.plantshuman.peopleandgreen.recommendation.domain.model.TemperatureBand.NORMAL,
                        bssm.plantshuman.peopleandgreen.recommendation.domain.model.HumidityBand.NORMAL,
                        bssm.plantshuman.peopleandgreen.recommendation.domain.model.CareLevel.MEDIUM,
                        bssm.plantshuman.peopleandgreen.recommendation.domain.model.ExperienceLevel.BEGINNER,
                        false,
                        bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlacementType.LIVING_ROOM
                ),
                new RecommendPlantsResult(
                        bssm.plantshuman.peopleandgreen.recommendation.domain.model.EnvironmentType.ENV_01_SUNNY,
                        List.of(),
                        List.of()
                ),
                Instant.parse("2026-04-14T10:00:00Z")
        );
    }

    private static final class RecordingDeleteRecommendationHistoryUseCase implements DeleteRecommendationHistoryUseCase {

        private Long userId;
        private Long historyId;

        @Override
        public void delete(Long userId, Long historyId) {
            this.userId = userId;
            this.historyId = historyId;
        }
    }
}
