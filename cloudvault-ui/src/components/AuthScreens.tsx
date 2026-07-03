import React, { useState } from 'react';
import { useCloudVaultStore } from '../store/useCloudVaultStore';
import { motion } from 'framer-motion';
import { Mail, Lock, ShieldCheck, HelpCircle } from 'lucide-react';

export const AuthScreens: React.FC = () => {
  const { currentView, setView, loginUser, twoFactorRequired, verify2FA } = useCloudVaultStore();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [code, setCode] = useState('');
  const [error, setError] = useState('');

  const handleLoginSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (email && password) {
      setError('');
      // Simulate OAuth/JWT login success and prompt 2FA
      loginUser(email, name || 'Aditya Kudipudi');
    } else {
      setError('Please provide valid authentication credentials.');
    }
  };

  const handle2FASubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const verified = verify2FA(code);
    if (!verified) {
      setError('Invalid 2FA code. Hint: Use code "123456" for verification simulation.');
    } else {
      setError('');
    }
  };

  if (twoFactorRequired) {
    return (
      <div className="min-h-screen bg-slate-950 flex items-center justify-center p-6 relative font-sans">
        <div className="absolute inset-0 bg-[linear-gradient(to_right,#0f172a_1px,transparent_1px),linear-gradient(to_bottom,#0f172a_1px,transparent_1px)] bg-[size:4rem_4rem] opacity-20 pointer-events-none" />
        
        <motion.div
          initial={{ scale: 0.95, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          className="w-full max-w-md bg-slate-900 border border-slate-800 rounded-3xl p-8 shadow-2xl flex flex-col gap-6 z-10"
        >
          <div className="flex flex-col gap-2 items-center text-center">
            <div className="p-3 bg-brand-500/10 border border-brand-500/25 rounded-full text-brand-400">
              <ShieldCheck className="w-6 h-6" />
            </div>
            <h2 className="text-xl font-bold text-white">Two-Factor Authentication</h2>
            <p className="text-xs text-slate-400 max-w-xs leading-relaxed">
              We dispatched an authentication passcode token to your authentication device. Please verify below.
            </p>
          </div>

          <form onSubmit={handle2FASubmit} className="flex flex-col gap-4">
            <div className="flex flex-col gap-1.5">
              <label className="text-[10px] text-slate-500 font-bold uppercase tracking-wider pl-1">Passcode Token</label>
              <input
                type="text"
                required
                value={code}
                onChange={(e) => setCode(e.target.value)}
                placeholder="Enter 6-digit code (Use 123456)"
                className="w-full bg-slate-950 border border-slate-800 focus:border-brand-500/50 rounded-xl px-4 py-2.5 text-xs text-white placeholder-slate-500 outline-none text-center tracking-widest font-mono transition-all"
              />
            </div>

            {error && <span className="text-[10px] text-rose-500 pl-1 font-medium">{error}</span>}

            <button
              type="submit"
              className="w-full py-2.5 rounded-xl bg-brand-600 hover:bg-brand-500 text-xs font-bold text-white shadow-lg shadow-brand-500/10 transition-all duration-200 mt-2"
            >
              Verify Identity
            </button>
          </form>

          <span className="text-[10px] text-slate-500 text-center flex items-center justify-center gap-1">
            <HelpCircle className="w-3 h-3 text-slate-600" />
            Simulating standard Google Authenticator TOTP token checks.
          </span>
        </motion.div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center p-6 relative font-sans">
      <div className="absolute inset-0 bg-[linear-gradient(to_right,#0f172a_1px,transparent_1px),linear-gradient(to_bottom,#0f172a_1px,transparent_1px)] bg-[size:4rem_4rem] opacity-20 pointer-events-none" />
      
      <motion.div
        initial={{ scale: 0.95, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        className="w-full max-w-md bg-slate-900 border border-slate-800 rounded-3xl p-8 shadow-2xl flex flex-col gap-6 z-10"
      >
        <div className="flex flex-col gap-1 text-center">
          <h2 className="text-xl font-bold text-white">
            {currentView === 'login' ? 'Welcome back' : 'Create account'}
          </h2>
          <p className="text-xs text-slate-400">
            {currentView === 'login'
              ? 'Enter email credentials to verify your JWT token sessions'
              : 'Register your key configurations on our secure database'}
          </p>
        </div>

        <form onSubmit={handleLoginSubmit} className="flex flex-col gap-4">
          {currentView === 'signup' && (
            <div className="flex flex-col gap-1.5">
              <label className="text-[10px] text-slate-500 font-bold uppercase tracking-wider pl-1">Full Name</label>
              <input
                type="text"
                required
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Your Name"
                className="w-full bg-slate-950 border border-slate-800 focus:border-brand-500/50 rounded-xl px-4 py-2.5 text-xs text-white placeholder-slate-500 outline-none transition-all"
              />
            </div>
          )}

          <div className="flex flex-col gap-1.5">
            <label className="text-[10px] text-slate-500 font-bold uppercase tracking-wider pl-1">Email Address</label>
            <div className="relative">
              <Mail className="w-3.5 h-3.5 text-slate-600 absolute left-3.5 top-1/2 -translate-y-1/2" />
              <input
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="you@domain.com"
                className="w-full bg-slate-950 border border-slate-800 focus:border-brand-500/50 rounded-xl pl-10 pr-4 py-2.5 text-xs text-white placeholder-slate-500 outline-none transition-all"
              />
            </div>
          </div>

          <div className="flex flex-col gap-1.5">
            <label className="text-[10px] text-slate-500 font-bold uppercase tracking-wider pl-1">Password</label>
            <div className="relative">
              <Lock className="w-3.5 h-3.5 text-slate-600 absolute left-3.5 top-1/2 -translate-y-1/2" />
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full bg-slate-950 border border-slate-800 focus:border-brand-500/50 rounded-xl pl-10 pr-4 py-2.5 text-xs text-white placeholder-slate-500 outline-none transition-all"
              />
            </div>
          </div>

          {error && <span className="text-[10px] text-rose-500 pl-1 font-medium">{error}</span>}

          <button
            type="submit"
            className="w-full py-2.5 rounded-xl bg-brand-600 hover:bg-brand-500 text-xs font-bold text-white shadow-lg shadow-brand-500/10 transition-all duration-200 mt-2"
          >
            {currentView === 'login' ? 'Continue with Email' : 'Register Account'}
          </button>
        </form>

        <div className="relative flex items-center justify-center my-1">
          <div className="border-t border-slate-800/80 w-full" />
          <span className="bg-slate-900 px-3 text-[9px] text-slate-500 font-bold uppercase tracking-wider absolute">or</span>
        </div>

        {/* Social Authentication buttons */}
        <div className="grid grid-cols-2 gap-3">
          <button
            onClick={() => loginUser('google@cloudvault.com', 'Google Collaborator')}
            className="px-4 py-2 rounded-xl bg-slate-950 border border-slate-850 hover:bg-slate-900 text-[10px] font-bold text-slate-300 hover:text-white transition-all flex items-center justify-center gap-2"
          >
            <svg className="w-3.5 h-3.5" viewBox="0 0 24 24">
              <path fill="currentColor" d="M12.24 10.285V13.4h6.887C18.2 15.614 15.645 18 12.24 18c-3.86 0-7-3.14-7-7s3.14-7 7-7c1.7 0 3.25.61 4.45 1.615l2.42-2.42C17.26 1.455 14.9 0 12.24 0 5.58 0 0 5.58 0 12.24s5.58 12.24 12.24 12.24c6.96 0 11.57-4.89 11.57-11.79 0-.795-.075-1.57-.22-2.31H12.24z"/>
            </svg>
            Google OAuth
          </button>
          <button
            onClick={() => loginUser('github@cloudvault.com', 'GitHub Collaborator')}
            className="px-4 py-2 rounded-xl bg-slate-950 border border-slate-850 hover:bg-slate-900 text-[10px] font-bold text-slate-300 hover:text-white transition-all flex items-center justify-center gap-2"
          >
            <svg className="w-3.5 h-3.5" viewBox="0 0 24 24">
              <path fill="currentColor" d="M12 0C5.37 0 0 5.37 0 12c0 5.3 3.438 9.8 8.205 11.385.6.11.82-.26.82-.577v-2.234c-3.338.724-4.042-1.61-4.042-1.61C4.422 18.07 3.633 17.7 3.633 17.7c-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22v3.293c0 .319.22.694.825.576C20.565 21.795 24 17.3 24 12c0-6.63-5.37-12-12-12z"/>
            </svg>
            GitHub OAuth
          </button>
        </div>

        {/* Form link redirect toggle */}
        <div className="text-center mt-2">
          {currentView === 'login' ? (
            <span className="text-[10px] text-slate-500">
              New to the platform?{' '}
              <button
                type="button"
                onClick={() => setView('signup')}
                className="text-brand-450 hover:underline font-bold"
              >
                Sign Up
              </button>
            </span>
          ) : (
            <span className="text-[10px] text-slate-500">
              Already have an account?{' '}
              <button
                type="button"
                onClick={() => setView('login')}
                className="text-brand-450 hover:underline font-bold"
              >
                Sign In
              </button>
            </span>
          )}
        </div>

      </motion.div>
    </div>
  );
};
