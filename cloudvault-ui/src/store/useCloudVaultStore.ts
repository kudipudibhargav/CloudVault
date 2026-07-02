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
  previewUrl?: string; // Live file preview pointer
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
  storageQuota: number; // in bytes
  storageUsed: number;  // in bytes
}

export interface CollaboratorModel {
  id: string;
  name: string;
  role: string;
  avatarUrl: string;
  status: 'online' | 'editing' | 'idle';
}

interface CloudVaultState {
  // Auth
  currentUser: { id: string; name: string; email: string; avatarUrl: string } | null;
  
  // Workspaces
  workspaces: WorkspaceModel[];
  activeWorkspace: WorkspaceModel | null;
  
  // Navigation
  currentFolder: FolderModel | null;
  folderPath: FolderModel[];
  folders: FolderModel[];
  
  // Files
  files: FileModel[];
  allFiles: FileModel[]; // unfiltered base
  
  // Collaboration
  collaborators: CollaboratorModel[];
  comments: Record<string, CommentModel[]>; // fileId -> comments
  activeFileForComments: FileModel | null;
  
  // Search
  searchQuery: string;
  filterMimeType: string;
  filterTag: string;
  
  // Notifications
  notification: { message: string; type: 'info' | 'success' | 'warning' } | null;

  // Actions
  setActiveWorkspace: (workspaceId: string) => void;
  setCurrentFolder: (folderId: string | null) => void;
  createFolder: (name: string) => void;
  uploadFile: (name: string, size: number, mimeType: string, previewUrl?: string) => void;
  deleteFile: (fileId: string) => void;
  renameFile: (fileId: string, newName: string) => void;
  addComment: (fileId: string, content: string) => void;
  setActiveFileForComments: (file: FileModel | null) => void;
  setSearchQuery: (query: string) => void;
  setFilterMimeType: (mimeType: string) => void;
  setFilterTag: (tag: string) => void;
  rollbackVersion: (fileId: string) => void;
  dismissNotification: () => void;
}

const mockWorkspaces: WorkspaceModel[] = [
  { id: 'ws-1', name: 'Engineering Devs', ownerName: 'Aditya Kudipudi', storageQuota: 10 * 1024 * 1024 * 1024, storageUsed: 8.7 * 1024 * 1024 * 1024 },
  { id: 'ws-2', name: 'Product Marketing', ownerName: 'Sarah Jenkins', storageQuota: 5 * 1024 * 1024 * 1024, storageUsed: 1.2 * 1024 * 1024 * 1024 },
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
    versionsCount: 2
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
    versionsCount: 1
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
  { id: 'c-1', name: 'Sarah Jenkins', role: 'Product Manager', avatarUrl: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=80&fit=crop&q=80', status: 'online' },
  { id: 'c-2', name: 'Alex Rivera', role: 'Staff Designer', avatarUrl: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=80&fit=crop&q=80', status: 'editing' },
  { id: 'c-3', name: 'Marcus Chen', role: 'Security Analyst', avatarUrl: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=80&fit=crop&q=80', status: 'idle' }
];

export const useCloudVaultStore = create<CloudVaultState>((set, get) => ({
  currentUser: {
    id: 'user-1',
    name: 'Aditya Kudipudi',
    email: 'aditya@cloudvault.com',
    avatarUrl: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=80&fit=crop&q=80'
  },
  workspaces: mockWorkspaces,
  activeWorkspace: mockWorkspaces[0],
  currentFolder: null,
  folderPath: [],
  folders: mockFolders.filter(f => f.parentId === null),
  files: mockFiles,
  allFiles: mockFiles,
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
  notification: null,

  setActiveWorkspace: (workspaceId) => {
    const ws = get().workspaces.find(w => w.id === workspaceId) || null;
    set({ activeWorkspace: ws, currentFolder: null, folderPath: [] });
  },

  setCurrentFolder: (folderId) => {
    const allMockFolders = mockFolders;
    const folder = allMockFolders.find(f => f.id === folderId) || null;
    
    // Build path breadcrumbs
    const path: FolderModel[] = [];
    let current = folder;
    while (current) {
      path.unshift(current);
      const parentId = current.parentId;
      current = allMockFolders.find(f => f.id === parentId) || null;
    }

    // Filter child folders
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
    
    set({
      folders: [...get().folders, newFolder],
      notification: { message: `Folder "${name}" created successfully`, type: 'success' }
    });
  },

  uploadFile: (name, size, mimeType, previewUrl) => {
    // Check storage limits
    const ws = get().activeWorkspace;
    if (ws && ws.storageUsed + size > ws.storageQuota) {
      set({
        notification: { message: 'Upload failed: Insufficient storage quota in this workspace.', type: 'warning' }
      });
      return;
    }

    // Create file record
    const newId = `file-${Date.now()}`;
    
    // Simulate AI pipeline summary and tags
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
      creatorName: get().currentUser?.name || 'Unknown',
      creatorId: get().currentUser?.id || 'Unknown',
      currentVersionId: 'v1',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      versionsCount: 1,
      previewUrl
    };

    mockFiles.push(newFile);

    // Update storage used
    if (ws) {
      ws.storageUsed += size;
    }

    set({
      allFiles: [...get().allFiles, newFile],
      files: [...get().files, newFile],
      notification: { message: `File "${name}" uploaded and AI-summarized successfully.`, type: 'success' }
    });

    // Simulate WebSocket event delay
    setTimeout(() => {
      set({
        notification: { message: `Collaborators notified of new upload: ${name}`, type: 'info' }
      });
    }, 2000);
  },

  deleteFile: (fileId) => {
    const file = get().allFiles.find(f => f.id === fileId);
    if (!file) return;

    // Deduct storage
    const ws = get().activeWorkspace;
    if (ws) {
      ws.storageUsed = Math.max(0, ws.storageUsed - file.size);
    }

    const updated = get().allFiles.filter(f => f.id !== fileId);
    set({
      allFiles: updated,
      files: get().files.filter(f => f.id !== fileId),
      notification: { message: `File "${file.name}" deleted successfully`, type: 'success' }
    });
  },

  renameFile: (fileId, newName) => {
    const updated = get().allFiles.map(f => {
      if (f.id === fileId) {
        return { ...f, name: newName, updatedAt: new Date().toISOString() };
      }
      return f;
    });

    set({
      allFiles: updated,
      files: get().files.map(f => f.id === fileId ? { ...f, name: newName, updatedAt: new Date().toISOString() } : f),
      notification: { message: 'File renamed successfully', type: 'success' }
    });
  },

  addComment: (fileId, content) => {
    const newComment: CommentModel = {
      id: `com-${Date.now()}`,
      authorName: get().currentUser?.name || 'Anonymous',
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

    // Check for @mention trigger
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
          updatedAt: new Date().toISOString(),
          notification: { message: `File rolled back to previous version`, type: 'success' }
        };
      }
      return f;
    });

    set({
      allFiles: updated,
      files: get().files.map(f => f.id === fileId ? { ...f, currentVersionId: `v${Math.max(1, f.versionsCount - 1)}`, updatedAt: new Date().toISOString() } : f),
      notification: { message: 'File version rollback executed (zero-copy pointer reset)', type: 'success' }
    });
  },

  dismissNotification: () => {
    set({ notification: null });
  }
}));
