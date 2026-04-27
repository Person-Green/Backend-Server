package bssm.plantshuman.peopleandgreen.catalog.adapter.in.web;

import bssm.plantshuman.peopleandgreen.auth.adapter.out.security.AuthenticatedUser;
import bssm.plantshuman.peopleandgreen.catalog.application.port.in.FavoritePlantUseCase;
import bssm.plantshuman.peopleandgreen.catalog.application.port.in.GetFavoritePlantsUseCase;
import bssm.plantshuman.peopleandgreen.catalog.application.port.in.GetPlantCatalogUseCase;
import bssm.plantshuman.peopleandgreen.catalog.application.port.in.GetPlantDetailUseCase;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogFilter;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogSortType;
import bssm.plantshuman.peopleandgreen.domain.plant.AirPurification;
import bssm.plantshuman.peopleandgreen.domain.plant.ManageDifficulty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/plants")
@Validated
@RequiredArgsConstructor
public class PlantCatalogController {

    private final GetPlantCatalogUseCase getPlantCatalogUseCase;
    private final GetPlantDetailUseCase getPlantDetailUseCase;
    private final GetFavoritePlantsUseCase getFavoritePlantsUseCase;
    private final FavoritePlantUseCase favoritePlantUseCase;

    @GetMapping
    public ResponseEntity<PlantCatalogPageResponse> getPlants(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int size,
            @RequestParam(defaultValue = "ID_ASC") PlantCatalogSortType sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<ManageDifficulty> manageDifficulty,
            @RequestParam(required = false) List<AirPurification> airPurification,
            @RequestParam(name = "plantSize", required = false) List<String> plantSize,
            @RequestParam(name = "environmentTypeId", required = false) List<String> environmentTypeId
    ) {
        PlantCatalogFilter filter = PlantCatalogFilter.of(
                keyword,
                toSet(manageDifficulty),
                toSet(airPurification),
                toSet(plantSize),
                toSet(environmentTypeId)
        );
        return ResponseEntity.ok(PlantCatalogPageResponse.from(
                getPlantCatalogUseCase.getCatalog(authenticatedUser.userId(), cursor, size, sort, filter)
        ));
    }

    @GetMapping("/{plantId}")
    public ResponseEntity<PlantDetailResponse> getPlant(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable String plantId
    ) {
        return ResponseEntity.ok(PlantDetailResponse.from(
                getPlantDetailUseCase.getDetail(authenticatedUser.userId(), plantId)
        ));
    }

    @GetMapping("/favorites")
    public ResponseEntity<FavoritePlantsListResponse> getFavoritePlants(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return ResponseEntity.ok(FavoritePlantsListResponse.from(
                getFavoritePlantsUseCase.getFavoritePlants(authenticatedUser.userId())
        ));
    }

    @PostMapping("/{plantId}/favorite")
    public ResponseEntity<Void> addFavorite(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable String plantId
    ) {
        favoritePlantUseCase.add(authenticatedUser.userId(), plantId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{plantId}/favorite")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable String plantId
    ) {
        favoritePlantUseCase.remove(authenticatedUser.userId(), plantId);
        return ResponseEntity.noContent().build();
    }

    private static <T> Set<T> toSet(List<T> values) {
        if (values == null || values.isEmpty()) {
            return Set.of();
        }
        Set<T> deduplicated = new LinkedHashSet<>();
        values.stream().filter(Objects::nonNull).forEach(deduplicated::add);
        return deduplicated;
    }
}
