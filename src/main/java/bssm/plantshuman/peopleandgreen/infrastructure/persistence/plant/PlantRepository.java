package bssm.plantshuman.peopleandgreen.infrastructure.persistence.plant;

import bssm.plantshuman.peopleandgreen.domain.plant.Plant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlantRepository extends JpaRepository<Plant, String> {

    @Query("""
            select p
            from Plant p
            where (:cursor is null or p.plantId > :cursor)
            order by p.plantId asc
            """)
    List<Plant> findPage(@Param("cursor") String cursor, Pageable pageable);
}
