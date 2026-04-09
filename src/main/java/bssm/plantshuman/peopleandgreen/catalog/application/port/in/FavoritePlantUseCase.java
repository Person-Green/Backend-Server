package bssm.plantshuman.peopleandgreen.catalog.application.port.in;

public interface FavoritePlantUseCase {

    void add(Long userId, String plantId);

    void remove(Long userId, String plantId);
}
