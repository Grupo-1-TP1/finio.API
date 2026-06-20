package com.finio.backend.profiles.interfaces.rest;

import com.finio.backend.profiles.domain.model.commands.DeleteProfileCommand;
import com.finio.backend.profiles.domain.model.commands.UpdatePrivacyPermissionsCommand;
import com.finio.backend.profiles.domain.model.commands.UpdateProfileNameCommand;
import com.finio.backend.profiles.domain.model.queries.GetProfileByIdQuery;
import com.finio.backend.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.finio.backend.profiles.domain.services.ProfileCommandService;
import com.finio.backend.profiles.domain.services.ProfileQueryService;
import com.finio.backend.profiles.interfaces.rest.resources.*;
import com.finio.backend.profiles.interfaces.rest.transform.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ProfileController
 * REST Controller for managing Profiles Bounded Context inbound requests.
 */
@RestController
@RequestMapping(value = "/api/v1/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Profiles", description = "Profile Management Endpoints")
public class ProfileController {

    private final ProfileQueryService profileQueryService;
    private final ProfileCommandService profileCommandService;

    public ProfileController(ProfileQueryService profileQueryService, ProfileCommandService profileCommandService) {
        this.profileQueryService = profileQueryService;
        this.profileCommandService = profileCommandService;
    }

    /**
     * Get profile by its technical ID
     * @param profileId the profile identifier
     * @return the profile resource if found, or 404 Not Found
     */
    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileResource> getProfileById(@PathVariable Long profileId) {
        var getProfileByIdQuery = new GetProfileByIdQuery(profileId);
        var profile = profileQueryService.handle(getProfileByIdQuery);

        if (profile.isEmpty()) return ResponseEntity.notFound().build();

        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get());
        return ResponseEntity.ok(profileResource);
    }

    /**
     * Get profile by the associated IAM User ID
     * Useful for Flutter after login to fetch financial profile details
     * @param userId the IAM user identifier
     * @return the profile resource if found, or 404 Not Found
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ProfileResource> getProfileByUserId(@PathVariable Long userId) {
        var getProfileByUserIdQuery = new GetProfileByUserIdQuery(userId);
        var profile = profileQueryService.handle(getProfileByUserIdQuery);

        if (profile.isEmpty()) return ResponseEntity.notFound().build();

        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get());
        return ResponseEntity.ok(profileResource);
    }

    @PutMapping("user/{userId}")
    public ResponseEntity<ProfileResource> updateProfile(@PathVariable Long userId, @RequestBody UpdateProfileResource resource) {
        return profileCommandService.handle(UpdateProfileCommandFromResourceAssembler.toCommandFromResource(userId, resource))
                .map(profile -> ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/permissions/{userId}")
    public ResponseEntity<ProfileResource> updatePrivacyPermissions(@PathVariable Long userId, @RequestBody UpdatePrivacyPermissionsResource resource) {
        return profileCommandService.handle(UpdatePrivacyPermissionsCommandFromResourceAssembler.toCommandFromResource(userId, resource))
                .map(profile -> ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/change-name/{userId}")
    public ResponseEntity<ProfileResource> updateName(@PathVariable Long userId, @RequestBody UpdateProfileNameResource resource) {
        return profileCommandService.handle(UpdateProfileNameCommandFromResourceAssembler.toCommandFromResource(userId, resource))
                .map(profile -> ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/saving-percentage/{userId}")
    public ResponseEntity<ProfileResource> updateSavingPercentage(@PathVariable Long userId, @RequestBody UpdateSavingPercentageResource resource) {
        return profileCommandService.handle(UpdateSavingPercentageCommandFromResourceAssembler.toCommandFromResource(userId, resource))
                .map(profile -> ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<?> deleteProfileById(@PathVariable Long profileId) {
        var command = new DeleteProfileCommand(profileId);
        var deleted = profileCommandService.handle(command);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Profile deleted successfully");
    }
}