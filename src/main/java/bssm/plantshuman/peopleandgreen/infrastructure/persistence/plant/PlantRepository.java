package bssm.plantshuman.peopleandgreen.infrastructure.persistence.plant;

import bssm.plantshuman.peopleandgreen.domain.plant.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantRepository extends JpaRepository<Plant, String>, PlantRepositoryCustom {
}
