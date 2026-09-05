import { useState, useCallback, useEffect } from 'react';
import categoryService from '../services/categoryService';

/**
 * UI Controller: Category Controller
 * 
 * Responsibilities:
 * - Decides page / modal display for Categories
 * - Calls UI Service (categoryService) which points to backend REST API endpoints
 */
export function useCategoryController({ onNotify } = {}) {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);

  const [modalState, setModalState] = useState({
    isOpen: false,
    mode: 'add',
    selectedCategory: null,
  });

  const loadCategories = useCallback(async () => {
    setLoading(true);
    try {
      console.log('[CategoryController] Calling categoryService.getCategories() backend endpoint');
      const data = await categoryService.getCategories();
      setCategories(Array.isArray(data) ? data : []);
    } catch (err) {
      if (onNotify) onNotify(err.message || 'Failed to fetch categories', 'error');
    } finally {
      setLoading(false);
    }
  }, [onNotify]);

  useEffect(() => {
    loadCategories();
  }, [loadCategories]);

  // View Display Decisions
  const decideOpenAddModal = useCallback(() => {
    console.log('[CategoryController] Deciding to display Add Category modal');
    setModalState({ isOpen: true, mode: 'add', selectedCategory: null });
  }, []);

  const decideOpenEditModal = useCallback((category) => {
    console.log('[CategoryController] Deciding to display Edit Category modal for ID:', category.categoryId);
    setModalState({ isOpen: true, mode: 'edit', selectedCategory: category });
  }, []);

  const decideCloseModal = useCallback(() => {
    console.log('[CategoryController] Deciding to dismiss category modal');
    setModalState({ isOpen: false, mode: 'add', selectedCategory: null });
  }, []);

  // Data Actions (calls UI Service)
  const handleSaveCategory = useCallback(async (formData) => {
    console.log('[CategoryController] Calling UI Service to save category');
    try {
      if (modalState.mode === 'edit' && modalState.selectedCategory) {
        await categoryService.updateCategory(modalState.selectedCategory.categoryId, formData);
        if (onNotify) onNotify('Category updated successfully!', 'success');
      } else {
        await categoryService.addCategory(formData);
        if (onNotify) onNotify('Category added successfully!', 'success');
      }
      decideCloseModal();
      loadCategories();
      return { success: true };
    } catch (err) {
      if (onNotify) onNotify(err.message || 'Error saving category', 'error');
      return { success: false, error: err.message };
    }
  }, [modalState, onNotify, decideCloseModal, loadCategories]);

  const handleDeleteCategory = useCallback(async (id) => {
    if (!window.confirm('Are you sure you want to delete this category?')) {
      return;
    }
    console.log('[CategoryController] Calling UI Service to delete category ID:', id);
    try {
      await categoryService.deleteCategory(id);
      if (onNotify) onNotify('Category deleted successfully', 'success');
      loadCategories();
    } catch (err) {
      if (onNotify) onNotify(err.message || 'Failed to delete category', 'error');
    }
  }, [onNotify, loadCategories]);

  return {
    categories,
    loading,
    modalState,
    openAddModal: decideOpenAddModal,
    openEditModal: decideOpenEditModal,
    closeModal: decideCloseModal,
    saveCategory: handleSaveCategory,
    deleteCategory: handleDeleteCategory,
    refreshCategories: loadCategories,
  };
}

export default useCategoryController;
