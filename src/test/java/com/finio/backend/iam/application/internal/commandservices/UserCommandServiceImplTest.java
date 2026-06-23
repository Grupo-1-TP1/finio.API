package com.finio.backend.iam.application.internal.commandservices;

import com.finio.backend.iam.application.internal.outboundservices.hashing.HashingService;
import com.finio.backend.iam.application.internal.outboundservices.tokens.TokenService;
import com.finio.backend.iam.domain.model.commands.SignUpCommand;
import com.finio.backend.iam.domain.services.outboundports.ProfileServiceFacade;
import com.finio.backend.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.finio.backend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HashingService hashingService;

    @Mock
    private TokenService tokenService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ProfileServiceFacade profileServiceFacade;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserCommandServiceImpl userCommandService;

    private SignUpCommand signUpCommand;
    private String userName;

    @BeforeEach
    void setUp() {
        userName = "Administrator";
        signUpCommand = new SignUpCommand(
                "admin@gmail.com",
                "Password123!",
                Collections.emptyList()
        );
    }

    @Test
    void shouldThrowRuntimeExceptionWhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(signUpCommand.email())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userCommandService.handle(signUpCommand, userName);
        });

        assertEquals("Email already exists", exception.getMessage());

        verify(userRepository, never()).save(any());
        verify(profileServiceFacade, never()).createProfileForUser(anyString(), anyLong());
    }
}