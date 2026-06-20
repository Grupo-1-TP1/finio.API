package com.finio.backend.iam.infrastructure.persistence.jpa.repositories;

import com.finio.backend.iam.domain.model.aggregates.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenCodeAndUserId(String tokenCode, Long userId);
    void deleteByUserId(Long userId);
}