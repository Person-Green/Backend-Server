package bssm.plantshuman.peopleandgreen.catalog.application.service;

import bssm.plantshuman.peopleandgreen.catalog.application.port.in.FavoritePlantUseCase;
import bssm.plantshuman.peopleandgreen.catalog.application.port.out.FavoritePlantCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoritePlantService implements FavoritePlantUseCase {

    private final FavoritePlantCommandPort favoritePlantCommandPort;

    @Override
    public void add(Long userId, String plantId) {
        favoritePlantCommandPort.addFavorite(userId, plantId);
    }

    @Override
    public void remove(Long userId, String plantId) {
        favoritePlantCommandPort.removeFavorite(userId, plantId);
    }
}
