package bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence.repository;

import bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);
}
