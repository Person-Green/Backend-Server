package bssm.plantshuman.peopleandgreen.recommendation.domain.service;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.EnvironmentType;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.HumidityBand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.SunlightLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.TemperatureBand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.UserEnvironment;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.VentilationLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RepresentativeEnvironmentResolverTest {

    private final RepresentativeEnvironmentResolver resolver = new RepresentativeEnvironmentResolver();

    @Test
    void returnsSunnyWhenSunlightIsHigh() {
        UserEnvironment environment = new UserEnvironment(
                SunlightLevel.HIGH,
                VentilationLevel.NORMAL,
                TemperatureBand.NORMAL,
                HumidityBand.LOW
        );

        EnvironmentType result = resolver.resolve(environment);

        assertEquals(EnvironmentType.ENV_01_SUNNY, result);
    }

    @Test
    void returnsDarkWhenSunlightIsLow() {
        UserEnvironment environment = new UserEnvironment(
                SunlightLevel.LOW,
                VentilationLevel.HIGH,
                TemperatureBand.HIGH,
                HumidityBand.HIGH
        );

        EnvironmentType result = resolver.resolve(environment);

        assertEquals(EnvironmentType.ENV_02_DARK, result);
    }

    @Test
    void usesTemperatureAndHumiditySignalsWhenSunlightIsMedium() {
        UserEnvironment environment = new UserEnvironment(
                SunlightLevel.MEDIUM,
                VentilationLevel.NORMAL,
                TemperatureBand.HIGH,
                HumidityBand.NORMAL
        );

        EnvironmentType result = resolver.resolve(environment);

        assertEquals(EnvironmentType.ENV_04_HOT, result);
    }

    @Test
    void fallsBackToBrightIndirectWhenAllAxesAreNormal() {
        UserEnvironment environment = new UserEnvironment(
                SunlightLevel.MEDIUM,
                VentilationLevel.NORMAL,
                TemperatureBand.NORMAL,
                HumidityBand.NORMAL
        );

        EnvironmentType result = resolver.resolve(environment);

        assertEquals(EnvironmentType.ENV_03_BRIGHT_INDIRECT, result);
    }
}
