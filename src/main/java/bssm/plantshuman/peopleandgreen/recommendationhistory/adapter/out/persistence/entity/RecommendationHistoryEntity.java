package bssm.plantshuman.peopleandgreen.recommendationhistory.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "recommendation_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendationHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "plant_summary_text", nullable = false, length = 255)
    private String plantSummaryText;

    @Lob
    @Column(name = "request_snapshot", nullable = false, columnDefinition = "TEXT")
    private String requestSnapshot;

    @Lob
    @Column(name = "result_snapshot", nullable = false, columnDefinition = "TEXT")
    private String resultSnapshot;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public RecommendationHistoryEntity(
            Long userId,
            String title,
            String plantSummaryText,
            String requestSnapshot,
            String resultSnapshot,
            Instant createdAt
    ) {
        this.userId = userId;
        this.title = title;
        this.plantSummaryText = plantSummaryText;
        this.requestSnapshot = requestSnapshot;
        this.resultSnapshot = resultSnapshot;
        this.createdAt = createdAt;
    }
}
