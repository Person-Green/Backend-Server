package bssm.plantshuman.peopleandgreen.recommendation.adapter.in.web;

import bssm.plantshuman.peopleandgreen.recommendation.adapter.in.web.dto.request.RecommendPlantsRequest;
import bssm.plantshuman.peopleandgreen.recommendation.adapter.in.web.dto.response.RecommendPlantsResponse;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.in.RecommendPlantsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diagnosis/recommendations")
public class PlantRecommendationController {

    private final RecommendPlantsUseCase recommendPlantsUseCase;

    @PostMapping
    public ResponseEntity<RecommendPlantsResponse> recommend(@RequestBody RecommendPlantsRequest request) {
        return ResponseEntity.ok(RecommendPlantsResponse.from(recommendPlantsUseCase.recommend(request.toCommand())));
    }
}
