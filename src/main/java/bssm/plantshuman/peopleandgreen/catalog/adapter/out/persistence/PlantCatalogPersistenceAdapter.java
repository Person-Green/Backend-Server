package bssm.plantshuman.peopleandgreen.catalog.adapter.out.persistence;

import bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence.UserPersistenceAdapter;
import bssm.plantshuman.peopleandgreen.catalog.adapter.out.persistence.entity.FavoritePlantEntity;
import bssm.plantshuman.peopleandgreen.catalog.adapter.out.persistence.repository.FavoritePlantRepository;
import bssm.plantshuman.peopleandgreen.catalog.application.port.out.FavoritePlantCommandPort;
import bssm.plantshuman.peopleandgreen.catalog.application.port.out.LoadPlantCatalogPagePort;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogItem;
import bssm.plantshuman.peopleandgreen.domain.plant.Plant;
import bssm.plantshuman.peopleandgreen.infrastructure.persistence.plant.PlantRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Component
public class PlantCatalogPersistenceAdapter implements LoadPlantCatalogPagePort, FavoritePlantCommandPort {

    private final PlantRepository plantRepository;
    private final FavoritePlantRepository favoritePlantRepository;
    private final UserPersistenceAdapter userPersistenceAdapter;

    public PlantCatalogPersistenceAdapter(
            PlantRepository plantRepository,
            FavoritePlantRepository favoritePlantRepository,
            UserPersistenceAdapter userPersistenceAdapter
    ) {
        this.plantRepository = plantRepository;
        this.favoritePlantRepository = favoritePlantRepository;
        this.userPersistenceAdapter = userPersistenceAdapter;
    }

    @Override
    public List<PlantCatalogItem> loadPage(String cursor, int sizePlusOne) {
        return plantRepository.findPage(cursor, PageRequest.of(0, sizePlusOne)).stream()
                .map(this::toItem)
                .toList();
    }

    @Override
    public Set<String> loadFavoritePlantIds(Long userId, Set<String> plantIds) {
        if (plantIds.isEmpty()) {
            return Set.of();
        }
        return favoritePlantRepository.findFavoritePlantIds(userId, plantIds);
    }

    @Override
    @Transactional
    public void addFavorite(Long userId, String plantId) {
        if (favoritePlantRepository.existsByUser_IdAndPlant_PlantId(userId, plantId)) {
            return;
        }

        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new IllegalArgumentException("Plant not found"));

        favoritePlantRepository.save(new FavoritePlantEntity(
                userPersistenceAdapter.getReference(userId),
                plant,
                Instant.now()
        ));
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, String plantId) {
        favoritePlantRepository.deleteByUser_IdAndPlant_PlantId(userId, plantId);
    }

    private PlantCatalogItem toItem(Plant plant) {
        return new PlantCatalogItem(
                plant.getPlantId(),
                plant.getPlantKoreanName(),
                plant.getPlantEnglishName(),
                plant.getSize(),
                plant.getAirPurification(),
                plant.getManageDifficulty()
        );
    }
}
