package com.finforge.model;

import java.time.LocalDateTime;

/**
 * Domain model representing an expense category that belongs to a user.
 */
public class Category {

    private int           categoryId;
    private String        name;
    private String        description;
    private int           userId;
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
