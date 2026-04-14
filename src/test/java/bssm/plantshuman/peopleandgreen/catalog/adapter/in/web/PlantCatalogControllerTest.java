package bssm.plantshuman.peopleandgreen.catalog.adapter.in.web;

import bssm.plantshuman.peopleandgreen.auth.adapter.out.security.AuthenticatedUser;
import bssm.plantshuman.peopleandgreen.catalog.application.port.in.FavoritePlantUseCase;
import bssm.plantshuman.peopleandgreen.catalog.application.port.in.GetFavoritePlantsUseCase;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.FavoritePlantView;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogCursorPage;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogView;
import bssm.plantshuman.peopleandgreen.domain.plant.AirPurification;
import bssm.plantshuman.peopleandgreen.domain.plant.ManageDifficulty;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlantCatalogControllerTest {

    @Test
    void returnsCatalogPageForAuthenticatedUser() {
        PlantCatalogController controller = new PlantCatalogController(
                (userId, cursor, size) -> new PlantCatalogCursorPage(
                        List.of(new PlantCatalogView("PLT-001", "스투키", "Stucky", "중형", AirPurification.HIGH, ManageDifficulty.EASY, true)),
                        "PLT-001",
                        false
                ),
                userId -> List.of(),
                new RecordingFavoriteUseCase()
        );

        ResponseEntity<PlantCatalogPageResponse> response = controller.getPlants(new AuthenticatedUser(1L), null, 20);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("PLT-001", response.getBody().plants().getFirst().plantId());
        assertEquals(true, response.getBody().plants().getFirst().isFavorite());
    }

    @Test
    void addsFavoriteForAuthenticatedUser() {
        RecordingFavoriteUseCase addFavoriteUseCase = new RecordingFavoriteUseCase();
        PlantCatalogController controller = new PlantCatalogController(
                (userId, cursor, size) -> new PlantCatalogCursorPage(List.of(), null, false),
                userId -> List.of(),
                addFavoriteUseCase
        );

        ResponseEntity<Void> response = controller.addFavorite(new AuthenticatedUser(1L), "PLT-001");

        assertEquals(204, response.getStatusCode().value());
        assertEquals(1L, addFavoriteUseCase.userId);
        assertEquals("PLT-001", addFavoriteUseCase.plantId);
    }

    @Test
    void returnsFavoritePlantsForAuthenticatedUser() {
        List<FavoritePlantView> stubFavorites = List.of(
                new FavoritePlantView("PLT-001", "스투키", "Stucky", "중형", AirPurification.HIGH, ManageDifficulty.EASY, 5L),
                new FavoritePlantView("PLT-002", "고무나무", "Rubber Plant", "대형", AirPurification.HIGH, ManageDifficulty.NORMAL, 12L)
        );
        RecordingGetFavoritePlantsUseCase getFavoritePlantsUseCase = new RecordingGetFavoritePlantsUseCase(stubFavorites);
        PlantCatalogController controller = new PlantCatalogController(
                (userId, cursor, size) -> new PlantCatalogCursorPage(List.of(), null, false),
                getFavoritePlantsUseCase,
                new RecordingFavoriteUseCase()
        );

        ResponseEntity<FavoritePlantsListResponse> response = controller.getFavoritePlants(new AuthenticatedUser(1L));

        assertEquals(1L, getFavoritePlantsUseCase.queriedUserId);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().favoritePlants().size());
        assertEquals("PLT-001", response.getBody().favoritePlants().get(0).plantId());
        assertEquals(5L, response.getBody().favoritePlants().get(0).favoriteCount());
        assertEquals(true, response.getBody().favoritePlants().get(0).isFavorite());
        assertEquals("PLT-002", response.getBody().favoritePlants().get(1).plantId());
        assertEquals(12L, response.getBody().favoritePlants().get(1).favoriteCount());
    }

    @Test
    void returnsFavoritePlantsForAuthenticatedUserWithEmptyList() {
        RecordingGetFavoritePlantsUseCase getFavoritePlantsUseCase = new RecordingGetFavoritePlantsUseCase(List.of());
        PlantCatalogController controller = new PlantCatalogController(
                (userId, cursor, size) -> new PlantCatalogCursorPage(List.of(), null, false),
                getFavoritePlantsUseCase,
                new RecordingFavoriteUseCase()
        );

        ResponseEntity<FavoritePlantsListResponse> response = controller.getFavoritePlants(new AuthenticatedUser(42L));

        assertEquals(42L, getFavoritePlantsUseCase.queriedUserId);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(0, response.getBody().favoritePlants().size());
    }

    private static final class RecordingGetFavoritePlantsUseCase implements GetFavoritePlantsUseCase {

        private Long queriedUserId;
        private final List<FavoritePlantView> stubbedResult;

        private RecordingGetFavoritePlantsUseCase(List<FavoritePlantView> stubbedResult) {
            this.stubbedResult = stubbedResult;
        }

        @Override
        public List<FavoritePlantView> getFavoritePlants(Long userId) {
            this.queriedUserId = userId;
            return stubbedResult;
        }
    }

    private static final class RecordingFavoriteUseCase implements FavoritePlantUseCase {

        private Long userId;
        private String plantId;

        @Override
        public void add(Long userId, String plantId) {
            this.userId = userId;
            this.plantId = plantId;
        }

        @Override
        public void remove(Long userId, String plantId) {
        }
    }
}
