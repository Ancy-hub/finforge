import { useState, useCallback, useEffect } from 'react';
import reportService from '../services/reportService';

/**
 * UI Controller: Report Controller
 * 
 * Responsibilities:
 * - Decides view tab display for reports (Monthly vs Category view)
 * - Calls UI Service (reportService) which points to backend REST API endpoints
 */
export function useReportController({ onNotify } = {}) {
  const [dashboardData, setDashboardData] = useState(null);
  const [monthlyData, setMonthlyData] = useState([]);
  const [categoryData, setCategoryData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState('overview'); // 'overview' | 'monthly' | 'categories'

  const loadAllReports = useCallback(async () => {
    setLoading(true);
    try {
      console.log('[ReportController] Calling reportService backend endpoints');
      const [dash, monthly, categories] = await Promise.allSettled([
        reportService.getDashboardSummary(),
        reportService.getMonthlyReport(),
        reportService.getCategoryReport(),
      ]);

      if (dash.status === 'fulfilled') setDashboardData(dash.value);
      if (monthly.status === 'fulfilled') setMonthlyData(Array.isArray(monthly.value) ? monthly.value : []);
      if (categories.status === 'fulfilled') setCategoryData(Array.isArray(categories.value) ? categories.value : []);
    } catch (err) {
      if (onNotify) onNotify('Failed to load report statistics', 'error');
    } finally {
      setLoading(false);
    }
  }, [onNotify]);

  useEffect(() => {
    loadAllReports();
  }, [loadAllReports]);

  // Controller Decision: Page / Tab Display
  const decideTab = useCallback((tabName) => {
    console.log(`[ReportController] Deciding report tab display: '${tabName}'`);
    setActiveTab(tabName);
  }, []);

  return {
    dashboardData,
    monthlyData,
    categoryData,
    loading,
    activeTab,
    selectTab: decideTab,
    refreshReports: loadAllReports,
  };
}

export default useReportController;
