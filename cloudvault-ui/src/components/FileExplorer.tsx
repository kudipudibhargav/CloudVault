import React, { useState, useRef } from 'react';
import { useCloudVaultStore, type FileModel } from '../store/useCloudVaultStore';
import {
  Folder,
  File,
  FileText,
  Image as ImageIcon,
  Share2,
  Trash2,
  MessageSquare,
  Plus,
  Upload,
  History,
  Lock,
  Calendar,
  X,
  Eye,
  Download
} from 'lucide-react';

export const FileExplorer: React.FC = () => {
  const {
    folders,
    allFiles,
    currentFolder,
    setCurrentFolder,
    createFolder,
    uploadFile,
    deleteFile,
    rollbackVersion,
    setActiveFileForComments,
    searchQuery,
    filterMimeType,
    filterTag,
    setFilterTag
  } = useCloudVaultStore();

  const fileInputRef = useRef<HTMLInputElement>(null);
  
  // Modals / Input States
  const [showFolderModal, setShowFolderModal] = useState(false);
  const [newFolderName, setNewFolderName] = useState('');
  const [showShareModal, setShowShareModal] = useState<FileModel | null>(null);
  const [sharePassword, setSharePassword] = useState('');
  const [shareLimit, setShareLimit] = useState(5);
  const [generatedLink, setGeneratedLink] = useState('');
  
  // Preview Modal State
  const [previewFile, setPreviewFile] = useState<FileModel | null>(null);

  // Folder actions
  const handleCreateFolderSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (newFolderName.trim()) {
      createFolder(newFolderName.trim());
      setNewFolderName('');
      setShowFolderModal(false);
    }
  };

  // Upload handler generating local blob URLs for live reviews
  const handleUploadChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFiles = e.target.files;
    if (selectedFiles && selectedFiles.length > 0) {
      const file = selectedFiles[0];
      const previewUrl = URL.createObjectURL(file);
      uploadFile(file.name, file.size, file.type || 'application/octet-stream', previewUrl);
    }
  };

  // Share link generator simulation
  const handleGenerateShare = () => {
    if (showShareModal) {
      const token = Math.random().toString(36).substring(2, 18);
      const link = `http://localhost:8080/api/v1/shares/resolve?token=${token}`;
      setGeneratedLink(link);
    }
  };

  // Grid/List filter calculations matching JPQL backend query patterns
  const filteredFiles = allFiles.filter(file => {
    const matchesQuery = !searchQuery || 
      file.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (file.aiSummary && file.aiSummary.toLowerCase().includes(searchQuery.toLowerCase())) ||
      file.tags.some(t => t.toLowerCase().includes(searchQuery.toLowerCase()));

    const matchesMime = !filterMimeType || file.mimeType === filterMimeType;
    const matchesTag = !filterTag || file.tags.includes(filterTag);

    return matchesQuery && matchesMime && matchesTag;
  });

  const getFileIcon = (mimeType: string) => {
    if (mimeType === 'application/pdf') return <FileText className="w-5 h-5 text-red-400" />;
    if (mimeType.startsWith('image/')) return <ImageIcon className="w-5 h-5 text-blue-400" />;
    return <File className="w-5 h-5 text-slate-400" />;
  };

  const formatSize = (bytes: number) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
  };

  return (
    <div className="flex-1 p-6 flex flex-col gap-6 overflow-y-auto">
      {/* Upper action row */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-bold text-white tracking-wide">
            {currentFolder ? currentFolder.name : 'All Files'}
          </h2>
          <p className="text-xs text-slate-400">Manage workspace document assets and preview files instantly.</p>
        </div>

        <div className="flex items-center gap-3">
          {/* Tag filter clear badge */}
          {filterTag && (
            <button
              onClick={() => setFilterTag('')}
              className="flex items-center gap-1.5 px-3 py-1.5 bg-brand-500/10 hover:bg-brand-500/20 text-brand-400 border border-brand-500/30 rounded-xl text-xs transition-all"
            >
              Filtered Tag: {filterTag} <X className="w-3.5 h-3.5" />
            </button>
          )}

          <button
            onClick={() => setShowFolderModal(true)}
            className="flex items-center gap-2 px-4 py-2 rounded-xl bg-slate-900 border border-slate-800 hover:border-slate-700 hover:bg-slate-850 text-xs font-semibold text-white transition-all duration-200"
          >
            <Plus className="w-4 h-4" />
            New Folder
          </button>
          
          <button
            onClick={() => fileInputRef.current?.click()}
            className="flex items-center gap-2 px-4 py-2 rounded-xl bg-brand-600 hover:bg-brand-500 text-xs font-semibold text-white shadow-lg shadow-brand-600/20 transition-all duration-200"
          >
            <Upload className="w-4 h-4" />
            Upload File
          </button>
          <input
            type="file"
            ref={fileInputRef}
            onChange={handleUploadChange}
            className="hidden"
          />
        </div>
      </div>

      {/* Directory Folders Grid */}
      {folders.length > 0 && (
        <div className="flex flex-col gap-3">
          <label className="text-[10px] text-slate-500 font-bold uppercase tracking-wider pl-1">
            Subdirectories
          </label>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            {folders.map(folder => (
              <div
                key={folder.id}
                onClick={() => setCurrentFolder(folder.id)}
                className="p-4 rounded-2xl glass-panel glass-panel-hover transition-all cursor-pointer flex items-center gap-3.5 group"
              >
                <Folder className="w-6 h-6 text-brand-400 group-hover:scale-105 transition-transform" />
                <div className="flex flex-col">
                  <span className="text-xs font-semibold text-slate-200 truncate w-36">{folder.name}</span>
                  <span className="text-[9px] text-slate-500 font-medium">Workspace Folder</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Files Table List */}
      <div className="flex flex-col gap-3">
        <label className="text-[10px] text-slate-500 font-bold uppercase tracking-wider pl-1">
          Files & Documents ({filteredFiles.length})
        </label>
        
        {filteredFiles.length === 0 ? (
          <div className="p-12 text-center border border-dashed border-slate-800/80 rounded-2xl bg-slate-900/10 flex flex-col items-center justify-center gap-3">
            <File className="w-8 h-8 text-slate-600" />
            <div>
              <p className="text-sm font-semibold text-slate-400">No files matched criteria</p>
              <p className="text-xs text-slate-500">Try adjusting your query, tags, or file formats.</p>
            </div>
          </div>
        ) : (
          <div className="overflow-x-auto border border-slate-800/80 rounded-2xl bg-slate-900/10">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-slate-800/80 text-[10px] text-slate-400 uppercase tracking-wider font-semibold bg-slate-950/20">
                  <th className="p-4">Name</th>
                  <th className="p-4">Size</th>
                  <th className="p-4">AI Content Indexing</th>
                  <th className="p-4">Tags</th>
                  <th className="p-4">Version</th>
                  <th className="p-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/40 text-xs text-slate-350">
                {filteredFiles.map(file => (
                  <tr key={file.id} className="hover:bg-slate-900/30 transition-colors">
                    {/* File Identity */}
                    <td 
                      className="p-4 flex items-center gap-3 cursor-pointer group/cell"
                      onClick={() => setPreviewFile(file)}
                    >
                      {getFileIcon(file.mimeType)}
                      <div className="flex flex-col">
                        <span className="font-semibold text-slate-200 group-hover/cell:text-brand-400 transition-colors flex items-center gap-1.5">
                          {file.name}
                          <Eye className="w-3.5 h-3.5 opacity-0 group-hover/cell:opacity-100 text-brand-400 transition-opacity" />
                        </span>
                        <span className="text-[9px] text-slate-500">Uploaded by {file.creatorName}</span>
                      </div>
                    </td>

                    {/* File Size */}
                    <td className="p-4 font-medium text-slate-300">
                      {formatSize(file.size)}
                    </td>

                    {/* AI Summaries */}
                    <td className="p-4 max-w-xs">
                      {file.aiSummary ? (
                        <div className="relative group cursor-pointer">
                          <p className="truncate text-slate-400 hover:text-slate-200 transition-colors">
                            {file.aiSummary}
                          </p>
                          {/* Hover tooltip */}
                          <div className="absolute left-0 bottom-full mb-2 hidden group-hover:block w-72 p-3 bg-slate-900 border border-slate-750 text-[11px] text-slate-300 rounded-xl shadow-2xl z-30">
                            <span className="font-bold text-brand-400 block mb-1">AI Generated Summary</span>
                            {file.aiSummary}
                          </div>
                        </div>
                      ) : (
                        <span className="text-slate-600 font-medium">Pending Summarization...</span>
                      )}
                    </td>

                    {/* Tags Badges */}
                    <td className="p-4">
                      <div className="flex flex-wrap gap-1.5">
                        {file.tags.map(tag => (
                          <button
                            key={tag}
                            onClick={() => setFilterTag(tag)}
                            className="px-2 py-0.5 rounded-md bg-slate-850 hover:bg-slate-750 text-slate-400 hover:text-slate-200 border border-slate-800 transition-all text-[10px] font-semibold"
                          >
                            {tag}
                          </button>
                        ))}
                      </div>
                    </td>

                    {/* Version Rollback Trigger */}
                    <td className="p-4">
                      <button
                        onClick={() => rollbackVersion(file.id)}
                        className="px-2.5 py-1 bg-brand-500/10 hover:bg-brand-500/20 text-brand-400 border border-brand-500/20 rounded-lg flex items-center gap-1.5 transition-all text-[10px] font-bold"
                        title="Click to rollback to previous version (Zero-Copy reset)"
                      >
                        <History className="w-3.5 h-3.5" />
                        {file.currentVersionId}
                      </button>
                    </td>

                    {/* Action buttons */}
                    <td className="p-4 text-right">
                      <div className="flex items-center justify-end gap-2.5">
                        <button
                          onClick={() => setShowShareModal(file)}
                          className="p-2 text-slate-400 hover:text-white hover:bg-slate-800/80 rounded-xl transition-all"
                          title="Generate expirable share link"
                        >
                          <Share2 className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => setActiveFileForComments(file)}
                          className="p-2 text-slate-400 hover:text-white hover:bg-slate-800/80 rounded-xl transition-all"
                          title="Open comment stream drawer"
                        >
                          <MessageSquare className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => deleteFile(file.id)}
                          className="p-2 text-slate-400 hover:text-rose-500 hover:bg-rose-950/20 rounded-xl transition-all"
                          title="Soft delete file"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Live File Preview Modal */}
      {previewFile && (
        <div className="fixed inset-0 bg-slate-950/85 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="w-full max-w-4xl bg-slate-900 border border-slate-800 rounded-3xl shadow-2xl flex flex-col overflow-hidden max-h-[90vh]">
            {/* Modal Header */}
            <div className="p-4 border-b border-slate-800 flex items-center justify-between">
              <div className="flex items-center gap-3">
                {getFileIcon(previewFile.mimeType)}
                <div className="flex flex-col">
                  <h3 className="text-sm font-bold text-white leading-none">{previewFile.name}</h3>
                  <span className="text-[9px] text-slate-500 mt-1">Format: {previewFile.mimeType} • Size: {formatSize(previewFile.size)}</span>
                </div>
              </div>
              <div className="flex items-center gap-2">
                {previewFile.previewUrl && (
                  <a
                    href={previewFile.previewUrl}
                    download={previewFile.name}
                    className="p-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-350 hover:text-white transition-all flex items-center justify-center"
                    title="Download local copy"
                  >
                    <Download className="w-4 h-4" />
                  </a>
                )}
                <button
                  onClick={() => setPreviewFile(null)}
                  className="p-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-350 hover:text-white transition-all flex items-center justify-center"
                >
                  <X className="w-4 h-4" />
                </button>
              </div>
            </div>

            {/* Modal Content */}
            <div className="flex-1 p-6 overflow-y-auto flex items-center justify-center bg-slate-950/40">
              {previewFile.previewUrl ? (
                previewFile.mimeType.startsWith('image/') ? (
                  <img
                    src={previewFile.previewUrl}
                    alt={previewFile.name}
                    className="max-w-full max-h-[65vh] object-contain rounded-xl shadow-lg border border-slate-800"
                  />
                ) : previewFile.mimeType === 'application/pdf' ? (
                  <iframe
                    src={previewFile.previewUrl}
                    title={previewFile.name}
                    className="w-full h-[65vh] rounded-xl border border-slate-800"
                  />
                ) : (
                  <div className="text-center p-8 flex flex-col items-center gap-4">
                    <File className="w-16 h-16 text-slate-600" />
                    <div>
                      <p className="text-sm font-semibold text-slate-300">Preview not supported for this format</p>
                      <p className="text-xs text-slate-500">You can download this file to view it on your device.</p>
                    </div>
                    <a
                      href={previewFile.previewUrl}
                      download={previewFile.name}
                      className="px-4 py-2 bg-brand-600 hover:bg-brand-500 rounded-xl text-xs font-semibold text-white transition-all"
                    >
                      Download File
                    </a>
                  </div>
                )
              ) : (
                /* Mock File Preview Placeholder */
                <div className="text-center p-8 flex flex-col items-center gap-4 max-w-md">
                  <div className="p-4 bg-brand-600/10 rounded-2xl border border-brand-500/20 text-brand-400">
                    {getFileIcon(previewFile.mimeType)}
                  </div>
                  <div>
                    <h4 className="text-sm font-bold text-slate-200">Mock Showcase Record</h4>
                    <p className="text-xs text-slate-400 mt-2 leading-relaxed">
                      This is a simulated document created by default to demonstrate metadata structures. No physical object file is stored in your web browser.
                    </p>
                    <p className="text-[11px] text-slate-500 mt-3 font-semibold">
                      💡 Tip: Click "Upload File" in the top bar and select any local image or PDF file to test the live preview window!
                    </p>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      {/* New Folder Modal Dialog */}
      {showFolderModal && (
        <div className="fixed inset-0 bg-slate-950/70 backdrop-blur-sm z-50 flex items-center justify-center p-4 animate-fade-in">
          <form
            onSubmit={handleCreateFolderSubmit}
            className="w-full max-w-md p-6 bg-slate-900 border border-slate-800 rounded-3xl shadow-2xl flex flex-col gap-4"
          >
            <div>
              <h3 className="text-base font-bold text-white">Create New Directory</h3>
              <p className="text-[11px] text-slate-400">Establish a folder hierarchy within this workspace path.</p>
            </div>
            
            <input
              type="text"
              required
              value={newFolderName}
              onChange={(e) => setNewFolderName(e.target.value)}
              placeholder="Folder Name"
              className="w-full bg-slate-950 border border-slate-800 focus:border-brand-500/50 rounded-xl px-4 py-2.5 text-xs text-white placeholder-slate-500 outline-none transition-all"
            />

            <div className="flex items-center justify-end gap-3 border-t border-slate-800/60 pt-4">
              <button
                type="button"
                onClick={() => setShowFolderModal(false)}
                className="px-4 py-2 rounded-xl text-xs font-semibold text-slate-400 hover:text-slate-200 hover:bg-slate-800 transition-all"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-4 py-2 rounded-xl text-xs font-semibold text-white bg-brand-600 hover:bg-brand-500 transition-all"
              >
                Create
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Share Link Modal Dialog */}
      {showShareModal && (
        <div className="fixed inset-0 bg-slate-950/70 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="w-full max-w-md p-6 bg-slate-900 border border-slate-800 rounded-3xl shadow-2xl flex flex-col gap-4">
            <div className="flex justify-between items-start">
              <div>
                <h3 className="text-base font-bold text-white">Secure Share Configuration</h3>
                <p className="text-[11px] text-slate-400">Generate expirable access links for: {showShareModal.name}</p>
              </div>
              <button
                onClick={() => {
                  setShowShareModal(null);
                  setSharePassword('');
                  setGeneratedLink('');
                }}
                className="p-1.5 hover:bg-slate-800 rounded-lg text-slate-500 hover:text-slate-350"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            {!generatedLink ? (
              <div className="flex flex-col gap-3">
                {/* Password field */}
                <div className="flex flex-col gap-1">
                  <label className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Passcode Protection</label>
                  <div className="relative">
                    <Lock className="w-3.5 h-3.5 text-slate-500 absolute left-3 top-1/2 -translate-y-1/2" />
                    <input
                      type="password"
                      value={sharePassword}
                      onChange={(e) => setSharePassword(e.target.value)}
                      placeholder="Optional Access Passcode (BCrypt hashed)"
                      className="w-full bg-slate-950 border border-slate-800 focus:border-brand-500/50 rounded-xl pl-9 pr-4 py-2 text-xs text-white placeholder-slate-500 outline-none transition-all"
                    />
                  </div>
                </div>

                {/* Download limits */}
                <div className="flex flex-col gap-1">
                  <label className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Download Limit Count</label>
                  <input
                    type="number"
                    value={shareLimit}
                    onChange={(e) => setShareLimit(parseInt(e.target.value))}
                    min={1}
                    className="w-full bg-slate-950 border border-slate-800 focus:border-brand-500/50 rounded-xl px-4 py-2 text-xs text-white outline-none transition-all"
                  />
                </div>

                <button
                  onClick={handleGenerateShare}
                  className="w-full py-2.5 rounded-xl bg-brand-600 hover:bg-brand-500 text-xs font-semibold text-white transition-all duration-200 mt-2"
                >
                  Generate Share Token
                </button>
              </div>
            ) : (
              <div className="flex flex-col gap-4">
                <div className="p-3 bg-emerald-950/20 border border-emerald-900/30 rounded-xl text-center text-xs text-emerald-400 font-medium">
                  Link successfully generated and secured!
                </div>
                
                <input
                  type="text"
                  readOnly
                  value={generatedLink}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2 text-xs text-slate-350 select-all outline-none"
                />

                <div className="flex justify-between items-center text-[10px] text-slate-500">
                  <span className="flex items-center gap-1"><Lock className="w-3 h-3" /> Password enabled: {sharePassword ? 'Yes' : 'No'}</span>
                  <span className="flex items-center gap-1"><Calendar className="w-3 h-3" /> Limit: {shareLimit} downloads</span>
                </div>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
