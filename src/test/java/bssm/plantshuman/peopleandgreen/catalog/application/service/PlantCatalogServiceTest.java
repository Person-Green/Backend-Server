package bssm.plantshuman.peopleandgreen.catalog.application.service;

import bssm.plantshuman.peopleandgreen.catalog.application.port.out.FavoritePlantCommandPort;
import bssm.plantshuman.peopleandgreen.catalog.application.port.out.LoadPlantCatalogPagePort;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogFilter;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogCursorPage;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogItem;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogSortType;
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
        PlantCatalogFilter filter = PlantCatalogFilter.of("스투키", Set.of(ManageDifficulty.EASY), Set.of(), Set.of(), Set.of());
        GetPlantCatalogService service = new GetPlantCatalogService(new LoadPlantCatalogPagePort() {
            @Override
            public List<PlantCatalogItem> loadPage(
                    String cursor,
                    int sizePlusOne,
                    PlantCatalogSortType sortType,
                    PlantCatalogFilter filter
            ) {
                return List.of(
                        new PlantCatalogItem("PLT-001", "스투키", "Stucky", "https://cdn.example.com/stucky.jpg", "중형", AirPurification.HIGH, ManageDifficulty.EASY, 5L),
                        new PlantCatalogItem("PLT-002", "고무나무", "Rubber Plant", "https://cdn.example.com/rubber-plant.jpg", "대형", AirPurification.HIGH, ManageDifficulty.NORMAL, 4L)
                );
            }

            @Override
            public Set<String> loadFavoritePlantIds(Long userId, Set<String> plantIds) {
                return Set.of("PLT-002");
            }
        });

        PlantCatalogCursorPage page = service.getCatalog(1L, null, 1, PlantCatalogSortType.ID_ASC, filter);

        assertEquals(1, page.plants().size());
        assertEquals("PLT-001", page.plants().getFirst().plantId());
        assertEquals("https://cdn.example.com/stucky.jpg", page.plants().getFirst().imageUrl());
        assertEquals(false, page.plants().getFirst().isFavorite());
        assertEquals(5L, page.plants().getFirst().favoriteCount());
        assertEquals("PLT-001", page.nextCursor());
        assertEquals(true, page.hasNext());
    }

    @Test
    void returnsCompositeCursorForLikeSort() {
        GetPlantCatalogService service = new GetPlantCatalogService(new LoadPlantCatalogPagePort() {
            @Override
            public List<PlantCatalogItem> loadPage(
                    String cursor,
                    int sizePlusOne,
                    PlantCatalogSortType sortType,
                    PlantCatalogFilter filter
            ) {
                return List.of(
                        new PlantCatalogItem("PLT-001", "스투키", "Stucky", "https://cdn.example.com/stucky.jpg", "중형", AirPurification.HIGH, ManageDifficulty.EASY, 9L),
                        new PlantCatalogItem("PLT-002", "고무나무", "Rubber Plant", "https://cdn.example.com/rubber-plant.jpg", "대형", AirPurification.HIGH, ManageDifficulty.NORMAL, 7L)
                );
            }

            @Override
            public Set<String> loadFavoritePlantIds(Long userId, Set<String> plantIds) {
                return Set.of();
            }
        });

        PlantCatalogCursorPage page = service.getCatalog(1L, null, 1, PlantCatalogSortType.LIKE_DESC, PlantCatalogFilter.empty());

        assertEquals("9|PLT-001", page.nextCursor());
    }

    @Test
    void togglesFavoriteByUserAndPlantId() {
        RecordingFavoritePlantCommandPort favoritePlantCommandPort = new RecordingFavoritePlantCommandPort();
        FavoritePlantService service = new FavoritePlantService(favoritePlantCommandPort);

        service.add(1L, "PLT-001");
        service.remove(1L, "PLT-001");

        assertEquals(1L, favoritePlantCommandPort.addUserId);
        assertEquals("PLT-001", favoritePlantCommandPort.addPlantId);
        assertEquals(1L, favoritePlantCommandPort.removeUserId);
        assertEquals("PLT-001", favoritePlantCommandPort.removePlantId);
        assertEquals(true, favoritePlantCommandPort.removed);
    }

    @Test
    void rejectsCatalogRequestSizeOutsideAllowedRange() {
        GetPlantCatalogService service = new GetPlantCatalogService(new LoadPlantCatalogPagePort() {
            @Override
            public List<PlantCatalogItem> loadPage(
                    String cursor,
                    int sizePlusOne,
                    PlantCatalogSortType sortType,
                    PlantCatalogFilter filter
            ) {
                return List.of();
            }

            @Override
            public Set<String> loadFavoritePlantIds(Long userId, Set<String> plantIds) {
                return Set.of();
            }
        });

        assertThrows(IllegalArgumentException.class, () -> service.getCatalog(1L, null, 0, PlantCatalogSortType.ID_ASC, PlantCatalogFilter.empty()));
        assertThrows(IllegalArgumentException.class, () -> service.getCatalog(1L, null, 51, PlantCatalogSortType.ID_ASC, PlantCatalogFilter.empty()));
    }
    private static final class RecordingFavoritePlantCommandPort implements FavoritePlantCommandPort {

        private Long addUserId;
        private String addPlantId;
        private Long removeUserId;
        private String removePlantId;
        private boolean removed;

        @Override
        public void addFavorite(Long userId, String plantId) {
            this.addUserId = userId;
            this.addPlantId = plantId;
        }

        @Override
        public void removeFavorite(Long userId, String plantId) {
            this.removeUserId = userId;
            this.removePlantId = plantId;
            this.removed = true;
        }
    }
}
