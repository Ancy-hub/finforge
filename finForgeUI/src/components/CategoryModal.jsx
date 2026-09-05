import React, { useState, useEffect } from 'react';
import { X, Check } from 'lucide-react';

export function CategoryModal({ isOpen, mode, initialData, onClose, onSave }) {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
  });
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (initialData && mode === 'edit') {
      setFormData({
        name: initialData.name || initialData.categoryName || '',
        description: initialData.description || '',
      });
    } else {
      setFormData({
        name: '',
        description: '',
      });
    }
  }, [initialData, mode, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.name) {
      alert('Please provide a category name.');
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
        maxWidth: '480px',
        padding: '30px',
        animation: 'fadeIn 0.2s ease-out',
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '22px' }}>
          <div>
            <h3 style={{ fontSize: '1.25rem', color: '#FFFFFF' }}>
              {mode === 'edit' ? 'Edit Category' : 'Create Category'}
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
            <label className="form-label">Category Name *</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g., Subscriptions, Health, Real Estate"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Description (Optional)</label>
            <textarea
              className="form-control"
              rows="3"
              placeholder="Brief description of this expense category..."
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
              <span>{submitting ? 'Saving...' : mode === 'edit' ? 'Update Category' : 'Create Category'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default CategoryModal;
