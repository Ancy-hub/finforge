import React from 'react';
import { 
  Plus, 
  Trash2, 
  Edit3, 
  ChevronLeft, 
  ChevronRight,
  TrendingUp,
  RefreshCw
} from 'lucide-react';
import useIncomeController from '../controllers/useIncomeController';
import IncomeModal from '../components/IncomeModal';

export function IncomesView({ onNotify }) {
  // Dispatches to UI Controller
  const {
    incomes,
    loading,
    page,
    totalPages,
    totalCount,
    modalState,
    openAddModal,
    openEditModal,
    closeModal,
    changePage,
    saveIncome,
    deleteIncome,
    refreshIncomes,
  } = useIncomeController({ onNotify });

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
            Income Records
          </h1>
          <p style={{ fontSize: '0.86rem', color: 'var(--text-secondary)' }}>
            Track salaries, dividends, consulting payouts, and revenue channels.
          </p>
        </div>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button 
            className="btn btn-secondary"
            onClick={refreshIncomes}
            disabled={loading}
          >
            <RefreshCw size={15} />
            <span>Sync</span>
          </button>
          <button 
            id="btn-add-income"
            className="btn btn-primary"
            style={{ background: 'linear-gradient(135deg, #10B981, #059669)' }}
            onClick={openAddModal}
          >
            <Plus size={16} />
            <span>Add Income</span>
          </button>
        </div>
      </div>

      {/* Income Table */}
      <div className="glass-panel" style={{ overflow: 'hidden' }}>
        <div className="table-container">
          <table className="custom-table">
            <thead>
              <tr>
                <th>Source / Channel</th>
                <th>Date Received</th>
                <th style={{ textAlign: 'right' }}>Amount</th>
                <th style={{ textAlign: 'center', width: '110px' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan="4" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
                    Loading income records from backend service...
                  </td>
                </tr>
              ) : incomes.length === 0 ? (
                <tr>
                  <td colSpan="4" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
                    No income records logged yet. Click "Add Income" to register your revenue.
                  </td>
                </tr>
              ) : (
                incomes.map((inc) => (
                  <tr key={inc.incomeId}>
                    <td style={{ fontWeight: 600 }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                        <div style={{
                          width: '28px',
                          height: '28px',
                          borderRadius: 'var(--radius-sm)',
                          background: 'var(--color-success-bg)',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                        }}>
                          <TrendingUp size={14} color="var(--color-success)" />
                        </div>
                        <span>{inc.source}</span>
                      </div>
                    </td>
                    <td style={{ color: 'var(--text-secondary)' }}>{inc.incomeDate}</td>
                    <td style={{ textAlign: 'right', fontWeight: 700, color: 'var(--color-success)' }}>
                      +{formatCurrency(inc.amount)}
                    </td>
                    <td>
                      <div style={{ display: 'flex', justifyContent: 'center', gap: '8px' }}>
                        <button
                          onClick={() => openEditModal(inc)}
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
                          onClick={() => deleteIncome(inc.incomeId)}
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

        {/* Pagination */}
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
            Showing Page <b>{page}</b> of <b>{totalPages}</b> ({totalCount} total entries)
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
      <IncomeModal
        isOpen={modalState.isOpen}
        mode={modalState.mode}
        initialData={modalState.selectedIncome}
        onClose={closeModal}
        onSave={saveIncome}
      />
    </div>
  );
}

export default IncomesView;
