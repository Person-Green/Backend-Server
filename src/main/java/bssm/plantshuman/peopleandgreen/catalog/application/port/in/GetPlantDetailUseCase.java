package bssm.plantshuman.peopleandgreen.catalog.application.port.in;

import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantDetail;

public interface GetPlantDetailUseCase {

    PlantDetail getDetail(Long userId, String plantId);
}
