package bssm.plantshuman.peopleandgreen.catalog.application.port.in;

public interface RemoveFavoritePlantUseCase {

    void remove(Long userId, String plantId);
}
