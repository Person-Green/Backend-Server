package bssm.plantshuman.peopleandgreen.auth.application.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private boolean requireHttps = true;
    private Cors cors = new Cors();

    @Getter
    @Setter
    public static class Cors {

        private List<String> allowedOrigins = List.of("http://localhost:3000");
    }
}
