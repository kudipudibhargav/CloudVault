import React, { useState, useEffect } from 'react';
import { Sidebar } from './components/Sidebar';
import { Navbar } from './components/Navbar';
import { FileExplorer } from './components/FileExplorer';
import { Collaborators } from './components/Collaborators';
import { CommentsDrawer } from './components/CommentsDrawer';
import { LandingPage } from './components/LandingPage';
import { AuthScreens } from './components/AuthScreens';
import { OnboardingWizard } from './components/OnboardingWizard';
import { AnalyticsTab } from './components/AnalyticsTab';
import { SecurityTab } from './components/SecurityTab';
import { TrashTab } from './components/TrashTab';
import { AiChatPanel } from './components/AiChatPanel';
import { useCloudVaultStore } from './store/useCloudVaultStore';
import { Info, CheckCircle2, AlertTriangle, X } from 'lucide-react';

const App: React.FC = () => {
  const { currentView, activeTab, notification, dismissNotification } = useCloudVaultStore();
  const [isAiChatOpen, setIsAiChatOpen] = useState(false);

  // Auto-dismiss notification banners
  useEffect(() => {
    if (notification) {
      const timer = setTimeout(() => {
        dismissNotification();
      }, 5000);
      return () => clearTimeout(timer);
    }
  }, [notification, dismissNotification]);

  const getNotificationIcon = (type: 'info' | 'success' | 'warning') => {
    if (type === 'success') return <CheckCircle2 className="w-4 h-4 text-emerald-400" />;
    if (type === 'warning') return <AlertTriangle className="w-4 h-4 text-amber-400" />;
    return <Info className="w-4 h-4 text-brand-400" />;
  };

  // Render based on user view state
  if (currentView === 'landing') {
    return <LandingPage />;
  }

  if (currentView === 'login' || currentView === 'signup') {
    return <AuthScreens />;
  }

  if (currentView === 'onboarding') {
    return <OnboardingWizard />;
  }

  return (
    <div className="flex h-screen w-screen bg-slate-950 overflow-hidden font-sans select-none antialiased">
      {/* Sidebar Navigation */}
      <Sidebar />

      {/* Main Panel Viewport */}
      <div className="flex-1 flex flex-col min-w-0 bg-slate-950">
        <Navbar onToggleAiChat={() => setIsAiChatOpen(prev => !prev)} isAiChatOpen={isAiChatOpen} />
        
        <div className="flex-1 flex min-h-0">
          {/* Active Tab Router */}
          {activeTab === 'explorer' && <FileExplorer />}
          {activeTab === 'analytics' && <AnalyticsTab />}
          {activeTab === 'security' && <SecurityTab />}
          {activeTab === 'trash' && <TrashTab />}

          {/* AI Chat Assistant sidebar drawer */}
          {isAiChatOpen && <AiChatPanel />}

          {/* Active Collaborators presence list */}
          <Collaborators />
        </div>
      </div>

      {/* Slide-out comments drawer */}
      <CommentsDrawer />

      {/* Toast Notification Alert */}
      {notification && (
        <div className="fixed bottom-5 right-5 z-50 p-4 rounded-2xl bg-slate-900 border border-slate-800 shadow-2xl flex items-center gap-3 animate-slide-in max-w-sm">
          {getNotificationIcon(notification.type)}
          <span className="text-xs font-medium text-slate-200 leading-relaxed pr-6">{notification.message}</span>
          <button
            onClick={dismissNotification}
            className="p-1 hover:bg-slate-800 rounded-lg text-slate-500 hover:text-slate-350 absolute right-3 top-1/2 -translate-y-1/2"
          >
            <X className="w-3.5 h-3.5" />
          </button>
        </div>
      )}
    </div>
  );
};

export default App;
