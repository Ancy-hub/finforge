import { useState, useCallback, useEffect } from 'react';
import expenseService from '../services/expenseService';
import categoryService from '../services/categoryService';

/**
 * UI Controller: Expense Controller
 * 
 * Responsibilities:
 * - Decides page / modal display (e.g., show add modal, edit view, close modal)
 * - Decides filtering and pagination display state
 * - Calls UI Service (expenseService) which points to backend REST API endpoints
 */
export function useExpenseController({ onNotify } = {}) {
  // Page display state
  const [expenses, setExpenses] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalCount, setTotalCount] = useState(0);

  // Filter display state
  const [filters, setFilters] = useState({
    categoryId: '',
    fromDate: '',
    toDate: '',
  });

  // Modal / Page display decision state
  const [modalState, setModalState] = useState({
    isOpen: false,
    mode: 'add', // 'add' | 'edit'
    selectedExpense: null,
  });

  /**
   * Controller Action: Calls UI Service to fetch categories
   */
  const loadCategories = useCallback(async () => {
    try {
      const data = await categoryService.getCategories();
      setCategories(Array.isArray(data) ? data : []);
    } catch (err) {
      console.warn('[ExpenseController] Failed to load categories', err);
    }
  }, []);

  /**
   * Controller Action: Calls UI Service to load paginated/filtered expenses from backend API
   */
  const loadExpenses = useCallback(async () => {
    setLoading(true);
    try {
      console.log('[ExpenseController] Calling expenseService.getExpenses() backend endpoint');
      const response = await expenseService.getExpenses({
        page,
        pageSize: 10,
        categoryId: filters.categoryId,
        fromDate: filters.fromDate,
        toDate: filters.toDate,
      });

      if (response && response.items) {
        setExpenses(response.items);
        setTotalPages(response.totalPages || 1);
        setTotalCount(response.totalCount || response.items.length);
      } else if (Array.isArray(response)) {
        setExpenses(response);
        setTotalCount(response.length);
      }
    } catch (err) {
      if (onNotify) onNotify(err.message || 'Failed to fetch expenses', 'error');
    } finally {
      setLoading(false);
    }
  }, [page, filters, onNotify]);

  useEffect(() => {
    loadCategories();
  }, [loadCategories]);

  useEffect(() => {
    loadExpenses();
  }, [loadExpenses]);

  // =========================================================================
  // Controller Decisions: Page & View Display
  // =========================================================================

  const decideOpenAddModal = useCallback(() => {
    console.log('[ExpenseController] Deciding to display Add Expense view/modal');
    setModalState({
      isOpen: true,
      mode: 'add',
      selectedExpense: null,
    });
  }, []);

  const decideOpenEditModal = useCallback((expense) => {
    console.log('[ExpenseController] Deciding to display Edit Expense view/modal for ID:', expense.expenseId);
    setModalState({
      isOpen: true,
      mode: 'edit',
      selectedExpense: expense,
    });
  }, []);

  const decideCloseModal = useCallback(() => {
    console.log('[ExpenseController] Deciding to dismiss modal view');
    setModalState({
      isOpen: false,
      mode: 'add',
      selectedExpense: null,
    });
  }, []);

  const decideChangePage = useCallback((newPage) => {
    console.log(`[ExpenseController] Deciding page display: page ${newPage}`);
    setPage(newPage);
  }, []);

  const decideApplyFilters = useCallback((newFilters) => {
    console.log('[ExpenseController] Deciding filter display update');
    setFilters(newFilters);
    setPage(1); // Reset to first page
  }, []);

  // =========================================================================
  // Controller Decisions: Data Mutations (calls UI Service)
  // =========================================================================

  const handleSaveExpense = useCallback(async (formData) => {
    console.log('[ExpenseController] User submitted expense data, calling UI Service');
    try {
      if (modalState.mode === 'edit' && modalState.selectedExpense) {
        await expenseService.updateExpense(modalState.selectedExpense.expenseId, formData);
        if (onNotify) onNotify('Expense updated successfully!', 'success');
      } else {
        await expenseService.addExpense(formData);
        if (onNotify) onNotify('Expense added successfully!', 'success');
      }
      // After service responds, UI controller decides to close modal & refresh view
      decideCloseModal();
      loadExpenses();
      return { success: true };
    } catch (err) {
      if (onNotify) onNotify(err.message || 'Error saving expense', 'error');
      return { success: false, error: err.message };
    }
  }, [modalState, onNotify, decideCloseModal, loadExpenses]);

  const handleDeleteExpense = useCallback(async (id) => {
    if (!window.confirm('Are you sure you want to delete this expense entry?')) {
      return;
    }
    console.log('[ExpenseController] Calling UI Service to delete expense ID:', id);
    try {
      await expenseService.deleteExpense(id);
      if (onNotify) onNotify('Expense deleted successfully', 'success');
      loadExpenses();
    } catch (err) {
      if (onNotify) onNotify(err.message || 'Failed to delete expense', 'error');
    }
  }, [onNotify, loadExpenses]);

  return {
    expenses,
    categories,
    loading,
    page,
    totalPages,
    totalCount,
    filters,
    modalState,
    // Controller decision methods
    openAddModal: decideOpenAddModal,
    openEditModal: decideOpenEditModal,
    closeModal: decideCloseModal,
    changePage: decideChangePage,
    applyFilters: decideApplyFilters,
    saveExpense: handleSaveExpense,
    deleteExpense: handleDeleteExpense,
    refreshExpenses: loadExpenses,
  };
}

export default useExpenseController;
