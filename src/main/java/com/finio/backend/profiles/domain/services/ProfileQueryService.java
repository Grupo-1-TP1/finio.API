package com.finio.backend.profiles.domain.services;

import com.finio.backend.profiles.domain.model.queries.GetProfileByIdQuery;
import com.finio.backend.profiles.domain.model.queries.GetProfileByNameQuery;
import com.finio.backend.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.finio.backend.profiles.domain.model.aggregates.Profile;
import java.util.Optional;

public interface ProfileQueryService {
    Optional<Profile> handle(GetProfileByIdQuery query);
    Optional<Profile> handle(GetProfileByUserIdQuery query);
    Optional<Profile> handle(GetProfileByNameQuery query);
}