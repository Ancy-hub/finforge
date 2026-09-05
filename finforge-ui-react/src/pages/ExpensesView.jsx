import React, { useState } from 'react';
import { 
  Plus, 
  Search, 
  Filter, 
  Trash2, 
  Edit3, 
  Calendar, 
  ChevronLeft, 
  ChevronRight,
  RefreshCw
} from 'lucide-react';
import useExpenseController from '../controllers/useExpenseController';
import ExpenseModal from '../components/ExpenseModal';

export function ExpensesView({ onNotify }) {
  // Connected directly to UI Controller
  const {
    expenses,
    categories,
    loading,
    page,
    totalPages,
    totalCount,
    filters,
    modalState,
    openAddModal,
    openEditModal,
    closeModal,
    changePage,
    applyFilters,
    saveExpense,
    deleteExpense,
    refreshExpenses,
  } = useExpenseController({ onNotify });

  const [localFilters, setLocalFilters] = useState({
    categoryId: filters.categoryId || '',
    fromDate: filters.fromDate || '',
    toDate: filters.toDate || '',
  });

  const handleFilterSubmit = (e) => {
    e.preventDefault();
    applyFilters(localFilters);
  };

  const handleClearFilters = () => {
    const cleared = { categoryId: '', fromDate: '', toDate: '' };
    setLocalFilters(cleared);
    applyFilters(cleared);
  };

  const formatCurrency = (val) => {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(val || 0);
  };

  return (
    <div style={{ animation: 'fadeIn 0.25s ease-out' }}>
      {/* Header */}
      <div style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        marginBottom: '24px',
        flexWrap: 'wrap',
        gap: '16px',
      }}>
        <div>
          <h1 style={{ fontSize: '1.85rem', color: '#FFFFFF', marginBottom: '4px' }}>
            Expense Tracker
          </h1>
          <p style={{ fontSize: '0.86rem', color: 'var(--text-secondary)' }}>
            Log, categorize, and filter your outgoing disbursements.
          </p>
        </div>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button 
            className="btn btn-secondary"
            onClick={refreshExpenses}
            disabled={loading}
            title="Refresh from Backend"
          >
            <RefreshCw size={15} className={loading ? 'spin' : ''} />
            <span>Sync</span>
          </button>
          <button 
            id="btn-add-expense"
            className="btn btn-primary"
            onClick={openAddModal}
          >
            <Plus size={16} />
            <span>Add Expense</span>
          </button>
        </div>
      </div>

      {/* Filter Toolbar (Handled by UI Controller) */}
      <div className="glass-panel" style={{ padding: '18px 22px', marginBottom: '24px' }}>
        <form onSubmit={handleFilterSubmit} style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr)) 100px 90px',
          gap: '14px',
          alignItems: 'end',
        }}>
          <div>
            <label className="form-label">Category</label>
            <select
              className="form-control"
              value={localFilters.categoryId}
              onChange={(e) => setLocalFilters({ ...localFilters, categoryId: e.target.value })}
            >
              <option value="">All Categories</option>
              {categories.map((c) => (
                <option key={c.categoryId} value={c.categoryId}>
                  {c.name || c.categoryName}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="form-label">From Date</label>
            <input
              type="date"
              className="form-control"
              value={localFilters.fromDate}
              onChange={(e) => setLocalFilters({ ...localFilters, fromDate: e.target.value })}
            />
          </div>

          <div>
            <label className="form-label">To Date</label>
            <input
              type="date"
              className="form-control"
              value={localFilters.toDate}
              onChange={(e) => setLocalFilters({ ...localFilters, toDate: e.target.value })}
            />
          </div>

          <button type="submit" className="btn btn-primary" style={{ height: '42px' }}>
            <Filter size={15} />
            <span>Filter</span>
          </button>

          <button
            type="button"
            className="btn btn-secondary"
            style={{ height: '42px' }}
            onClick={handleClearFilters}
          >
            Reset
          </button>
        </form>
      </div>

      {/* Expenses Table */}
      <div className="glass-panel" style={{ overflow: 'hidden' }}>
        <div className="table-container">
          <table className="custom-table">
            <thead>
              <tr>
                <th>Title</th>
                <th>Category</th>
                <th>Date</th>
                <th>Description</th>
                <th style={{ textAlign: 'right' }}>Amount</th>
                <th style={{ textAlign: 'center', width: '110px' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
                    Loading expenses from backend service...
                  </td>
                </tr>
              ) : expenses.length === 0 ? (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
                    No expense records found. Click "Add Expense" to create one.
                  </td>
                </tr>
              ) : (
                expenses.map((exp) => (
                  <tr key={exp.expenseId}>
                    <td style={{ fontWeight: 600 }}>{exp.title}</td>
                    <td>
                      <span className="badge badge-info">
                        {exp.categoryName || 'General'}
                      </span>
                    </td>
                    <td style={{ color: 'var(--text-secondary)' }}>{exp.expenseDate}</td>
                    <td style={{ color: 'var(--text-muted)', fontSize: '0.82rem', maxWidth: '240px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                      {exp.description || '—'}
                    </td>
                    <td style={{ textAlign: 'right', fontWeight: 700, color: 'var(--color-danger)' }}>
                      -{formatCurrency(exp.amount)}
                    </td>
                    <td>
                      <div style={{ display: 'flex', justifyContent: 'center', gap: '8px' }}>
                        <button
                          onClick={() => openEditModal(exp)}
                          title="Edit"
                          style={{
                            background: 'rgba(255, 255, 255, 0.05)',
                            border: '1px solid var(--border-subtle)',
                            color: 'var(--text-secondary)',
                            borderRadius: 'var(--radius-sm)',
                            padding: '6px',
                            cursor: 'pointer',
                          }}
                        >
                          <Edit3 size={14} />
                        </button>
                        <button
                          onClick={() => deleteExpense(exp.expenseId)}
                          title="Delete"
                          style={{
                            background: 'var(--color-danger-bg)',
                            border: '1px solid rgba(244, 63, 94, 0.2)',
                            color: 'var(--color-danger)',
                            borderRadius: 'var(--radius-sm)',
                            padding: '6px',
                            cursor: 'pointer',
                          }}
                        >
                          <Trash2 size={14} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination Bar */}
        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          padding: '16px 20px',
          borderTop: '1px solid var(--border-subtle)',
          fontSize: '0.84rem',
          color: 'var(--text-secondary)',
        }}>
          <div>
            Showing Page <b>{page}</b> of <b>{totalPages}</b> ({totalCount} total records)
          </div>
          <div style={{ display: 'flex', gap: '8px' }}>
            <button
              className="btn btn-secondary btn-sm"
              disabled={page <= 1 || loading}
              onClick={() => changePage(page - 1)}
            >
              <ChevronLeft size={16} />
              <span>Previous</span>
            </button>
            <button
              className="btn btn-secondary btn-sm"
              disabled={page >= totalPages || loading}
              onClick={() => changePage(page + 1)}
            >
              <span>Next</span>
              <ChevronRight size={16} />
            </button>
          </div>
        </div>
      </div>

      {/* Modal - Controlled by UI Controller */}
      <ExpenseModal
        isOpen={modalState.isOpen}
        mode={modalState.mode}
        initialData={modalState.selectedExpense}
        categories={categories}
        onClose={closeModal}
        onSave={saveExpense}
      />
    </div>
  );
}

export default ExpensesView;
