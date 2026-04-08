package bssm.plantshuman.peopleandgreen.catalog.application.port.in;

public interface AddFavoritePlantUseCase {

    void add(Long userId, String plantId);
}
