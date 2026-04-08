package bssm.plantshuman.peopleandgreen.infrastructure.config;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacyPlantSeedSqlTest {

    @Test
    void usesUtf8mb4WhenImportingLegacyPlantSeed() throws Exception {
        String sql = new String(
                LegacyPlantSeedSqlTest.class.getResourceAsStream("/db/seed/legacy_plant_seed.sql").readAllBytes(),
                StandardCharsets.UTF_8
        );

        assertTrue(sql.startsWith("-- Excel 원본"));
        assertTrue(sql.contains("SET NAMES utf8mb4;"));
        assertTrue(sql.contains("DELETE FROM favorite_plant;"));
        assertTrue(sql.indexOf("DELETE FROM favorite_plant;") < sql.indexOf("DELETE FROM plant;"));
    }
}
