package bssm.plantshuman.peopleandgreen.catalog.application.service;

import bssm.plantshuman.peopleandgreen.catalog.application.port.out.LoadPlantCatalogPagePort;
import bssm.plantshuman.peopleandgreen.catalog.application.port.out.LoadPlantDetailPort;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogFilter;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogItem;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogSortType;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantDetail;
import bssm.plantshuman.peopleandgreen.domain.plant.AirPurification;
import bssm.plantshuman.peopleandgreen.domain.plant.ManageDifficulty;
import bssm.plantshuman.peopleandgreen.domain.plant.Plant;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetPlantDetailServiceTest {

    @Test
    void mapsPlantImageUrlIntoDetail() throws Exception {
        Plant plant = plant(
                "PLT-001",
                "스투키",
                "Stucky",
                "https://cdn.example.com/stucky.jpg"
        );
        GetPlantDetailService service = new GetPlantDetailService(
                plantId -> Optional.of(plant),
                new RecordingLoadPlantCatalogPagePort(Set.of("PLT-001"))
        );

        PlantDetail detail = service.getDetail(1L, "PLT-001");

        assertEquals("PLT-001", detail.plantId());
        assertEquals("https://cdn.example.com/stucky.jpg", detail.imageUrl());
        assertEquals(true, detail.isFavorite());
    }

    private static Plant plant(
            String plantId,
            String koreanName,
            String englishName,
            String imageUrl
    ) throws Exception {
        Constructor<Plant> constructor = Plant.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Plant plant = constructor.newInstance();
        ReflectionTestUtils.setField(plant, "plantId", plantId);
        ReflectionTestUtils.setField(plant, "plantKoreanName", koreanName);
        ReflectionTestUtils.setField(plant, "plantEnglishName", englishName);
        ReflectionTestUtils.setField(plant, "imageUrl", imageUrl);
        ReflectionTestUtils.setField(plant, "manageDifficulty", ManageDifficulty.EASY);
        ReflectionTestUtils.setField(plant, "waterPeriod", "2~3주 1회");
        ReflectionTestUtils.setField(plant, "appropriateTemperature", "18~30°C");
        ReflectionTestUtils.setField(plant, "appropriateHumidity", "40~60%");
        ReflectionTestUtils.setField(plant, "sunlightRequirements", "높음");
        ReflectionTestUtils.setField(plant, "size", "중형");
        ReflectionTestUtils.setField(plant, "recommendedIndoorLocation", "남향 창가");
        ReflectionTestUtils.setField(plant, "airPurification", AirPurification.HIGH);
        ReflectionTestUtils.setField(plant, "petSafety", "안전");
        ReflectionTestUtils.setField(plant, "description", "공기정화 효과가 뛰어납니다");
        return plant;
    }

    private record RecordingLoadPlantCatalogPagePort(Set<String> favoritePlantIds) implements LoadPlantCatalogPagePort {

        @Override
        public List<PlantCatalogItem> loadPage(
                String cursor,
                int sizePlusOne,
                PlantCatalogSortType sortType,
                PlantCatalogFilter filter
        ) {
            return List.of();
        }

        @Override
        public Set<String> loadFavoritePlantIds(Long userId, Set<String> plantIds) {
            return favoritePlantIds;
        }
    }
}
