package bssm.plantshuman.peopleandgreen.recommendation.domain.service;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.EnvironmentType;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.HumidityBand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.SunlightLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.TemperatureBand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.UserEnvironment;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.VentilationLevel;

import java.util.ArrayList;
import java.util.List;

public class SecondaryEnvironmentTagResolver {

    public List<EnvironmentType> resolve(UserEnvironment environment, EnvironmentType representativeEnvironment) {
        List<EnvironmentType> tags = new ArrayList<>();

        if (environment.sunlight() == SunlightLevel.MEDIUM) {
            tags.add(EnvironmentType.ENV_03_BRIGHT_INDIRECT);
        } else if (environment.sunlight() == SunlightLevel.HIGH) {
            tags.add(EnvironmentType.ENV_01_SUNNY);
        } else {
            tags.add(EnvironmentType.ENV_02_DARK);
        }

        if (environment.temperature() == TemperatureBand.HIGH) {
            tags.add(EnvironmentType.ENV_04_HOT);
        } else if (environment.temperature() == TemperatureBand.LOW) {
            tags.add(EnvironmentType.ENV_05_COLD);
        }

        if (environment.humidity() == HumidityBand.LOW) {
            tags.add(EnvironmentType.ENV_06_DRY);
        } else if (environment.humidity() == HumidityBand.HIGH) {
            tags.add(EnvironmentType.ENV_07_HUMID);
        }

        if (environment.ventilation() == VentilationLevel.HIGH) {
            tags.add(EnvironmentType.ENV_08_WELL_VENTILATED);
        } else if (environment.ventilation() == VentilationLevel.LOW) {
            tags.add(EnvironmentType.ENV_09_CLOSED);
        }

        return tags.stream()
                .distinct()
                .filter(tag -> tag != representativeEnvironment)
                .toList();
    }
}
