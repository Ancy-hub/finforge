import apiClient from './apiClient';

/**
 * UI Service: Income Service
 * Directly interacts with backend API endpoints at /api/incomes
 */
export const incomeService = {
  /**
   * Fetches paginated incomes from backend
   */
  async getIncomes({ page = 1, pageSize = 10 } = {}) {
    return await apiClient.get('/incomes', {
      page,
      pageSize,
    });
  },

  /**
   * Fetches a single income record by ID
   */
  async getIncomeById(id) {
    return await apiClient.get(`/incomes/${id}`);
  },

  /**
   * Creates a new income record on backend endpoint
   */
  async addIncome(incomeData) {
    return await apiClient.post('/incomes', incomeData);
  },

  /**
   * Updates an existing income on backend endpoint
   */
  async updateIncome(id, incomeData) {
    return await apiClient.put(`/incomes/${id}`, incomeData);
  },

  /**
   * Deletes an income record from backend
   */
  async deleteIncome(id) {
    return await apiClient.delete(`/incomes/${id}`);
  },
};

export default incomeService;
