import React from 'react';
import { useCloudVaultStore } from '../store/useCloudVaultStore';
import { Users, Eye, Pencil } from 'lucide-react';

export const Collaborators: React.FC = () => {
  const { collaborators } = useCloudVaultStore();

  return (
    <div className="w-64 border-l border-slate-800/80 bg-slate-900/10 p-5 flex flex-col gap-4">
      <div>
        <h3 className="text-xs font-bold text-white flex items-center gap-2">
          <Users className="w-4 h-4 text-brand-400" />
          Active Collaborators
        </h3>
        <p className="text-[10px] text-slate-500 mt-0.5">Workspace team presence status syncs.</p>
      </div>

      <div className="flex flex-col gap-3">
        {collaborators.map(c => (
          <div key={c.id} className="flex items-center justify-between p-2 rounded-xl bg-slate-900/35 border border-slate-850">
            <div className="flex items-center gap-3">
              <div className="relative">
                <img
                  src={c.avatarUrl}
                  alt={c.name}
                  className="w-8 h-8 rounded-lg object-cover ring-1 ring-slate-800"
                />
                <span className={`absolute bottom-0 right-0 block h-2 w-2 rounded-full ring-2 ring-slate-900 ${
                  c.status === 'editing' ? 'bg-amber-400' : 'bg-emerald-400'
                }`} />
              </div>
              <div className="flex flex-col text-[11px]">
                <span className="font-semibold text-slate-200">{c.name}</span>
                <span className="text-[9px] text-slate-500 font-medium">{c.role}</span>
              </div>
            </div>
            
            {/* Status symbol indicator */}
            <div className="opacity-80">
              {c.status === 'editing' ? (
                <span className="text-[9px] text-amber-400 font-bold flex items-center gap-1">
                  <Pencil className="w-3 h-3 animate-bounce" />
                  Typing
                </span>
              ) : (
                <span className="text-[9px] text-emerald-400 font-bold flex items-center gap-1">
                  <Eye className="w-3 h-3" />
                  Viewing
                </span>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
