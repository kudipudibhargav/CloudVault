import React, { useState } from 'react';
import { useCloudVaultStore } from '../store/useCloudVaultStore';
import { motion, AnimatePresence } from 'framer-motion';
import { Shield, Key, LayoutGrid, CheckCircle2, ChevronRight } from 'lucide-react';

export const OnboardingWizard: React.FC = () => {
  const { completeOnboarding } = useCloudVaultStore();
  const [step, setStep] = useState(1);
  const [wsName, setWsName] = useState('My Workspace');
  const [encryption, setEncryption] = useState<'AES-256' | 'Zero-Knowledge'>('Zero-Knowledge');
  const [selectedTheme, setSelectedTheme] = useState<'dark' | 'light'>('dark');

  const nextStep = () => setStep(prev => prev + 1);

  const slideVariants = {
    initial: { x: 50, opacity: 0 },
    animate: { x: 0, opacity: 1, transition: { duration: 0.4, ease: 'easeOut' } },
    exit: { x: -50, opacity: 0, transition: { duration: 0.3 } }
  } as any;

  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center p-6 relative font-sans">
      <div className="absolute inset-0 bg-[linear-gradient(to_right,#0f172a_1px,transparent_1px),linear-gradient(to_bottom,#0f172a_1px,transparent_1px)] bg-[size:4rem_4rem] opacity-20 pointer-events-none" />
      
      <div className="w-full max-w-lg bg-slate-900 border border-slate-800 rounded-3xl p-8 shadow-2xl flex flex-col justify-between min-h-[480px] z-10">
        
        {/* Top stepper indicator */}
        <div className="flex items-center justify-between mb-8">
          <span className="text-[10px] text-brand-400 font-bold uppercase tracking-widest">Setup Configuration</span>
          <div className="flex items-center gap-1.5">
            {[1, 2, 3, 4].map(s => (
              <span
                key={s}
                className={`h-1.5 rounded-full transition-all duration-300 ${
                  s === step ? 'w-6 bg-brand-500' : 'w-1.5 bg-slate-805'
                }`}
              />
            ))}
          </div>
        </div>

        {/* Dynamic step view */}
        <div className="flex-1 flex flex-col justify-center">
          <AnimatePresence mode="wait">
            {step === 1 && (
              <motion.div
                key="step-1"
                variants={slideVariants}
                initial="initial"
                animate="animate"
                exit="exit"
                className="flex flex-col gap-4"
              >
                <div className="p-3 bg-brand-500/10 border border-brand-500/25 rounded-2xl w-fit text-brand-400">
                  <LayoutGrid className="w-5 h-5" />
                </div>
                <div>
                  <h2 className="text-lg font-bold text-white">Create your Workspace</h2>
                  <p className="text-[11px] text-slate-400 mt-1">Workspaces isolate security keys, quota allocations, and collaborator permissions.</p>
                </div>
                <input
                  type="text"
                  value={wsName}
                  onChange={(e) => setWsName(e.target.value)}
                  placeholder="Workspace Name"
                  className="w-full bg-slate-950 border border-slate-800 focus:border-brand-500/50 rounded-xl px-4 py-2.5 text-xs text-white placeholder-slate-500 outline-none transition-all mt-2"
                />
              </motion.div>
            )}

            {step === 2 && (
              <motion.div
                key="step-2"
                variants={slideVariants}
                initial="initial"
                animate="animate"
                exit="exit"
                className="flex flex-col gap-4"
              >
                <div className="p-3 bg-brand-500/10 border border-brand-500/25 rounded-2xl w-fit text-brand-400">
                  <Shield className="w-5 h-5" />
                </div>
                <div>
                  <h2 className="text-lg font-bold text-white">Select theme profile</h2>
                  <p className="text-[11px] text-slate-400 mt-1">Configure layout appearance preferences for this device session.</p>
                </div>
                
                <div className="grid grid-cols-2 gap-4 mt-2">
                  <button
                    onClick={() => setSelectedTheme('dark')}
                    className={`p-4 rounded-2xl border text-left transition-all ${
                      selectedTheme === 'dark'
                        ? 'bg-brand-600/5 border-brand-500/40 text-brand-400 shadow-md shadow-brand-500/5'
                        : 'bg-slate-950/40 border-slate-850 text-slate-400 hover:border-slate-800'
                    }`}
                  >
                    <span className="text-xs font-bold block text-white">Dark Mode</span>
                    <span className="text-[9px] text-slate-500 mt-1 block">Premium sleek obsidian aesthetic</span>
                  </button>
                  <button
                    onClick={() => setSelectedTheme('light')}
                    className={`p-4 rounded-2xl border text-left transition-all ${
                      selectedTheme === 'light'
                        ? 'bg-brand-600/5 border-brand-500/40 text-brand-400 shadow-md shadow-brand-500/5'
                        : 'bg-slate-950/40 border-slate-850 text-slate-400 hover:border-slate-800'
                    }`}
                  >
                    <span className="text-xs font-bold block text-white">Light Mode</span>
                    <span className="text-[9px] text-slate-500 mt-1 block">High contrast developer aesthetic</span>
                  </button>
                </div>
              </motion.div>
            )}

            {step === 3 && (
              <motion.div
                key="step-3"
                variants={slideVariants}
                initial="initial"
                animate="animate"
                exit="exit"
                className="flex flex-col gap-4"
              >
                <div className="p-3 bg-brand-500/10 border border-brand-500/25 rounded-2xl w-fit text-brand-400">
                  <Key className="w-5 h-5" />
                </div>
                <div>
                  <h2 className="text-lg font-bold text-white">Encryption preferences</h2>
                  <p className="text-[11px] text-slate-400 mt-1">Configure client cryptographic key models for uploaded binary objects.</p>
                </div>

                <div className="flex flex-col gap-3 mt-2">
                  <button
                    onClick={() => setEncryption('Zero-Knowledge')}
                    className={`p-3.5 rounded-2xl border text-left transition-all ${
                      encryption === 'Zero-Knowledge'
                        ? 'bg-brand-600/5 border-brand-500/40 text-brand-400'
                        : 'bg-slate-950/40 border-slate-850 text-slate-400 hover:border-slate-800'
                    }`}
                  >
                    <div className="flex items-center justify-between">
                      <span className="text-xs font-bold text-white">Zero-Knowledge End-to-End</span>
                      <span className="px-2 py-0.5 rounded bg-emerald-600/10 border border-emerald-500/25 text-[8px] font-bold text-emerald-400 uppercase tracking-wide">Secure</span>
                    </div>
                    <span className="text-[9px] text-slate-500 mt-1 block leading-relaxed">
                      Files are encrypted on your local hardware node. No unencrypted content ever reaches S3 blocks.
                    </span >
                  </button>
                  <button
                    onClick={() => setEncryption('AES-256')}
                    className={`p-3.5 rounded-2xl border text-left transition-all ${
                      encryption === 'AES-256'
                        ? 'bg-brand-600/5 border-brand-500/40 text-brand-400'
                        : 'bg-slate-950/40 border-slate-850 text-slate-400 hover:border-slate-800'
                    }`}
                  >
                    <div className="flex items-center justify-between">
                      <span className="text-xs font-bold text-white">AES-256 Server-Side</span>
                      <span className="px-2 py-0.5 rounded bg-amber-600/10 border border-amber-500/25 text-[8px] font-bold text-amber-400 uppercase tracking-wide">Standard</span>
                    </div>
                    <span className="text-[9px] text-slate-500 mt-1 block leading-relaxed">
                      Server encrypts blocks before saving. Ideal for high performance zero-copy assemblies search.
                    </span>
                  </button>
                </div>
              </motion.div>
            )}

            {step === 4 && (
              <motion.div
                key="step-4"
                variants={slideVariants}
                initial="initial"
                animate="animate"
                exit="exit"
                className="flex flex-col gap-4 text-center items-center py-6"
              >
                <div className="p-4 bg-brand-600/10 rounded-full border border-brand-500/25 text-brand-400 animate-pulse">
                  <CheckCircle2 className="w-10 h-10" />
                </div>
                <div>
                  <h2 className="text-xl font-bold text-white">All systems configured</h2>
                  <p className="text-xs text-slate-400 mt-2 max-w-sm mx-auto leading-relaxed">
                    Your workspace **{wsName}** has been successfully initialized using **{encryption}** encryption standards.
                  </p>
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>

        {/* Bottom actions */}
        <div className="flex items-center justify-end gap-3 border-t border-slate-800/60 pt-6 mt-8">
          {step > 1 && step < 4 && (
            <button
              onClick={() => setStep(prev => prev - 1)}
              className="px-4 py-2 text-xs font-semibold text-slate-400 hover:text-slate-200 transition-all"
            >
              Back
            </button>
          )}

          {step < 4 ? (
            <button
              onClick={nextStep}
              className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-brand-600 hover:bg-brand-500 text-xs font-bold text-white shadow-lg shadow-brand-500/10 transition-all duration-200"
            >
              Continue
              <ChevronRight className="w-4 h-4 text-white" />
            </button>
          ) : (
            <button
              onClick={() => completeOnboarding(wsName, encryption)}
              className="w-full py-3 rounded-xl bg-brand-600 hover:bg-brand-500 text-xs font-bold text-white shadow-lg shadow-brand-600/20 transition-all duration-200"
            >
              Open Dashboard Portal
            </button>
          )}
        </div>

      </div>
    </div>
  );
};
