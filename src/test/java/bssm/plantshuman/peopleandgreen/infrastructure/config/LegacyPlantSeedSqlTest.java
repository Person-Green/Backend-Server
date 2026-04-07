package bssm.plantshuman.peopleandgreen.infrastructure.config;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacyPlantSeedSqlTest {

    @Test
    void usesUtf8mb4WhenImportingLegacyPlantSeed() throws Exception {
        String sql = Files.readString(Path.of("src/main/resources/db/seed/legacy_plant_seed.sql"));

        assertTrue(sql.startsWith("-- Excel 원본"));
        assertTrue(sql.contains("SET NAMES utf8mb4;"));
    }
}
