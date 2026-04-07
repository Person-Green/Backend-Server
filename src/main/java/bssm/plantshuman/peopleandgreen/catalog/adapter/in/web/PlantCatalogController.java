package bssm.plantshuman.peopleandgreen.catalog.adapter.in.web;

import bssm.plantshuman.peopleandgreen.auth.adapter.out.security.AuthenticatedUser;
import bssm.plantshuman.peopleandgreen.catalog.application.port.in.AddFavoritePlantUseCase;
import bssm.plantshuman.peopleandgreen.catalog.application.port.in.GetPlantCatalogUseCase;
import bssm.plantshuman.peopleandgreen.catalog.application.port.in.RemoveFavoritePlantUseCase;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

@RestController
@RequestMapping("/plants")
@Validated
public class PlantCatalogController {

    private final GetPlantCatalogUseCase getPlantCatalogUseCase;
    private final AddFavoritePlantUseCase addFavoritePlantUseCase;
    private final RemoveFavoritePlantUseCase removeFavoritePlantUseCase;

    public PlantCatalogController(
            GetPlantCatalogUseCase getPlantCatalogUseCase,
            AddFavoritePlantUseCase addFavoritePlantUseCase,
            RemoveFavoritePlantUseCase removeFavoritePlantUseCase
    ) {
        this.getPlantCatalogUseCase = getPlantCatalogUseCase;
        this.addFavoritePlantUseCase = addFavoritePlantUseCase;
        this.removeFavoritePlantUseCase = removeFavoritePlantUseCase;
    }

    @GetMapping
    public ResponseEntity<PlantCatalogPageResponse> getPlants(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int size
    ) {
        return ResponseEntity.ok(PlantCatalogPageResponse.from(
                getPlantCatalogUseCase.getCatalog(authenticatedUser.userId(), cursor, size)
        ));
    }

    @PostMapping("/{plantId}/favorite")
    public ResponseEntity<Void> addFavorite(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable String plantId
    ) {
        addFavoritePlantUseCase.add(authenticatedUser.userId(), plantId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{plantId}/favorite")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable String plantId
    ) {
        removeFavoritePlantUseCase.remove(authenticatedUser.userId(), plantId);
        return ResponseEntity.noContent().build();
    }
}
