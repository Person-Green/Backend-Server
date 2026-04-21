package bssm.plantshuman.peopleandgreen.auth.adapter.out.security;

public final class SecurityPublicPaths {

    public static final String[] ALL = {
            "/auth/google/authorize",
            "/auth/google/login",
            "/auth/token/refresh",
            "/auth/logout",
            "/actuator/health",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    private SecurityPublicPaths() {
    }
}
