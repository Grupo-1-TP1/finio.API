package com.finio.backend.profiles.application.internal.eventhandlers;

import com.finio.backend.iam.domain.model.events.UserAccountDeletedEvent; // Importa el evento de IAM
import com.finio.backend.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProfileContextEventHandler {

    private final ProfileRepository profileRepository;

    public ProfileContextEventHandler(com.finio.backend.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @EventListener
    @Transactional //
    public void on(UserAccountDeletedEvent event) {
        profileRepository.deleteByUserId(event.userId());
        System.out.println("🗑️ [Profiles] Perfil eliminado para el userId: " + event.userId());
    }
}