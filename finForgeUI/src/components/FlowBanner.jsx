import React from 'react';
import { ArrowRight, Layers, Cpu, Server, Monitor } from 'lucide-react';

export function FlowBanner() {
  return (
    <div className="architecture-banner">
      <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
        <Monitor size={16} color="var(--accent-primary)" />
        <span style={{ fontWeight: 600, color: 'var(--text-primary)' }}>Architecture Flow Active:</span>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '10px', flexWrap: 'wrap' }}>
        <div className="flow-step-badge">
          <span>React UI View</span>
        </div>
        <ArrowRight size={13} color="var(--text-muted)" />

        <div className="flow-step-badge" style={{ background: 'rgba(99, 102, 241, 0.25)', color: '#A5B4FC' }}>
          <Layers size={13} />
          <span>UI Controller (Route vs Service Decision)</span>
        </div>
        <ArrowRight size={13} color="var(--text-muted)" />

        <div className="flow-step-badge" style={{ background: 'rgba(139, 92, 246, 0.25)', color: '#C4B5FD' }}>
          <Cpu size={13} />
          <span>UI Service (API Client)</span>
        </div>
        <ArrowRight size={13} color="var(--text-muted)" />

        <div className="flow-step-badge" style={{ background: 'rgba(16, 185, 129, 0.2)', color: '#6EE7B7' }}>
          <Server size={13} />
          <span>Backend API (:8080/api)</span>
        </div>
      </div>
    </div>
  );
}

export default FlowBanner;
