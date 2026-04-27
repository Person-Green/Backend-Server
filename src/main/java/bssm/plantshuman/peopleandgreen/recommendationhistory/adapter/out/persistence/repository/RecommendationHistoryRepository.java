package bssm.plantshuman.peopleandgreen.recommendationhistory.adapter.out.persistence.repository;

import bssm.plantshuman.peopleandgreen.recommendationhistory.adapter.out.persistence.entity.RecommendationHistoryEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecommendationHistoryRepository extends JpaRepository<RecommendationHistoryEntity, Long> {

    List<RecommendationHistoryEntity> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);

    List<RecommendationHistoryEntity> findByUserIdAndIdLessThanOrderByIdDesc(Long userId, Long cursor, Pageable pageable);

    Optional<RecommendationHistoryEntity> findByIdAndUserId(Long historyId, Long userId);

    @Modifying
    @Query("delete from RecommendationHistoryEntity h where h.id = :historyId and h.userId = :userId")
    int deleteOwnedHistory(@Param("userId") Long userId, @Param("historyId") Long historyId);
}
