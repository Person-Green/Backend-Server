package bssm.plantshuman.peopleandgreen.recommendation.adapter.in.web;

import bssm.plantshuman.peopleandgreen.auth.adapter.out.security.AuthenticatedUser;
import bssm.plantshuman.peopleandgreen.recommendation.adapter.in.web.dto.request.RecommendPlantsRequest;
import bssm.plantshuman.peopleandgreen.recommendation.adapter.in.web.dto.response.RecommendPlantsResponse;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.in.RecommendPlantsWithHistoryUseCase;
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
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsExecutionResult;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsResult;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.SizeCategory;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.SunlightLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.TemperatureBand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.VentilationLevel;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlantRecommendationControllerTest {

    @Test
    void mapsUseCaseResultToResponseBody() {
        RecommendPlantsWithHistoryUseCase useCase = new StubRecommendPlantsUseCase();
        PlantRecommendationController controller = new PlantRecommendationController(useCase);

        RecommendPlantsRequest request = new RecommendPlantsRequest(
                SunlightLevel.MEDIUM,
                VentilationLevel.NORMAL,
                TemperatureBand.NORMAL,
                HumidityBand.HIGH,
                CareLevel.LOW,
                ExperienceLevel.BEGINNER,
                Boolean.TRUE,
                PlacementType.BATHROOM
        );

        ResponseEntity<RecommendPlantsResponse> response = controller.recommend(new AuthenticatedUser(1L), request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(23L, response.getBody().historyId());
        assertEquals(true, response.getBody().saved());
        assertEquals("ENV_07_HUMID", response.getBody().representativeEnvironment());
        assertEquals("PLT-023", response.getBody().plants().getFirst().plantId());
        assertEquals(88, response.getBody().plants().getFirst().score());
    }

    @Test
    void rejectsRequestWhenRequiredFieldIsMissing() {
        RecommendPlantsWithHistoryUseCase useCase = new StubRecommendPlantsUseCase();
        PlantRecommendationController controller = new PlantRecommendationController(useCase);

        RecommendPlantsRequest request = new RecommendPlantsRequest(
                null,
                VentilationLevel.NORMAL,
                TemperatureBand.NORMAL,
                HumidityBand.HIGH,
                CareLevel.LOW,
                ExperienceLevel.BEGINNER,
                Boolean.TRUE,
                PlacementType.BATHROOM
        );

        assertThrows(IllegalArgumentException.class, () -> controller.recommend(new AuthenticatedUser(1L), request));
    }

    @Test
    void rejectsRequestWhenHasPetIsMissing() {
        RecommendPlantsWithHistoryUseCase useCase = new StubRecommendPlantsUseCase();
        PlantRecommendationController controller = new PlantRecommendationController(useCase);

        RecommendPlantsRequest request = new RecommendPlantsRequest(
                SunlightLevel.MEDIUM,
                VentilationLevel.NORMAL,
                TemperatureBand.NORMAL,
                HumidityBand.HIGH,
                CareLevel.LOW,
                ExperienceLevel.BEGINNER,
                null,
                PlacementType.BATHROOM
        );

        assertThrows(IllegalArgumentException.class, () -> controller.recommend(new AuthenticatedUser(1L), request));
    }

    private static final class StubRecommendPlantsUseCase implements RecommendPlantsWithHistoryUseCase {

        @Override
        public RecommendPlantsExecutionResult recommend(Long userId, RecommendPlantsCommand command) {
            return new RecommendPlantsExecutionResult(
                    23L,
                    true,
                    new RecommendPlantsResult(
                    EnvironmentType.ENV_07_HUMID,
                    List.of(EnvironmentType.ENV_03_BRIGHT_INDIRECT),
                    List.of(new PlantRecommendation(
                            "PLT-023",
                            "틸란드시아",
                            "Tillandsia",
                            88,
                            List.of("현재 습도 환경에 잘 맞아요."),
                            List.of("반려동물이 잎을 씹지 않도록 주의해 주세요."),
                            EnvironmentType.ENV_07_HUMID,
                            List.of(EnvironmentType.ENV_03_BRIGHT_INDIRECT),
                            AirPurificationLevel.MEDIUM,
                            PetSafetyLevel.CAUTION,
                            DifficultyLevel.EASY,
                            SizeCategory.SMALL,
                            List.of(PlacementType.BATHROOM, PlacementType.KITCHEN),
                            "습한 공간에 잘 적응하는 식물"
                    ))
                    )
            );
        }
    }
}
