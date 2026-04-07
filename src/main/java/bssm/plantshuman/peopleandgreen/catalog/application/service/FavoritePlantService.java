package bssm.plantshuman.peopleandgreen.catalog.application.service;

import bssm.plantshuman.peopleandgreen.catalog.application.port.in.AddFavoritePlantUseCase;
import bssm.plantshuman.peopleandgreen.catalog.application.port.in.RemoveFavoritePlantUseCase;
import bssm.plantshuman.peopleandgreen.catalog.application.port.out.FavoritePlantCommandPort;
import org.springframework.stereotype.Service;

@Service
public class FavoritePlantService implements AddFavoritePlantUseCase, RemoveFavoritePlantUseCase {

    private final FavoritePlantCommandPort favoritePlantCommandPort;

    public FavoritePlantService(FavoritePlantCommandPort favoritePlantCommandPort) {
        this.favoritePlantCommandPort = favoritePlantCommandPort;
    }

    @Override
    public void add(Long userId, String plantId) {
        favoritePlantCommandPort.addFavorite(userId, plantId);
    }

    @Override
    public void remove(Long userId, String plantId) {
        favoritePlantCommandPort.removeFavorite(userId, plantId);
    }
}
