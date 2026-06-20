package com.finio.backend.profiles.application.internal.commandservices;

import com.finio.backend.finance.domain.model.aggregates.Account;
import com.finio.backend.finance.infrastructure.persistence.jpa.AccountRepository;
import com.finio.backend.profiles.domain.model.commands.*;
import com.finio.backend.profiles.domain.model.aggregates.Profile;
import com.finio.backend.profiles.domain.services.ProfileCommandService;
import com.finio.backend.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {

    private final ProfileRepository profileRepository;
    private final AccountRepository accountRepository;

    public ProfileCommandServiceImpl(ProfileRepository profileRepository, AccountRepository accountRepository) {
        this.profileRepository = profileRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public Optional<Profile> handle(CreateProfileCommand command) {
        if (profileRepository.findByUserId(command.user_id()).isPresent()) {
            throw new IllegalArgumentException("Profile already exists for this user");
        }
        var profile = new Profile(command.name(), command.user_id());
        profileRepository.save(profile);
        return Optional.of(profile);
    }

    @Override
    @Transactional
    public Optional<Profile> handle(UpdateProfileCommand command) {
        Profile profile = profileRepository.findByUserId(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        profile.setName(command.name());
        profile.setSaving_percentage(command.saving_percentage());
        profile.setAllow_ml_analysis(command.allow_ml_analysis());
        profile.setAllow_push_notifications(command.allow_push_notifications());
        profile.setUse_biometrics(command.use_biometrics());

        Profile savedProfile = profileRepository.save(profile);

        List<Account> accounts = accountRepository.findByUserId(command.userId());
        for (Account account : accounts) {
            account.updateSavingsMetrics(savedProfile.getSaving_percentage(), account.getBalance());
            accountRepository.save(account);
        }

        return Optional.of(savedProfile);
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
    public Optional<Profile> handle(UpdateSavingPercentageCommand command) {
        Profile profile = profileRepository.findByUserId(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for user ID: " + command.userId()));

        profile.setSaving_percentage(command.percentage());

        Profile savedProfile = profileRepository.save(profile);

        List<Account> accounts = accountRepository.findByUserId(command.userId());
        for (Account account : accounts) {
            account.updateSavingsMetrics(savedProfile.getSaving_percentage(), account.getBalance());
            accountRepository.save(account);
        }

        return Optional.of(savedProfile);
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