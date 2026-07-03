import React, { useState, useRef, useEffect } from 'react';
import { useCloudVaultStore } from '../store/useCloudVaultStore';
import { Sparkles, Send, Trash2, ArrowUpRight } from 'lucide-react';

export const AiChatPanel: React.FC = () => {
  const { chatMessages, sendChatMessage, clearChat, chatLoading } = useCloudVaultStore();
  const [inputText, setInputText] = useState('');
  const chatEndRef = useRef<HTMLDivElement>(null);

  const suggestions = [
    "Find my resume",
    "Summarize migration plan",
    "Simulate Security Alert"
  ];

  // Auto-scroll chat history
  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [chatMessages, chatLoading]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (inputText.trim() && !chatLoading) {
      sendChatMessage(inputText.trim());
      setInputText('');
    }
  };

  const handleSuggestionClick = (sug: string) => {
    if (!chatLoading) {
      sendChatMessage(sug);
    }
  };

  return (
    <div className="w-80 border-l border-slate-800/80 bg-slate-900/10 flex flex-col justify-between select-none animate-slide-in shrink-0 z-20">
      
      {/* Header */}
      <div className="p-4 border-b border-slate-800/80 flex items-center justify-between">
        <div className="flex items-center gap-2 text-white">
          <Sparkles className="w-4 h-4 text-brand-400" />
          <h3 className="text-xs font-bold uppercase tracking-wider">AI Chat Assistant</h3>
        </div>
        <button
          onClick={clearChat}
          className="p-1 hover:bg-slate-800 rounded-lg text-slate-500 hover:text-slate-350 transition-all"
          title="Clear Chat Logs"
        >
          <Trash2 className="w-3.5 h-3.5" />
        </button>
      </div>

      {/* Suggestion action pills (only show when chat has fewer logs) */}
      {chatMessages.length <= 1 && (
        <div className="p-4 flex flex-col gap-2 border-b border-slate-850 bg-slate-950/20">
          <span className="text-[9px] text-slate-500 font-bold uppercase tracking-wider pl-1">Suggested Searches</span>
          <div className="flex flex-col gap-1.5">
            {suggestions.map((sug, i) => (
              <button
                key={i}
                onClick={() => handleSuggestionClick(sug)}
                className="w-full text-left p-2 rounded-xl bg-slate-900/40 border border-slate-850 hover:border-slate-700/80 text-[10px] text-slate-350 hover:text-white transition-all flex items-center justify-between group"
              >
                <span>{sug}</span>
                <ArrowUpRight className="w-3 h-3 text-slate-500 group-hover:text-brand-400 transition-colors" />
              </button>
            ))}
          </div>
        </div>
      )}

      {/* Messages logs */}
      <div className="flex-1 p-4 overflow-y-auto flex flex-col gap-4">
        {chatMessages.map(msg => (
          <div
            key={msg.id}
            className={`flex flex-col gap-1 max-w-[85%] ${
              msg.role === 'user' ? 'self-end items-end' : 'self-start items-start'
            }`}
          >
            <div className={`p-3 rounded-2xl text-[11px] leading-relaxed ${
              msg.role === 'user'
                ? 'bg-brand-600 text-white rounded-br-none'
                : 'bg-slate-950/40 border border-slate-850 text-slate-300 rounded-bl-none'
            }`}>
              <p className="whitespace-pre-wrap">{msg.content}</p>
            </div>
            <span className="text-[8px] text-slate-600 font-medium px-1">
              {msg.role === 'user' ? 'You' : 'Assistant'}
            </span>
          </div>
        ))}

        {/* Loading skeletons */}
        {chatLoading && (
          <div className="self-start flex flex-col gap-1 max-w-[85%] items-start">
            <div className="p-3 rounded-2xl bg-slate-950/40 border border-slate-850 rounded-bl-none flex items-center gap-1">
              <span className="w-1.5 h-1.5 rounded-full bg-slate-500 animate-bounce" style={{ animationDelay: '0ms' }} />
              <span className="w-1.5 h-1.5 rounded-full bg-slate-500 animate-bounce" style={{ animationDelay: '150ms' }} />
              <span className="w-1.5 h-1.5 rounded-full bg-slate-500 animate-bounce" style={{ animationDelay: '300ms' }} />
            </div>
          </div>
        )}
        <div ref={chatEndRef} />
      </div>

      {/* Input box */}
      <form onSubmit={handleSubmit} className="p-4 border-t border-slate-850 bg-slate-900/60 flex items-center gap-2">
        <input
          type="text"
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          placeholder="Ask AI Search or type query..."
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
