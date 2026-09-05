import apiClient from './apiClient';

/**
 * UI Service: Category Service
 * Directly interacts with backend API endpoints at /api/categories
 */
export const categoryService = {
  /**
   * Fetches all categories for the authenticated user
   */
  async getCategories() {
    return await apiClient.get('/categories');
  },

  /**
   * Fetches category details by ID
   */
  async getCategoryById(id) {
    return await apiClient.get(`/categories/${id}`);
  },

  /**
   * Creates a new category on backend endpoint
   */
  async addCategory(categoryData) {
    return await apiClient.post('/categories', categoryData);
  },

  /**
   * Updates an existing category
   */
  async updateCategory(id, categoryData) {
    return await apiClient.put(`/categories/${id}`, categoryData);
  },

  /**
   * Deletes a category
   */
  async deleteCategory(id) {
    return await apiClient.delete(`/categories/${id}`);
  },
};

export default categoryService;
