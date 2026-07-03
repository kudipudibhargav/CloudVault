import React from 'react';
import { useCloudVaultStore } from '../store/useCloudVaultStore';
import { Search, SlidersHorizontal, Home, ChevronRight, LogOut, Sparkles } from 'lucide-react';

interface NavbarProps {
  onToggleAiChat: () => void;
  isAiChatOpen: boolean;
}

export const Navbar: React.FC<NavbarProps> = ({ onToggleAiChat, isAiChatOpen }) => {
  const {
    folderPath,
    setCurrentFolder,
    searchQuery,
    setSearchQuery,
    filterMimeType,
    setFilterMimeType,
    setView
  } = useCloudVaultStore();

  const handleBreadcrumbClick = (id: string | null) => {
    setCurrentFolder(id);
  };

  const handleSignOut = () => {
    setView('landing');
  };

  return (
    <header className="h-16 border-b border-slate-800/80 px-6 flex items-center justify-between bg-slate-950/20 backdrop-blur-md select-none shrink-0 z-30">
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

        {/* AI chat assistant toggle */}
        <button
          onClick={onToggleAiChat}
          className={`p-2 rounded-xl border transition-all flex items-center justify-center ${
            isAiChatOpen
              ? 'bg-brand-600/15 border-brand-500/35 text-brand-400'
              : 'bg-slate-900/40 border-slate-850 text-slate-450 hover:text-white hover:border-slate-700'
          }`}
          title="Toggle AI Chat Assistant"
        >
          <Sparkles className="w-4 h-4" />
        </button>

        {/* Sign Out */}
        <button
          onClick={handleSignOut}
          className="p-2 rounded-xl bg-slate-900/40 border border-slate-850 text-slate-500 hover:text-rose-400 hover:border-rose-900/30 transition-all flex items-center justify-center"
          title="Sign Out"
        >
          <LogOut className="w-4 h-4" />
        </button>
      </div>
    </header>
  );
};
