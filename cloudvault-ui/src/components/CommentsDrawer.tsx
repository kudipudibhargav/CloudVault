import React, { useState } from 'react';
import { useCloudVaultStore } from '../store/useCloudVaultStore';
import { X, Send, MessageCircle } from 'lucide-react';

export const CommentsDrawer: React.FC = () => {
  const {
    activeFileForComments,
    setActiveFileForComments,
    comments,
    addComment
  } = useCloudVaultStore();

  const [newCommentText, setNewCommentText] = useState('');

  if (!activeFileForComments) return null;

  const fileComments = comments[activeFileForComments.id] || [];

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (newCommentText.trim()) {
      addComment(activeFileForComments.id, newCommentText.trim());
      setNewCommentText('');
    }
  };

  const formatDate = (isoString: string) => {
    const date = new Date(isoString);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) + ' - ' + date.toLocaleDateString();
  };

  return (
    <div className="fixed inset-y-0 right-0 w-80 bg-slate-900 border-l border-slate-800 shadow-2xl z-40 flex flex-col justify-between animate-slide-in">
      {/* Header */}
      <div className="p-4 border-b border-slate-800 flex items-center justify-between">
        <div className="flex items-center gap-2 text-white">
          <MessageCircle className="w-4 h-4 text-brand-400" />
          <h3 className="text-xs font-bold truncate w-52" title={activeFileForComments.name}>
            Comments: {activeFileForComments.name}
          </h3>
        </div>
        <button
          onClick={() => setActiveFileForComments(null)}
          className="p-1 hover:bg-slate-800 rounded-lg text-slate-500 hover:text-slate-350 transition-all"
        >
          <X className="w-4 h-4" />
        </button>
      </div>

      {/* Discussion List */}
      <div className="flex-1 p-4 overflow-y-auto flex flex-col gap-4">
        {fileComments.length === 0 ? (
          <div className="h-full flex flex-col items-center justify-center gap-2 text-center select-none opacity-60">
            <MessageCircle className="w-8 h-8 text-slate-600" />
            <p className="text-xs font-semibold text-slate-500">No comments yet</p>
            <p className="text-[10px] text-slate-600">Start the conversation or @mention collaborators.</p>
          </div>
        ) : (
          fileComments.map(comment => (
            <div key={comment.id} className="flex flex-col gap-1.5 p-3 rounded-2xl bg-slate-950/40 border border-slate-850">
              <div className="flex items-center justify-between text-[10px]">
                <span className="font-bold text-slate-200 flex items-center gap-1.5">
                  <div className="w-4 h-4 rounded-full bg-slate-800 flex items-center justify-center text-[8px] text-slate-400 font-bold">
                    {comment.authorName.charAt(0)}
                  </div>
                  {comment.authorName}
                </span>
                <span className="text-slate-500 font-medium">{formatDate(comment.createdAt)}</span>
              </div>
              <p className="text-[11px] text-slate-300 leading-relaxed pl-5 whitespace-pre-wrap">
                {comment.content.split(' ').map((word, i) => {
                  if (word.startsWith('@')) {
                    return <span key={i} className="text-brand-400 font-bold">{word} </span>;
                  }
                  return word + ' ';
                })}
              </p>
            </div>
          ))
        )}
      </div>

      {/* Post comment input box */}
      <form onSubmit={handleSubmit} className="p-4 border-t border-slate-850 bg-slate-900/60 flex items-center gap-2">
        <input
          type="text"
          value={newCommentText}
          onChange={(e) => setNewCommentText(e.target.value)}
          placeholder="Add comment, use @mention..."
          className="flex-1 bg-slate-950 border border-slate-800 focus:border-brand-500/50 rounded-xl px-3 py-2 text-xs text-white placeholder-slate-500 outline-none transition-all"
        />
        <button
          type="submit"
          className="p-2 rounded-xl bg-brand-600 hover:bg-brand-500 text-white shadow-md shadow-brand-600/10 transition-all flex items-center justify-center"
        >
          <Send className="w-3.5 h-3.5" />
        </button>
      </form>
    </div>
  );
};
