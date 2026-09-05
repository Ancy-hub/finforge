import React from 'react';
import { Plus, Trash2, Edit3, FolderKanban, Tag } from 'lucide-react';
import useCategoryController from '../controllers/useCategoryController';
import CategoryModal from '../components/CategoryModal';

export function CategoriesView({ onNotify }) {
  // Dispatches to UI Controller
  const {
    categories,
    loading,
    modalState,
    openAddModal,
    openEditModal,
    closeModal,
    saveCategory,
    deleteCategory,
    refreshCategories,
  } = useCategoryController({ onNotify });

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
            Expense Categories
          </h1>
          <p style={{ fontSize: '0.86rem', color: 'var(--text-secondary)' }}>
            Organize transactions to unlock detailed budgetary breakdowns.
          </p>
        </div>
        <button 
          id="btn-add-category"
          className="btn btn-primary"
          onClick={openAddModal}
        >
          <Plus size={16} />
          <span>Add Category</span>
        </button>
      </div>

      {/* Categories Grid */}
      {loading ? (
        <div className="glass-panel" style={{ padding: '40px', textAlign: 'center', color: 'var(--text-muted)' }}>
          Loading categories from backend service...
        </div>
      ) : categories.length === 0 ? (
        <div className="glass-panel" style={{ padding: '40px', textAlign: 'center', color: 'var(--text-muted)' }}>
          No custom categories set up. Click "Add Category" to get started.
        </div>
      ) : (
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
          gap: '20px',
        }}>
          {categories.map((c) => (
            <div
              key={c.categoryId}
              className="glass-panel"
              style={{
                padding: '22px',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'space-between',
                position: 'relative',
              }}
            >
              <div>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '12px' }}>
                  <div style={{
                    width: '38px',
                    height: '38px',
                    borderRadius: 'var(--radius-md)',
                    background: 'rgba(99, 102, 241, 0.15)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                  }}>
                    <Tag size={18} color="var(--accent-primary)" />
                  </div>
                  <div style={{ display: 'flex', gap: '6px' }}>
                    <button
                      onClick={() => openEditModal(c)}
                      title="Edit Category"
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
                      onClick={() => deleteCategory(c.categoryId)}
                      title="Delete Category"
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
                </div>

                <h3 style={{ fontSize: '1.15rem', color: '#FFFFFF', marginBottom: '6px' }}>
                  {c.name || c.categoryName}
                </h3>
                <p style={{ fontSize: '0.82rem', color: 'var(--text-muted)', lineHeight: '1.4' }}>
                  {c.description || 'General expense classification tag.'}
                </p>
              </div>

              <div style={{
                marginTop: '18px',
                paddingTop: '12px',
                borderTop: '1px solid rgba(255, 255, 255, 0.05)',
                fontSize: '0.75rem',
                color: 'var(--text-secondary)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
              }}>
                <span>Tag ID: #{c.categoryId}</span>
                <span className="badge badge-info">Active</span>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Category Modal - Controlled by UI Controller */}
      <CategoryModal
        isOpen={modalState.isOpen}
        mode={modalState.mode}
        initialData={modalState.selectedCategory}
        onClose={closeModal}
        onSave={saveCategory}
      />
    </div>
  );
}

export default CategoriesView;
