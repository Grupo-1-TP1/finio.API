package com.finio.backend.iam.infrastructure.services;

import com.finio.backend.iam.domain.services.outboundports.ProfileServiceFacade;
import com.finio.backend.profiles.domain.model.commands.CreateProfileCommand;
import com.finio.backend.profiles.domain.services.ProfileCommandService;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceFacadeImpl implements ProfileServiceFacade {

    // Inyectamos el servicio legítimo del Bounded Context de Profile
    private final ProfileCommandService profileCommandService;

    public ProfileServiceFacadeImpl(ProfileCommandService profileCommandService) {
        this.profileCommandService = profileCommandService;
    }

    @Override
    public void createProfileForUser(String name, Long user_id) {
        // Traducimos los datos al lenguaje del dominio de Profiles (su Command)
        var createProfileCommand = new CreateProfileCommand(name, user_id);

        // Ejecutamos la acción en el otro contexto de forma segura
        profileCommandService.handle(createProfileCommand);
    }
}