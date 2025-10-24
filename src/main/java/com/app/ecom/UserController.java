package com.app.ecom;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UserController is the REST controller layer for handling all HTTP requests
 * related to User entity operations such as:
 *  - Fetching all users
 *  - Fetching a specific user by ID
 *  - Creating a new user
 *  - Updating an existing user
 *
 * It follows REST principles and interacts with the service layer (UserService)
 * which in turn communicates with the repository layer to perform CRUD operations.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users") // Common base path for all user-related endpoints
public class UserController {

    /**
     * Injecting UserService to delegate business logic.
     * The @Autowired annotation is used for dependency injection,
     * although constructor injection (via Lombok’s @RequiredArgsConstructor)
     * is preferred in modern Spring Boot applications.
     */
    @Autowired
    private UserService userService;

    /**
     * GET /api/users
     * Fetches all users from the database.
     *
     * @return ResponseEntity containing a list of users and HTTP status 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        // Either ResponseEntity.ok(...) or new ResponseEntity<>(..., HttpStatus.OK) both are valid.
        return new ResponseEntity<>(userService.fetchAllUsers(), HttpStatus.OK);
    }

    /**
     * GET /api/users/{id}
     * Fetches a single user by their unique ID.
     *
     * @param id the ID of the user to fetch
     * @return ResponseEntity containing the user object and HTTP status 200 (OK) if found,
     *         or 404 (Not Found) if no user exists with the given ID.
     *
     * This implementation uses Optional and Streams for cleaner, null-safe code.
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        // Using Optional's map to directly wrap the user in a ResponseEntity if present
        return userService.fetchUser(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/users
     * Creates a new user in the database.
     *
     * @param user the User object received from the request body
     * @return ResponseEntity with a success message and HTTP status 200 (OK)
     *
     * @RequestBody tells Spring to map the incoming JSON payload to a User object.
     */
    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody User user) {
        userService.addUser(user);
        return ResponseEntity.ok("User Added Successfully");
    }

    /**
     * PUT /api/users/{id}
     * Updates an existing user's details.
     *
     * Important considerations when designing PUT APIs:
     *  1. Which user do you want to update? → Identified by path variable (ID)
     *  2. What details do you want to update? → Passed in request body (User object)
     *
     * @param id the ID of the user to update
     * @param updatedUser the updated user details
     * @return ResponseEntity with success or failure message based on update status
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        boolean updated = userService.updateUser(id, updatedUser);
        if (updated) {
            return ResponseEntity.ok("User Updated Successfully");
        }
        // Returns HTTP 404 if user not found
        return ResponseEntity.notFound().build();
    }
}
