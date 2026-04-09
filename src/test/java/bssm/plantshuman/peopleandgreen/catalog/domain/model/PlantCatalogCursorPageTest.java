package bssm.plantshuman.peopleandgreen.catalog.domain.model;

import bssm.plantshuman.peopleandgreen.domain.plant.AirPurification;
import bssm.plantshuman.peopleandgreen.domain.plant.ManageDifficulty;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlantCatalogCursorPageTest {

    @Test
    void copiesPlantsListDefensively() {
        List<PlantCatalogView> plants = new ArrayList<>();
        plants.add(new PlantCatalogView(
                "PLT-001",
                "스투키",
                "Stucky",
                "중형",
                AirPurification.HIGH,
                ManageDifficulty.EASY,
                true
        ));

        PlantCatalogCursorPage page = new PlantCatalogCursorPage(plants, "PLT-001", false);
        plants.clear();

        assertEquals(1, page.plants().size());
        assertThrows(UnsupportedOperationException.class, () -> page.plants().add(
                new PlantCatalogView("PLT-002", "고무나무", "Rubber Plant", "대형", AirPurification.HIGH, ManageDifficulty.NORMAL, false)
        ));
    }
}
