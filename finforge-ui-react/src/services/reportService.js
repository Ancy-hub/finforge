import apiClient from './apiClient';

/**
 * UI Service: Report Service
 * Directly interacts with backend API endpoints at /api/reports/...
 */
export const reportService = {
  /**
   * Fetches the dashboard overview summary (income, expense, net savings, aggregates)
   */
  async getDashboardSummary() {
    return await apiClient.get('/reports/dashboard');
  },

  /**
   * Fetches monthly expense breakdown
   */
  async getMonthlyReport(year) {
    return await apiClient.get('/reports/monthly', { year });
  },

  /**
   * Fetches category-wise expense breakdown
   */
  async getCategoryReport() {
    return await apiClient.get('/reports/categories');
  },
};

export default reportService;
