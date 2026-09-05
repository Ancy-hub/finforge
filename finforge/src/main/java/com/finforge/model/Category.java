package com.finforge.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Domain model representing an expense category that belongs to a user.
 */
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private int categoryId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Category() {}

    public Category(int categoryId, String name, String description,
                    int userId, LocalDateTime createdAt) {
        this.categoryId  = categoryId;
        this.name        = name;
        this.description = description;
        this.userId      = userId;
        this.createdAt   = createdAt;
    }

    // ---- Getters and Setters ----

    public int getCategoryId()                      { return categoryId; }
    public void setCategoryId(int categoryId)       { this.categoryId = categoryId; }

    public String getName()                         { return name; }
    public void setName(String name)                { this.name = name; }

    public String getDescription()                  { return description; }
    public void setDescription(String description)  { this.description = description; }

    public int getUserId()                          { return userId; }
    public void setUserId(int userId)               { this.userId = userId; }

    public LocalDateTime getCreatedAt()             { return createdAt; }
    public void setCreatedAt(LocalDateTime t)       { this.createdAt = t; }

    @Override
    public String toString() {
        return "Category{categoryId=" + categoryId + ", name='" + name + "', userId=" + userId + "}";
    }
}
