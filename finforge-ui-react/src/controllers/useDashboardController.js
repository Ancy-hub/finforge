import { useState, useCallback, useEffect } from 'react';
import reportService from '../services/reportService';
import expenseService from '../services/expenseService';
import incomeService from '../services/incomeService';

/**
 * UI Controller: Dashboard Controller
 * 
 * Responsibilities:
 * - Coordinates summary data and recent transaction feeds
 * - Controls quick actions and page routing decisions
 * - Calls UI Services (reportService, expenseService, incomeService) pointing to backend API
 */
export function useDashboardController({ onNavigate, onNotify } = {}) {
  const [summary, setSummary] = useState({
    totalIncome: 0,
    totalExpense: 0,
    netSavings: 0,
  });
  const [recentExpenses, setRecentExpenses] = useState([]);
  const [recentIncomes, setRecentIncomes] = useState([]);
  const [loading, setLoading] = useState(false);

  const loadDashboard = useCallback(async () => {
    setLoading(true);
    try {
      console.log('[DashboardController] Calling reportService & transaction services');
      const [reportRes, expRes, incRes] = await Promise.allSettled([
        reportService.getDashboardSummary(),
        expenseService.getExpenses({ page: 1, pageSize: 5 }),
        incomeService.getIncomes({ page: 1, pageSize: 5 }),
      ]);

      if (reportRes.status === 'fulfilled' && reportRes.value) {
        setSummary({
          totalIncome: Number(reportRes.value.totalIncome || 0),
          totalExpense: Number(reportRes.value.totalExpense || 0),
          netSavings: Number(reportRes.value.netSavings || 0),
        });
      }

      if (expRes.status === 'fulfilled' && expRes.value) {
        setRecentExpenses(expRes.value.items || (Array.isArray(expRes.value) ? expRes.value : []));
      }

      if (incRes.status === 'fulfilled' && incRes.value) {
        setRecentIncomes(incRes.value.items || (Array.isArray(incRes.value) ? incRes.value : []));
      }
    } catch (err) {
      if (onNotify) onNotify('Error loading dashboard overview', 'error');
    } finally {
      setLoading(false);
    }
  }, [onNotify]);

  useEffect(() => {
    loadDashboard();
  }, [loadDashboard]);

  // Controller Decisions: Navigate to other pages
  const decideViewExpenses = useCallback(() => {
    console.log('[DashboardController] User clicked View All Expenses -> navigating to expenses page');
    if (onNavigate) onNavigate('expenses');
  }, [onNavigate]);

  const decideViewIncomes = useCallback(() => {
    console.log('[DashboardController] User clicked View All Incomes -> navigating to incomes page');
    if (onNavigate) onNavigate('incomes');
  }, [onNavigate]);

  const decideViewReports = useCallback(() => {
    console.log('[DashboardController] User clicked View Analytics -> navigating to reports page');
    if (onNavigate) onNavigate('reports');
  }, [onNavigate]);

  return {
    summary,
    recentExpenses,
    recentIncomes,
    loading,
    refreshDashboard: loadDashboard,
    viewExpenses: decideViewExpenses,
    viewIncomes: decideViewIncomes,
    viewReports: decideViewReports,
  };
}

export default useDashboardController;
