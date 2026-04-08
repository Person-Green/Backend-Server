package bssm.plantshuman.peopleandgreen.auth.adapter.out.redis;

import bssm.plantshuman.peopleandgreen.auth.domain.model.OAuthSession;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 인메모리 OAuth 세션 저장소 (Redis 의존 없음, 단일 인스턴스 전용).
 *
 * <p>{@link #consume}은 ConcurrentHashMap.remove로 원자적 GET+DELETE를 구현하여
 * state 재사용(replay) 공격을 방지한다. TTL은 {@link #evictExpired} 스케줄러로 관리한다.</p>
 */
@Component
public class OAuthSessionStore {

    private static final long SESSION_TTL_SECONDS = 300;

    private record StoredSession(OAuthSession session, Instant expiresAt) {}

    private final ConcurrentHashMap<String, StoredSession> sessions = new ConcurrentHashMap<>();

    public void save(String stateJwt, OAuthSession session) {
        sessions.put(toKey(stateJwt), new StoredSession(session, Instant.now().plusSeconds(SESSION_TTL_SECONDS)));
    }

    /**
     * 세션을 원자적으로 읽고 삭제한다.
     * 이미 사용됐거나 만료된 경우 {@link Optional#empty()}를 반환한다.
     */
    public Optional<OAuthSession> consume(String stateJwt) {
        StoredSession stored = sessions.remove(toKey(stateJwt));
        if (stored == null || Instant.now().isAfter(stored.expiresAt())) {
            return Optional.empty();
        }
        return Optional.of(stored.session());
    }

    /** 60초마다 만료된 세션을 정리한다. */
    @Scheduled(fixedDelay = 60_000)
    public void evictExpired() {
        Instant now = Instant.now();
        sessions.entrySet().removeIf(entry -> now.isAfter(entry.getValue().expiresAt()));
    }

    private String toKey(String stateJwt) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(stateJwt.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(64);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
