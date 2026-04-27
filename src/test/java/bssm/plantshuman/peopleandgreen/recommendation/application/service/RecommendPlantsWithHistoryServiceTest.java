package bssm.plantshuman.peopleandgreen.recommendation.application.service;

import bssm.plantshuman.peopleandgreen.recommendation.application.port.in.RecommendPlantsUseCase;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.RecommendationHistoryCommandPort;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.AirPurificationLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.DifficultyLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.EnvironmentType;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PetSafetyLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlacementType;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlantRecommendation;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsCommand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsExecutionResult;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsResult;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationHistoryDraft;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.SizeCategory;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecommendPlantsWithHistoryServiceTest {

    @Test
    void savesRecommendationHistoryAfterRecommendation() {
        RecordingRecommendationHistoryCommandPort historyCommandPort = new RecordingRecommendationHistoryCommandPort();
        RecommendPlantsWithHistoryService service = new RecommendPlantsWithHistoryService(
                command -> result(),
                historyCommandPort
        );

        RecommendPlantsExecutionResult executionResult = service.recommend(1L, command());

        assertEquals(1L, historyCommandPort.savedDraft.userId());
        assertEquals("햇빛이 잘드는 공간", historyCommandPort.savedDraft.title());
        assertEquals("스투키, 몬스테라, 산세베리아", historyCommandPort.savedDraft.plantSummaryText());
        assertEquals(55L, executionResult.historyId());
        assertEquals(true, executionResult.saved());
        assertEquals("스투키", executionResult.result().plants().getFirst().plantName());
    }

    private RecommendPlantsCommand command() {
        return new RecommendPlantsCommand(
                bssm.plantshuman.peopleandgreen.recommendation.domain.model.SunlightLevel.HIGH,
                bssm.plantshuman.peopleandgreen.recommendation.domain.model.VentilationLevel.NORMAL,
                bssm.plantshuman.peopleandgreen.recommendation.domain.model.TemperatureBand.NORMAL,
                bssm.plantshuman.peopleandgreen.recommendation.domain.model.HumidityBand.NORMAL,
                bssm.plantshuman.peopleandgreen.recommendation.domain.model.CareLevel.MEDIUM,
                bssm.plantshuman.peopleandgreen.recommendation.domain.model.ExperienceLevel.BEGINNER,
                false,
                PlacementType.LIVING_ROOM
        );
    }

    private RecommendPlantsResult result() {
        return new RecommendPlantsResult(
                EnvironmentType.ENV_01_SUNNY,
                List.of(EnvironmentType.ENV_03_BRIGHT_INDIRECT),
                List.of(
                        plant("PLT-001", "스투키"),
                        plant("PLT-002", "몬스테라"),
                        plant("PLT-003", "산세베리아")
                )
        );
    }

    private PlantRecommendation plant(String plantId, String name) {
        return new PlantRecommendation(
                plantId,
                name,
                name + "-EN",
                90,
                List.of("이유"),
                List.of("주의"),
                EnvironmentType.ENV_01_SUNNY,
                List.of(EnvironmentType.ENV_03_BRIGHT_INDIRECT),
                AirPurificationLevel.HIGH,
                PetSafetyLevel.SAFE,
                DifficultyLevel.EASY,
                SizeCategory.MEDIUM,
                List.of(PlacementType.LIVING_ROOM),
                "설명"
        );
    }

    private static final class RecordingRecommendationHistoryCommandPort implements RecommendationHistoryCommandPort {

        private RecommendationHistoryDraft savedDraft;

        @Override
        public Long save(RecommendationHistoryDraft draft) {
            this.savedDraft = draft;
            return 55L;
        }

        @Override
        public void delete(Long userId, Long historyId) {
        }
    }
}
