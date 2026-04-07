package bssm.plantshuman.peopleandgreen.catalog.application.service;

import bssm.plantshuman.peopleandgreen.catalog.application.port.in.GetPlantCatalogUseCase;
import bssm.plantshuman.peopleandgreen.catalog.application.port.out.LoadPlantCatalogPagePort;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogCursorPage;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogItem;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogView;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GetPlantCatalogService implements GetPlantCatalogUseCase {

    private static final int MAX_SIZE = 50;

    private final LoadPlantCatalogPagePort loadPlantCatalogPagePort;

    public GetPlantCatalogService(LoadPlantCatalogPagePort loadPlantCatalogPagePort) {
        this.loadPlantCatalogPagePort = loadPlantCatalogPagePort;
    }

    @Override
    public PlantCatalogCursorPage getCatalog(Long userId, String cursor, int size) {
        if (size < 1 || size > MAX_SIZE) {
            throw new IllegalArgumentException("Catalog size must be between 1 and " + MAX_SIZE);
        }

        List<PlantCatalogItem> items = loadPlantCatalogPagePort.loadPage(cursor, size + 1);
        boolean hasNext = items.size() > size;
        List<PlantCatalogItem> pageItems = hasNext ? items.subList(0, size) : items;
        Set<String> favoriteIds = loadPlantCatalogPagePort.loadFavoritePlantIds(
                userId,
                pageItems.stream().map(PlantCatalogItem::plantId).collect(Collectors.toSet())
        );

        List<PlantCatalogView> views = pageItems.stream()
                .map(item -> new PlantCatalogView(
                        item.plantId(),
                        item.plantKoreanName(),
                        item.plantEnglishName(),
                        item.size(),
                        item.airPurification(),
                        item.manageDifficulty(),
                        favoriteIds.contains(item.plantId())
                ))
                .toList();

        String nextCursor = hasNext && !views.isEmpty() ? views.getLast().plantId() : null;
        return new PlantCatalogCursorPage(views, nextCursor, hasNext);
    }
}
