package com.finio.backend.iam.application.internal.commandservices;

import com.finio.backend.iam.application.internal.outboundservices.hashing.HashingService;
import com.finio.backend.iam.application.internal.outboundservices.tokens.TokenService;
import com.finio.backend.iam.domain.model.aggregates.User;
import com.finio.backend.iam.domain.model.commands.SignInCommand;
import com.finio.backend.iam.domain.model.commands.SignUpCommand;
import com.finio.backend.iam.domain.services.UserCommandService;
import com.finio.backend.iam.domain.services.outboundports.ProfileServiceFacade;
import com.finio.backend.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.finio.backend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * User command service implementation
 * <p>
 *     This class implements the {@link UserCommandService} interface and provides the implementation for the
 *     {@link SignInCommand} and {@link SignUpCommand} commands.
 * </p>
 * @version 1.0.0
 */

@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;
    private final ProfileServiceFacade profileServiceFacade;

    public UserCommandServiceImpl(UserRepository userRepository, HashingService hashingService, TokenService tokenService, RoleRepository roleRepository, ProfileServiceFacade profileServiceFacade) {

        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
        this.profileServiceFacade = profileServiceFacade;

    }

    /**
     * Handle the sign-in command
     * <p>
     *     This method handles the {@link SignInCommand} command and returns the user and the token.
     *     If the user is not found or the password is invalid, a {@link RuntimeException} will be thrown.
     * </p>
     * @param command the sign-in command containing the username and password
     * @return and optional containing the user matching the username and the generated token
     * @throws RuntimeException if the user is not found or the password is invalid
     */
    @Override
    public Optional<ImmutablePair<User, String>> handle(SignInCommand command) {

        var user = userRepository.findByEmail(command.email());

        if(user.isEmpty())
            throw new RuntimeException("User not found");

        if(!hashingService.matches(command.password(), user.get().getPassword()))
            throw new RuntimeException("Invalid password");

        var token = tokenService.generateToken(user.get().getEmail());
        return Optional.of(ImmutablePair.of(user.get(), token));
    }

    /**
     * Handle the sign-up command
     * <p>
     *     This method handles the {@link SignUpCommand} command and creates a new user.
     * </p>
     * @param command the sign-up command containing the username and password
     * @return an optional containing the created user
     */
    @Override
    public Optional<User> handle(SignUpCommand command, String name) {
        if(userRepository.existsByEmail(command.email())) {
            throw new RuntimeException("Email already exists");
        }
        var roles = command.roles().stream().
                map(role -> roleRepository.findByName(role.getName()).orElseThrow(() -> new RuntimeException("Role not found"))).
                toList();
        var user = new User(command.email(), hashingService.encode(command.password()), roles);
        userRepository.save(user);

        profileServiceFacade.createProfileForUser(name, user.getId());

        return userRepository.findByEmail(command.email());
    }
}
