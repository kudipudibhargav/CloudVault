import React from 'react';
import { useCloudVaultStore } from '../store/useCloudVaultStore';
import { HardDrive, Activity, TrendingUp, Calendar, AlertTriangle } from 'lucide-react';

export const AnalyticsTab: React.FC = () => {
  const { activeWorkspace } = useCloudVaultStore();

  const getQuotaPercentage = () => {
    if (!activeWorkspace) return 0;
    return (activeWorkspace.storageUsed / activeWorkspace.storageQuota) * 100;
  };

  const formatSize = (bytes: number) => {
    const gb = bytes / (1024 * 1024 * 1024);
    return `${gb.toFixed(1)} GB`;
  };

  // Generate GitHub style heatmap grid (5 rows x 20 cols)
  const heatmapData = Array.from({ length: 100 }, (_, i) => {
    const count = Math.floor(Math.sin(i * 0.15) * 5) + Math.floor(Math.random() * 4);
    return Math.max(0, count);
  });

  return (
    <div className="flex-1 p-6 flex flex-col gap-6 overflow-y-auto">
      {/* Tab Header */}
      <div>
        <h2 className="text-lg font-bold text-white tracking-wide">Workspace Storage Analytics</h2>
        <p className="text-xs text-slate-400">Monitor storage allocation growth, data ingress rates, and contribution timelines.</p>
      </div>

      {/* Metrics Row */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Card 1: Used Storage */}
        {activeWorkspace && (
          <div className="p-5 rounded-2xl bg-slate-900/40 border border-slate-850 flex flex-col gap-3 relative overflow-hidden">
            <div className="flex items-center justify-between text-slate-400 text-xs font-medium">
              <span className="flex items-center gap-1.5"><HardDrive className="w-4 h-4 text-brand-400" /> Space Used</span>
              <span className="font-mono text-brand-400 font-bold">{getQuotaPercentage().toFixed(1)}%</span>
            </div>
            <div className="flex items-baseline gap-1 mt-1">
              <span className="text-2xl font-black text-white">{formatSize(activeWorkspace.storageUsed)}</span>
              <span className="text-[10px] text-slate-500 font-semibold">of {formatSize(activeWorkspace.storageQuota)} allocated</span>
            </div>
            <div className="w-full h-2 bg-slate-800 rounded-full overflow-hidden mt-1">
              <div
                className={`h-full rounded-full transition-all duration-500 ${
                  getQuotaPercentage() > 85 ? 'bg-rose-500' : 'bg-brand-500'
                }`}
                style={{ width: `${getQuotaPercentage()}%` }}
              />
            </div>
          </div>
        )}

        {/* Card 2: Traffic Load */}
        <div className="p-5 rounded-2xl bg-slate-900/40 border border-slate-850 flex flex-col gap-3">
          <div className="flex items-center justify-between text-slate-400 text-xs font-medium">
            <span className="flex items-center gap-1.5"><Activity className="w-4 h-4 text-emerald-400" /> Data Ingress</span>
            <span className="text-emerald-400 text-[10px] font-bold uppercase">Healthy</span>
          </div>
          <div className="flex items-baseline gap-1 mt-1">
            <span className="text-2xl font-black text-white">425.2 MB</span>
            <span className="text-[10px] text-slate-500 font-semibold">transferred this week</span>
          </div>
          <p className="text-[10px] text-slate-500 leading-relaxed mt-2">
            Average transaction speed: **84.3 MB/s** backed by MinIO chunk compositions.
          </p>
        </div>

        {/* Card 3: Storage growth Forecast */}
        <div className="p-5 rounded-2xl bg-slate-900/40 border border-slate-850 flex flex-col gap-3">
          <div className="flex items-center justify-between text-slate-400 text-xs font-medium">
            <span className="flex items-center gap-1.5"><TrendingUp className="w-4 h-4 text-amber-400" /> Growth Forecast</span>
            <span className="text-[10px] text-amber-400 font-bold uppercase">Capacity Warnings</span>
          </div>
          <div className="flex items-baseline gap-1 mt-1">
            <span className="text-2xl font-black text-white">35 Days</span>
            <span className="text-[10px] text-slate-500 font-semibold">until limit exhaustion</span>
          </div>
          <p className="text-[10px] text-slate-500 leading-relaxed mt-2 flex items-center gap-1.5">
            <AlertTriangle className="w-3.5 h-3.5 text-amber-500/80" />
            AI predicts threshold breach on **August 7, 2026**.
          </p>
        </div>
      </div>

      {/* SVG Ingress Graph */}
      <div className="p-5 rounded-2xl bg-slate-900/20 border border-slate-850 flex flex-col gap-4">
        <div>
          <h3 className="text-xs font-bold text-white uppercase tracking-wider pl-1">Ingress Traffic History</h3>
          <p className="text-[10px] text-slate-500 mt-0.5">Real-time object packet streaming ingress tracking metrics.</p>
        </div>
        <div className="w-full h-48 bg-slate-950/40 rounded-xl border border-slate-850 p-4 flex items-center justify-center relative">
          <svg className="w-full h-full" viewBox="0 0 500 150" preserveAspectRatio="none">
            <defs>
              <linearGradient id="chartGradient" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stopColor="#4f46e5" stopOpacity="0.4" />
                <stop offset="100%" stopColor="#4f46e5" stopOpacity="0.0" />
              </linearGradient>
            </defs>
            {/* Grid Lines */}
            <line x1="0" y1="50" x2="500" y2="50" stroke="#1e293b" strokeDasharray="5,5" strokeWidth="0.5" />
            <line x1="0" y1="100" x2="500" y2="100" stroke="#1e293b" strokeDasharray="5,5" strokeWidth="0.5" />
            {/* Graph Path */}
            <path
              d="M 0 120 Q 50 80, 100 110 T 200 60 T 300 90 T 400 40 T 500 70 L 500 150 L 0 150 Z"
              fill="url(#chartGradient)"
            />
            <path
              d="M 0 120 Q 50 80, 100 110 T 200 60 T 300 90 T 400 40 T 500 70"
              fill="none"
              stroke="#4338ca"
              strokeWidth="2.5"
            />
          </svg>
          <div className="absolute bottom-2 left-4 text-[9px] text-slate-600 font-bold">MON</div>
          <div className="absolute bottom-2 left-1/4 text-[9px] text-slate-600 font-bold">WED</div>
          <div className="absolute bottom-2 left-2/4 text-[9px] text-slate-600 font-bold">FRI</div>
          <div className="absolute bottom-2 right-4 text-[9px] text-slate-600 font-bold">SUN</div>
        </div>
      </div>

      {/* Contribution Activity Heatmap */}
      <div className="p-5 rounded-2xl bg-slate-900/20 border border-slate-850 flex flex-col gap-4">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-xs font-bold text-white uppercase tracking-wider pl-1">Contribution Heat Map</h3>
            <p className="text-[10px] text-slate-500 mt-0.5">Frequency of file uploads, version rolls, and folder adjustments.</p>
          </div>
          <span className="text-[9px] text-slate-500 font-semibold flex items-center gap-1">
            <Calendar className="w-3 h-3 text-slate-600" />
            Last 100 Sessions
          </span>
        </div>

        <div className="flex flex-wrap gap-1.5 p-3 rounded-xl bg-slate-950/40 border border-slate-850 justify-center">
          {heatmapData.map((count, index) => {
            const getColorClass = (c: number) => {
              if (c === 0) return 'bg-slate-900 border-slate-850';
              if (c === 1) return 'bg-brand-950 border-brand-900/30';
              if (c === 2) return 'bg-brand-800 border-brand-700/30';
              if (c === 3) return 'bg-brand-600 border-brand-500/30';
              return 'bg-brand-500 border-brand-400/40';
            };

            return (
              <div
                key={index}
                className={`w-3.5 h-3.5 rounded-sm border ${getColorClass(count)}`}
                title={`${count} commits/actions in session`}
              />
            );
          })}
        </div>
      </div>

    </div>
  );
};
