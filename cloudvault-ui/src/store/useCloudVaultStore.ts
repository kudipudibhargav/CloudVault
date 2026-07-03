import { create } from 'zustand';

export interface FileModel {
  id: string;
  name: string;
  mimeType: string;
  size: number;
  aiSummary?: string;
  tags: string[];
  creatorName: string;
  creatorId: string;
  currentVersionId: string;
  createdAt: string;
  updatedAt: string;
  versionsCount: number;
  previewUrl?: string;
  isFavorite?: boolean;
  isPinned?: boolean;
}

export interface FolderModel {
  id: string;
  name: string;
  parentId: string | null;
  pathMaterialized: string;
}

export interface CommentModel {
  id: string;
  authorName: string;
  content: string;
  createdAt: string;
}

export interface WorkspaceModel {
  id: string;
  name: string;
  ownerName: string;
  storageQuota: number;
  storageUsed: number;
  encryptionType?: 'AES-256' | 'Zero-Knowledge';
}

export interface CollaboratorModel {
  id: string;
  name: string;
  role: string;
  avatarUrl: string;
  status: 'online' | 'editing' | 'idle';
}

export interface AuditLogModel {
  id: string;
  action: string;
  details: string;
  userEmail: string;
  ipAddress: string;
  timestamp: string;
  type: 'info' | 'warning' | 'security';
}

export interface ChatMessage {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  timestamp: string;
}

interface CloudVaultState {
  // Navigation / Auth Mode
  currentView: 'landing' | 'login' | 'signup' | 'onboarding' | 'app';
  activeTab: 'explorer' | 'analytics' | 'security' | 'trash';
  
  // Auth
  currentUser: { id: string; name: string; email: string; avatarUrl: string } | null;
  twoFactorRequired: boolean;
  twoFactorVerified: boolean;
  
  // Workspaces & Settings
  workspaces: WorkspaceModel[];
  activeWorkspace: WorkspaceModel | null;
  theme: 'dark' | 'light';
  encryptionPreset: 'AES-256' | 'Zero-Knowledge';
  
  // Navigation Path
  currentFolder: FolderModel | null;
  folderPath: FolderModel[];
  folders: FolderModel[];
  
  // Files
  files: FileModel[];
  allFiles: FileModel[];
  deletedFiles: FileModel[]; // Recycle Bin
  
  // Collaboration
  collaborators: CollaboratorModel[];
  comments: Record<string, CommentModel[]>;
  activeFileForComments: FileModel | null;
  
  // Search
  searchQuery: string;
  filterMimeType: string;
  filterTag: string;
  
  // AI Chat Assistant
  chatMessages: ChatMessage[];
  chatLoading: boolean;
  
  // Security & Audit Logs
  auditLogs: AuditLogModel[];
  suspiciousLoginDetected: boolean;
  
  // Notifications
  notification: { message: string; type: 'info' | 'success' | 'warning' } | null;

  // Actions
  setView: (view: 'landing' | 'login' | 'signup' | 'onboarding' | 'app') => void;
  setActiveTab: (tab: 'explorer' | 'analytics' | 'security' | 'trash') => void;
  setActiveWorkspace: (workspaceId: string) => void;
  setCurrentFolder: (folderId: string | null) => void;
  createFolder: (name: string) => void;
  uploadFile: (name: string, size: number, mimeType: string, previewUrl?: string) => void;
  deleteFile: (fileId: string) => void;
  restoreFile: (fileId: string) => void;
  permanentlyDeleteFile: (fileId: string) => void;
  renameFile: (fileId: string, newName: string) => void;
  toggleFavorite: (fileId: string) => void;
  togglePin: (fileId: string) => void;
  addComment: (fileId: string, content: string) => void;
  setActiveFileForComments: (file: FileModel | null) => void;
  setSearchQuery: (query: string) => void;
  setFilterMimeType: (mimeType: string) => void;
  setFilterTag: (tag: string) => void;
  rollbackVersion: (fileId: string) => void;
  sendChatMessage: (content: string) => void;
  clearChat: () => void;
  addAuditLog: (action: string, details: string, type?: 'info' | 'warning' | 'security') => void;
  dismissNotification: () => void;
  loginUser: (email: string, name: string) => void;
  verify2FA: (code: string) => boolean;
  completeOnboarding: (wsName: string, encryption: 'AES-256' | 'Zero-Knowledge') => void;
  triggerSecurityAlert: () => void;
}

