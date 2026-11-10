"use client";

import { useState, useEffect } from "react";
import { Sidebar } from "@/components/layout/sidebar";
import { Sparkles, Shield, Menu, X } from "lucide-react";
import { useI18n } from "@/lib/i18n";
import { LanguageSwitcher, ThemeSwitcher } from "@/components/ui/controls";
import { ensureDemoSession } from "@/lib/api";

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const { t } = useI18n();
  const [tenantId, setTenantId] = useState<string>("tenant-demo-001");
  const [mobileSidebarOpen, setMobileSidebarOpen] = useState(false);

  useEffect(() => {
    if (typeof window !== "undefined") {
      const stored = localStorage.getItem("currentTenantId");
      if (stored && stored !== "undefined" && stored !== "null") {
        setTenantId(stored);
      } else {
        localStorage.setItem("currentTenantId", "tenant-demo-001");
        setTenantId("tenant-demo-001");
      }
      // Ensure active authentication session so API requests never fail with 401/403
      ensureDemoSession().then(() => {
        const tId = localStorage.getItem("currentTenantId");
        if (tId && tId !== "undefined" && tId !== "null") {
          setTenantId(tId);
        }
      });
    }
  }, []);

  return (
    <div className="flex min-h-screen bg-slate-950 text-slate-100">
      {/* Desktop Sidebar */}
      <div className="hidden md:block">
        <Sidebar />
      </div>

      {/* Mobile Drawer Backdrop & Sidebar */}
      {mobileSidebarOpen && (
        <div className="fixed inset-0 z-50 md:hidden flex">
          <div
            className="fixed inset-0 bg-slate-950/80 backdrop-blur-sm"
            onClick={() => setMobileSidebarOpen(false)}
          />
          <div className="relative z-50 w-72 max-w-[85vw] bg-slate-950 shadow-2xl">
            <Sidebar onClose={() => setMobileSidebarOpen(false)} />
          </div>
        </div>
      )}

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col min-w-0">
        <header className="h-16 border-b border-slate-800 bg-slate-950/80 backdrop-blur px-4 sm:px-8 flex items-center justify-between sticky top-0 z-40">
          <div className="flex items-center space-x-3">
            {/* Mobile Sidebar Hamburger */}
            <button
              onClick={() => setMobileSidebarOpen(true)}
              className="md:hidden p-2 rounded-lg border border-slate-800 bg-slate-900 text-slate-300"
              aria-label="Open sidebar menu"
            >
              <Menu className="h-5 w-5" />
            </button>

            <span className="hidden sm:inline text-xs font-semibold uppercase tracking-wider text-slate-400">
              {t.nav.workspace}
            </span>
            <span className="text-xs font-mono bg-slate-900 border border-slate-700 px-2 py-1 rounded text-indigo-300 truncate max-w-[140px] sm:max-w-none">
              {!tenantId || tenantId === "undefined" || tenantId === "null" ? "Enterprise Workspace" : tenantId}
            </span>
          </div>

          <div className="flex items-center space-x-2 sm:space-x-4 text-xs text-slate-400">
            <div className="hidden lg:flex items-center space-x-1.5 text-emerald-400 bg-emerald-950/30 border border-emerald-800/40 px-2.5 py-1 rounded-full">
              <Shield className="h-3.5 w-3.5" />
              <span>SSRF Guard: Active</span>
            </div>
            <div className="hidden xl:flex items-center space-x-1.5 text-indigo-400 bg-indigo-950/30 border border-indigo-800/40 px-2.5 py-1 rounded-full">
              <Sparkles className="h-3.5 w-3.5" />
              <span>AI Engine: Gemini 1.5 + OpenAI Fallback</span>
            </div>
            <div className="md:hidden flex items-center space-x-1.5">
              <LanguageSwitcher />
              <ThemeSwitcher />
            </div>
          </div>
        </header>

        <main className="flex-1 p-4 sm:p-6 lg:p-8 overflow-y-auto">
          {children}
        </main>
      </div>
    </div>
  );
}
