package bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.repository;

import bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.entity.RecommendationPlantEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecommendationPlantRepository extends JpaRepository<RecommendationPlantEntity, String> {

    @Query("select case when count(p) > 0 then true else false end from RecommendationPlantEntity p")
    boolean existsAnyPlant();

    @Override
    @EntityGraph(attributePaths = {"condition", "condition.supportedPlacements", "environmentFits"})
    List<RecommendationPlantEntity> findAll();
}