const mockWorkspaces: WorkspaceModel[] = [
  { id: 'ws-1', name: 'Engineering Devs', ownerName: 'Aditya Kudipudi', storageQuota: 15 * 1024 * 1024 * 1024, storageUsed: 8.7 * 1024 * 1024 * 1024, encryptionType: 'Zero-Knowledge' },
  { id: 'ws-2', name: 'Product Marketing', ownerName: 'Sarah Jenkins', storageQuota: 5 * 1024 * 1024 * 1024, storageUsed: 1.2 * 1024 * 1024 * 1024, encryptionType: 'AES-256' },
];

const mockFolders: FolderModel[] = [
  { id: 'fold-1', name: 'Architecture Specifications', parentId: null, pathMaterialized: '/fold-1/' },
  { id: 'fold-2', name: 'Invoices & Receipts', parentId: null, pathMaterialized: '/fold-2/' },
  { id: 'fold-3', name: 'Deployment Logs', parentId: 'fold-1', pathMaterialized: '/fold-1/fold-3/' },
];

const mockFiles: FileModel[] = [
  {
    id: 'file-1',
    name: 'migration_plan.pdf',
    mimeType: 'application/pdf',
    size: 2.5 * 1024 * 1024,
    aiSummary: 'AI PDF Text summary processed: Annual enterprise architecture report covering cloud migration strategies.',
    tags: ['pdf', 'document', 'report', 'migration'],
    creatorName: 'Aditya Kudipudi',
    creatorId: 'user-1',
    currentVersionId: 'v2',
    createdAt: '2026-07-01T10:15:30Z',
    updatedAt: '2026-07-02T12:00:00Z',
    versionsCount: 2,
    isFavorite: true,
    isPinned: true
  },
  {
    id: 'file-2',
    name: 'invoice_receipt.png',
    mimeType: 'image/png',
    size: 450 * 1024,
    aiSummary: 'AI Image tag analysis processed. Detected document invoice sheet containing tax listings.',
    tags: ['image', 'document', 'invoice', 'receipt'],
    creatorName: 'Sarah Jenkins',
    creatorId: 'user-2',
    currentVersionId: 'v1',
    createdAt: '2026-07-02T09:30:15Z',
    updatedAt: '2026-07-02T09:30:15Z',
    versionsCount: 1,
    isPinned: true
  },
  {
    id: 'file-3',
    name: 'database_logs.txt',
    mimeType: 'text/plain',
    size: 1.2 * 1024 * 1024,
    aiSummary: 'AI Content analysis processed: Generic text database logs compiled successfully.',
    tags: ['file', 'data', 'text', 'logs'],
    creatorName: 'Aditya Kudipudi',
    creatorId: 'user-1',
    currentVersionId: 'v3',
    createdAt: '2026-06-30T16:00:00Z',
    updatedAt: '2026-07-02T15:20:00Z',
    versionsCount: 3
  }
];

