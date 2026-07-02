import React from 'react';
import { useCloudVaultStore } from '../store/useCloudVaultStore';
import { Search, SlidersHorizontal, Home, ChevronRight } from 'lucide-react';

export const Navbar: React.FC = () => {
  const {
    folderPath,
    setCurrentFolder,
    searchQuery,
    setSearchQuery,
    filterMimeType,
    setFilterMimeType,
  } = useCloudVaultStore();

  const handleBreadcrumbClick = (id: string | null) => {
    setCurrentFolder(id);
  };

  return (
    <header className="h-16 border-b border-slate-800/80 px-6 flex items-center justify-between bg-slate-950/20 backdrop-blur-md">
      {/* Path Breadcrumbs */}
      <div className="flex items-center gap-1.5 text-xs font-medium text-slate-400">
        <button
          onClick={() => handleBreadcrumbClick(null)}
          className="hover:text-slate-200 transition-colors flex items-center gap-1"
        >
          <Home className="w-3.5 h-3.5" />
          <span>Home</span>
        </button>

        {folderPath.map((folder, index) => (
          <React.Fragment key={folder.id}>
            <ChevronRight className="w-3.5 h-3.5 text-slate-600" />
            <button
              onClick={() => handleBreadcrumbClick(folder.id)}
              className={`hover:text-slate-200 transition-colors ${
                index === folderPath.length - 1 ? 'text-slate-200 font-semibold' : ''
              }`}
            >
              {folder.name}
            </button>
          </React.Fragment>
        ))}
      </div>

      {/* Advanced search controls */}
      <div className="flex items-center gap-4">
        {/* Search bar */}
        <div className="relative w-64">
          <Search className="w-4 h-4 text-slate-500 absolute left-3 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search files, tags, AI summaries..."
            className="w-full bg-slate-900/50 border border-slate-850 hover:border-slate-700/80 focus:border-brand-500/50 rounded-xl py-1.5 pl-9 pr-4 text-xs text-white placeholder-slate-500 outline-none transition-all duration-200"
          />
        </div>

        {/* MIME filter */}
        <div className="flex items-center gap-2 bg-slate-900/40 border border-slate-850 px-3 py-1.5 rounded-xl">
          <SlidersHorizontal className="w-3.5 h-3.5 text-slate-500" />
          <select
            value={filterMimeType}
            onChange={(e) => setFilterMimeType(e.target.value)}
            className="bg-transparent text-xs text-slate-400 focus:text-slate-200 outline-none cursor-pointer"
          >
            <option value="">All Formats</option>
            <option value="application/pdf">PDFs</option>
            <option value="image/png">Images</option>
            <option value="text/plain">Text Files</option>
          </select>
        </div>

        {/* WebSocket broadcast node status */}
        <div className="flex items-center gap-2 px-3 py-1.5 bg-emerald-950/20 border border-emerald-900/30 rounded-xl select-none">
          <span className="relative flex h-2 w-2">
            <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
            <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
          </span>
          <span className="text-[10px] text-emerald-400 font-bold uppercase tracking-wider">WS SYNCED</span>
        </div>
      </div>
    </header>
  );
};
