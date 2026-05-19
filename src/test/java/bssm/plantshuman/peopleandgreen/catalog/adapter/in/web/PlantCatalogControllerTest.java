package bssm.plantshuman.peopleandgreen.catalog.adapter.in.web;

import bssm.plantshuman.peopleandgreen.auth.adapter.out.security.AuthenticatedUser;
import bssm.plantshuman.peopleandgreen.catalog.application.port.in.FavoritePlantUseCase;
import bssm.plantshuman.peopleandgreen.catalog.application.port.in.GetFavoritePlantsUseCase;
import bssm.plantshuman.peopleandgreen.catalog.application.port.in.GetPlantCatalogUseCase;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.FavoritePlantView;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogFilter;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogCursorPage;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogSortType;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogView;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantDetail;
import bssm.plantshuman.peopleandgreen.domain.plant.AirPurification;
import bssm.plantshuman.peopleandgreen.domain.plant.ManageDifficulty;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlantCatalogControllerTest {

    @Test
    void returnsCatalogPageForAuthenticatedUser() {
        RecordingGetPlantCatalogUseCase getPlantCatalogUseCase = new RecordingGetPlantCatalogUseCase(
                new PlantCatalogCursorPage(
                        List.of(new PlantCatalogView("PLT-001", "스투키", "Stucky", "https://cdn.example.com/stucky.jpg", "중형", AirPurification.HIGH, ManageDifficulty.EASY, true, 7L)),
                        "7|PLT-001",
                        false
                )
        );
        PlantCatalogController controller = new PlantCatalogController(
                getPlantCatalogUseCase,
                (u, p) -> null,
                userId -> List.of(),
                new RecordingFavoriteUseCase()
        );

        ResponseEntity<PlantCatalogPageResponse> response = controller.getPlants(
                new AuthenticatedUser(1L),
                null,
                20,
                PlantCatalogSortType.LIKE_DESC,
                "스투키",
                List.of(ManageDifficulty.EASY),
                List.of(AirPurification.HIGH),
                List.of("중형"),
                List.of("ENV-01")
        );

        assertEquals(200, response.getStatusCode().value());
        assertEquals("PLT-001", response.getBody().plants().getFirst().plantId());
        assertEquals("https://cdn.example.com/stucky.jpg", response.getBody().plants().getFirst().imageUrl());
        assertEquals(true, response.getBody().plants().getFirst().isFavorite());
        assertEquals(7L, response.getBody().plants().getFirst().favoriteCount());
        assertEquals("7|PLT-001", response.getBody().nextCursor());
        assertEquals(PlantCatalogSortType.LIKE_DESC, getPlantCatalogUseCase.sortType);
        assertEquals("스투키", getPlantCatalogUseCase.filter.keyword());
        assertEquals(Set.of(ManageDifficulty.EASY), getPlantCatalogUseCase.filter.manageDifficulties());
        assertEquals(Set.of(AirPurification.HIGH), getPlantCatalogUseCase.filter.airPurifications());
        assertEquals(Set.of("중형"), getPlantCatalogUseCase.filter.sizes());
        assertEquals(Set.of("ENV-01"), getPlantCatalogUseCase.filter.environmentTypeIds());
    }

    @Test
    void ignoresBlankStringFilters() {
        RecordingGetPlantCatalogUseCase getPlantCatalogUseCase = new RecordingGetPlantCatalogUseCase(
                new PlantCatalogCursorPage(List.of(), null, false)
        );
        PlantCatalogController controller = new PlantCatalogController(
                getPlantCatalogUseCase,
                (u, p) -> null,
                userId -> List.of(),
                new RecordingFavoriteUseCase()
        );

        ResponseEntity<PlantCatalogPageResponse> response = controller.getPlants(
                new AuthenticatedUser(1L),
                null,
                20,
                PlantCatalogSortType.ID_ASC,
                null,
                null,
                null,
                List.of(""),
                List.of("  ")
        );

        assertEquals(200, response.getStatusCode().value());
        assertEquals(Set.of(), getPlantCatalogUseCase.filter.sizes());
        assertEquals(Set.of(), getPlantCatalogUseCase.filter.environmentTypeIds());
    }

    @Test
    void returnsPlantDetailWithImageUrlForAuthenticatedUser() {
        PlantCatalogController controller = new PlantCatalogController(
                (userId, cursor, size, sort, filter) -> new PlantCatalogCursorPage(List.of(), null, false),
                (userId, plantId) -> new PlantDetail(
                        "PLT-001",
                        "스투키",
                        "Stucky",
                        "https://cdn.example.com/stucky.jpg",
                        ManageDifficulty.EASY,
                        "2~3주 1회",
                        "18~30°C",
                        "40~60%",
                        "높음",
                        "중형",
                        "남향 창가",
                        AirPurification.HIGH,
                        "안전",
                        "공기정화 효과가 뛰어납니다",
                        true
                ),
                userId -> List.of(),
                new RecordingFavoriteUseCase()
        );

        ResponseEntity<PlantDetailResponse> response = controller.getPlant(new AuthenticatedUser(1L), "PLT-001");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("PLT-001", response.getBody().plantId());
        assertEquals("https://cdn.example.com/stucky.jpg", response.getBody().imageUrl());
    }

    @Test
    void addsFavoriteForAuthenticatedUser() {
        RecordingFavoriteUseCase addFavoriteUseCase = new RecordingFavoriteUseCase();
        PlantCatalogController controller = new PlantCatalogController(
                (userId, cursor, size, sort, filter) -> new PlantCatalogCursorPage(List.of(), null, false),
                (u, p) -> null,
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
                new FavoritePlantView("PLT-001", "스투키", "Stucky", "https://cdn.example.com/stucky.jpg", "중형", AirPurification.HIGH, ManageDifficulty.EASY, 5L),
                new FavoritePlantView("PLT-002", "고무나무", "Rubber Plant", "https://cdn.example.com/rubber-plant.jpg", "대형", AirPurification.HIGH, ManageDifficulty.NORMAL, 12L)
        );
        RecordingGetFavoritePlantsUseCase getFavoritePlantsUseCase = new RecordingGetFavoritePlantsUseCase(stubFavorites);
        PlantCatalogController controller = new PlantCatalogController(
                (userId, cursor, size, sort, filter) -> new PlantCatalogCursorPage(List.of(), null, false),
                (u, p) -> null,
                getFavoritePlantsUseCase,
                new RecordingFavoriteUseCase()
        );

        ResponseEntity<FavoritePlantsListResponse> response = controller.getFavoritePlants(new AuthenticatedUser(1L));

        assertEquals(1L, getFavoritePlantsUseCase.queriedUserId);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().favoritePlants().size());
        assertEquals("PLT-001", response.getBody().favoritePlants().get(0).plantId());
        assertEquals("https://cdn.example.com/stucky.jpg", response.getBody().favoritePlants().get(0).imageUrl());
        assertEquals(5L, response.getBody().favoritePlants().get(0).favoriteCount());
        assertEquals(true, response.getBody().favoritePlants().get(0).isFavorite());
        assertEquals("PLT-002", response.getBody().favoritePlants().get(1).plantId());
        assertEquals(12L, response.getBody().favoritePlants().get(1).favoriteCount());
    }

    @Test
    void returnsFavoritePlantsForAuthenticatedUserWithEmptyList() {
        RecordingGetFavoritePlantsUseCase getFavoritePlantsUseCase = new RecordingGetFavoritePlantsUseCase(List.of());
        PlantCatalogController controller = new PlantCatalogController(
                (userId, cursor, size, sort, filter) -> new PlantCatalogCursorPage(List.of(), null, false),
                (u, p) -> null,
                getFavoritePlantsUseCase,
                new RecordingFavoriteUseCase()
        );

        ResponseEntity<FavoritePlantsListResponse> response = controller.getFavoritePlants(new AuthenticatedUser(42L));

        assertEquals(42L, getFavoritePlantsUseCase.queriedUserId);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(0, response.getBody().favoritePlants().size());
    }

    private static final class RecordingGetPlantCatalogUseCase implements GetPlantCatalogUseCase {

        private final PlantCatalogCursorPage stubbedPage;
        private PlantCatalogSortType sortType;
        private PlantCatalogFilter filter;

        private RecordingGetPlantCatalogUseCase(PlantCatalogCursorPage stubbedPage) {
            this.stubbedPage = stubbedPage;
        }

        @Override
        public PlantCatalogCursorPage getCatalog(
                Long userId,
                String cursor,
                int size,
                PlantCatalogSortType sortType,
                PlantCatalogFilter filter
        ) {
            this.sortType = sortType;
            this.filter = filter;
            return stubbedPage;
        }
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
