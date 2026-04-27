package bssm.plantshuman.peopleandgreen.recommendation.adapter.in.web;

import bssm.plantshuman.peopleandgreen.auth.adapter.out.security.AuthenticatedUser;
import bssm.plantshuman.peopleandgreen.recommendation.adapter.in.web.dto.request.RecommendPlantsRequest;
import bssm.plantshuman.peopleandgreen.recommendation.adapter.in.web.dto.response.RecommendPlantsResponse;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.in.RecommendPlantsWithHistoryUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diagnosis/recommendations")
public class PlantRecommendationController {

    private final RecommendPlantsWithHistoryUseCase recommendPlantsWithHistoryUseCase;

    @PostMapping
    public ResponseEntity<RecommendPlantsResponse> recommend(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody RecommendPlantsRequest request
    ) {
        var executionResult = recommendPlantsWithHistoryUseCase.recommend(authenticatedUser.userId(), request.toCommand());
        return ResponseEntity.ok(RecommendPlantsResponse.from(
                executionResult.historyId(),
                executionResult.saved(),
                executionResult.result()
        ));
    }
}
