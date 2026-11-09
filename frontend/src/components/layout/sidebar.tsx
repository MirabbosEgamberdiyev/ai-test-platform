"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import {
  LayoutDashboard,
  Globe2,
  PlusCircle,
  PlayCircle,
  ListTodo,
  FileBarChart2,
  History,
  Workflow,
  Users2,
  Settings,
  LogOut,
  Bot,
  ShieldCheck,
} from "lucide-react";
import { cn } from "@/lib/utils";
import { clearStoredTokens } from "@/lib/api";
import { useI18n } from "@/lib/i18n";
import { LanguageSwitcher, ThemeSwitcher } from "@/components/ui/controls";

export function Sidebar({ className, onClose }: { className?: string; onClose?: () => void }) {
  const pathname = usePathname();
  const router = useRouter();
  const { t } = useI18n();

  const handleLogout = () => {
    clearStoredTokens();
    router.push("/login");
  };

  const navigation = [
    { name: t.nav.dashboard, href: "/dashboard", icon: LayoutDashboard },
    { name: t.nav.websites, href: "/dashboard/websites", icon: Globe2 },
    { name: t.nav.newTest, href: "/dashboard/tests/new", icon: PlusCircle },
    { name: t.nav.testRuns, href: "/dashboard/tests", icon: PlayCircle },
    { name: t.nav.testCases, href: "/dashboard/test-cases", icon: ListTodo },
    { name: t.nav.aiReports, href: "/dashboard/reports", icon: FileBarChart2 },
    { name: t.nav.testHistory, href: "/dashboard/history", icon: History },
    { name: t.nav.integrations, href: "/dashboard/integrations", icon: Workflow },
    { name: t.nav.team, href: "/dashboard/team", icon: Users2 },
    { name: t.nav.settings, href: "/dashboard/settings", icon: Settings },
  ];

  return (
    <aside className={cn("w-64 border-r border-slate-800 bg-slate-950 flex flex-col justify-between h-screen sticky top-0 overflow-y-auto", className)}>
      <div>
        {/* Brand */}
        <div className="h-16 flex items-center justify-between px-6 border-b border-slate-800 sticky top-0 bg-slate-950/95 backdrop-blur z-10">
          <Link href="/dashboard" className="flex items-center space-x-3" onClick={onClose}>
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-indigo-600 shadow-sm shadow-indigo-600/30">
              <Bot className="h-4 w-4 text-white" />
            </div>
            <span className="font-bold text-base text-white">{t.nav.brand}</span>
          </Link>
          <div className="flex items-center space-x-1.5">
            <LanguageSwitcher />
            <ThemeSwitcher />
          </div>
        </div>

        {/* Links */}
        <nav className="p-3 space-y-1" aria-label="Sidebar Navigation">
          {navigation.map((item) => {
            const isActive = pathname === item.href || (item.href !== "/dashboard" && pathname.startsWith(item.href));
            return (
              <Link
                key={item.name}
                href={item.href}
                onClick={onClose}
                className={cn(
                  "flex items-center space-x-3 px-3 py-2 rounded-lg text-xs font-medium transition-colors",
                  isActive
                    ? "bg-indigo-600/15 text-indigo-400 border border-indigo-500/20 shadow-sm shadow-indigo-500/10"
                    : "text-slate-400 hover:text-slate-200 hover:bg-slate-900"
                )}
              >
                <item.icon className="h-4 w-4 flex-shrink-0" />
                <span className="truncate">{item.name}</span>
              </Link>
            );
          })}
        </nav>
      </div>

      {/* Footer Info / Logout */}
      <div className="p-4 border-t border-slate-800 space-y-3 sticky bottom-0 bg-slate-950">
        <div className="rounded-lg bg-slate-900 p-2.5 border border-slate-800 text-xs">
          <div className="flex items-center space-x-2 text-emerald-400 mb-0.5">
            <ShieldCheck className="h-3.5 w-3.5 flex-shrink-0" />
            <span className="font-semibold truncate">Universal AI Web Testing</span>
          </div>
          <p className="text-[11px] text-slate-400">Playwright & SSRF Guard Active</p>
        </div>

        <button
          onClick={handleLogout}
          className="flex w-full items-center space-x-3 px-3 py-2 text-xs font-medium text-slate-400 hover:text-red-400 hover:bg-red-500/10 rounded-lg transition-colors"
          aria-label={t.nav.signOut}
        >
          <LogOut className="h-4 w-4 flex-shrink-0" />
          <span>{t.nav.signOut}</span>
        </button>
      </div>
    </aside>
  );
}
