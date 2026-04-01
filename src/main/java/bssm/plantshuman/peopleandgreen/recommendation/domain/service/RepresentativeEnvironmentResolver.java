package bssm.plantshuman.peopleandgreen.recommendation.domain.service;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.EnvironmentType;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.HumidityBand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.SunlightLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.TemperatureBand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.UserEnvironment;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.VentilationLevel;

public class RepresentativeEnvironmentResolver {

    public EnvironmentType resolve(UserEnvironment environment) {
        if (environment.sunlight() == SunlightLevel.HIGH) {
            return EnvironmentType.ENV_01_SUNNY;
        }
        if (environment.sunlight() == SunlightLevel.LOW) {
            return EnvironmentType.ENV_02_DARK;
        }
        if (environment.temperature() == TemperatureBand.HIGH) {
            return EnvironmentType.ENV_04_HOT;
        }
        if (environment.temperature() == TemperatureBand.LOW) {
            return EnvironmentType.ENV_05_COLD;
        }
        if (environment.humidity() == HumidityBand.LOW) {
            return EnvironmentType.ENV_06_DRY;
        }
        if (environment.humidity() == HumidityBand.HIGH) {
            return EnvironmentType.ENV_07_HUMID;
        }
        if (environment.ventilation() == VentilationLevel.HIGH) {
            return EnvironmentType.ENV_08_WELL_VENTILATED;
        }
        if (environment.ventilation() == VentilationLevel.LOW) {
            return EnvironmentType.ENV_09_CLOSED;
        }
        return EnvironmentType.ENV_03_BRIGHT_INDIRECT;
    }
}