const mockCollaborators: CollaboratorModel[] = [
  { id: 'c-1', name: 'Sarah Jenkins', role: 'Product PM', avatarUrl: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=80&fit=crop&q=80', status: 'online' },
  { id: 'c-2', name: 'Alex Rivera', role: 'Staff Eng', avatarUrl: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=80&fit=crop&q=80', status: 'editing' },
  { id: 'c-3', name: 'Marcus Chen', role: 'Security Sec', avatarUrl: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=80&fit=crop&q=80', status: 'idle' }
];

const initialAuditLogs: AuditLogModel[] = [
  { id: 'log-1', action: 'WORKSPACE_CREATE', details: 'Workspace "Engineering Devs" created successfully', userEmail: 'aditya@cloudvault.com', ipAddress: '192.168.1.5', timestamp: '2026-07-01T10:00:00Z', type: 'info' },
  { id: 'log-2', action: 'USER_LOGIN', details: 'Successful JWT Login from Chrome / Windows', userEmail: 'aditya@cloudvault.com', ipAddress: '192.168.1.5', timestamp: '2026-07-02T08:15:00Z', type: 'info' },
  { id: 'log-3', action: 'FILE_UPLOAD', details: 'File "migration_plan.pdf" uploaded (2.5MB)', userEmail: 'aditya@cloudvault.com', ipAddress: '192.168.1.5', timestamp: '2026-07-02T12:00:00Z', type: 'info' }
];

export const useCloudVaultStore = create<CloudVaultState>((set, get) => ({
  currentView: 'landing',
  activeTab: 'explorer',
  currentUser: null,
  twoFactorRequired: false,
  twoFactorVerified: false,
  workspaces: mockWorkspaces,
  activeWorkspace: mockWorkspaces[0],
  theme: 'dark',
  encryptionPreset: 'Zero-Knowledge',
  currentFolder: null,
  folderPath: [],
  folders: mockFolders.filter(f => f.parentId === null),
  files: mockFiles,
  allFiles: mockFiles,
  deletedFiles: [],
  collaborators: mockCollaborators,
  comments: {
    'file-1': [
      { id: 'com-1', authorName: 'Sarah Jenkins', content: 'Did we include the egress cost estimations in the migration plan?', createdAt: '2026-07-02T10:30:00Z' },
      { id: 'com-2', authorName: 'Aditya Kudipudi', content: 'Yes, they are listed on page 4 under Infrastructure Cost Breakdown.', createdAt: '2026-07-02T11:00:00Z' }
    ]
  },
  activeFileForComments: null,
  searchQuery: '',
  filterMimeType: '',
  filterTag: '',
  
  chatMessages: [
    { id: 'm-1', role: 'assistant', content: 'Hello! I am your CloudVault AI Assistant. I can search documents semantically, caption images, or run summaries. Ask me things like "Find my resume" or "Summarize migration plan".', timestamp: new Date().toISOString() }
  ],
  chatLoading: false,
  
  auditLogs: initialAuditLogs,
  suspiciousLoginDetected: false,
  notification: null,

  setView: (view) => set({ currentView: view }),
  
  setActiveTab: (tab) => set({ activeTab: tab }),

  setActiveWorkspace: (workspaceId) => {
    const ws = get().workspaces.find(w => w.id === workspaceId) || null;
    set({ activeWorkspace: ws, currentFolder: null, folderPath: [] });
    get().addAuditLog('WORKSPACE_SWITCH', `Switched to active workspace: "${ws?.name}"`, 'info');
  },

  setCurrentFolder: (folderId) => {
    const allMockFolders = mockFolders;
    const folder = allMockFolders.find(f => f.id === folderId) || null;
    
    const path: FolderModel[] = [];
    let current = folder;
    while (current) {
      path.unshift(current);
      const parentId = current.parentId;
      current = allMockFolders.find(f => f.id === parentId) || null;
    }

    const childFolders = allMockFolders.filter(f => f.parentId === folderId);
    set({
      currentFolder: folder,
      folderPath: path,
      folders: childFolders
    });
  },

  createFolder: (name) => {
    const newId = `fold-${Date.now()}`;
    const parent = get().currentFolder;
    const newFolder: FolderModel = {
      id: newId,
      name,
      parentId: parent ? parent.id : null,
      pathMaterialized: parent ? `${parent.pathMaterialized}${newId}/` : `/${newId}/`
    };

    mockFolders.push(newFolder);
    get().addAuditLog('FOLDER_CREATE', `Created folder "${name}" under path ${newFolder.pathMaterialized}`, 'info');

    set({
      folders: [...get().folders, newFolder],
      notification: { message: `Folder "${name}" created successfully`, type: 'success' }
    });
  },

  uploadFile: (name, size, mimeType, previewUrl) => {
    const ws = get().activeWorkspace;
    if (ws && ws.storageUsed + size > ws.storageQuota) {
      set({
        notification: { message: 'Upload failed: Insufficient storage quota in this workspace.', type: 'warning' }
      });
      return;
    }

    const newId = `file-${Date.now()}`;
    let aiSummary = 'AI Content analysis processed: Generic text database logs compiled successfully.';
    let tags = ['file', 'data', 'text'];
    
    if (mimeType.startsWith('image/')) {
      aiSummary = 'AI Image tag analysis processed. Detected document invoice sheet containing tax listings.';
      tags = ['image', 'document', 'invoice', 'receipt'];
    } else if (mimeType === 'application/pdf') {
      aiSummary = 'AI PDF Text summary processed: Annual enterprise architecture report covering cloud migration strategies.';
      tags = ['pdf', 'document', 'report'];
    }

    const newFile: FileModel = {
      id: newId,
      name,
      mimeType,
      size,
      aiSummary,
      tags,
      creatorName: get().currentUser?.name || 'Aditya Kudipudi',
      creatorId: get().currentUser?.id || 'user-1',
      currentVersionId: 'v1',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      versionsCount: 1,
      previewUrl
    };

    mockFiles.push(newFile);
    if (ws) ws.storageUsed += size;

    get().addAuditLog('FILE_UPLOAD', `Uploaded file "${name}" (${(size / 1024).toFixed(1)} KB) - AI metadata populated`, 'info');

    set({
      allFiles: [...get().allFiles, newFile],
      files: [...get().files, newFile],
      notification: { message: `File "${name}" uploaded and AI-summarized successfully.`, type: 'success' }
    });
  },

  deleteFile: (fileId) => {
    const file = get().allFiles.find(f => f.id === fileId);
    if (!file) return;

    // Move to Recycle Bin (soft delete)
    const updatedAll = get().allFiles.filter(f => f.id !== fileId);
    const updatedFiles = get().files.filter(f => f.id !== fileId);
    
    get().addAuditLog('FILE_DELETE_SOFT', `Soft deleted file "${file.name}" (Moved to Recycle Bin)`, 'info');

    set({
      allFiles: updatedAll,
      files: updatedFiles,
      deletedFiles: [...get().deletedFiles, file],
      notification: { message: `File "${file.name}" moved to Recycle Bin`, type: 'success' }
    });
  },

  restoreFile: (fileId) => {
    const file = get().deletedFiles.find(f => f.id === fileId);
    if (!file) return;

    const updatedDeleted = get().deletedFiles.filter(f => f.id !== fileId);
    
    get().addAuditLog('FILE_RESTORE', `Restored file "${file.name}" from Recycle Bin`, 'info');

    set({
      deletedFiles: updatedDeleted,
      allFiles: [...get().allFiles, file],
      files: [...get().files, file],
      notification: { message: `File "${file.name}" restored successfully`, type: 'success' }
    });
  },

  permanentlyDeleteFile: (fileId) => {
    const file = get().deletedFiles.find(f => f.id === fileId);
    if (!file) return;

    const ws = get().activeWorkspace;
    if (ws) {
      ws.storageUsed = Math.max(0, ws.storageUsed - file.size);
    }

    const updatedDeleted = get().deletedFiles.filter(f => f.id !== fileId);
    
    get().addAuditLog('FILE_DELETE_HARD', `Permanently deleted binary object file "${file.name}" from S3 storage`, 'warning');

    set({
      deletedFiles: updatedDeleted,
      notification: { message: `Permanently deleted "${file.name}"`, type: 'success' }
    });
  },

  renameFile: (fileId, newName) => {
    const updated = get().allFiles.map(f => {
      if (f.id === fileId) {
        return { ...f, name: newName, updatedAt: new Date().toISOString() };
      }
      return f;
    });

    get().addAuditLog('FILE_RENAME', `Renamed file to "${newName}"`, 'info');

    set({
      allFiles: updated,
      files: get().files.map(f => f.id === fileId ? { ...f, name: newName, updatedAt: new Date().toISOString() } : f),
      notification: { message: 'File renamed successfully', type: 'success' }
    });
  },

  toggleFavorite: (fileId) => {
    const updated = get().allFiles.map(f => {
      if (f.id === fileId) {
        const nextFav = !f.isFavorite;
        get().addAuditLog('FILE_FAVORITE', `${nextFav ? 'Starred' : 'Unstarred'} file "${f.name}"`, 'info');
        return { ...f, isFavorite: nextFav };
      }
      return f;
    });
    set({
      allFiles: updated,
      files: get().files.map(f => f.id === fileId ? { ...f, isFavorite: !f.isFavorite } : f)
    });
  },

  togglePin: (fileId) => {
    const updated = get().allFiles.map(f => {
      if (f.id === fileId) {
        const nextPin = !f.isPinned;
        get().addAuditLog('FILE_PIN', `${nextPin ? 'Pinned' : 'Unpinned'} file "${f.name}" to dashboard workspace`, 'info');
        return { ...f, isPinned: nextPin };
      }
      return f;
    });
    set({
      allFiles: updated,
      files: get().files.map(f => f.id === fileId ? { ...f, isPinned: !f.isPinned } : f)
    });
  },

  addComment: (fileId, content) => {
    const newComment: CommentModel = {
      id: `com-${Date.now()}`,
      authorName: get().currentUser?.name || 'Aditya Kudipudi',
      content,
      createdAt: new Date().toISOString()
    };

    const fileComments = get().comments[fileId] || [];
    set({
      comments: {
        ...get().comments,
        [fileId]: [...fileComments, newComment]
      }
    });

    get().addAuditLog('COMMENT_ADD', `Added comment to file: "${content}"`, 'info');

    if (content.includes('@')) {
      const match = content.match(/@(\w+)/);
      if (match) {
        set({
          notification: { message: `Notification dispatched for @mention of "${match[1]}"`, type: 'info' }
        });
      }
    }
  },

  setActiveFileForComments: (file) => {
    set({ activeFileForComments: file });
  },

  setSearchQuery: (query) => {
    set({ searchQuery: query });
  },

  setFilterMimeType: (mimeType) => {
    set({ filterMimeType: mimeType });
  },

  setFilterTag: (tag) => {
    set({ filterTag: tag });
  },

  rollbackVersion: (fileId) => {
    const updated = get().allFiles.map(f => {
      if (f.id === fileId) {
        return {
          ...f,
          currentVersionId: `v${Math.max(1, f.versionsCount - 1)}`,
          updatedAt: new Date().toISOString()
        };
      }
      return f;
    });

    get().addAuditLog('FILE_ROLLBACK', `Executed version rollback (Zero-Copy reset)`, 'warning');

    set({
      allFiles: updated,
      files: get().files.map(f => f.id === fileId ? { ...f, currentVersionId: `v${Math.max(1, f.versionsCount - 1)}`, updatedAt: new Date().toISOString() } : f),
      notification: { message: 'File version rollback executed (zero-copy pointer reset)', type: 'success' }
    });
  },

  sendChatMessage: (content) => {
    const userMsg: ChatMessage = {
      id: `msg-${Date.now()}`,
      role: 'user',
      content,
      timestamp: new Date().toISOString()
    };

    set({
      chatMessages: [...get().chatMessages, userMsg],
      chatLoading: true
    });

    // Simulate AI pipeline response delay
    setTimeout(() => {
      let replyText = "I've searched your workspace. ";
      const query = content.toLowerCase();

      if (query.includes('resume') || query.includes('cv')) {
        replyText += "I found 0 direct matches for 'resume'. However, you have an enterprise migration plan available: 'migration_plan.pdf'. Would you like me to summarize it?";
      } else if (query.includes('summarize') || query.includes('migration')) {
        replyText += "Here is the summary of 'migration_plan.pdf': This document details the step-by-step enterprise cloud migration specifications, covering network configurations, Redis clusters setups, and S3 assemblies pipelines.";
      } else if (query.includes('alert') || query.includes('security')) {
        replyText += "You have 3 healthy login locations. No suspicious logins detected. I can trigger a simulated security warning for testing purposes if you ask me to 'Simulate Security Alert'.";
      } else if (query.includes('simulate security alert') || query.includes('simulate alert')) {
        get().triggerSecurityAlert();
        replyText += "⚠️ Suspicious Login Alert Simulation triggered! Check the notification banner and the security logs panel.";
      } else {
        replyText += "I ran a semantic text search query in Elasticsearch. Matches were found inside tag filters: 'document', 'report'. Adjust tags filtering in the navbar to isolate matching items.";
      }

      const assistantMsg: ChatMessage = {
        id: `msg-${Date.now() + 1}`,
        role: 'assistant',
        content: replyText,
        timestamp: new Date().toISOString()
      };

      set({
        chatMessages: [...get().chatMessages, assistantMsg],
        chatLoading: false
      });
    }, 1500);
  },

  clearChat: () => {
    set({
      chatMessages: [
        { id: 'm-1', role: 'assistant', content: 'Chat history cleared. How can I help you optimize your storage workspace today?', timestamp: new Date().toISOString() }
      ]
    });
  },

  addAuditLog: (action, details, type = 'info') => {
    const newLog: AuditLogModel = {
      id: `log-${Date.now()}`,
      action,
      details,
      userEmail: get().currentUser?.email || 'aditya@cloudvault.com',
      ipAddress: '192.168.1.5',
      timestamp: new Date().toISOString(),
      type
    };
    set({ auditLogs: [newLog, ...get().auditLogs] });
  },

  loginUser: (email, name) => {
    set({
      currentUser: {
        id: 'user-1',
        name,
        email,
        avatarUrl: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=80&fit=crop&q=80'
      },
      twoFactorRequired: true
    });
  },

  verify2FA: (code) => {
    if (code === '123456') {
      set({
        twoFactorVerified: true,
        twoFactorRequired: false,
        currentView: 'onboarding'
      });
      get().addAuditLog('USER_AUTH_2FA', 'Two-Factor authentication verified successfully', 'info');
      return true;
    }
    return false;
  },

  completeOnboarding: (wsName, encryption) => {
    const newWorkspace: WorkspaceModel = {
      id: `ws-${Date.now()}`,
      name: wsName,
      ownerName: get().currentUser?.name || 'Aditya Kudipudi',
      storageQuota: 10 * 1024 * 1024 * 1024,
      storageUsed: 0,
      encryptionType: encryption
    };

    set({
      workspaces: [...get().workspaces, newWorkspace],
      activeWorkspace: newWorkspace,
      encryptionPreset: encryption,
      currentView: 'app'
    });

    get().addAuditLog('WORKSPACE_CREATE', `Provisioned onboarding workspace "${wsName}" with ${encryption} key configurations`, 'info');
  },

  triggerSecurityAlert: () => {
    set({
      suspiciousLoginDetected: true,
      notification: { message: '⚠️ Suspicious Login Detected: Unauthorized IP attempted connection to core services.', type: 'warning' }
    });
    get().addAuditLog('SUSPICIOUS_LOGIN', 'Unauthorized OAuth session attempt detected from IP 45.22.88.19 (Frankfurt, DE)', 'security');
  },

  dismissNotification: () => set({ notification: null })
}));
