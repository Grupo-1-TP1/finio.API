package com.finio.backend.profiles.interfaces.rest.transform;

import com.finio.backend.profiles.domain.model.aggregates.Profile;
import com.finio.backend.profiles.interfaces.rest.resources.ProfileResource;

public class ProfileResourceFromEntityAssembler {
    public static ProfileResource toResourceFromEntity(Profile entity) {
        return new ProfileResource(entity.getProfile_id(), entity.getName(), entity.getUserId(), entity.getSaving_percentage(), entity.getAllow_ml_analysis(), entity.getAllow_push_notifications(), entity.getUse_biometrics());
    }
}