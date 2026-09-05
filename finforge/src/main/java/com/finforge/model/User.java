package com.finforge.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Domain model representing an application user.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 64)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "phone", length = 15)
    private String phone;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public User() {}

    public User(int userId, String username, String email, String passwordHash,
                String firstName, String lastName, String phone,
                LocalDateTime createdAt, LocalDateTime updatedAt, boolean active) {
        this.userId       = userId;
        this.username     = username;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.firstName    = firstName;
        this.lastName     = lastName;
        this.phone        = phone;
        this.createdAt    = createdAt;
        this.updatedAt    = updatedAt;
        this.active       = active;
    }

    // ---- Getters and Setters ----

    public int getUserId()                       { return userId; }
    public void setUserId(int userId)            { this.userId = userId; }

    public String getUsername()                  { return username; }
    public void setUsername(String username)     { this.username = username; }

    public String getEmail()                     { return email; }
    public void setEmail(String email)           { this.email = email; }

    public String getPasswordHash()              { return passwordHash; }
    public void setPasswordHash(String h)        { this.passwordHash = h; }

    public String getFirstName()                 { return firstName; }
    public void setFirstName(String firstName)   { this.firstName = firstName; }

    public String getLastName()                  { return lastName; }
    public void setLastName(String lastName)     { this.lastName = lastName; }

    public String getPhone()                     { return phone; }
    public void setPhone(String phone)           { this.phone = phone; }

    public LocalDateTime getCreatedAt()          { return createdAt; }
    public void setCreatedAt(LocalDateTime t)    { this.createdAt = t; }

    public LocalDateTime getUpdatedAt()          { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t)    { this.updatedAt = t; }

    public boolean isActive()                    { return active; }
    public void setActive(boolean active)        { this.active = active; }

    /** Convenience method: full name = firstName + lastName. */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "User{userId=" + userId + ", username='" + username + "', email='" + email + "'}";
    }
}
