package bssm.plantshuman.peopleandgreen.catalog.domain.model;

import bssm.plantshuman.peopleandgreen.domain.plant.AirPurification;
import bssm.plantshuman.peopleandgreen.domain.plant.ManageDifficulty;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PlantCatalogFilterTest {

    @Test
    void normalizesKeywordAndStringSets() {
        PlantCatalogFilter filter = PlantCatalogFilter.of(
                "   ",
                Set.of(ManageDifficulty.EASY),
                Set.of(AirPurification.HIGH),
                Set.of("", "  ", "중형"),
                Set.of("ENV-01", " ")
        );

        assertNull(filter.keyword());
        assertEquals(Set.of("중형"), filter.sizes());
        assertEquals(Set.of("ENV-01"), filter.environmentTypeIds());
    }
}
