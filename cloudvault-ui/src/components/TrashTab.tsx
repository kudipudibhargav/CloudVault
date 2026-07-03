import React from 'react';
import { useCloudVaultStore } from '../store/useCloudVaultStore';
import { Trash2, RotateCcw, AlertTriangle, FileText, Image as ImageIcon, File } from 'lucide-react';

export const TrashTab: React.FC = () => {
  const { deletedFiles, restoreFile, permanentlyDeleteFile } = useCloudVaultStore();

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
      {/* Header */}
      <div>
        <h2 className="text-lg font-bold text-white tracking-wide">Recycle Bin</h2>
        <p className="text-xs text-slate-400">Manage soft-deleted documents. Items can be restored or permanently purged from object stores.</p>
      </div>

      {deletedFiles.length > 0 && (
        <div className="p-4 rounded-2xl bg-amber-950/10 border border-amber-900/20 text-amber-400 flex items-start gap-3">
          <AlertTriangle className="w-4 h-4 text-amber-500 mt-0.5 shrink-0" />
          <div className="flex flex-col gap-1 text-[11px] leading-relaxed">
            <span className="font-bold">Permanent Deletion Notice</span>
            <p className="text-slate-400">
              Purging files permanently removes their raw binaries from MinIO S3 storage and deletes version history logs. This action is irreversible.
            </p>
          </div>
        </div>
      )}

      {/* Recycle Bin files table */}
      <div className="flex-1 flex flex-col gap-3 min-h-0">
        <label className="text-[10px] text-slate-500 font-bold uppercase tracking-wider pl-1">
          Deleted Items ({deletedFiles.length})
        </label>

        {deletedFiles.length === 0 ? (
          <div className="flex-1 flex flex-col items-center justify-center gap-3 text-center border border-dashed border-slate-800/80 rounded-2xl bg-slate-900/10 p-12">
            <Trash2 className="w-8 h-8 text-slate-700" />
            <div>
              <p className="text-sm font-semibold text-slate-400">Recycle Bin is empty</p>
              <p className="text-xs text-slate-500">Deleted files will appear here for 30 days before automatic cleanup.</p>
            </div>
          </div>
        ) : (
          <div className="overflow-x-auto border border-slate-800/80 rounded-2xl bg-slate-900/10">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-slate-800/80 text-[10px] text-slate-400 uppercase tracking-wider font-semibold bg-slate-950/20">
                  <th className="p-4">Name</th>
                  <th className="p-4">Size</th>
                  <th className="p-4">Original Format</th>
                  <th className="p-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/40 text-xs text-slate-350">
                {deletedFiles.map(file => (
                  <tr key={file.id} className="hover:bg-slate-900/30 transition-colors">
                    {/* File identity */}
                    <td className="p-4 flex items-center gap-3">
                      {getFileIcon(file.mimeType)}
                      <div className="flex flex-col">
                        <span className="font-semibold text-slate-200">{file.name}</span>
                        <span className="text-[9px] text-slate-500">Deleted by {file.creatorName}</span>
                      </div>
                    </td>

                    {/* Size */}
                    <td className="p-4 font-medium text-slate-300">
                      {formatSize(file.size)}
                    </td>

                    {/* Format */}
                    <td className="p-4 font-medium text-slate-500 font-mono text-[10px]">
                      {file.mimeType}
                    </td>

                    {/* Actions */}
                    <td className="p-4 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <button
                          onClick={() => restoreFile(file.id)}
                          className="px-3 py-1.5 rounded-xl bg-slate-850 hover:bg-slate-750 text-[10px] font-bold text-emerald-400 hover:text-emerald-350 border border-slate-800 transition-all flex items-center gap-1.5"
                          title="Restore file to workspace folders"
                        >
                          <RotateCcw className="w-3.5 h-3.5" />
                          Restore
                        </button>
                        <button
                          onClick={() => permanentlyDeleteFile(file.id)}
                          className="px-3 py-1.5 rounded-xl bg-rose-950/20 hover:bg-rose-950/45 text-[10px] font-bold text-rose-500 hover:text-rose-450 border border-rose-900/25 transition-all flex items-center gap-1.5"
                          title="Permanently purge binary object"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                          Purge
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

    </div>
  );
};
