package com.finio.backend.profiles.domain.services;

import com.finio.backend.profiles.domain.model.commands.*;
import com.finio.backend.profiles.domain.model.aggregates.Profile;

import java.util.Optional;

public interface ProfileCommandService {
    Optional<Profile> handle(CreateProfileCommand command);
    boolean handle(DeleteProfileCommand command);
    Optional<Profile> handle(UpdateProfileCommand command);
    Optional<Profile> handle(UpdateProfileNameCommand command);
    Optional<Profile> handle(UpdatePrivacyPermissionsCommand command);
}