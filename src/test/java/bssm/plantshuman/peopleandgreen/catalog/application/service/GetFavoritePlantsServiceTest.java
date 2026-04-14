package bssm.plantshuman.peopleandgreen.catalog.application.service;

import bssm.plantshuman.peopleandgreen.catalog.application.port.out.LoadFavoritePlantsPort;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.FavoritePlantView;
import bssm.plantshuman.peopleandgreen.domain.plant.AirPurification;
import bssm.plantshuman.peopleandgreen.domain.plant.ManageDifficulty;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GetFavoritePlantsServiceTest {

    @Test
    void returnsFavoritePlantsWithCountsForUser() {
        List<FavoritePlantView> stubbed = List.of(
                new FavoritePlantView("PLT-001", "스투키", "Stucky", "중형", AirPurification.HIGH, ManageDifficulty.EASY, 5L),
                new FavoritePlantView("PLT-002", "고무나무", "Rubber Plant", "대형", AirPurification.HIGH, ManageDifficulty.NORMAL, 12L)
        );
        RecordingLoadFavoritePlantsPort port = new RecordingLoadFavoritePlantsPort(stubbed);
        GetFavoritePlantsService service = new GetFavoritePlantsService(port);

        List<FavoritePlantView> result = service.getFavoritePlants(1L);

        assertEquals(1L, port.queriedUserId);
        assertEquals(2, result.size());
        assertEquals("PLT-001", result.get(0).plantId());
        assertEquals(5L, result.get(0).favoriteCount());
        assertEquals("PLT-002", result.get(1).plantId());
        assertEquals(12L, result.get(1).favoriteCount());
    }

    @Test
    void returnsEmptyListWhenUserHasNoFavorites() {
        RecordingLoadFavoritePlantsPort port = new RecordingLoadFavoritePlantsPort(List.of());
        GetFavoritePlantsService service = new GetFavoritePlantsService(port);

        List<FavoritePlantView> result = service.getFavoritePlants(42L);

        assertEquals(42L, port.queriedUserId);
        assertTrue(result.isEmpty());
    }

    private static final class RecordingLoadFavoritePlantsPort implements LoadFavoritePlantsPort {

        private Long queriedUserId;
        private final List<FavoritePlantView> stubbedResult;

        private RecordingLoadFavoritePlantsPort(List<FavoritePlantView> stubbedResult) {
            this.stubbedResult = stubbedResult;
        }

        @Override
        public List<FavoritePlantView> loadFavoritePlants(Long userId) {
            this.queriedUserId = userId;
            return stubbedResult;
        }
    }
}
