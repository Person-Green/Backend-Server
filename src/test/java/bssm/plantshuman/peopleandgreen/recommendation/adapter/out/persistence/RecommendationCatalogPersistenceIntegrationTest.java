package bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence;

import bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.repository.RecommendationPlantRepository;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.LoadPlantCatalogPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RecommendationCatalogPersistenceIntegrationTest {

    @Autowired
    private LoadPlantCatalogPort loadPlantCatalogPort;

    @Autowired
    private RecommendationPlantRepository recommendationPlantRepository;

    @Test
    void loadsRecommendationCatalogFromDatabaseSeed() {
        assertEquals("JpaPlantCatalogAdapter", loadPlantCatalogPort.getClass().getSimpleName());
        assertTrue(recommendationPlantRepository.count() >= 30);
        assertFalse(loadPlantCatalogPort.loadCatalog().isEmpty());
    }
}
