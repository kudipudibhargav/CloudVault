import React, { useState } from 'react';
import { useCloudVaultStore } from '../store/useCloudVaultStore';
import { ShieldAlert, Key, Globe, Eye, Server } from 'lucide-react';

export const SecurityTab: React.FC = () => {
  const { auditLogs, suspiciousLoginDetected, triggerSecurityAlert, encryptionPreset } = useCloudVaultStore();
  const [filterType, setFilterType] = useState<'all' | 'info' | 'warning' | 'security'>('all');

  const filteredLogs = auditLogs.filter(log => {
    if (filterType === 'all') return true;
    return log.type === filterType;
  });

  const getLogBadgeColor = (type: 'info' | 'warning' | 'security') => {
    if (type === 'security') return 'bg-rose-950/20 border-rose-900/30 text-rose-400';
    if (type === 'warning') return 'bg-amber-950/20 border-amber-900/30 text-amber-400';
    return 'bg-slate-900 border-slate-800 text-slate-400';
  };

  const formatDate = (isoString: string) => {
    const date = new Date(isoString);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' }) + ' - ' + date.toLocaleDateString();
  };

  return (
    <div className="flex-1 p-6 flex flex-col gap-6 overflow-y-auto">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-bold text-white tracking-wide">Security & Audit logs</h2>
          <p className="text-xs text-slate-400">Track user logins, folder creations, file rollbacks, and security key changes.</p>
        </div>

        {/* Simulate Threat button */}
        <button
          onClick={triggerSecurityAlert}
          className="flex items-center gap-1.5 px-3 py-1.5 bg-rose-500/10 hover:bg-rose-500/20 border border-rose-500/30 rounded-xl text-[10px] font-bold text-rose-400 transition-all"
        >
          <ShieldAlert className="w-3.5 h-3.5" />
          Simulate Threat Alert
        </button>
      </div>

      {/* Security Status Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Card 1: Key Encryption */}
        <div className="p-4 rounded-2xl bg-slate-900/40 border border-slate-850 flex items-center gap-4">
          <div className="p-3 bg-brand-500/10 border border-brand-500/25 rounded-xl text-brand-400">
            <Key className="w-5 h-5" />
          </div>
          <div className="flex flex-col text-[11px]">
            <span className="text-slate-500 font-bold uppercase tracking-wider text-[9px]">Keys Status</span>
            <span className="font-semibold text-slate-200 mt-0.5">{encryptionPreset} Enabled</span>
            <span className="text-[9px] text-emerald-400 mt-0.5">Secure client decrypts</span>
          </div>
        </div>

        {/* Card 2: Login locations */}
        <div className="p-4 rounded-2xl bg-slate-900/40 border border-slate-850 flex items-center gap-4">
          <div className="p-3 bg-emerald-500/10 border border-emerald-500/25 rounded-xl text-emerald-400">
            <Globe className="w-5 h-5" />
          </div>
          <div className="flex flex-col text-[11px]">
            <span className="text-slate-500 font-bold uppercase tracking-wider text-[9px]">Login Locations</span>
            <span className="font-semibold text-slate-200 mt-0.5">1 Location Active</span>
            <span className="text-[9px] text-slate-500 mt-0.5">IP: 192.168.1.5 (India, AP)</span>
          </div>
        </div>

        {/* Card 3: Core service status */}
        <div className="p-4 rounded-2xl bg-slate-900/40 border border-slate-850 flex items-center gap-4">
          <div className="p-3 bg-slate-800 border border-slate-750 rounded-xl text-slate-400">
            <Server className="w-5 h-5" />
          </div>
          <div className="flex flex-col text-[11px]">
            <span className="text-slate-500 font-bold uppercase tracking-wider text-[9px]">Ingest Firewalls</span>
            <span className="font-semibold text-slate-200 mt-0.5">BCrypt Validation Active</span>
            <span className="text-[9px] text-slate-500 mt-0.5">Rate limiter: 100 req/min</span>
          </div>
        </div>
      </div>

      {/* Threat Alert Panel */}
      {suspiciousLoginDetected && (
        <div className="p-4 rounded-2xl bg-rose-950/20 border border-rose-900/30 flex items-start gap-3.5 animate-pulse">
          <ShieldAlert className="w-5 h-5 text-rose-400 mt-0.5" />
          <div className="flex-1 flex flex-col gap-1 text-xs">
            <span className="font-bold text-rose-400">Suspicious Session Intercepted</span>
            <p className="text-[11px] text-rose-300 leading-relaxed">
              Our AI analysis flagged an unauthorized API session request trying to read Core files metadata from Frankfurt, DE. Verification protocols (2FA lockouts) have been reinforced.
            </p>
          </div>
        </div>
      )}

      {/* Audit Logs List */}
      <div className="flex-1 flex flex-col gap-3 min-h-0">
        <div className="flex items-center justify-between pl-1">
          <label className="text-[10px] text-slate-500 font-bold uppercase tracking-wider">
            System Log History ({filteredLogs.length})
          </label>
          
          {/* Logs filtering badges */}
          <div className="flex items-center gap-1.5">
            {(['all', 'info', 'warning', 'security'] as const).map(type => (
              <button
                key={type}
                onClick={() => setFilterType(type)}
                className={`px-2.5 py-1 rounded-lg text-[9px] font-bold uppercase tracking-wider border transition-all ${
                  filterType === type
                    ? 'bg-brand-600/10 border-brand-500/30 text-brand-400 shadow-md shadow-brand-500/5'
                    : 'bg-slate-900/40 border-slate-850 text-slate-500 hover:text-slate-350'
                }`}
              >
                {type}
              </button>
            ))}
          </div>
        </div>

        <div className="flex-1 overflow-y-auto border border-slate-850 bg-slate-900/10 rounded-2xl">
          {filteredLogs.length === 0 ? (
            <div className="h-full flex flex-col items-center justify-center gap-2 text-center select-none opacity-50 py-12">
              <Eye className="w-8 h-8 text-slate-600" />
              <p className="text-xs font-semibold text-slate-500">No logs found matching filter</p>
            </div>
          ) : (
            <div className="divide-y divide-slate-850">
              {filteredLogs.map(log => (
                <div key={log.id} className="p-4 flex items-center justify-between gap-4 text-xs hover:bg-slate-900/20 transition-colors">
                  <div className="flex items-start gap-4">
                    <span className={`px-2 py-0.5 rounded-md border text-[9px] font-bold uppercase tracking-wider ${getLogBadgeColor(log.type)}`}>
                      {log.action}
                    </span>
                    <div className="flex flex-col gap-1">
                      <p className="text-slate-200 font-medium">{log.details}</p>
                      <span className="text-[10px] text-slate-500 font-medium flex items-center gap-2">
                        <span>User: {log.userEmail}</span>
                        <span className="text-slate-700">•</span>
                        <span>IP: {log.ipAddress}</span>
                      </span>
                    </div>
                  </div>
                  <span className="text-[10px] text-slate-500 font-semibold shrink-0">
                    {formatDate(log.timestamp)}
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

    </div>
  );
};
