import { useState, useCallback, useEffect } from 'react';
import incomeService from '../services/incomeService';

/**
 * UI Controller: Income Controller
 * 
 * Responsibilities:
 * - Decides view display for Incomes (list, add modal, edit modal)
 * - Manages pagination and user interactions
 * - Calls UI Service (incomeService) which points to backend REST API endpoints
 */
export function useIncomeController({ onNotify } = {}) {
  const [incomes, setIncomes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalCount, setTotalCount] = useState(0);

  const [modalState, setModalState] = useState({
    isOpen: false,
    mode: 'add',
    selectedIncome: null,
  });

  const loadIncomes = useCallback(async () => {
    setLoading(true);
    try {
      console.log('[IncomeController] Calling incomeService.getIncomes() backend endpoint');
      const response = await incomeService.getIncomes({ page, pageSize: 10 });
      if (response && response.items) {
        setIncomes(response.items);
        setTotalPages(response.totalPages || 1);
        setTotalCount(response.totalCount || response.items.length);
      } else if (Array.isArray(response)) {
        setIncomes(response);
        setTotalCount(response.length);
      }
    } catch (err) {
      if (onNotify) onNotify(err.message || 'Failed to fetch income entries', 'error');
    } finally {
      setLoading(false);
    }
  }, [page, onNotify]);

  useEffect(() => {
    loadIncomes();
  }, [loadIncomes]);

  // View Display Decisions
  const decideOpenAddModal = useCallback(() => {
    console.log('[IncomeController] Deciding to display Add Income view/modal');
    setModalState({ isOpen: true, mode: 'add', selectedIncome: null });
  }, []);

  const decideOpenEditModal = useCallback((income) => {
    console.log('[IncomeController] Deciding to display Edit Income view/modal for ID:', income.incomeId);
    setModalState({ isOpen: true, mode: 'edit', selectedIncome: income });
  }, []);

  const decideCloseModal = useCallback(() => {
    console.log('[IncomeController] Deciding to dismiss income modal');
    setModalState({ isOpen: false, mode: 'add', selectedIncome: null });
  }, []);

  const decideChangePage = useCallback((newPage) => {
    console.log(`[IncomeController] Deciding page display: page ${newPage}`);
    setPage(newPage);
  }, []);

  // Data Actions (calls UI Service)
  const handleSaveIncome = useCallback(async (formData) => {
    console.log('[IncomeController] User submitted income data, calling UI Service');
    try {
      if (modalState.mode === 'edit' && modalState.selectedIncome) {
        await incomeService.updateIncome(modalState.selectedIncome.incomeId, formData);
        if (onNotify) onNotify('Income updated successfully!', 'success');
      } else {
        await incomeService.addIncome(formData);
        if (onNotify) onNotify('Income added successfully!', 'success');
      }
      decideCloseModal();
      loadIncomes();
      return { success: true };
    } catch (err) {
      if (onNotify) onNotify(err.message || 'Error saving income', 'error');
      return { success: false, error: err.message };
    }
  }, [modalState, onNotify, decideCloseModal, loadIncomes]);

  const handleDeleteIncome = useCallback(async (id) => {
    if (!window.confirm('Are you sure you want to delete this income entry?')) {
      return;
    }
    console.log('[IncomeController] Calling UI Service to delete income ID:', id);
    try {
      await incomeService.deleteIncome(id);
      if (onNotify) onNotify('Income deleted successfully', 'success');
      loadIncomes();
    } catch (err) {
      if (onNotify) onNotify(err.message || 'Failed to delete income', 'error');
    }
  }, [onNotify, loadIncomes]);

  return {
    incomes,
    loading,
    page,
    totalPages,
    totalCount,
    modalState,
    openAddModal: decideOpenAddModal,
    openEditModal: decideOpenEditModal,
    closeModal: decideCloseModal,
    changePage: decideChangePage,
    saveIncome: handleSaveIncome,
    deleteIncome: handleDeleteIncome,
    refreshIncomes: loadIncomes,
  };
}

export default useIncomeController;
