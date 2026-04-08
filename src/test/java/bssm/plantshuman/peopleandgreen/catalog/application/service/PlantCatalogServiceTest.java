package bssm.plantshuman.peopleandgreen.catalog.application.service;

import bssm.plantshuman.peopleandgreen.catalog.application.port.out.FavoritePlantCommandPort;
import bssm.plantshuman.peopleandgreen.catalog.application.port.out.LoadPlantCatalogPagePort;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogCursorPage;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogItem;
import bssm.plantshuman.peopleandgreen.domain.plant.AirPurification;
import bssm.plantshuman.peopleandgreen.domain.plant.ManageDifficulty;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlantCatalogServiceTest {

    @Test
    void returnsCursorPageWithFavoriteFlags() {
        GetPlantCatalogService service = new GetPlantCatalogService(new LoadPlantCatalogPagePort() {
            @Override
            public List<PlantCatalogItem> loadPage(String cursor, int sizePlusOne) {
                return List.of(
                        new PlantCatalogItem("PLT-001", "스투키", "Stucky", "중형", AirPurification.HIGH, ManageDifficulty.EASY),
                        new PlantCatalogItem("PLT-002", "고무나무", "Rubber Plant", "대형", AirPurification.HIGH, ManageDifficulty.NORMAL)
                );
            }

            @Override
            public Set<String> loadFavoritePlantIds(Long userId, Set<String> plantIds) {
                return Set.of("PLT-002");
            }
        });

        PlantCatalogCursorPage page = service.getCatalog(1L, null, 1);

        assertEquals(1, page.plants().size());
        assertEquals("PLT-001", page.plants().getFirst().plantId());
        assertEquals(false, page.plants().getFirst().isFavorite());
        assertEquals("PLT-001", page.nextCursor());
        assertEquals(true, page.hasNext());
    }

    @Test
    void togglesFavoriteByUserAndPlantId() {
        RecordingFavoritePlantCommandPort favoritePlantCommandPort = new RecordingFavoritePlantCommandPort();
        FavoritePlantService service = new FavoritePlantService(favoritePlantCommandPort);

        service.add(1L, "PLT-001");
        service.remove(1L, "PLT-001");

        assertEquals(1L, favoritePlantCommandPort.userId);
        assertEquals("PLT-001", favoritePlantCommandPort.plantId);
        assertEquals(true, favoritePlantCommandPort.removed);
    }

    @Test
    void rejectsCatalogRequestSizeOutsideAllowedRange() {
        GetPlantCatalogService service = new GetPlantCatalogService(new LoadPlantCatalogPagePort() {
            @Override
            public List<PlantCatalogItem> loadPage(String cursor, int sizePlusOne) {
                return List.of();
            }

            @Override
            public Set<String> loadFavoritePlantIds(Long userId, Set<String> plantIds) {
                return Set.of();
            }
        });

        assertThrows(IllegalArgumentException.class, () -> service.getCatalog(1L, null, 0));
        assertThrows(IllegalArgumentException.class, () -> service.getCatalog(1L, null, 51));
    }
    private static final class RecordingFavoritePlantCommandPort implements FavoritePlantCommandPort {

        private Long userId;
        private String plantId;
        private boolean removed;

        @Override
        public void addFavorite(Long userId, String plantId) {
            this.userId = userId;
            this.plantId = plantId;
        }

        @Override
        public void removeFavorite(Long userId, String plantId) {
            this.userId = userId;
            this.plantId = plantId;
            this.removed = true;
        }
    }
}
