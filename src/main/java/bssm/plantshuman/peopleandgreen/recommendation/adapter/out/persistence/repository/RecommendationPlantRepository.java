package bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.repository;

import bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.entity.RecommendationPlantEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationPlantRepository extends JpaRepository<RecommendationPlantEntity, String> {

    @Override
    @EntityGraph(attributePaths = {"condition", "condition.supportedPlacements", "environmentFits"})
    List<RecommendationPlantEntity> findAll();
}
