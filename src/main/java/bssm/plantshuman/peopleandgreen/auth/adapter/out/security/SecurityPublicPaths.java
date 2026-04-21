package bssm.plantshuman.peopleandgreen.auth.adapter.out.security;

import java.util.List;

public final class SecurityPublicPaths {

    public static final List<String> ALL = List.of(
            "/auth/google/authorize",
            "/auth/google/login",
            "/auth/token/refresh",
            "/auth/logout",
            "/actuator/health",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    );

    private SecurityPublicPaths() {
    }
}
