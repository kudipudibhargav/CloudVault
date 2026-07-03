import React, { useState } from 'react';
import { useCloudVaultStore } from '../store/useCloudVaultStore';
import { motion } from 'framer-motion';
import { Cloud, Shield, Zap, Sparkles, ArrowRight, Check, Play, Users } from 'lucide-react';
import { Sidebar } from './Sidebar';
import { Navbar } from './Navbar';
import { FileExplorer } from './FileExplorer';
import { Collaborators } from './Collaborators';
import { AnalyticsTab } from './AnalyticsTab';
import { SecurityTab } from './SecurityTab';
import { TrashTab } from './TrashTab';
import { AiChatPanel } from './AiChatPanel';

export const LandingPage: React.FC = () => {
  const { setView, activeTab } = useCloudVaultStore();
  const [activeFaq, setActiveFaq] = useState<number | null>(null);
  const [showAiChat, setShowAiChat] = useState(false);

  // Set currentUser mock for landing page sandbox so profile renders cleanly
  const store = useCloudVaultStore();
  React.useEffect(() => {
    if (!store.currentUser) {
      useCloudVaultStore.setState({
        currentUser: {
          id: 'user-1',
          name: 'Aditya Kudipudi',
          email: 'aditya@cloudvault.com',
          avatarUrl: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=80&fit=crop&q=80'
        }
      });
    }
  }, [store.currentUser]);

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: { staggerChildren: 0.15 }
    }
  };

  const itemVariants = {
    hidden: { y: 30, opacity: 0 },
    visible: {
      y: 0,
      opacity: 1,
      transition: { duration: 0.6, ease: [0.16, 1, 0.3, 1] as any }
    }
  };

  const faqs = [
    { q: "How does the zero-copy upload composition work?", a: "When you upload files in chunks, CloudVault coordinates with the underlying S3 compatible storage to assemble them directly in-storage using the S3 Compose API, completely avoiding server memory buffers." },
    { q: "Is my data encrypted?", a: "Yes. CloudVault supports client-side Zero-Knowledge encryption, meaning your file contents are encrypted before reaching our object storage servers." },
    { q: "What is vector semantic search?", a: "Unlike standard prefix queries, semantic search parses file tags and extracts textual context using an AI pipeline, enabling queries like 'Find my tax files' to match relevant invoices." }
  ];

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 selection:bg-brand-500/30 overflow-x-hidden relative font-sans">
      {/* Decorative Animated Gradients */}
      <div className="absolute top-[-10%] left-[-20%] w-[600px] h-[600px] rounded-full bg-brand-600/10 blur-[150px] pointer-events-none animate-pulse" />
      <div className="absolute bottom-[20%] right-[-10%] w-[500px] h-[500px] rounded-full bg-emerald-600/5 blur-[120px] pointer-events-none" />

      {/* Floating dot background grid */}
      <div className="absolute inset-0 bg-[linear-gradient(to_right,#0f172a_1px,transparent_1px),linear-gradient(to_bottom,#0f172a_1px,transparent_1px)] bg-[size:4rem_4rem] [mask-image:radial-gradient(ellipse_60%_50%_at_50%_0%,#000_70%,transparent_100%)] opacity-30 pointer-events-none" />

      {/* Header / Navbar */}
      <header className="max-w-7xl mx-auto px-6 h-20 flex items-center justify-between relative z-30">
        <div className="flex items-center gap-3">
          <div className="p-2.5 bg-brand-600 rounded-xl shadow-lg shadow-brand-500/20">
            <Cloud className="w-5 h-5 text-white" />
          </div>
          <span className="font-bold text-white tracking-wide text-sm">CloudVault</span>
        </div>
        <div className="flex items-center gap-4">
          <button
            onClick={() => setView('login')}
            className="px-4 py-2 text-xs font-semibold text-slate-400 hover:text-white transition-all duration-200"
          >
            Sign In
          </button>
          <button
            onClick={() => setView('signup')}
            className="px-4 py-2 text-xs font-semibold text-white bg-brand-600 hover:bg-brand-500 rounded-xl shadow-md shadow-brand-500/10 transition-all duration-200"
          >
            Get Started
          </button>
        </div>
      </header>

      {/* Hero Section */}
      <main className="max-w-7xl mx-auto px-6 pt-16 pb-24 relative z-20">
        <motion.div
          variants={containerVariants}
          initial="hidden"
          animate="visible"
          className="text-center flex flex-col items-center gap-6"
        >
          {/* Tag badge */}
          <motion.div
            variants={itemVariants}
            className="inline-flex items-center gap-2 px-3 py-1 bg-brand-500/10 border border-brand-500/25 rounded-full text-[10px] font-bold text-brand-400 uppercase tracking-widest"
          >
            <Sparkles className="w-3.5 h-3.5" />
            Zero-Knowledge Distributed Platform
          </motion.div>

          {/* Title */}
          <motion.h1
            variants={itemVariants}
            className="text-4xl sm:text-6xl md:text-7xl font-extrabold text-white tracking-tight leading-[1.05] max-w-4xl"
          >
            Secure storage for <br />
            <span className="bg-clip-text text-transparent bg-gradient-to-r from-brand-400 via-indigo-400 to-emerald-400">
              Modern Enterprise Teams
            </span>
          </motion.h1>

          {/* Subtitle */}
          <motion.p
            variants={itemVariants}
            className="text-slate-400 text-sm sm:text-base max-w-xl leading-relaxed"
          >
            An elite cloud workspace mapping zero-copy chunk compositions, AOP-driven audit trails, Resilience4j circuit breakers, and vector search indexing.
          </motion.p>

          {/* Action Call to Action */}
          <motion.div variants={itemVariants} className="flex items-center gap-4 mt-2">
            <button
              onClick={() => setView('signup')}
              className="flex items-center gap-2 px-6 py-3 rounded-xl bg-white hover:bg-slate-100 text-xs font-bold text-slate-950 shadow-xl transition-all duration-200"
            >
              Start Free Trial
              <ArrowRight className="w-4 h-4 text-slate-950" />
            </button>
            <button
              onClick={() => {
                const element = document.getElementById('sandbox-dashboard');
                element?.scrollIntoView({ behavior: 'smooth' });
              }}
              className="flex items-center gap-2 px-6 py-3 rounded-xl bg-slate-900 border border-slate-800 hover:border-slate-700 text-xs font-bold text-slate-200 transition-all duration-200"
            >
              <Play className="w-4 h-4 opacity-75" />
              Try Live Demo
            </button>
          </motion.div>

          {/* Interactive Live Sandbox Dashboard Mockup */}
          <motion.div
            id="sandbox-dashboard"
            variants={itemVariants}
            className="w-full max-w-5xl mt-12 p-1.5 rounded-3xl bg-slate-900 border border-slate-800 shadow-2xl relative"
          >
            <div className="absolute inset-0 bg-brand-500/5 blur-3xl pointer-events-none -z-10" />

            <div className="rounded-2xl overflow-hidden border border-slate-800 bg-slate-950 h-[580px] flex flex-col relative text-left">
              {/* Core App Layout inside the frame */}
              <div className="flex-1 flex min-h-0">
                <Sidebar />
                <div className="flex-1 flex flex-col min-w-0 bg-slate-950">
                  <Navbar onToggleAiChat={() => setShowAiChat(prev => !prev)} isAiChatOpen={showAiChat} />
                  
                  <div className="flex-1 flex min-h-0 relative">
                    {/* Active tab route */}
                    {activeTab === 'explorer' && <FileExplorer />}
                    {activeTab === 'analytics' && <AnalyticsTab />}
                    {activeTab === 'security' && <SecurityTab />}
                    {activeTab === 'trash' && <TrashTab />}

                    {/* AI collapsible panel */}
                    {showAiChat && <AiChatPanel />}

                    {/* Team Presence */}
                    <Collaborators />
                  </div>
                </div>
              </div>
            </div>
          </motion.div>
        </motion.div>
      </main>

      {/* Trusted Logos */}
      <section className="border-t border-b border-slate-900/60 bg-slate-950/40 py-10">
        <div className="max-w-7xl mx-auto px-6 text-center">
          <p className="text-[10px] text-slate-500 font-bold uppercase tracking-widest mb-6">Securing asset pipelines at scale</p>
          <div className="flex flex-wrap items-center justify-center gap-12 sm:gap-20 opacity-30 grayscale hover:opacity-50 transition-opacity">
            <span className="text-xs font-black tracking-tighter text-white">APPLE</span>
            <span className="text-xs font-black tracking-tighter text-white">STRIPE</span>
            <span className="text-xs font-black tracking-tighter text-white">VERCEL</span>
            <span className="text-xs font-black tracking-tighter text-white">GITHUB</span>
            <span className="text-xs font-black tracking-tighter text-white">LINEAR</span>
          </div>
        </div>
      </section>

      {/* Key Architectural Pillars */}
      <section className="max-w-7xl mx-auto px-6 py-24">
        <div className="text-center mb-16">
          <h2 className="text-2xl sm:text-4xl font-bold text-white">Engineered for absolute resilience</h2>
          <p className="text-xs text-slate-500 mt-2">Zero compromises. Built for enterprise SLA thresholds.</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div className="p-6 rounded-2xl bg-slate-900/20 border border-slate-850 hover:border-slate-700/80 transition-all flex flex-col gap-4">
            <Zap className="w-6 h-6 text-brand-400" />
            <h3 className="text-sm font-bold text-white">Zero-Copy Composition</h3>
            <p className="text-xs text-slate-400 leading-relaxed">
              Chunk assemblies bypass memory streams by executing composable S3 object instructions, resolving network limits.
            </p>
          </div>

          <div className="p-6 rounded-2xl bg-slate-900/20 border border-slate-850 hover:border-slate-700/80 transition-all flex flex-col gap-4">
            <Shield className="w-6 h-6 text-brand-400" />
            <h3 className="text-sm font-bold text-white">Resilience4j Shielding</h3>
            <p className="text-xs text-slate-400 leading-relaxed">
              Circuit breakers monitor metadata channels, degrading to safe deduplication bypasses when services undergo latency spikes.
            </p>
          </div>

          <div className="p-6 rounded-2xl bg-slate-900/20 border border-slate-850 hover:border-slate-700/80 transition-all flex flex-col gap-4">
            <Users className="w-6 h-6 text-brand-400" />
            <h3 className="text-sm font-bold text-white">Reactive Presence</h3>
            <p className="text-xs text-slate-400 leading-relaxed">
              Horizontal collaboration channels broadcast user cursors and file revisions instantly using Redis Pub/Sub brokers.
            </p>
          </div>
        </div>
      </section>

      {/* Pricing Cards */}
      <section className="max-w-7xl mx-auto px-6 py-16 border-t border-slate-900/60">
        <div className="text-center mb-16">
          <h2 className="text-2xl sm:text-4xl font-bold text-white">Clean, transparent pricing</h2>
          <p className="text-xs text-slate-500 mt-2">Scale quota capacities organically as you grow.</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 max-w-4xl mx-auto">
          {/* Card 1 */}
          <div className="p-6 rounded-2xl bg-slate-900/10 border border-slate-850 flex flex-col justify-between gap-6">
            <div className="flex flex-col gap-2">
              <span className="text-[10px] font-bold uppercase text-slate-500 tracking-wider">Basic</span>
              <h3 className="text-xl font-bold text-white">Free</h3>
              <p className="text-xs text-slate-400">Perfect to test live S3 uploads, folders tree, and sharing.</p>
            </div>
            <ul className="flex flex-col gap-2 text-xs text-slate-400">
              <li className="flex items-center gap-2"><Check className="w-3.5 h-3.5 text-brand-400" /> 10 GB Storage limit</li>
              <li className="flex items-center gap-2"><Check className="w-3.5 h-3.5 text-brand-400" /> Standard searches</li>
              <li className="flex items-center gap-2"><Check className="w-3.5 h-3.5 text-brand-400" /> Version control pointers</li>
            </ul>
            <button onClick={() => setView('signup')} className="w-full py-2 bg-slate-800 hover:bg-slate-700 text-xs font-semibold rounded-xl text-white transition-all">
              Sign Up Free
            </button>
          </div>

          {/* Card 2 */}
          <div className="p-6 rounded-2xl bg-brand-600/5 border border-brand-500/25 flex flex-col justify-between gap-6 relative">
            <span className="absolute top-3 right-3 px-2 py-0.5 rounded bg-brand-600 text-[8px] font-bold uppercase text-white tracking-widest">Popular</span>
            <div className="flex flex-col gap-2">
              <span className="text-[10px] font-bold uppercase text-brand-400 tracking-wider">Pro</span>
              <h3 className="text-xl font-bold text-white">$12<span className="text-xs font-medium text-slate-500"> / mo</span></h3>
              <p className="text-xs text-slate-400">For active engineering teams requiring AI summarization.</p>
            </div>
            <ul className="flex flex-col gap-2 text-xs text-slate-400">
              <li className="flex items-center gap-2"><Check className="w-3.5 h-3.5 text-brand-400" /> 100 GB Storage limit</li>
              <li className="flex items-center gap-2"><Check className="w-3.5 h-3.5 text-brand-400" /> AI content summarization</li>
              <li className="flex items-center gap-2"><Check className="w-3.5 h-3.5 text-brand-400" /> Threaded comments & alerts</li>
            </ul>
            <button onClick={() => setView('signup')} className="w-full py-2 bg-brand-600 hover:bg-brand-500 text-xs font-semibold rounded-xl text-white shadow-lg shadow-brand-600/15 transition-all">
              Upgrade to Pro
            </button>
          </div>

          {/* Card 3 */}
          <div className="p-6 rounded-2xl bg-slate-900/10 border border-slate-850 flex flex-col justify-between gap-6">
            <div className="flex flex-col gap-2">
              <span className="text-[10px] font-bold uppercase text-slate-500 tracking-wider">Enterprise</span>
              <h3 className="text-xl font-bold text-white">Custom</h3>
              <p className="text-xs text-slate-400">Complete compliance, custom SLA, and Dedicated DLQs.</p>
            </div>
            <ul className="flex flex-col gap-2 text-xs text-slate-400">
              <li className="flex items-center gap-2"><Check className="w-3.5 h-3.5 text-brand-400" /> Unlimited allocation</li>
              <li className="flex items-center gap-2"><Check className="w-3.5 h-3.5 text-brand-400" /> Resilience4j metrics dashboard</li>
              <li className="flex items-center gap-2"><Check className="w-3.5 h-3.5 text-brand-400" /> Dedicated RabbitMQ instances</li>
            </ul>
            <button onClick={() => setView('signup')} className="w-full py-2 bg-slate-800 hover:bg-slate-700 text-xs font-semibold rounded-xl text-white transition-all">
              Contact Sales
            </button>
          </div>
        </div>
      </section>

      {/* FAQ Section */}
      <section className="max-w-3xl mx-auto px-6 py-24 border-t border-slate-900/60">
        <h2 className="text-2xl font-bold text-white text-center mb-12">Frequently Asked Questions</h2>
        <div className="flex flex-col gap-3">
          {faqs.map((faq, index) => {
            const isOpen = activeFaq === index;
            return (
              <div key={index} className="border border-slate-850 bg-slate-900/10 rounded-2xl overflow-hidden transition-all">
                <button
                  onClick={() => setActiveFaq(isOpen ? null : index)}
                  className="w-full text-left p-5 flex items-center justify-between text-xs font-bold text-slate-200 hover:text-white"
                >
                  <span>{faq.q}</span>
                  <span className="text-slate-500">{isOpen ? '−' : '+'}</span>
                </button>
                {isOpen && (
                  <div className="px-5 pb-5 text-xs text-slate-400 leading-relaxed border-t border-slate-850/50 pt-3">
                    {faq.a}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-slate-900/80 bg-slate-950 py-12 text-xs text-slate-500">
        <div className="max-w-7xl mx-auto px-6 flex flex-col sm:flex-row items-center justify-between gap-6">
          <div className="flex items-center gap-2.5">
            <div className="p-1.5 bg-slate-900 border border-slate-800 rounded-lg">
              <Cloud className="w-4 h-4 text-brand-400" />
            </div>
            <span className="font-semibold text-slate-400">CloudVault Enterprise</span>
          </div>
          <p>© 2026 CloudVault Inc. Designed for premium resilience.</p>
        </div>
      </footer>
    </div>
  );
};
