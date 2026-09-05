package com.finforge.dto;

/**
 * Data Transfer Object for category add / edit forms.
 */
public class CategoryDTO {

    private String categoryId;  // present only on edit
    private String name;
    private String description;

    public CategoryDTO() {}

    // ---- Getters and Setters ----

    public String getCategoryId()                   { return categoryId; }
    public void setCategoryId(String categoryId)    { this.categoryId = categoryId; }

    public String getName()                         { return name; }
    public void setName(String name)                { this.name = name; }

    public String getDescription()                  { return description; }
    public void setDescription(String d)            { this.description = d; }
}
