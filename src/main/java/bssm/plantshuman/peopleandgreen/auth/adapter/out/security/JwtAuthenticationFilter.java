package bssm.plantshuman.peopleandgreen.auth.adapter.out.security;

import bssm.plantshuman.peopleandgreen.auth.application.port.out.IssueJwtPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    private final IssueJwtPort issueJwtPort;

    public JwtAuthenticationFilter(IssueJwtPort issueJwtPort) {
        this.issueJwtPort = issueJwtPort;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = extractApplicationPath(request);
        for (String publicPath : SecurityPublicPaths.ALL) {
            if (matches(path, publicPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length());
        try {
            if (issueJwtPort.isRefreshToken(token)) {
                unauthorized(response, "Refresh token cannot be used as access token");
                return;
            }

            Long userId = issueJwtPort.parseUserId(token);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    new AuthenticatedUser(userId),
                    null,
                    List.of()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (RuntimeException exception) {
            SecurityContextHolder.clearContext();
            unauthorized(response, "Invalid access token");
        }
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }

    private boolean matches(String requestPath, String pattern) {
        return PATH_MATCHER.match(pattern, requestPath);
    }

    private String extractApplicationPath(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();

        if (servletPath != null && !servletPath.isEmpty()) {
            return pathInfo == null ? servletPath : servletPath + pathInfo;
        }
        if (pathInfo != null && !pathInfo.isEmpty()) {
            return pathInfo;
        }

        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && requestUri != null && requestUri.startsWith(contextPath)) {
            String path = requestUri.substring(contextPath.length());
            return path.isEmpty() ? "/" : path;
        }
        return requestUri == null ? "/" : requestUri;
    }
}
