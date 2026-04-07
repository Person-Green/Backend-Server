package bssm.plantshuman.peopleandgreen.catalog.adapter.out.persistence.repository;

import bssm.plantshuman.peopleandgreen.catalog.adapter.out.persistence.entity.FavoritePlantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface FavoritePlantRepository extends JpaRepository<FavoritePlantEntity, Long> {

    boolean existsByUser_IdAndPlant_PlantId(Long userId, String plantId);

    void deleteByUser_IdAndPlant_PlantId(Long userId, String plantId);

    @Query("select f.plant.plantId from FavoritePlantEntity f where f.user.id = :userId and f.plant.plantId in :plantIds")
    Set<String> findFavoritePlantIds(@Param("userId") Long userId, @Param("plantIds") Set<String> plantIds);
}
