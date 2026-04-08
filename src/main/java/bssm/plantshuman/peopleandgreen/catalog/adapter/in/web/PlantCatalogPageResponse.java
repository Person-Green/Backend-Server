package bssm.plantshuman.peopleandgreen.catalog.adapter.in.web;

import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogCursorPage;

import java.util.List;

public record PlantCatalogPageResponse(
        List<PlantCatalogItemResponse> plants,
        String nextCursor,
        boolean hasNext
) {
    public static PlantCatalogPageResponse from(PlantCatalogCursorPage page) {
        return new PlantCatalogPageResponse(
                page.plants().stream().map(PlantCatalogItemResponse::from).toList(),
                page.nextCursor(),
                page.hasNext()
        );
    }
}
