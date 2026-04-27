package bssm.plantshuman.peopleandgreen.recommendationhistory.adapter.in.web;

import bssm.plantshuman.peopleandgreen.auth.adapter.out.security.AuthenticatedUser;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.in.DeleteRecommendationHistoryUseCase;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.in.GetRecommendationHistoriesUseCase;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.in.GetRecommendationHistoryUseCase;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/users/me/recommendation-histories")
public class RecommendationHistoryController {

    private final GetRecommendationHistoriesUseCase getRecommendationHistoriesUseCase;
    private final GetRecommendationHistoryUseCase getRecommendationHistoryUseCase;
    private final DeleteRecommendationHistoryUseCase deleteRecommendationHistoryUseCase;

    @GetMapping
    public ResponseEntity<RecommendationHistoryPageResponse> getHistories(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int size
    ) {
        return ResponseEntity.ok(RecommendationHistoryPageResponse.from(
                getRecommendationHistoriesUseCase.getHistories(authenticatedUser.userId(), cursor, size)
        ));
    }

    @GetMapping("/{historyId}")
    public ResponseEntity<RecommendationHistoryDetailResponse> getHistory(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long historyId
    ) {
        return ResponseEntity.ok(RecommendationHistoryDetailResponse.from(
                getRecommendationHistoryUseCase.getHistory(authenticatedUser.userId(), historyId)
        ));
    }

    @DeleteMapping("/{historyId}")
    public ResponseEntity<Void> deleteHistory(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long historyId
    ) {
        deleteRecommendationHistoryUseCase.delete(authenticatedUser.userId(), historyId);
        return ResponseEntity.noContent().build();
    }
}
