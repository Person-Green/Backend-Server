package bssm.plantshuman.peopleandgreen.catalog.adapter.out.persistence;

import bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence.UserPersistenceAdapter;
import bssm.plantshuman.peopleandgreen.catalog.adapter.out.persistence.entity.FavoritePlantEntity;
import bssm.plantshuman.peopleandgreen.catalog.adapter.out.persistence.repository.FavoritePlantRepository;
import bssm.plantshuman.peopleandgreen.catalog.application.port.out.FavoritePlantCommandPort;
import bssm.plantshuman.peopleandgreen.catalog.application.port.out.LoadFavoritePlantsPort;
import bssm.plantshuman.peopleandgreen.catalog.application.port.out.LoadPlantCatalogPagePort;
import bssm.plantshuman.peopleandgreen.catalog.domain.exception.PlantNotFoundException;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.FavoritePlantView;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogItem;
import bssm.plantshuman.peopleandgreen.domain.plant.Plant;
import bssm.plantshuman.peopleandgreen.infrastructure.persistence.plant.PlantRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PlantCatalogPersistenceAdapter implements LoadPlantCatalogPagePort, FavoritePlantCommandPort, LoadFavoritePlantsPort {

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
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(PlantNotFoundException::new);

        try {
            favoritePlantRepository.save(new FavoritePlantEntity(
                    userPersistenceAdapter.getReference(userId),
                    plant,
                    Instant.now()
            ));
        } catch (DataIntegrityViolationException ignored) {
            // Treat duplicate favorite requests as idempotent success.
        }
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, String plantId) {
        favoritePlantRepository.deleteByUser_IdAndPlant_PlantId(userId, plantId);
    }

    @Override
    public List<FavoritePlantView> loadFavoritePlants(Long userId) {
        List<FavoritePlantEntity> favorites = favoritePlantRepository.findByUserIdWithPlant(userId);
        if (favorites.isEmpty()) {
            return List.of();
        }
        Set<String> plantIds = favorites.stream()
                .map(f -> f.getPlant().getPlantId())
                .collect(Collectors.toSet());
        Map<String, Long> countByPlantId = favoritePlantRepository.countGroupedByPlantId(plantIds)
                .stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));
        return favorites.stream()
                .map(f -> new FavoritePlantView(
                        f.getPlant().getPlantId(),
                        f.getPlant().getPlantKoreanName(),
                        f.getPlant().getPlantEnglishName(),
                        f.getPlant().getSize(),
                        f.getPlant().getAirPurification(),
                        f.getPlant().getManageDifficulty(),
                        countByPlantId.getOrDefault(f.getPlant().getPlantId(), 0L)
                ))
                .toList();
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
