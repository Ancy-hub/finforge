import React, { useState } from 'react';
import useAppFlowController from './controllers/useAppFlowController';
import Navbar from './components/Navbar';
import FlowBanner from './components/FlowBanner';
import NotificationToast from './components/NotificationToast';
import DashboardView from './pages/DashboardView';
import ExpensesView from './pages/ExpensesView';
import IncomesView from './pages/IncomesView';
import CategoriesView from './pages/CategoriesView';
import ReportsView from './pages/ReportsView';
import LoginView from './pages/LoginView';
import ExpenseModal from './components/ExpenseModal';
import IncomeModal from './components/IncomeModal';
import expenseService from './services/expenseService';
import incomeService from './services/incomeService';
import categoryService from './services/categoryService';

export function App() {
  // Main UI Flow Controller
  // Dispatches actions: decides which page to display OR calls UI services
  const {
    activePage,
    currentUser,
    notification,
    navigatePage,
    login,
    logout,
    setCurrentUser,
    showNotification,
  } = useAppFlowController();

  // Quick Action Modal States triggered from Global / Dashboard
  const [globalExpenseModal, setGlobalExpenseModal] = useState(false);
  const [globalIncomeModal, setGlobalIncomeModal] = useState(false);
  const [categories, setCategories] = useState([]);

  const handleOpenGlobalExpense = async () => {
    try {
      const cats = await categoryService.getCategories();
      setCategories(Array.isArray(cats) ? cats : []);
    } catch {}
    setGlobalExpenseModal(true);
  };

  const handleOpenGlobalIncome = () => {
    setGlobalIncomeModal(true);
  };

  const handleSaveGlobalExpense = async (data) => {
    try {
      await expenseService.addExpense(data);
      showNotification('Expense logged successfully!', 'success');
      setGlobalExpenseModal(false);
      // Controller decides to switch to expenses view to inspect the record
      navigatePage('expenses');
    } catch (err) {
      showNotification(err.message || 'Failed to save expense', 'error');
    }
  };

  const handleSaveGlobalIncome = async (data) => {
    try {
      await incomeService.addIncome(data);
      showNotification('Income logged successfully!', 'success');
      setGlobalIncomeModal(false);
      // Controller decides to switch to incomes view
      navigatePage('incomes');
    } catch (err) {
      showNotification(err.message || 'Failed to save income', 'error');
    }
  };

  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      {/* Top Navigation */}
      <Navbar
        activePage={activePage}
        onNavigate={navigatePage}
        currentUser={currentUser}
        onLogout={logout}
      />

      {/* Main Content Area */}
      <main style={{
        maxWidth: '1280px',
        width: '100%',
        margin: '0 auto',
        padding: '24px 24px 60px',
        flex: 1,
      }}>
        {/* Visual Architecture Flow Banner */}
        <FlowBanner />

        {/* Page Routing Handled by UI Controller */}
        {activePage === 'dashboard' && (
          <DashboardView
            onNavigate={navigatePage}
            onNotify={showNotification}
            onAddExpense={handleOpenGlobalExpense}
            onAddIncome={handleOpenGlobalIncome}
          />
        )}

        {activePage === 'expenses' && (
          <ExpensesView onNotify={showNotification} />
        )}

        {activePage === 'incomes' && (
          <IncomesView onNotify={showNotification} />
        )}

        {activePage === 'categories' && (
          <CategoriesView onNotify={showNotification} />
        )}

        {activePage === 'reports' && (
          <ReportsView onNotify={showNotification} />
        )}

        {activePage === 'login' && (
          <LoginView
            onLoginSuccess={(user) => {
              setCurrentUser(user);
              navigatePage('dashboard');
            }}
            onNotify={showNotification}
          />
        )}
      </main>

      {/* Toast Alerts */}
      <NotificationToast notification={notification} />

      {/* Global Quick Action Modals */}
      <ExpenseModal
        isOpen={globalExpenseModal}
        mode="add"
        categories={categories}
        onClose={() => setGlobalExpenseModal(false)}
        onSave={handleSaveGlobalExpense}
      />

      <IncomeModal
        isOpen={globalIncomeModal}
        mode="add"
        onClose={() => setGlobalIncomeModal(false)}
        onSave={handleSaveGlobalIncome}
      />
    </div>
  );
}

export default App;
