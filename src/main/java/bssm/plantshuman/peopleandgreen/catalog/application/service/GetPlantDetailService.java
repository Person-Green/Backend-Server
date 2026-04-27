package bssm.plantshuman.peopleandgreen.catalog.application.service;

import bssm.plantshuman.peopleandgreen.catalog.application.port.in.GetPlantDetailUseCase;
import bssm.plantshuman.peopleandgreen.catalog.application.port.out.LoadPlantDetailPort;
import bssm.plantshuman.peopleandgreen.catalog.application.port.out.LoadPlantCatalogPagePort;
import bssm.plantshuman.peopleandgreen.catalog.domain.exception.PlantNotFoundException;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantDetail;
import bssm.plantshuman.peopleandgreen.domain.plant.Plant;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class GetPlantDetailService implements GetPlantDetailUseCase {

    private final LoadPlantDetailPort loadPlantDetailPort;
    private final LoadPlantCatalogPagePort loadPlantCatalogPagePort;

    public GetPlantDetailService(LoadPlantDetailPort loadPlantDetailPort, LoadPlantCatalogPagePort loadPlantCatalogPagePort) {
        this.loadPlantDetailPort = loadPlantDetailPort;
        this.loadPlantCatalogPagePort = loadPlantCatalogPagePort;
    }

    @Override
    public PlantDetail getDetail(Long userId, String plantId) {
        Plant plant = loadPlantDetailPort.loadById(plantId)
                .orElseThrow(PlantNotFoundException::new);

        Set<String> favoriteIds = loadPlantCatalogPagePort.loadFavoritePlantIds(userId, Set.of(plantId));

        return new PlantDetail(
                plant.getPlantId(),
                plant.getPlantKoreanName(),
                plant.getPlantEnglishName(),
                plant.getManageDifficulty(),
                plant.getWaterPeriod(),
                plant.getAppropriateTemperature(),
                plant.getAppropriateHumidity(),
                plant.getSunlightRequirements(),
                plant.getSize(),
                plant.getRecommendedIndoorLocation(),
                plant.getAirPurification(),
                plant.getPetSafety(),
                plant.getDescription(),
                favoriteIds.contains(plantId)
        );
    }
}
