package com.finio.backend.profiles.infrastructure.persistence.jpa.repositories;

import com.finio.backend.profiles.domain.model.aggregates.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(Long user_id);
    Optional<Profile> findByName(Long name);
    void deleteByUserId(Long user_id);
}