package com.finio.backend.profiles.domain.services;

import com.finio.backend.profiles.domain.model.commands.CreateProfileCommand;
import com.finio.backend.profiles.domain.model.aggregates.Profile;
import com.finio.backend.profiles.domain.model.commands.DeleteProfileCommand;

import java.util.Optional;

public interface ProfileCommandService {
    Optional<Profile> handle(CreateProfileCommand command);
    boolean handle(DeleteProfileCommand command);
}