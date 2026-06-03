package com.finio.backend.iam.domain.services.outboundports;

/**
 * Outbound Port / Facade interface inside IAM context.
 * Acts as an Anti-Corruption Layer (ACL) boundary.
 */
public interface ProfileServiceFacade {
    /**
     * Triggers the profile creation in the Profiles context.
     * @param name the full name of the user
     * @param userId the generated technical ID from IAM
     */
    void createProfileForUser(String name, Long userId);
}