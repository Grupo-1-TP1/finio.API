package com.finio.backend.profiles.application.internal.queryservices;

import com.finio.backend.profiles.domain.model.queries.GetProfileByIdQuery;
import com.finio.backend.profiles.domain.model.queries.GetProfileByNameQuery;
import com.finio.backend.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.finio.backend.profiles.domain.model.aggregates.Profile;
import com.finio.backend.profiles.domain.services.ProfileQueryService;
import com.finio.backend.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ProfileQueryServiceImpl implements ProfileQueryService {

    private final ProfileRepository profileRepository;

    public ProfileQueryServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<Profile> handle(GetProfileByIdQuery query) {
        return profileRepository.findById(query.profile_id());
    }

    @Override
    public Optional<Profile> handle(GetProfileByUserIdQuery query) {
        return profileRepository.findByUserId(query.user_id());
    }

    @Override
    public Optional<Profile> handle(GetProfileByNameQuery query) {
        return profileRepository.findByName(query.name());
    }
}