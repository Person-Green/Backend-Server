package bssm.plantshuman.peopleandgreen.catalog.adapter.out.persistence;

import bssm.plantshuman.peopleandgreen.catalog.application.port.out.LoadPlantCatalogPagePort;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogFilter;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogItem;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogSortType;
import bssm.plantshuman.peopleandgreen.domain.plant.AirPurification;
import bssm.plantshuman.peopleandgreen.domain.plant.ManageDifficulty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class PlantCatalogPersistenceAdapterIntegrationTest {

    @Autowired
    private LoadPlantCatalogPagePort loadPlantCatalogPagePort;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM favorite_plant");
        jdbcTemplate.execute("DELETE FROM app_user");
        jdbcTemplate.execute("DELETE FROM plant_environment_mapping");
        jdbcTemplate.execute("DELETE FROM plant");
        jdbcTemplate.execute("DELETE FROM plant_environment");

        jdbcTemplate.execute("""
                INSERT INTO plant_environment (type_id, type_name, key_condition, sunlight, ventilation, temperature, humidity, description)
                VALUES
                    ('ENV-01', '햇빛 풍부', '조건1', '높음', '보통', '보통', '보통', '설명1'),
                    ('ENV-02', '햇빛 부족', '조건2', '낮음', '보통', '보통', '보통', '설명2')
                """);

        jdbcTemplate.execute("""
                INSERT INTO plant (plant_id, plant_korean_name, plant_english_name, primary_type_id, manage_difficulty,
                    water_period, appropriate_temperature, appropriate_humidity, sunlight_requirements, size,
                    recommended_indoor_location, air_purification, pet_safety, description)
                VALUES
                    ('PLT-T1', '스투키', 'Stucky', 'ENV-01', 'EASY', '주 1회', '18-25도', '40-60%', '간접광', '중형', '거실', 'HIGH', '안전', '설명1'),
                    ('PLT-T2', '고무나무', 'Rubber Plant', 'ENV-01', 'NORMAL', '주 1회', '18-25도', '40-60%', '직사광', '대형', '거실', 'HIGH', '안전', '설명2'),
                    ('PLT-T3', '몬스테라', 'Monstera', 'ENV-02', 'EASY', '주 1회', '18-25도', '50-70%', '간접광', '대형', '거실', 'NORMAL', '안전', '설명3')
                """);

        jdbcTemplate.execute("""
                INSERT INTO app_user (oauth_provider, oauth_provider_user_id, email, name)
                VALUES
                    ('GOOGLE', 'google-uid-1', 'user1@test.com', '유저1'),
                    ('GOOGLE', 'google-uid-2', 'user2@test.com', '유저2'),
                    ('GOOGLE', 'google-uid-3', 'user3@test.com', '유저3')
                """);

        jdbcTemplate.execute("""
                INSERT INTO favorite_plant (user_id, plant_id, created_at)
                SELECT u.id, p.plant_id, NOW()
                FROM app_user u, plant p
                WHERE (u.email = 'user1@test.com' AND p.plant_id IN ('PLT-T1', 'PLT-T2', 'PLT-T3'))
                   OR (u.email = 'user2@test.com' AND p.plant_id IN ('PLT-T1', 'PLT-T2'))
                   OR (u.email = 'user3@test.com' AND p.plant_id = 'PLT-T1')
                """);
    }

    @Test
    void returnsPlantsSortedByGlobalFavoriteCountDesc() {
        List<PlantCatalogItem> items = loadPlantCatalogPagePort.loadPage(
                null,
                10,
                PlantCatalogSortType.LIKE_DESC,
                PlantCatalogFilter.empty()
        );

        assertEquals(3, items.size());
        assertEquals("PLT-T1", items.get(0).plantId());
        assertEquals(3L, items.get(0).favoriteCount());
        assertEquals("PLT-T2", items.get(1).plantId());
        assertEquals(2L, items.get(1).favoriteCount());
        assertEquals("PLT-T3", items.get(2).plantId());
        assertEquals(1L, items.get(2).favoriteCount());
    }

    @Test
    void supportsLikeSortCursor() {
        List<PlantCatalogItem> items = loadPlantCatalogPagePort.loadPage(
                "2|PLT-T2",
                10,
                PlantCatalogSortType.LIKE_DESC,
                PlantCatalogFilter.empty()
        );

        assertEquals(1, items.size());
        assertEquals("PLT-T3", items.getFirst().plantId());
    }

    @Test
    void appliesKeywordAndAttributeFilters() {
        PlantCatalogFilter filter = PlantCatalogFilter.of(
                "무",
                Set.of(ManageDifficulty.NORMAL),
                Set.of(AirPurification.HIGH),
                Set.of("대형"),
                Set.of("ENV-01")
        );

        List<PlantCatalogItem> items = loadPlantCatalogPagePort.loadPage(
                null,
                10,
                PlantCatalogSortType.ID_ASC,
                filter
        );

        assertEquals(1, items.size());
        assertEquals("PLT-T2", items.getFirst().plantId());
    }
}
