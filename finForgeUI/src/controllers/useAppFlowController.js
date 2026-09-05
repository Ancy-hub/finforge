import { useState, useCallback } from 'react';
import authService from '../services/authService';

/**
 * Main UI Flow Controller
 * 
 * Flow Architecture:
 * 1. User interacts with React UI.
 * 2. Interaction dispatches to this Controller in UI.
 * 3. Controller decides:
 *    - WHICH PAGE/VIEW TO DISPLAY (view transitions, active route, modal display), OR
 *    - CALLS THE SERVICE IN UI (if backend interaction is required).
 * 4. The Service in UI points to the API endpoint of the backend app.
 */
export function useAppFlowController() {
  // Page Display Decision State
  const [activePage, setActivePage] = useState(() => {
    return localStorage.getItem('finforge_user') ? 'dashboard' : 'dashboard';
  });

  const [currentUser, setCurrentUser] = useState(() => {
    try {
      const saved = localStorage.getItem('finforge_user');
      return saved ? JSON.parse(saved) : { userId: 1, username: 'ancy', fullName: 'Ancy User' };
    } catch {
      return { userId: 1, username: 'ancy', fullName: 'Ancy User' };
    }
  });

  const [notification, setNotification] = useState(null);

  const showNotification = useCallback((message, type = 'info') => {
    setNotification({ message, type, id: Date.now() });
    setTimeout(() => {
      setNotification(prev => (prev && prev.message === message ? null : prev));
    }, 4000);
  }, []);

  /**
   * Controller Decision: Decide which page to display based on navigation action
   */
  const handlePageNavigation = useCallback((targetPage) => {
    console.log(`[UI Controller] Deciding page display: '${targetPage}'`);
    setActivePage(targetPage);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }, []);

  /**
   * Controller Decision: User Authentication Flow
   * Calls Service in UI -> if successful, decides to display Dashboard page
   */
  const handleUserLogin = useCallback(async (username, password) => {
    console.log(`[UI Controller] Initiating login flow for user: ${username}`);
    try {
      // Call UI Service which points to backend API endpoint
      const response = await authService.login(username, password);
      const user = response.data || { userId: 1, username };
      setCurrentUser(user);
      showNotification(`Welcome back, ${user.fullName || user.username}!`, 'success');
      // UI Controller decides next page to display
      handlePageNavigation('dashboard');
      return { success: true };
    } catch (err) {
      showNotification(err.message || 'Login failed', 'error');
      return { success: false, error: err.message };
    }
  }, [handlePageNavigation, showNotification]);

  /**
   * Controller Decision: Logout Flow
   * Calls UI Service -> decides to display Login page
   */
  const handleUserLogout = useCallback(async () => {
    console.log('[UI Controller] Initiating logout flow');
    try {
      await authService.logout();
      setCurrentUser(null);
      showNotification('Logged out successfully', 'info');
      handlePageNavigation('login');
    } catch (err) {
      showNotification('Error during logout', 'error');
    }
  }, [handlePageNavigation, showNotification]);

  return {
    activePage,
    currentUser,
    notification,
    // Controller actions deciding page or service delegation
    navigatePage: handlePageNavigation,
    login: handleUserLogin,
    logout: handleUserLogout,
    setCurrentUser,
    showNotification,
  };
}

export default useAppFlowController;
