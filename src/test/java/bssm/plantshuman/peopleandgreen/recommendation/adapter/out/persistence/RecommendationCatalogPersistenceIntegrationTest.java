package bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence;

import bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.JpaPlantCatalogAdapter;
import bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.repository.RecommendationPlantRepository;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.LoadPlantCatalogPort;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RecommendationCatalogPersistenceIntegrationTest {

    @Autowired
    private LoadPlantCatalogPort loadPlantCatalogPort;

    @Autowired
    private RecommendationPlantRepository recommendationPlantRepository;

    @Test
    void loadsRecommendationCatalogFromDatabaseSeed() {
        assertEquals(JpaPlantCatalogAdapter.class, AopUtils.getTargetClass(loadPlantCatalogPort));
        assertEquals(30L, recommendationPlantRepository.count());
        assertEquals(30, loadPlantCatalogPort.loadCatalog().size());
    }
}
