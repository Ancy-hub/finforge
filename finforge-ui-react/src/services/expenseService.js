import apiClient from './apiClient';

/**
 * UI Service: Expense Service
 * Directly interacts with backend API endpoints at /api/expenses
 */
export const expenseService = {
  /**
   * Fetches paginated and optionally filtered expenses from backend
   */
  async getExpenses({ page = 1, pageSize = 10, categoryId, fromDate, toDate } = {}) {
    return await apiClient.get('/expenses', {
      page,
      pageSize,
      categoryId,
      fromDate,
      toDate,
    });
  },

  /**
   * Fetches a single expense record by ID
   */
  async getExpenseById(id) {
    return await apiClient.get(`/expenses/${id}`);
  },

  /**
   * Sends a new expense entry to backend endpoint
   */
  async addExpense(expenseData) {
    return await apiClient.post('/expenses', expenseData);
  },

  /**
   * Updates an existing expense on backend endpoint
   */
  async updateExpense(id, expenseData) {
    return await apiClient.put(`/expenses/${id}`, expenseData);
  },

  /**
   * Deletes an expense from backend
   */
  async deleteExpense(id) {
    return await apiClient.delete(`/expenses/${id}`);
  },
};

export default expenseService;
