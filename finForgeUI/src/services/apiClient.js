/**
 * UI Service Layer: Base HTTP Client
 * Responsible for communicating directly with the backend app API endpoints.
 */

// Use relative '/api' so Vite proxy forwards to backend on port 8082 without CORS issues,
// or fallback to direct URL if VITE_API_URL is configured.
const API_BASE_URL = import.meta.env.VITE_API_URL || '/api';

class ApiClient {
  constructor(baseUrl) {
    this.baseUrl = baseUrl;
  }

  getHeaders() {
    const headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    };

    const storedUser = localStorage.getItem('finforge_user');
    if (storedUser) {
      try {
        const user = JSON.parse(storedUser);
        if (user && user.userId) {
          headers['X-User-Id'] = String(user.userId);
        }
      } catch (e) {
        console.warn('Failed to parse user from localStorage', e);
      }
    }
    return headers;
  }

  async request(endpoint, options = {}) {
    const url = endpoint.startsWith('http') ? endpoint : `${this.baseUrl}${endpoint.startsWith('/') ? '' : '/'}${endpoint}`;
    const config = {
      ...options,
      headers: {
        ...this.getHeaders(),
        ...(options.headers || {}),
      },
      credentials: 'include',
    };

    try {
      const response = await fetch(url, config);
      const text = await response.text();
      let data = null;
      if (text) {
        try {
          data = JSON.parse(text);
        } catch {
          data = text;
        }
      }

      if (!response.ok) {
        const errorMessage = (data && data.error) || (data && data.message) || `HTTP error ${response.status}`;
        const error = new Error(errorMessage);
        error.status = response.status;
        error.data = data;
        throw error;
      }

      return data;
    } catch (err) {
      console.error(`[ApiClient Error] ${options.method || 'GET'} ${url}:`, err.message);
      throw err;
    }
  }

  get(endpoint, params = {}) {
    const query = new URLSearchParams();
    Object.entries(params).forEach(([key, val]) => {
      if (val !== undefined && val !== null && val !== '') {
        query.append(key, val);
      }
    });
    const queryString = query.toString();
    const fullEndpoint = queryString ? `${endpoint}?${queryString}` : endpoint;
    return this.request(fullEndpoint, { method: 'GET' });
  }

  post(endpoint, body) {
    return this.request(endpoint, {
      method: 'POST',
      body: JSON.stringify(body),
    });
  }

  put(endpoint, body) {
    return this.request(endpoint, {
      method: 'PUT',
      body: JSON.stringify(body),
    });
  }

  delete(endpoint) {
    return this.request(endpoint, { method: 'DELETE' });
  }
}

export const apiClient = new ApiClient(API_BASE_URL);
export default apiClient;
