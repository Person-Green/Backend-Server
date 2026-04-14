package bssm.plantshuman.peopleandgreen.catalog.adapter.out.persistence;

import bssm.plantshuman.peopleandgreen.catalog.application.port.out.LoadFavoritePlantsPort;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.FavoritePlantView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class FavoritePlantsPersistenceIntegrationTest {

    @Autowired
    private LoadFavoritePlantsPort loadFavoritePlantsPort;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM favorite_plant");
        jdbcTemplate.execute("DELETE FROM app_user");
        jdbcTemplate.execute("DELETE FROM plant");

        jdbcTemplate.execute("""
                INSERT INTO plant (plant_id, plant_korean_name, plant_english_name, manage_difficulty,
                    water_period, appropriate_temperature, appropriate_humidity,
                    sunlight_requirements, size, recommended_indoor_location,
                    air_purification, pet_safety, description)
                VALUES
                    ('PLT-T1', '스투키', 'Stucky', 'EASY', '주 1회', '18-25도', '40-60%', '간접광', '중형', '거실', 'HIGH', '안전', '설명1'),
                    ('PLT-T2', '고무나무', 'Rubber Plant', 'NORMAL', '주 1회', '18-25도', '40-60%', '직사광', '대형', '거실', 'HIGH', '안전', '설명2'),
                    ('PLT-T3', '몬스테라', 'Monstera', 'EASY', '주 1회', '18-25도', '50-70%', '간접광', '대형', '거실', 'NORMAL', '안전', '설명3')
                """);

        jdbcTemplate.execute("""
                INSERT INTO app_user (oauth_provider, oauth_provider_user_id, email, name)
                VALUES
                    ('GOOGLE', 'google-uid-1', 'user1@test.com', '유저1'),
                    ('GOOGLE', 'google-uid-2', 'user2@test.com', '유저2'),
                    ('GOOGLE', 'google-uid-3', 'user3@test.com', '유저3')
                """);

        // PLT-T1: 유저1, 유저2, 유저3이 즐겨찾기 → count=3
        // PLT-T2: 유저1만 즐겨찾기 → count=1
        // PLT-T3: 유저1, 유저2가 즐겨찾기 → count=2
        jdbcTemplate.execute("""
                INSERT INTO favorite_plant (user_id, plant_id, created_at)
                SELECT u.id, p.plant_id, NOW()
                FROM app_user u, plant p
                WHERE (u.email = 'user1@test.com' AND p.plant_id IN ('PLT-T1', 'PLT-T2', 'PLT-T3'))
                   OR (u.email = 'user2@test.com' AND p.plant_id IN ('PLT-T1', 'PLT-T3'))
                   OR (u.email = 'user3@test.com' AND p.plant_id = 'PLT-T1')
                """);
    }

    @Test
    void returnsFavoritePlantsSortedByFavoriteCountDesc() {
        Long userId = jdbcTemplate.queryForObject(
                "SELECT id FROM app_user WHERE email = 'user1@test.com'", Long.class);

        List<FavoritePlantView> result = loadFavoritePlantsPort.loadFavoritePlants(userId);

        assertEquals(3, result.size());
        assertEquals("PLT-T1", result.get(0).plantId());
        assertEquals(3L, result.get(0).favoriteCount());
        assertEquals("PLT-T3", result.get(1).plantId());
        assertEquals(2L, result.get(1).favoriteCount());
        assertEquals("PLT-T2", result.get(2).plantId());
        assertEquals(1L, result.get(2).favoriteCount());
    }

    @Test
    void returnsEmptyListForUserWithNoFavorites() {
        Long userId = jdbcTemplate.queryForObject(
                "SELECT id FROM app_user WHERE email = 'user1@test.com'", Long.class);
        jdbcTemplate.execute("DELETE FROM favorite_plant WHERE user_id = " + userId);

        List<FavoritePlantView> result = loadFavoritePlantsPort.loadFavoritePlants(userId);

        assertTrue(result.isEmpty());
    }
}
