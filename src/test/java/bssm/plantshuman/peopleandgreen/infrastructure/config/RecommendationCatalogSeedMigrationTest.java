package bssm.plantshuman.peopleandgreen.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:recommendation-seed;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class RecommendationCatalogSeedMigrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void seedsRecommendationCatalogTables() {
        assertEquals(30, countRows("recommendation_plant"));
        assertEquals(30, countRows("recommendation_plant_condition"));
        assertEquals(70, countRows("recommendation_plant_env_fit"));
        assertEquals(62, countRows("recommendation_plant_placement"));
    }

    private Integer countRows(String tableName) {
        return jdbcTemplate.queryForObject("select count(*) from " + tableName, Integer.class);
    }
}
