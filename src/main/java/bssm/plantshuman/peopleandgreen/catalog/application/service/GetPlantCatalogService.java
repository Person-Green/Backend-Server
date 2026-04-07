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

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 50;

    private final LoadPlantCatalogPagePort loadPlantCatalogPagePort;

    public GetPlantCatalogService(LoadPlantCatalogPagePort loadPlantCatalogPagePort) {
        this.loadPlantCatalogPagePort = loadPlantCatalogPagePort;
    }

    @Override
    public PlantCatalogCursorPage getCatalog(Long userId, String cursor, int size) {
        int normalizedSize = size <= 0 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        List<PlantCatalogItem> items = loadPlantCatalogPagePort.loadPage(cursor, normalizedSize + 1);
        boolean hasNext = items.size() > normalizedSize;
        List<PlantCatalogItem> pageItems = hasNext ? items.subList(0, normalizedSize) : items;
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
