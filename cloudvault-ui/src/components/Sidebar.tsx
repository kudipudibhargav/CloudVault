import React from 'react';
import { useCloudVaultStore } from '../store/useCloudVaultStore';
import { HardDrive, FolderOpen, Shield, Cloud } from 'lucide-react';

export const Sidebar: React.FC = () => {
  const { workspaces, activeWorkspace, setActiveWorkspace, currentUser } = useCloudVaultStore();

  const formatSize = (bytes: number) => {
    const gb = bytes / (1024 * 1024 * 1024);
    return `${gb.toFixed(1)} GB`;
  };

  const getQuotaPercentage = () => {
    if (!activeWorkspace) return 0;
    return (activeWorkspace.storageUsed / activeWorkspace.storageQuota) * 100;
  };

  return (
    <aside className="w-64 border-r border-slate-800/80 bg-slate-900/40 p-5 flex flex-col justify-between">
      <div className="flex flex-col gap-6">
        {/* SaaS Header */}
        <div className="flex items-center gap-3">
          <div className="p-2.5 bg-brand-600 rounded-xl shadow-lg shadow-brand-500/25 flex items-center justify-center">
            <Cloud className="w-5 h-5 text-white" />
          </div>
          <div>
            <h1 className="font-semibold text-white tracking-wide text-sm">CloudVault</h1>
            <span className="text-[10px] text-slate-400 font-medium uppercase tracking-wider">Enterprise SaaS</span>
          </div>
        </div>

        {/* Workspaces List */}
        <div className="flex flex-col gap-2">
          <label className="text-[10px] text-slate-500 font-bold uppercase tracking-wider pl-1">
            Active Workspaces
          </label>
          <div className="flex flex-col gap-1">
            {workspaces.map((ws) => {
              const isActive = activeWorkspace?.id === ws.id;
              return (
                <button
                  key={ws.id}
                  onClick={() => setActiveWorkspace(ws.id)}
                  className={`w-full text-left px-3.5 py-2.5 rounded-xl text-xs font-medium transition-all duration-200 flex items-center justify-between ${
                    isActive
                      ? 'bg-brand-600/15 text-brand-400 border border-brand-500/30'
                      : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200 border border-transparent'
                  }`}
                >
                  <span className="truncate">{ws.name}</span>
                  <FolderOpen className="w-3.5 h-3.5 opacity-60" />
                </button>
              );
            })}
          </div>
        </div>
      </div>

      {/* Quota Space Utilization and User Profiles */}
      <div className="flex flex-col gap-5">
        {/* Storage Quota Card */}
        {activeWorkspace && (
          <div className="p-4 rounded-2xl bg-slate-950/60 border border-slate-800/80 flex flex-col gap-3">
            <div className="flex items-center justify-between text-[11px] font-medium text-slate-400">
              <span className="flex items-center gap-1.5">
                <HardDrive className="w-3.5 h-3.5 text-brand-400" />
                Quota Allocation
              </span>
              <span>{getQuotaPercentage().toFixed(0)}%</span>
            </div>
            
            {/* Progress Bar */}
            <div className="w-full h-1.5 bg-slate-800 rounded-full overflow-hidden">
              <div
                className={`h-full rounded-full transition-all duration-500 ${
                  getQuotaPercentage() > 85 ? 'bg-rose-500' : 'bg-brand-500'
                }`}
                style={{ width: `${getQuotaPercentage()}%` }}
              />
            </div>

            <div className="flex justify-between text-[10px] text-slate-500 font-medium">
              <span>{formatSize(activeWorkspace.storageUsed)} used</span>
              <span>{formatSize(activeWorkspace.storageQuota)} total</span>
            </div>
          </div>
        )}

        {/* User Card */}
        {currentUser && (
          <div className="flex items-center justify-between border-t border-slate-800/80 pt-4">
            <div className="flex items-center gap-3">
              <img
                src={currentUser.avatarUrl}
                alt={currentUser.name}
                className="w-9 h-9 rounded-xl object-cover ring-2 ring-slate-800"
              />
              <div className="flex flex-col">
                <span className="text-xs font-semibold text-white truncate w-32">{currentUser.name}</span>
                <span className="text-[10px] text-slate-500 truncate w-32">{currentUser.email}</span>
              </div>
            </div>
            <span title="Auth Role Secured">
              <Shield className="w-4 h-4 text-emerald-400 opacity-80" />
            </span>
          </div>
        )}
      </div>
    </aside>
  );
};
