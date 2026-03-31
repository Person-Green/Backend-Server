package bssm.plantshuman.peopleandgreen.presentation.diagnosis;

import bssm.plantshuman.peopleandgreen.application.diagonsis.DiagnosisUseCase;
import bssm.plantshuman.peopleandgreen.presentation.diagnosis.dto.request.DiagnosisRequest;
import bssm.plantshuman.peopleandgreen.presentation.diagnosis.dto.resposne.DiagnosisResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diagnosis")
public class DiagnosisController {

    private final DiagnosisUseCase diagnosisUseCase;

    @PostMapping
    public ResponseEntity<DiagnosisResultResponse> execute(@RequestBody DiagnosisRequest request) {
        return ResponseEntity.ok(diagnosisUseCase.execute(request));
    }

}
