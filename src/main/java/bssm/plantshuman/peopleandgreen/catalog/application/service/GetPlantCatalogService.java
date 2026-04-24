package bssm.plantshuman.peopleandgreen.catalog.application.service;

import bssm.plantshuman.peopleandgreen.catalog.application.port.in.GetPlantCatalogUseCase;
import bssm.plantshuman.peopleandgreen.catalog.application.port.out.LoadPlantCatalogPagePort;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogCursorPage;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogFilter;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogItem;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogSortType;
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
    public PlantCatalogCursorPage getCatalog(
            Long userId,
            String cursor,
            int size,
            PlantCatalogSortType sortType,
            PlantCatalogFilter filter
    ) {
        if (size < 1 || size > MAX_SIZE) {
            throw new IllegalArgumentException("Catalog size must be between 1 and " + MAX_SIZE);
        }
        if (sortType == null) {
            throw new IllegalArgumentException("Catalog sort type is required");
        }
        PlantCatalogFilter safeFilter = filter == null ? PlantCatalogFilter.empty() : filter;

        List<PlantCatalogItem> items = loadPlantCatalogPagePort.loadPage(cursor, size + 1, sortType, safeFilter);
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
                        favoriteIds.contains(item.plantId()),
                        item.favoriteCount()
                ))
                .toList();

        String nextCursor = hasNext && !views.isEmpty()
                ? nextCursor(sortType, pageItems.getLast())
                : null;
        return new PlantCatalogCursorPage(views, nextCursor, hasNext);
    }

    private String nextCursor(PlantCatalogSortType sortType, PlantCatalogItem item) {
        if (sortType == PlantCatalogSortType.LIKE_DESC) {
            return item.favoriteCount() + "|" + item.plantId();
        }
        return item.plantId();
    }
}
