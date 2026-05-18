package bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence;

import bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.JpaPlantCatalogAdapter;
import bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.repository.RecommendationPlantRepository;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.in.RecommendPlantsUseCase;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.LoadPlantCatalogPort;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.CareLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.ExperienceLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.HumidityBand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlacementType;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsCommand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.SunlightLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.TemperatureBand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.VentilationLevel;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "recommendation.policy.max-recommendations=10"
})
class RecommendationCatalogPersistenceIntegrationTest {

    @Autowired
    private LoadPlantCatalogPort loadPlantCatalogPort;

    @Autowired
    private RecommendationPlantRepository recommendationPlantRepository;

    @Autowired
    private RecommendPlantsUseCase recommendPlantsUseCase;

    @Test
    void usesBundledCatalogFallbackWhenDatabaseCatalogIsEmpty() {
        assertEquals(JpaPlantCatalogAdapter.class, AopUtils.getTargetClass(loadPlantCatalogPort));
        assertEquals(0L, recommendationPlantRepository.count());
        assertFalse(loadPlantCatalogPort.loadCatalog().isEmpty());

        var result = recommendPlantsUseCase.recommend(new RecommendPlantsCommand(
                SunlightLevel.LOW,
                VentilationLevel.HIGH,
                TemperatureBand.NORMAL,
                HumidityBand.NORMAL,
                CareLevel.HIGH,
                ExperienceLevel.INTERMEDIATE,
                false,
                PlacementType.DESK
        ));

        assertFalse(result.plants().isEmpty());
    }
}
