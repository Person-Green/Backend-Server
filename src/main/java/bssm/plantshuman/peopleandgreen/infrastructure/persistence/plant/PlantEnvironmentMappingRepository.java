package bssm.plantshuman.peopleandgreen.infrastructure.persistence.plant;

import bssm.plantshuman.peopleandgreen.domain.diagnosis.PlantEnvironmentMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlantEnvironmentMappingRepository extends JpaRepository<PlantEnvironmentMapping, Integer> {
    List<PlantEnvironmentMapping> findByPlantEnvironment_TypeId(String envTypeId);
}
