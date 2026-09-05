import React, { useState, useEffect } from 'react';
import { X, Check } from 'lucide-react';

export function IncomeModal({ isOpen, mode, initialData, onClose, onSave }) {
  const [formData, setFormData] = useState({
    source: '',
    amount: '',
    incomeDate: new Date().toISOString().split('T')[0],
  });
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (initialData && mode === 'edit') {
      setFormData({
        source: initialData.source || '',
        amount: initialData.amount || '',
        incomeDate: initialData.incomeDate || new Date().toISOString().split('T')[0],
      });
    } else {
      setFormData({
        source: '',
        amount: '',
        incomeDate: new Date().toISOString().split('T')[0],
      });
    }
  }, [initialData, mode, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.source || !formData.amount) {
      alert('Please fill in source and amount.');
      return;
    }
    setSubmitting(true);
    await onSave(formData);
    setSubmitting(false);
  };

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      zIndex: 100,
      background: 'rgba(0, 0, 0, 0.75)',
      backdropFilter: 'blur(8px)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '20px',
    }}>
      <div className="glass-panel" style={{
        width: '100%',
        maxWidth: '500px',
        padding: '30px',
        animation: 'fadeIn 0.2s ease-out',
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '22px' }}>
          <div>
            <h3 style={{ fontSize: '1.25rem', color: '#FFFFFF' }}>
              {mode === 'edit' ? 'Edit Income Entry' : 'Add New Income'}
            </h3>
            <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
              Flow: Action handled by UI Controller → UI Service → Backend API
            </p>
          </div>
          <button
            onClick={onClose}
            style={{
              background: 'transparent',
              border: 'none',
              color: 'var(--text-muted)',
              cursor: 'pointer',
              padding: '6px',
            }}
          >
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Income Source *</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g., Tech Salary, Freelance project, Investment"
              value={formData.source}
              onChange={(e) => setFormData({ ...formData, source: e.target.value })}
              required
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
            <div className="form-group">
              <label className="form-label">Amount ($) *</label>
              <input
                type="number"
                step="0.01"
                min="0.01"
                className="form-control"
                placeholder="0.00"
                value={formData.amount}
                onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                required
              />
            </div>
            <div className="form-group">
              <label className="form-label">Date Received *</label>
              <input
                type="date"
                className="form-control"
                value={formData.incomeDate}
                onChange={(e) => setFormData({ ...formData, incomeDate: e.target.value })}
                required
              />
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '24px' }}>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={onClose}
              disabled={submitting}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={submitting}
              style={{ background: 'linear-gradient(135deg, #10B981, #059669)' }}
            >
              <Check size={16} />
              <span>{submitting ? 'Saving...' : mode === 'edit' ? 'Update Income' : 'Save Income'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default IncomeModal;
