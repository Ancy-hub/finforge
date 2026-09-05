import React from 'react';
import { 
  TrendingUp, 
  TrendingDown, 
  PiggyBank, 
  Plus, 
  ArrowUpRight, 
  ArrowRight,
  Clock,
  Calendar,
  Layers
} from 'lucide-react';
import useDashboardController from '../controllers/useDashboardController';

export function DashboardView({ onNavigate, onNotify, onAddExpense, onAddIncome }) {
  // Dispatches to UI Controller
  const {
    summary,
    recentExpenses,
    recentIncomes,
    loading,
    viewExpenses,
    viewIncomes,
    viewReports,
  } = useDashboardController({ onNavigate, onNotify });

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount || 0);
  };

  return (
    <div style={{ animation: 'fadeIn 0.25s ease-out' }}>
      {/* Page Header */}
      <div style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        marginBottom: '28px',
        flexWrap: 'wrap',
        gap: '16px',
      }}>
        <div>
          <h1 style={{ fontSize: '1.85rem', color: '#FFFFFF', marginBottom: '4px' }}>
            Financial Overview
          </h1>
          <p style={{ fontSize: '0.86rem', color: 'var(--text-secondary)' }}>
            Real-time balance, recent transactions, and portfolio metrics.
          </p>
        </div>

        {/* Quick Actions (UI Controller actions) */}
        <div style={{ display: 'flex', gap: '12px' }}>
          <button 
            id="btn-quick-add-income"
            className="btn btn-secondary"
            onClick={onAddIncome}
          >
            <Plus size={16} color="var(--color-success)" />
            <span>Add Income</span>
          </button>
          <button 
            id="btn-quick-add-expense"
            className="btn btn-primary"
            onClick={onAddExpense}
          >
            <Plus size={16} />
            <span>Add Expense</span>
          </button>
        </div>
      </div>

      {/* Metric Cards Grid */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
        gap: '20px',
        marginBottom: '32px',
      }}>
        {/* Total Income */}
        <div className="glass-panel" style={{ padding: '24px', position: 'relative', overflow: 'hidden' }}>
          <div style={{
            position: 'absolute',
            top: '-15px',
            right: '-15px',
            width: '80px',
            height: '80px',
            background: 'radial-gradient(circle, rgba(16, 185, 129, 0.15) 0%, transparent 70%)',
          }} />
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '14px' }}>
            <span style={{ fontSize: '0.84rem', fontWeight: 600, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.04em' }}>
              Total Income
            </span>
            <div style={{
              width: '36px',
              height: '36px',
              borderRadius: 'var(--radius-md)',
              background: 'var(--color-success-bg)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}>
              <TrendingUp size={18} color="var(--color-success)" />
            </div>
          </div>
          <div style={{ fontSize: '1.85rem', fontWeight: 700, color: '#FFFFFF', marginBottom: '8px' }}>
            {formatCurrency(summary.totalIncome)}
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.78rem', color: 'var(--color-success)' }}>
            <span className="badge badge-success">Active Revenue</span>
          </div>
        </div>

        {/* Total Expense */}
        <div className="glass-panel" style={{ padding: '24px', position: 'relative', overflow: 'hidden' }}>
          <div style={{
            position: 'absolute',
            top: '-15px',
            right: '-15px',
            width: '80px',
            height: '80px',
            background: 'radial-gradient(circle, rgba(244, 63, 94, 0.15) 0%, transparent 70%)',
          }} />
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '14px' }}>
            <span style={{ fontSize: '0.84rem', fontWeight: 600, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.04em' }}>
              Total Expenses
            </span>
            <div style={{
              width: '36px',
              height: '36px',
              borderRadius: 'var(--radius-md)',
              background: 'var(--color-danger-bg)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}>
              <TrendingDown size={18} color="var(--color-danger)" />
            </div>
          </div>
          <div style={{ fontSize: '1.85rem', fontWeight: 700, color: '#FFFFFF', marginBottom: '8px' }}>
            {formatCurrency(summary.totalExpense)}
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.78rem', color: 'var(--color-danger)' }}>
            <span className="badge badge-danger">Outflows</span>
          </div>
        </div>

        {/* Net Savings */}
        <div className="glass-panel" style={{ padding: '24px', position: 'relative', overflow: 'hidden' }}>
          <div style={{
            position: 'absolute',
            top: '-15px',
            right: '-15px',
            width: '80px',
            height: '80px',
            background: 'radial-gradient(circle, rgba(99, 102, 241, 0.2) 0%, transparent 70%)',
          }} />
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '14px' }}>
            <span style={{ fontSize: '0.84rem', fontWeight: 600, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.04em' }}>
              Net Savings
            </span>
            <div style={{
              width: '36px',
              height: '36px',
              borderRadius: 'var(--radius-md)',
              background: 'rgba(99, 102, 241, 0.15)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}>
              <PiggyBank size={18} color="var(--accent-primary)" />
            </div>
          </div>
          <div style={{
            fontSize: '1.85rem',
            fontWeight: 700,
            color: summary.netSavings >= 0 ? '#FFFFFF' : 'var(--color-danger)',
            marginBottom: '8px',
          }}>
            {formatCurrency(summary.netSavings)}
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.78rem' }}>
            <span className="badge badge-info">
              {summary.totalIncome > 0
                ? `${Math.round((summary.netSavings / summary.totalIncome) * 100)}% Savings Rate`
                : 'Balanced'}
            </span>
          </div>
        </div>
      </div>

      {/* Dual Activity Feed */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(420px, 1fr))',
        gap: '24px',
      }}>
        {/* Recent Expenses */}
        <div className="glass-panel" style={{ padding: '24px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '18px' }}>
            <div>
              <h3 style={{ fontSize: '1.1rem', color: '#FFFFFF' }}>Recent Expenses</h3>
              <p style={{ fontSize: '0.78rem', color: 'var(--text-secondary)' }}>Latest disbursements logged</p>
            </div>
            <button 
              className="btn btn-secondary btn-sm"
              onClick={viewExpenses}
            >
              <span>View All</span>
              <ArrowRight size={14} />
            </button>
          </div>

          {recentExpenses.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '36px 0', color: 'var(--text-muted)', fontSize: '0.88rem' }}>
              No expenses recorded yet. Click "Add Expense" to begin.
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
              {recentExpenses.map((exp) => (
                <div
                  key={exp.expenseId}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    padding: '12px 14px',
                    borderRadius: 'var(--radius-md)',
                    background: 'rgba(255, 255, 255, 0.02)',
                    border: '1px solid rgba(255, 255, 255, 0.04)',
                  }}
                >
                  <div>
                    <div style={{ fontWeight: 600, fontSize: '0.88rem', color: 'var(--text-primary)' }}>
                      {exp.title}
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                      <span>{exp.categoryName || 'General'}</span>
                      <span>•</span>
                      <span>{exp.expenseDate}</span>
                    </div>
                  </div>
                  <div style={{ fontWeight: 700, fontSize: '0.92rem', color: 'var(--color-danger)' }}>
                    -{formatCurrency(exp.amount)}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Recent Income */}
        <div className="glass-panel" style={{ padding: '24px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '18px' }}>
            <div>
              <h3 style={{ fontSize: '1.1rem', color: '#FFFFFF' }}>Recent Income</h3>
              <p style={{ fontSize: '0.78rem', color: 'var(--text-secondary)' }}>Latest incoming deposits</p>
            </div>
            <button 
              className="btn btn-secondary btn-sm"
              onClick={viewIncomes}
            >
              <span>View All</span>
              <ArrowRight size={14} />
            </button>
          </div>

          {recentIncomes.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '36px 0', color: 'var(--text-muted)', fontSize: '0.88rem' }}>
              No income entries recorded yet. Click "Add Income" to begin.
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
              {recentIncomes.map((inc) => (
                <div
                  key={inc.incomeId}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    padding: '12px 14px',
                    borderRadius: 'var(--radius-md)',
                    background: 'rgba(255, 255, 255, 0.02)',
                    border: '1px solid rgba(255, 255, 255, 0.04)',
                  }}
                >
                  <div>
                    <div style={{ fontWeight: 600, fontSize: '0.88rem', color: 'var(--text-primary)' }}>
                      {inc.source}
                    </div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                      {inc.incomeDate}
                    </div>
                  </div>
                  <div style={{ fontWeight: 700, fontSize: '0.92rem', color: 'var(--color-success)' }}>
                    +{formatCurrency(inc.amount)}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default DashboardView;
