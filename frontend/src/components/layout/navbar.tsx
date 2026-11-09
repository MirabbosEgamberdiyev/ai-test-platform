"use client";

import Link from "next/link";
import { useState } from "react";
import { Sparkles, LogIn, UserPlus, Menu, X } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useI18n } from "@/lib/i18n";
import { LanguageSwitcher, ThemeSwitcher } from "@/components/ui/controls";

export function Navbar() {
  const { t } = useI18n();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <header className="sticky top-0 z-50 w-full border-b border-slate-800 bg-slate-950/80 backdrop-blur-md">
      <div className="container mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8">
        <Link href="/" className="flex items-center space-x-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-tr from-indigo-600 to-violet-500 shadow-md shadow-indigo-500/20">
            <Sparkles className="h-5 w-5 text-white" />
          </div>
          <span className="text-xl font-bold tracking-tight text-white">
            AiTest<span className="text-indigo-400">Ops</span>
          </span>
        </Link>

        {/* Desktop Navigation */}
        <nav className="hidden lg:flex items-center space-x-6 text-sm font-medium text-slate-300">
          <Link href="/#features" className="hover:text-indigo-400 transition-colors">
            {t.nav.features}
          </Link>
          <Link href="/#architecture" className="hover:text-indigo-400 transition-colors">
            {t.nav.principles}
          </Link>
          <Link href="/#security" className="hover:text-indigo-400 transition-colors">
            {t.nav.security}
          </Link>
          <Link href="/dashboard" className="text-indigo-400 hover:text-indigo-300 font-semibold transition-colors">
            {t.nav.dashboard}
          </Link>
        </nav>

        {/* Desktop Action Controls */}
        <div className="hidden lg:flex items-center space-x-3">
          <LanguageSwitcher />
          <ThemeSwitcher />
          <Link href="/login">
            <Button variant="ghost" size="sm">
              <LogIn className="mr-1.5 h-4 w-4" />
              {t.nav.signIn}
            </Button>
          </Link>
          <Link href="/register">
            <Button variant="primary" size="sm">
              <UserPlus className="mr-1.5 h-4 w-4" />
              {t.nav.getStarted}
            </Button>
          </Link>
        </div>

        {/* Mobile / Tablet Hamburger Button */}
        <div className="flex lg:hidden items-center space-x-2">
          <LanguageSwitcher />
          <ThemeSwitcher />
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="p-2 rounded-lg border border-slate-700 bg-slate-900 text-slate-200"
            aria-label="Toggle navigation menu"
          >
            {mobileMenuOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
          </button>
        </div>
      </div>

      {/* Mobile / Tablet Drawer */}
      {mobileMenuOpen && (
        <div className="lg:hidden border-b border-slate-800 bg-slate-950 px-4 py-4 space-y-3">
          <Link
            href="/#features"
            onClick={() => setMobileMenuOpen(false)}
            className="block text-sm font-medium text-slate-300 hover:text-white py-1"
          >
            {t.nav.features}
          </Link>
          <Link
            href="/#architecture"
            onClick={() => setMobileMenuOpen(false)}
            className="block text-sm font-medium text-slate-300 hover:text-white py-1"
          >
            {t.nav.principles}
          </Link>
          <Link
            href="/#security"
            onClick={() => setMobileMenuOpen(false)}
            className="block text-sm font-medium text-slate-300 hover:text-white py-1"
          >
            {t.nav.security}
          </Link>
          <Link
            href="/dashboard"
            onClick={() => setMobileMenuOpen(false)}
            className="block text-sm font-medium text-indigo-400 hover:text-indigo-300 py-1"
          >
            {t.nav.dashboard}
          </Link>

          <div className="pt-3 border-t border-slate-800 flex flex-col space-y-2">
            <Link href="/login" onClick={() => setMobileMenuOpen(false)}>
              <Button variant="outline" size="sm" className="w-full">
                <LogIn className="mr-2 h-4 w-4" />
                {t.nav.signIn}
              </Button>
            </Link>
            <Link href="/register" onClick={() => setMobileMenuOpen(false)}>
              <Button size="sm" className="w-full">
                <UserPlus className="mr-2 h-4 w-4" />
                {t.nav.getStarted}
              </Button>
            </Link>
          </div>
        </div>
      )}
    </header>
  );
}
