package bssm.plantshuman.peopleandgreen.recommendationhistory.adapter.out.persistence;

import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.RecommendationHistoryCommandPort;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.RecommendationHistoryQueryPort;
import bssm.plantshuman.peopleandgreen.recommendation.domain.exception.RecommendationHistoryNotFoundException;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsCommand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsResult;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationHistory;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationHistoryCursorPage;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationHistoryDraft;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationHistorySummary;
import bssm.plantshuman.peopleandgreen.recommendationhistory.adapter.out.persistence.entity.RecommendationHistoryEntity;
import bssm.plantshuman.peopleandgreen.recommendationhistory.adapter.out.persistence.repository.RecommendationHistoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationHistoryPersistenceAdapter implements RecommendationHistoryCommandPort, RecommendationHistoryQueryPort {

    private final RecommendationHistoryRepository recommendationHistoryRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Long save(RecommendationHistoryDraft draft) {
        RecommendationHistoryEntity saved = recommendationHistoryRepository.save(new RecommendationHistoryEntity(
                draft.userId(),
                draft.title(),
                draft.plantSummaryText(),
                writeJson(draft.requestSnapshot()),
                writeJson(draft.resultSnapshot()),
                Instant.now()
        ));
        return saved.getId();
    }

    @Override
    public RecommendationHistoryCursorPage getHistories(Long userId, String cursor, int size) {
        List<RecommendationHistoryEntity> entities = cursor == null
                ? recommendationHistoryRepository.findByUserIdOrderByIdDesc(userId, PageRequest.of(0, size + 1))
                : recommendationHistoryRepository.findByUserIdAndIdLessThanOrderByIdDesc(userId, Long.parseLong(cursor), PageRequest.of(0, size + 1));

        boolean hasNext = entities.size() > size;
        List<RecommendationHistorySummary> items = entities.stream()
                .limit(size)
                .map(entity -> new RecommendationHistorySummary(
                        entity.getId(),
                        entity.getTitle(),
                        entity.getPlantSummaryText(),
                        entity.getCreatedAt()
                ))
                .toList();
        String nextCursor = hasNext ? String.valueOf(items.getLast().historyId()) : null;
        return new RecommendationHistoryCursorPage(items, nextCursor, hasNext);
    }

    @Override
    public Optional<RecommendationHistory> getHistory(Long userId, Long historyId) {
        return recommendationHistoryRepository.findByIdAndUserId(historyId, userId).map(this::toDomain);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long historyId) {
        if (recommendationHistoryRepository.deleteOwnedHistory(userId, historyId) == 0) {
            throw new RecommendationHistoryNotFoundException();
        }
    }

    private RecommendationHistory toDomain(RecommendationHistoryEntity entity) {
        return new RecommendationHistory(
                entity.getId(),
                entity.getUserId(),
                entity.getTitle(),
                entity.getPlantSummaryText(),
                readJson(entity.getRequestSnapshot(), RecommendPlantsCommand.class),
                readJson(entity.getResultSnapshot(), RecommendPlantsResult.class),
                entity.getCreatedAt()
        );
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize recommendation history", exception);
        }
    }

    private <T> T readJson(String value, Class<T> type) {
        try {
            return objectMapper.readValue(value, type);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to deserialize recommendation history", exception);
        }
    }
}
