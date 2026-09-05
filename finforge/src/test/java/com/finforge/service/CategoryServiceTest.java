package com.finforge.service;

import com.finforge.dao.CategoryDAO;
import com.finforge.dto.CategoryDTO;
import com.finforge.exception.DAOException;
import com.finforge.exception.ValidationException;
import com.finforge.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CategoryServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Tests")
class CategoryServiceTest {

    @Mock private CategoryDAO categoryDAO;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(categoryDAO);
    }

    // -----------------------------------------------------------------------
    // addCategory()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("addCategory() - should save category when name is unique for user")
    void addCategory_shouldSave_whenNameUnique() throws Exception {
        CategoryDTO dto   = new CategoryDTO();
        dto.setName("Health");
        dto.setDescription("Health expenses");

        Category saved = buildCategory(3, "Health");
        when(categoryDAO.existsByNameAndUserId("Health", 1)).thenReturn(false);
        when(categoryDAO.save(any(Category.class))).thenReturn(saved);

        Category result = categoryService.addCategory(1, dto);

        assertNotNull(result);
        assertEquals("Health", result.getName());
    }

    @Test
    @DisplayName("addCategory() - should throw ValidationException when name already exists for user")
    void addCategory_shouldThrow_whenNameDuplicate() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Food");

        when(categoryDAO.existsByNameAndUserId("Food", 1)).thenReturn(true);

        assertThrows(ValidationException.class, () -> categoryService.addCategory(1, dto));
        verify(categoryDAO, never()).save(any());
    }

    @Test
    @DisplayName("addCategory() - should throw ValidationException when name is blank")
    void addCategory_shouldThrow_whenNameBlank() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("");

        assertThrows(ValidationException.class, () -> categoryService.addCategory(1, dto));
        verifyNoInteractions(categoryDAO);
    }

    // -----------------------------------------------------------------------
    // updateCategory()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updateCategory() - should update category when it belongs to user")
    void updateCategory_shouldUpdate_whenOwned() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId("3");
        dto.setName("Wellness");
        dto.setDescription("Updated description");

        when(categoryDAO.existsByIdAndUserId(3, 1)).thenReturn(true);

        categoryService.updateCategory(1, dto);

        verify(categoryDAO).update(any(Category.class));
    }

    @Test
    @DisplayName("updateCategory() - should throw ValidationException when category not owned")
    void updateCategory_shouldThrow_whenNotOwned() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId("3");
        dto.setName("Wellness");

        when(categoryDAO.existsByIdAndUserId(3, 1)).thenReturn(false);

        assertThrows(ValidationException.class, () -> categoryService.updateCategory(1, dto));
        verify(categoryDAO, never()).update(any());
    }

    @Test
    @DisplayName("updateCategory() - should throw ValidationException when name is blank on update")
    void updateCategory_shouldThrow_whenNameBlankOnUpdate() {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId("3");
        dto.setName("   ");

        assertThrows(ValidationException.class, () -> categoryService.updateCategory(1, dto));
    }

    // -----------------------------------------------------------------------
    // deleteCategory()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteCategory() - should delete when category belongs to user")
    void deleteCategory_shouldDelete_whenOwned() throws Exception {
        when(categoryDAO.existsByIdAndUserId(3, 1)).thenReturn(true);

        categoryService.deleteCategory(3, 1);

        verify(categoryDAO).delete(3);
    }

    @Test
    @DisplayName("deleteCategory() - should throw ValidationException when category not owned")
    void deleteCategory_shouldThrow_whenNotOwned() throws Exception {
        when(categoryDAO.existsByIdAndUserId(3, 1)).thenReturn(false);

        assertThrows(ValidationException.class, () -> categoryService.deleteCategory(3, 1));
        verify(categoryDAO, never()).delete(anyInt());
    }

    // -----------------------------------------------------------------------
    // getAllCategories()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getAllCategories() - should return all categories for user")
    void getAllCategories_shouldReturnList() throws Exception {
        List<Category> cats = Arrays.asList(
                buildCategory(1, "Food"),
                buildCategory(2, "Travel"),
                buildCategory(3, "Rent")
        );
        when(categoryDAO.findAllByUserId(1)).thenReturn(cats);

        List<Category> result = categoryService.getAllCategories(1);

        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("getAllCategories() - should return empty list when no categories")
    void getAllCategories_shouldReturnEmptyList_whenNone() throws Exception {
        when(categoryDAO.findAllByUserId(1)).thenReturn(Collections.emptyList());

        assertTrue(categoryService.getAllCategories(1).isEmpty());
    }

    // -----------------------------------------------------------------------
    // getCategoryById()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getCategoryById() - should return category when it belongs to user")
    void getCategoryById_shouldReturn_whenOwned() throws Exception {
        Category cat = buildCategory(2, "Travel");
        cat.setUserId(1);

        when(categoryDAO.findById(2)).thenReturn(Optional.of(cat));

        Category result = categoryService.getCategoryById(2, 1);

        assertNotNull(result);
        assertEquals("Travel", result.getName());
    }

    @Test
    @DisplayName("getCategoryById() - should throw ValidationException when category not owned")
    void getCategoryById_shouldThrow_whenNotOwned() throws Exception {
        Category cat = buildCategory(2, "Travel");
        cat.setUserId(99); // Different user

        when(categoryDAO.findById(2)).thenReturn(Optional.of(cat));

        assertThrows(ValidationException.class, () -> categoryService.getCategoryById(2, 1));
    }

    @Test
    @DisplayName("getCategoryById() - should throw ValidationException when category not found")
    void getCategoryById_shouldThrow_whenNotFound() throws Exception {
        when(categoryDAO.findById(999)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> categoryService.getCategoryById(999, 1));
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private Category buildCategory(int id, String name) {
        Category c = new Category();
        c.setCategoryId(id);
        c.setName(name);
        c.setDescription(name + " expenses");
        c.setUserId(1);
        return c;
    }
}
