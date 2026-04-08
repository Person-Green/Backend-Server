package bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence;

import bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.JpaPlantCatalogAdapter;
import bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.repository.RecommendationPlantRepository;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.LoadPlantCatalogPort;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class RecommendationCatalogPersistenceIntegrationTest {

    @Autowired
    private LoadPlantCatalogPort loadPlantCatalogPort;

    @Autowired
    private RecommendationPlantRepository recommendationPlantRepository;

    @Test
    void doesNotAutoSeedRecommendationCatalog() {
        assertEquals(JpaPlantCatalogAdapter.class, AopUtils.getTargetClass(loadPlantCatalogPort));
        assertEquals(0L, recommendationPlantRepository.count());
        assertEquals(0, loadPlantCatalogPort.loadCatalog().size());
    }
}
