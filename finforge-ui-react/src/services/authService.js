import apiClient from './apiClient';

/**
 * UI Service: Auth & User Service
 * Directly interacts with backend API endpoints at /api/auth/... and /api/user/...
 */
export const authService = {
  /**
   * Submits user credentials to backend login endpoint
   */
  async login(username, password) {
    const response = await apiClient.post('/auth/login', { username, password });
    if (response && response.data) {
      localStorage.setItem('finforge_user', JSON.stringify(response.data));
    }
    return response;
  },

  /**
   * Registers a new account via backend endpoint
   */
  async register(userData) {
    const response = await apiClient.post('/auth/register', userData);
    if (response && response.data) {
      localStorage.setItem('finforge_user', JSON.stringify(response.data));
    }
    return response;
  },

  /**
   * Logs out from backend session
   */
  async logout() {
    try {
      await apiClient.post('/auth/logout', {});
    } finally {
      localStorage.removeItem('finforge_user');
    }
  },

  /**
   * Returns the current authenticated user
   */
  async getCurrentUser() {
    return await apiClient.get('/auth/me');
  },

  /**
   * Fetches user profile from backend
   */
  async getProfile() {
    return await apiClient.get('/user/profile');
  },

  /**
   * Updates profile data on backend
   */
  async updateProfile(profileData) {
    return await apiClient.put('/user/profile', profileData);
  },

  /**
   * Changes password on backend
   */
  async changePassword(passwordData) {
    return await apiClient.post('/user/change-password', passwordData);
  },
};

export default authService;
