package bssm.plantshuman.peopleandgreen.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:existing-recommendation-schema;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class FlywayExistingSchemaMigrationTest {

    static {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                "jdbc:h2:mem:existing-recommendation-schema;MODE=MySQL;DB_CLOSE_DELAY=-1",
                "sa",
                ""
        );
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        createExistingRecommendationTables(jdbcTemplate);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void appliesSeedMigrationWhenExistingSchemaHasNoFlywayHistory() {
        assertEquals(30, countRows("recommendation_plant"));
        assertEquals(30, countRows("recommendation_plant_condition"));
        assertEquals(70, countRows("recommendation_plant_env_fit"));
        assertEquals(62, countRows("recommendation_plant_placement"));
    }

    private static void createExistingRecommendationTables(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute("""
                CREATE TABLE recommendation_plant (
                    plant_id VARCHAR(30) PRIMARY KEY,
                    name_ko VARCHAR(100) NOT NULL,
                    name_en VARCHAR(100) NOT NULL,
                    difficulty VARCHAR(30) NOT NULL,
                    pet_safety VARCHAR(30) NOT NULL,
                    air_purification_level VARCHAR(30) NOT NULL,
                    size_category VARCHAR(30) NOT NULL,
                    display_water_cycle VARCHAR(100) NOT NULL,
                    display_temp_range VARCHAR(100) NOT NULL,
                    display_humidity_range VARCHAR(100) NOT NULL,
                    display_light_requirement VARCHAR(100) NOT NULL,
                    one_line_description VARCHAR(500) NOT NULL,
                    recommended_location_text VARCHAR(500) NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE recommendation_plant_condition (
                    plant_id VARCHAR(30) PRIMARY KEY,
                    sunlight_level VARCHAR(30) NOT NULL,
                    ventilation_need VARCHAR(30) NOT NULL,
                    temp_min INT NOT NULL,
                    temp_max INT NOT NULL,
                    humidity_min INT NOT NULL,
                    humidity_max INT NOT NULL,
                    water_cycle_min_days INT NOT NULL,
                    water_cycle_max_days INT NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE recommendation_plant_env_fit (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    plant_id VARCHAR(30) NOT NULL,
                    environment_type VARCHAR(50) NOT NULL,
                    fit_level VARCHAR(30) NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE recommendation_plant_placement (
                    plant_id VARCHAR(30) NOT NULL,
                    placement_type VARCHAR(30) NOT NULL,
                    PRIMARY KEY (plant_id, placement_type)
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE recommendation_history (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    user_id BIGINT NOT NULL,
                    title VARCHAR(100) NOT NULL,
                    plant_summary_text VARCHAR(255) NOT NULL,
                    request_snapshot TEXT NOT NULL,
                    result_snapshot TEXT NOT NULL,
                    created_at DATETIME(6) NOT NULL
                )
                """);
    }

    private Integer countRows(String tableName) {
        return jdbcTemplate.queryForObject("select count(*) from " + tableName, Integer.class);
    }
}
