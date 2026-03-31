package bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence;

import bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.entity.RecommendationPlantEntity;
import bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.repository.RecommendationPlantRepository;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.AirPurificationLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.DifficultyLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PetSafetyLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.SizeCategory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RecommendationCatalogSeedServiceTest {

    @Test
    void failsFastWhenExistingCatalogIsIncomplete() {
        RecommendationPlantRepository recommendationPlantRepository = mock(RecommendationPlantRepository.class);
        when(recommendationPlantRepository.count()).thenReturn(1L);
        when(recommendationPlantRepository.findAll()).thenReturn(List.of(new RecommendationPlantEntity(
                "PLT-001",
                "스투키",
                "Stucky",
                DifficultyLevel.EASY,
                PetSafetyLevel.SAFE,
                AirPurificationLevel.HIGH,
                SizeCategory.MEDIUM,
                "2~3주 1회",
                "18~30°C",
                "40~60%",
                "직사광",
                "불완전한 시드 데이터",
                "창가"
        )));

        RecommendationCatalogSeedService seedService = new RecommendationCatalogSeedService(
                recommendationPlantRepository,
                new ObjectMapper()
        );

        assertThrows(IllegalStateException.class, seedService::seedCatalog);
    }
}
