import React from 'react';
import { CheckCircle2, AlertCircle, Info } from 'lucide-react';

export function NotificationToast({ notification }) {
  if (!notification) return null;

  const isSuccess = notification.type === 'success';
  const isError = notification.type === 'error';

  return (
    <div style={{
      position: 'fixed',
      bottom: '24px',
      right: '24px',
      zIndex: 200,
      background: isSuccess ? '#064E3B' : isError ? '#881337' : '#1E1B4B',
      border: `1px solid ${isSuccess ? '#059669' : isError ? '#E11D48' : '#6366F1'}`,
      color: '#FFFFFF',
      padding: '12px 20px',
      borderRadius: 'var(--radius-md)',
      boxShadow: 'var(--shadow-lg)',
      display: 'flex',
      alignItems: 'center',
      gap: '12px',
      fontSize: '0.88rem',
      fontWeight: 500,
      animation: 'slideUp 0.25s ease-out',
      maxWidth: '400px',
    }}>
      {isSuccess && <CheckCircle2 size={18} color="#34D399" />}
      {isError && <AlertCircle size={18} color="#FB7185" />}
      {!isSuccess && !isError && <Info size={18} color="#818CF8" />}
      <span>{notification.message}</span>
    </div>
  );
}

export default NotificationToast;
