import React, { useState, useEffect } from 'react';
import { X, Check } from 'lucide-react';

export function ExpenseModal({ isOpen, mode, initialData, categories, onClose, onSave }) {
  const [formData, setFormData] = useState({
    title: '',
    amount: '',
    expenseDate: new Date().toISOString().split('T')[0],
    categoryId: '',
    description: '',
  });
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (initialData && mode === 'edit') {
      setFormData({
        title: initialData.title || '',
        amount: initialData.amount || '',
        expenseDate: initialData.expenseDate || new Date().toISOString().split('T')[0],
        categoryId: String(initialData.categoryId || ''),
        description: initialData.description || '',
      });
    } else {
      setFormData({
        title: '',
        amount: '',
        expenseDate: new Date().toISOString().split('T')[0],
        categoryId: categories.length > 0 ? String(categories[0].categoryId) : '',
        description: '',
      });
    }
  }, [initialData, mode, categories, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.title || !formData.amount || !formData.categoryId) {
      alert('Please fill in title, amount, and category.');
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
        maxWidth: '520px',
        padding: '30px',
        position: 'relative',
        animation: 'fadeIn 0.2s ease-out',
      }}>
        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '22px' }}>
          <div>
            <h3 style={{ fontSize: '1.25rem', color: '#FFFFFF' }}>
              {mode === 'edit' ? 'Edit Expense' : 'Add New Expense'}
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

        {/* Form */}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Expense Title *</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g., Grocery shopping, AWS hosting"
              value={formData.title}
              onChange={(e) => setFormData({ ...formData, title: e.target.value })}
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
              <label className="form-label">Date *</label>
              <input
                type="date"
                className="form-control"
                value={formData.expenseDate}
                onChange={(e) => setFormData({ ...formData, expenseDate: e.target.value })}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Category *</label>
            <select
              className="form-control"
              value={formData.categoryId}
              onChange={(e) => setFormData({ ...formData, categoryId: e.target.value })}
              required
            >
              <option value="">Select Category</option>
              {categories.map((c) => (
                <option key={c.categoryId} value={c.categoryId}>
                  {c.name || c.categoryName}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Notes / Description (Optional)</label>
            <textarea
              className="form-control"
              rows="3"
              placeholder="Provide extra details regarding this payment..."
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
            />
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
            >
              <Check size={16} />
              <span>{submitting ? 'Saving...' : mode === 'edit' ? 'Update Expense' : 'Save Expense'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default ExpenseModal;
