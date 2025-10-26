package com.app.ecom.service;

import com.app.ecom.dto.AddressDTO;
import com.app.ecom.dto.UserRequest;
import com.app.ecom.dto.UserResponse;
import com.app.ecom.model.Address;
import com.app.ecom.model.User;
import com.app.ecom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * =============================================================
 * 🧩 UserService — Core Business Logic for User Management
 * =============================================================
 *
 * This class represents the **Service Layer** in the Spring Boot 3-tier architecture.
 *
 * 🎯 Responsibilities:
 * - Handles business logic related to users.
 * - Communicates with the persistence layer via {@link UserRepository}.
 * - Converts between domain entities and Data Transfer Objects (DTOs).
 *
 * 🏗 Architectural Role:
 * - Controller Layer → handles incoming HTTP requests.
 * - Service Layer → encapsulates business logic (this class).
 * - Repository Layer → handles DB operations (CRUD).
 *
 * 📘 Design Notes:
 * - Uses `Optional` to handle nullable data safely.
 * - Converts `User` ↔ `UserResponse` / `UserRequest` via helper mappers.
 * - Demonstrates immutability and clean mapping logic.
 */
@Service // Marks this class as a service bean managed by Spring IoC container
public class UserService {

    /**
     * Injects the {@link UserRepository} dependency.
     *
     * ⚙️ Modern Recommendation:
     * Prefer **constructor injection** (with Lombok’s @RequiredArgsConstructor)
     * for better immutability and easier unit testing.
     *
     * For readability in smaller projects, @Autowired field injection is acceptable.
     */
    @Autowired
    private UserRepository userRepository;

    // --------------------------------------------------------------------
    // 🔹 FETCH ALL USERS
    // --------------------------------------------------------------------

    /**
     * Fetches all user records from the database.
     *
     * @return List<UserResponse> containing all users mapped to DTOs.
     *
     * JPA’s `findAll()` runs the query:
     * <pre>SELECT * FROM users;</pre>
     */
    public List<UserResponse> fetchAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse) // entity → DTO
                .collect(Collectors.toList());
    }

    // --------------------------------------------------------------------
    // 🔹 CREATE NEW USER
    // --------------------------------------------------------------------

    /**
     * Creates a new user record in the database.
     *
     * @param userRequest DTO carrying user details (firstName, lastName, email, etc.)
     *
     * Internally performs:
     * <ol>
     *   <li>Maps DTO → Entity</li>
     *   <li>Persists entity via JPA’s save()</li>
     *   <li>save() performs INSERT if ID is null, UPDATE otherwise</li>
     * </ol>
     */
    public void addUser(UserRequest userRequest) {
        User user = new User();
        updateUserFromRequest(user, userRequest);
        userRepository.save(user);
    }

    // --------------------------------------------------------------------
    // 🔹 FETCH SINGLE USER BY ID
    // --------------------------------------------------------------------

    /**
     * Retrieves a single user by ID.
     *
     * @param id Primary key of the user record.
     * @return Optional<UserResponse> — empty if not found.
     *
     * Using {@link Optional} avoids NullPointerExceptions and allows clean handling:
     * <pre>
     * userService.fetchUser(id)
     *      .ifPresent(user -> System.out.println(user.getFirstName()));
     * </pre>
     */
    public Optional<UserResponse> fetchUser(Long id) {
        return userRepository.findById(id)
                .map(this::mapToUserResponse);
    }

    // --------------------------------------------------------------------
    // 🔹 UPDATE EXISTING USER
    // --------------------------------------------------------------------

    /**
     * Updates an existing user's details.
     *
     * @param id The user’s unique ID.
     * @param updatedUserRequest DTO containing updated fields.
     * @return true if user exists and is updated successfully, false otherwise.
     *
     * Workflow:
     *  1️⃣ findById() → fetch user.
     *  2️⃣ map() → executed only if user exists.
     *  3️⃣ updateUserFromRequest() → copy DTO values.
     *  4️⃣ save() → persist changes.
     */
    public boolean updateUser(Long id, UserRequest updatedUserRequest) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    updateUserFromRequest(existingUser, updatedUserRequest);
                    userRepository.save(existingUser);
                    return true;
                })
                .orElse(false);
    }

    // --------------------------------------------------------------------
    // 🔸 HELPER METHOD — MAP DTO → ENTITY
    // --------------------------------------------------------------------

    /**
     * Maps a {@link UserRequest} DTO onto a {@link User} entity.
     *
     * Used for both **creation** and **update** operations.
     *
     * @param user The User entity to be updated.
     * @param userRequest The incoming DTO with request data.
     */
    private void updateUserFromRequest(User user, UserRequest userRequest) {
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());

        // ✅ Nested Address mapping (User → Address → AddressDTO)
        if (userRequest.getAddress() != null) {
            Address address = new Address();
            address.setStreet(userRequest.getAddress().getStreet());
            address.setCity(userRequest.getAddress().getCity());
            address.setState(userRequest.getAddress().getState());
            address.setCountry(userRequest.getAddress().getCountry());
            address.setZipcode(userRequest.getAddress().getZipcode());
            user.setAddress(address);
        }
    }

    // --------------------------------------------------------------------
    // 🔸 HELPER METHOD — MAP ENTITY → DTO
    // --------------------------------------------------------------------

    /**
     * Converts a {@link User} entity into a {@link UserResponse} DTO.
     *
     * This ensures that internal JPA entities are never exposed directly to API clients.
     */
    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();

        response.setId(String.valueOf(user.getId()));
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());

        // ✅ Handle nested address if present
        if (user.getAddress() != null) {
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setStreet(user.getAddress().getStreet());
            addressDTO.setCity(user.getAddress().getCity());
            addressDTO.setState(user.getAddress().getState());
            addressDTO.setZipcode(user.getAddress().getZipcode());
            addressDTO.setCountry(user.getAddress().getCountry());
            response.setAddress(addressDTO);
        }

        return response;
    }
}
