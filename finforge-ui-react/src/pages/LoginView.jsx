import React, { useState } from 'react';
import { Wallet, LogIn, UserPlus, Sparkles, ArrowRight, ShieldCheck } from 'lucide-react';
import authService from '../services/authService';

export function LoginView({ onLoginSuccess, onNotify }) {
  const [isRegister, setIsRegister] = useState(false);
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({
    username: '',
    password: '',
    email: '',
    firstName: '',
    lastName: '',
    phone: '',
    confirmPassword: '',
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      if (isRegister) {
        if (form.password !== form.confirmPassword) {
          throw new Error('Passwords do not match');
        }
        console.log('[UI Controller] Submitting registration via authService');
        const res = await authService.register(form);
        onNotify('Registration successful! Welcome to FinForge.', 'success');
        if (onLoginSuccess) onLoginSuccess(res.data || { username: form.username });
      } else {
        console.log('[UI Controller] Submitting login via authService');
        const res = await authService.login(form.username, form.password);
        onNotify('Login successful!', 'success');
        if (onLoginSuccess) onLoginSuccess(res.data || { username: form.username });
      }
    } catch (err) {
      onNotify(err.message || 'Authentication error', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleDemoSignIn = async () => {
    setLoading(true);
    try {
      console.log('[UI Controller] Demo sign-in initiated');
      const res = await authService.login('ancy', 'Password123!');
      onNotify('Signed in as Demo User!', 'success');
      if (onLoginSuccess) onLoginSuccess(res.data || { userId: 1, username: 'ancy', fullName: 'Ancy User' });
    } catch {
      // Fallback demo user
      if (onLoginSuccess) onLoginSuccess({ userId: 1, username: 'ancy', fullName: 'Ancy User' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      maxWidth: '440px',
      margin: '40px auto',
      animation: 'fadeIn 0.3s ease-out',
    }}>
      <div className="glass-panel" style={{ padding: '36px 32px', position: 'relative' }}>
        {/* Header Branding */}
        <div style={{ textAlign: 'center', marginBottom: '28px' }}>
          <div style={{
            width: '48px',
            height: '48px',
            borderRadius: 'var(--radius-md)',
            background: 'var(--accent-gradient)',
            display: 'inline-flex',
            alignItems: 'center',
            justifyContent: 'center',
            boxShadow: 'var(--shadow-glow)',
            marginBottom: '14px',
          }}>
            <Wallet size={26} color="#FFFFFF" />
          </div>
          <h2 style={{ fontSize: '1.5rem', color: '#FFFFFF', marginBottom: '4px' }}>
            {isRegister ? 'Create Your Account' : 'Welcome to FinForge'}
          </h2>
          <p style={{ fontSize: '0.82rem', color: 'var(--text-secondary)' }}>
            Enterprise financial management platform
          </p>
        </div>

        {/* Tab Toggle */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: '1fr 1fr',
          gap: '4px',
          background: 'rgba(255, 255, 255, 0.04)',
          padding: '4px',
          borderRadius: 'var(--radius-md)',
          marginBottom: '24px',
        }}>
          <button
            type="button"
            onClick={() => setIsRegister(false)}
            style={{
              padding: '8px',
              borderRadius: 'var(--radius-sm)',
              border: 'none',
              background: !isRegister ? 'var(--accent-primary)' : 'transparent',
              color: !isRegister ? '#FFFFFF' : 'var(--text-secondary)',
              fontWeight: 600,
              fontSize: '0.82rem',
              cursor: 'pointer',
              transition: 'var(--transition)',
            }}
          >
            Sign In
          </button>
          <button
            type="button"
            onClick={() => setIsRegister(true)}
            style={{
              padding: '8px',
              borderRadius: 'var(--radius-sm)',
              border: 'none',
              background: isRegister ? 'var(--accent-primary)' : 'transparent',
              color: isRegister ? '#FFFFFF' : 'var(--text-secondary)',
              fontWeight: 600,
              fontSize: '0.82rem',
              cursor: 'pointer',
              transition: 'var(--transition)',
            }}
          >
            Register
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Username</label>
            <input
              type="text"
              className="form-control"
              placeholder="Username"
              value={form.username}
              onChange={(e) => setForm({ ...form, username: e.target.value })}
              required
            />
          </div>

          {isRegister && (
            <>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                <div className="form-group">
                  <label className="form-label">First Name</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="First name"
                    value={form.firstName}
                    onChange={(e) => setForm({ ...form, firstName: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Last Name</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Last name"
                    value={form.lastName}
                    onChange={(e) => setForm({ ...form, lastName: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Email Address</label>
                <input
                  type="email"
                  className="form-control"
                  placeholder="name@finforge.com"
                  value={form.email}
                  onChange={(e) => setForm({ ...form, email: e.target.value })}
                  required
                />
              </div>
            </>
          )}

          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              type="password"
              className="form-control"
              placeholder="••••••••"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              required
            />
          </div>

          {isRegister && (
            <div className="form-group">
              <label className="form-label">Confirm Password</label>
              <input
                type="password"
                className="form-control"
                placeholder="••••••••"
                value={form.confirmPassword}
                onChange={(e) => setForm({ ...form, confirmPassword: e.target.value })}
                required
              />
            </div>
          )}

          <button
            type="submit"
            className="btn btn-primary"
            style={{ width: '100%', marginTop: '16px', padding: '12px' }}
            disabled={loading}
          >
            {isRegister ? <UserPlus size={16} /> : <LogIn size={16} />}
            <span>{loading ? 'Processing...' : isRegister ? 'Create Account' : 'Sign In'}</span>
          </button>
        </form>

        {/* Quick Demo Access */}
        <div style={{ marginTop: '24px', paddingTop: '20px', borderTop: '1px solid var(--border-subtle)' }}>
          <button
            type="button"
            onClick={handleDemoSignIn}
            className="btn btn-secondary"
            style={{ width: '100%', display: 'flex', justifyContent: 'space-between', padding: '10px 14px' }}
            disabled={loading}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Sparkles size={16} color="var(--accent-primary)" />
              <span style={{ fontSize: '0.84rem' }}>One-Click Instant Demo Login</span>
            </div>
            <ArrowRight size={14} color="var(--text-muted)" />
          </button>
        </div>
      </div>
    </div>
  );
}

export default LoginView;
