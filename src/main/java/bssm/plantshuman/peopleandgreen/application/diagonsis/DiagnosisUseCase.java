package bssm.plantshuman.peopleandgreen.application.diagonsis;

import bssm.plantshuman.peopleandgreen.domain.diagnosis.FitType;
import bssm.plantshuman.peopleandgreen.domain.diagnosis.PlantEnvironment;
import bssm.plantshuman.peopleandgreen.domain.diagnosis.PlantEnvironmentMapping;
import bssm.plantshuman.peopleandgreen.infrastructure.persistence.plant.PlantEnvironmentMappingRepository;
import bssm.plantshuman.peopleandgreen.infrastructure.persistence.plant.PlantEnvironmentRepository;
import bssm.plantshuman.peopleandgreen.presentation.diagnosis.dto.request.DiagnosisRequest;
import bssm.plantshuman.peopleandgreen.presentation.diagnosis.dto.resposne.DiagnosisResponse;
import bssm.plantshuman.peopleandgreen.presentation.diagnosis.dto.resposne.DiagnosisResultResponse;
import bssm.plantshuman.peopleandgreen.presentation.diagnosis.dto.resposne.PlantSummaryResponse;
import bssm.plantshuman.peopleandgreen.shared.EnvTypeResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiagnosisUseCase {

    private final PlantEnvironmentMappingRepository plantEnvironmentMappingRepository;
    private final PlantEnvironmentRepository plantEnvironmentRepository;

    public DiagnosisResultResponse execute(DiagnosisRequest request) {
        String envTypeId = EnvTypeResolver.resolve(request);

        List<PlantEnvironmentMapping> mappings =
                plantEnvironmentMappingRepository.findByPlantEnvironment_TypeId(envTypeId);

        PlantEnvironment environment = plantEnvironmentRepository.findById(envTypeId).get();

        List<PlantSummaryResponse> plants = mappings.stream()
                .sorted(Comparator.comparing(m ->
                        m.getFit() == FitType.OPTIMAL ? 0 : 1))
                .map(m -> PlantSummaryResponse.from(m.getPlant(), m.getFit()))
                .toList();

        return new DiagnosisResultResponse(DiagnosisResponse.from(environment), plants);
    }

}
