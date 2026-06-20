package com.finio.backend.iam.interfaces.rest;

import com.finio.backend.iam.domain.model.commands.DeleteUserCommand;
import com.finio.backend.iam.domain.model.queries.GetAllUsersQuery;
import com.finio.backend.iam.domain.model.queries.GetUserByIdQuery;
import com.finio.backend.iam.domain.model.queries.GetUserByUsernameQuery;
import com.finio.backend.iam.domain.services.UserCommandService;
import com.finio.backend.iam.domain.services.UserQueryService;
import com.finio.backend.iam.interfaces.rest.resources.ResetPasswordResource;
import com.finio.backend.iam.interfaces.rest.resources.UserResource;
import com.finio.backend.iam.interfaces.rest.transform.ResetPasswordCommandFromResourceAssembler;
import com.finio.backend.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This class is a REST controller that exposes the users resource.
 * It includes the following operations:
 * - GET /api/v1/users: returns all the users
 * - GET /api/v1/users/{userId}: returns the user with the given id
 **/
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "User Management Endpoints")
public class UsersController {
    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    public UsersController(UserQueryService userQueryService, UserCommandService userCommandService) {
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
    }

    /**
     * This method returns all the users.
     *
     * @return a list of user resources
     * @see UserResource
     */
    @GetMapping
    public ResponseEntity<List<UserResource>> getAllUsers() {
        var getAllUsersQuery = new GetAllUsersQuery();
        var users = userQueryService.handle(getAllUsersQuery);
        var userResources = users.stream().map(UserResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(userResources);
    }

    /**
     * This method returns the user with the given id.
     *
     * @param id the user id
     * @return the user resource with the given id
     * @throws RuntimeException if the user is not found
     * @see UserResource
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserResource> getUserById(@PathVariable Long id) {
        var getUserByIdQuery = new GetUserByIdQuery(id);
        var user = userQueryService.handle(getUserByIdQuery);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return ResponseEntity.ok(userResource);
    }

    @GetMapping(value = "/email/{email}")
    public ResponseEntity<UserResource> getUserByEmail(@PathVariable String email) {
        var getUserByEmailQuery = new GetUserByUsernameQuery(email);
        var user = userQueryService.handle(getUserByEmailQuery);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return ResponseEntity.ok(userResource);
    }

    @PutMapping("/change-password/{email}")
    public ResponseEntity<UserResource> changePassword(@PathVariable String email, @RequestBody ResetPasswordResource resource) {
        return userCommandService.handle(ResetPasswordCommandFromResourceAssembler.toCommandFromResource(email, resource))
                .map(user -> ResponseEntity.ok(UserResourceFromEntityAssembler.toResourceFromEntity(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        var command = new DeleteUserCommand(userId);
        var deleted = userCommandService.handle(command);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("User deleted successfully");
    }

}
