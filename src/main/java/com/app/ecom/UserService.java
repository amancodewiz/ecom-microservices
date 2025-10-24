package com.app.ecom;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * UserService acts as the **Service Layer** in the 3-tier Spring Boot architecture.
 *
 * ✅ Responsibilities:
 * - Contains business logic related to User operations.
 * - Serves as a bridge between the Controller layer (UserController)
 *   and the Data Access layer (UserRepository).
 * - Uses Spring Data JPA methods to interact with the database.
 *
 * 💡 Follows the principle of separation of concerns:
 *   Controller → handles HTTP requests
 *   Service → contains business logic
 *   Repository → handles database communication
 */
@Service // Marks this class as a Service component (managed by Spring's IoC container)
public class UserService {

    /**
     * Injects an instance of UserRepository.
     *
     * The @Autowired annotation tells Spring to automatically provide
     * an implementation of UserRepository at runtime.
     *
     * Best Practice:
     * In modern Spring Boot, **constructor injection** (using @RequiredArgsConstructor)
     * is preferred over field injection for immutability and testing,
     * but @Autowired is still perfectly valid and readable for smaller projects.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Fetches all users from the database.
     *
     * @return List of all User entities present in the database.
     *
     * JPA's findAll() method is a built-in method provided by JpaRepository
     * which automatically executes: SELECT * FROM user_table;
     */
    public List<User> fetchAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Adds (creates) a new user record into the database.
     *
     * @param user The User entity containing firstName and lastName.
     *
     * The save() method in JpaRepository automatically performs an INSERT
     * if the ID is null, or an UPDATE if the ID already exists.
     */
    public void addUser(User user) {
        userRepository.save(user);
    }

    /**
     * Fetches a single user by their ID.
     *
     * @param id The unique identifier of the user.
     * @return An Optional<User> containing the user if found,
     *         otherwise an empty Optional.
     *
     * Optional helps avoid NullPointerExceptions and allows clean functional handling.
     */
    public Optional<User> fetchUser(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Updates an existing user's information in the database.
     *
     * @param id The unique identifier of the user to be updated.
     * @param updatedUser The updated User object containing new firstName and lastName.
     * @return true if the user was found and updated, false if not found.
     *
     * Process:
     *  1. findById() fetches the existing record (returns Optional<User>).
     *  2. map() executes only if the user exists.
     *  3. Updates firstName and lastName fields.
     *  4. save() persists the updated record to the database.
     *  5. Returns true if update successful; false otherwise.
     */
    public boolean updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    // Update only allowed fields (for better data integrity)
                    existingUser.setFirstName(updatedUser.getFirstName());
                    existingUser.setLastName(updatedUser.getLastName());
                    // Persist the updated entity
                    userRepository.save(existingUser);
                    return true;
                })
                .orElse(false); // Return false if user not found
    }
}
