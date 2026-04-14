package bssm.plantshuman.peopleandgreen.catalog.application.service;

import bssm.plantshuman.peopleandgreen.catalog.application.port.in.GetFavoritePlantsUseCase;
import bssm.plantshuman.peopleandgreen.catalog.application.port.out.LoadFavoritePlantsPort;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.FavoritePlantView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetFavoritePlantsService implements GetFavoritePlantsUseCase {

    private final LoadFavoritePlantsPort loadFavoritePlantsPort;

    @Override
    public List<FavoritePlantView> getFavoritePlants(Long userId) {
        return loadFavoritePlantsPort.loadFavoritePlants(userId);
    }
}
