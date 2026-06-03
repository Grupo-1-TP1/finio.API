package com.finio.backend.profiles.application.internal.commandservices;

import com.finio.backend.profiles.domain.model.commands.CreateProfileCommand;
import com.finio.backend.profiles.domain.model.aggregates.Profile;
import com.finio.backend.profiles.domain.model.commands.DeleteProfileCommand;
import com.finio.backend.profiles.domain.services.ProfileCommandService;
import com.finio.backend.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {

    private final ProfileRepository profileRepository;

    public ProfileCommandServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<Profile> handle(CreateProfileCommand command) {
        if (profileRepository.findByUserId(command.user_id()).isPresent()) {
            throw new IllegalArgumentException("Profile already exists for this user");
        }
        var profile = new Profile(command.name(), command.user_id());
        profileRepository.save(profile);
        return Optional.of(profile);
    }

    @Override
    public boolean handle(DeleteProfileCommand command) {
        if (!profileRepository.existsById(command.profile_id())) {
            return false;
        }
        try {
            profileRepository.deleteById(command.profile_id());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}