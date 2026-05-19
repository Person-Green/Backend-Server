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
                "https://cdn.example.com/stucky.jpg",
                "중형",
                AirPurification.HIGH,
                ManageDifficulty.EASY,
                true,
                3L
        ));

        PlantCatalogCursorPage page = new PlantCatalogCursorPage(plants, "PLT-001", false);
        plants.clear();

        assertEquals(1, page.plants().size());
        assertThrows(UnsupportedOperationException.class, () -> page.plants().add(
                new PlantCatalogView("PLT-002", "고무나무", "Rubber Plant", "https://cdn.example.com/rubber-plant.jpg", "대형", AirPurification.HIGH, ManageDifficulty.NORMAL, false, 1L)
        ));
    }
}
