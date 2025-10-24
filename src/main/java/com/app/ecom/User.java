package com.app.ecom;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The User entity class represents the "user_table" in the relational database.
 *
 * ✅ Key Responsibilities:
 * - Acts as a JPA Entity mapped to a database table.
 * - Defines columns corresponding to each field (id, firstName, lastName).
 * - Used by the JPA repository to perform CRUD operations automatically.
 *
 * JPA (Jakarta Persistence API) takes care of converting this POJO into
 * a persistent entity that can be stored and retrieved from the database.
 */
@Data // Lombok annotation → generates getters, setters, toString(), equals(), and hashCode() automatically
@NoArgsConstructor // Generates a no-argument constructor (required by JPA for entity instantiation)
@AllArgsConstructor // Generates a constructor with all fields as parameters
@Entity(name = "user_table") // Marks this class as a JPA entity and maps it to a custom table name in the DB
//@Entity // (Alternate option) Without parameters, would map to a default table named "user"
public class User {

    /**
     * The primary key for the user table.
     *
     * @Id → Marks this field as the primary key.
     * @GeneratedValue → Configures how the ID is generated.
     * GenerationType.IDENTITY → Auto-increments ID (handled by the database).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The first name of the user.
     *
     * Automatically mapped to a column named 'first_name' (if using @Column annotation).
     * Here, since @Column is omitted, JPA uses the field name as the column name by default.
     */
    private String firstName;

    /**
     * The last name of the user.
     *
     * Represents another column in the "user_table" for storing user's last name.
     */
    private String lastName;

}
