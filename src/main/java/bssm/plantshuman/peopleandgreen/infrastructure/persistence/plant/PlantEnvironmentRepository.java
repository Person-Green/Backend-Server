package bssm.plantshuman.peopleandgreen.infrastructure.persistence.plant;

import bssm.plantshuman.peopleandgreen.domain.diagnosis.PlantEnvironment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantEnvironmentRepository extends JpaRepository<PlantEnvironment, String> {
}
