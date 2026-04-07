package bssm.plantshuman.peopleandgreen.catalog.application.port.out;

public interface FavoritePlantCommandPort {

    void addFavorite(Long userId, String plantId);

    void removeFavorite(Long userId, String plantId);
}
