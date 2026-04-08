package bssm.plantshuman.peopleandgreen.auth.adapter.out.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SecurityConfigurationTest {

    @Test
    void trimsConfiguredCorsOrigins() {
        SecurityConfiguration configuration = new SecurityConfiguration(null);
        ReflectionTestUtils.setField(configuration, "allowedOrigins", "https://app.example.com, https://admin.example.com,");

        CorsConfigurationSource source = configuration.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/test");

        CorsConfiguration corsConfiguration = source.getCorsConfiguration(request);

        assertNotNull(corsConfiguration);
        assertEquals(
                java.util.List.of("https://app.example.com", "https://admin.example.com"),
                corsConfiguration.getAllowedOrigins()
        );
    }
}
