package com.finio.backend.profiles.application.internal.commandservices;

import com.finio.backend.profiles.domain.model.commands.*;
import com.finio.backend.profiles.domain.model.aggregates.Profile;
import com.finio.backend.profiles.domain.services.ProfileCommandService;
import com.finio.backend.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import jakarta.transaction.Transactional;
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
    public Optional<Profile> handle(UpdateProfileCommand command) {
        Profile profile = profileRepository.findByUserId(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        profile.setName(command.name());
        profile.setSaving_percentage(command.saving_percentage());
        profile.setAllow_ml_analysis(command.allow_ml_analysis());
        profile.setAllow_push_notifications(command.allow_push_notifications());
        profile.setUse_biometrics(command.use_biometrics());
        return Optional.of(profileRepository.save(profile));
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

    @Override
    @Transactional
    public Optional<Profile> handle(UpdateProfileNameCommand command) {
        Profile profile = profileRepository.findByUserId(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for user ID: " + command.userId()));

        profile.setName(command.name());

        return Optional.of(profileRepository.save(profile));
    }

    @Override
    @Transactional
    public Optional<Profile> handle(UpdatePrivacyPermissionsCommand command) {
        Profile profile = profileRepository.findByUserId(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for user ID: " + command.userId()));

        profile.setAllow_ml_analysis(command.allow_ml_analysis());
        profile.setAllow_push_notifications(command.allow_push_notifications());
        profile.setUse_biometrics(command.use_biometrics());

        return Optional.of(profileRepository.save(profile));
    }
}