package bssm.plantshuman.peopleandgreen.recommendation.application.service;

import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.LoadRecommendationFavoriteMetadataPort;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.RecommendationHistoryQueryPort;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.AirPurificationLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.CareLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.DifficultyLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.EnvironmentType;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.ExperienceLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.HumidityBand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PetSafetyLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlacementType;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlantRecommendation;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsCommand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsResult;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationFavoriteMetadata;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationHistory;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationHistoryCursorPage;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.SizeCategory;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.SunlightLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.TemperatureBand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.VentilationLevel;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetRecommendationHistoryServiceTest {

    @Test
    void returnsHistoryWithCurrentFavoriteMetadata() {
        GetRecommendationHistoryService service = new GetRecommendationHistoryService(
                new StubRecommendationHistoryQueryPort(),
                new StubLoadRecommendationFavoriteMetadataPort()
        );

        RecommendationHistory history = service.getHistory(1L, 10L);

        PlantRecommendation firstPlant = history.resultSnapshot().plants().getFirst();
        PlantRecommendation secondPlant = history.resultSnapshot().plants().get(1);
        assertEquals(true, firstPlant.isFavorite());
        assertEquals(9L, firstPlant.favoriteCount());
        assertEquals(false, secondPlant.isFavorite());
        assertEquals(3L, secondPlant.favoriteCount());
    }

    private static RecommendationHistory history() {
        return new RecommendationHistory(
                10L,
                1L,
                "햇빛이 잘드는 공간",
                "스투키, 몬스테라",
                new RecommendPlantsCommand(
                        SunlightLevel.HIGH,
                        VentilationLevel.NORMAL,
                        TemperatureBand.NORMAL,
                        HumidityBand.NORMAL,
                        CareLevel.MEDIUM,
                        ExperienceLevel.BEGINNER,
                        false,
                        PlacementType.LIVING_ROOM
                ),
                new RecommendPlantsResult(
                        EnvironmentType.ENV_01_SUNNY,
                        List.of(EnvironmentType.ENV_03_BRIGHT_INDIRECT),
                        List.of(
                                plant("PLT-001", "스투키", false, 0L),
                                plant("PLT-002", "몬스테라", true, 20L)
                        )
                ),
                Instant.parse("2026-04-14T10:00:00Z")
        );
    }

    private static PlantRecommendation plant(String plantId, String name, boolean isFavorite, long favoriteCount) {
        return new PlantRecommendation(
                plantId,
                name,
                name + "-EN",
                null,
                isFavorite,
                favoriteCount,
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

    private static final class StubRecommendationHistoryQueryPort implements RecommendationHistoryQueryPort {

        @Override
        public RecommendationHistoryCursorPage getHistories(Long userId, Long cursor, int size) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<RecommendationHistory> getHistory(Long userId, Long historyId) {
            assertEquals(1L, userId);
            assertEquals(10L, historyId);
            return Optional.of(history());
        }
    }

    private static final class StubLoadRecommendationFavoriteMetadataPort implements LoadRecommendationFavoriteMetadataPort {

        @Override
        public RecommendationFavoriteMetadata load(Long userId, Set<String> plantIds) {
            assertEquals(1L, userId);
            assertEquals(Set.of("PLT-001", "PLT-002"), plantIds);
            return new RecommendationFavoriteMetadata(
                    Set.of("PLT-001"),
                    Map.of(
                            "PLT-001", 9L,
                            "PLT-002", 3L
                    )
            );
        }
    }
}
