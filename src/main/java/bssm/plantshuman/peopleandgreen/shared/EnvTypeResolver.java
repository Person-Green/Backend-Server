package bssm.plantshuman.peopleandgreen.shared;

import bssm.plantshuman.peopleandgreen.presentation.diagnosis.dto.request.DiagnosisRequest;
import org.springframework.stereotype.Component;

@Component
public class EnvTypeResolver {

    public static String resolve(DiagnosisRequest request){
        if ("많음".equals(request.sunlight()))    return "ENV-01";
        if ("없음".equals(request.sunlight()))    return "ENV-02";
        if ("간접광".equals(request.sunlight()))  return "ENV-03";

        if ("25도이상".equals(request.temperature())) return "ENV-04";
        if ("15도이하".equals(request.temperature())) return "ENV-05";
        if ("건조".equals(request.humidity()))        return "ENV-06";
        if ("습함".equals(request.humidity()))        return "ENV-07";

        if ("좋음".equals(request.ventilation())) return "ENV-08";
        if ("밀폐".equals(request.ventilation())) return "ENV-09";

        return "ENV-03";
    }
}
