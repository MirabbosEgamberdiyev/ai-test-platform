"use client";

import React, { useState, useRef, useEffect } from "react";
import { useI18n } from "@/lib/i18n";
import { useTheme } from "@/lib/theme";
import { SupportedLocale } from "@/lib/i18n/types";
import { Globe, Sun, Moon, Check, ChevronDown } from "lucide-react";
import { cn } from "@/lib/utils";

export function LanguageSwitcher({ className }: { className?: string }) {
  const { locale, setLocale, supportedLocales } = useI18n();
  const [open, setOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  const current = supportedLocales.find((l) => l.code === locale) || supportedLocales[0];

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (ref.current && !ref.current.contains(e.target as Node)) {
        setOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <div className={cn("relative inline-block text-left", className)} ref={ref}>
      <button
        onClick={() => setOpen(!open)}
        className="inline-flex items-center space-x-1.5 px-2.5 py-1.5 rounded-lg border border-slate-700 bg-slate-900/80 hover:bg-slate-800 text-xs font-medium text-slate-200 focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-colors"
        aria-label="Select Language"
      >
        <span className="text-sm">{current.flag}</span>
        <span className="hidden sm:inline font-mono">{current.code}</span>
        <ChevronDown className="h-3 w-3 text-slate-400" />
      </button>

      {open && (
        <div className="absolute right-0 mt-2 w-44 rounded-xl border border-slate-700 bg-slate-900 shadow-xl z-50 py-1 text-xs backdrop-blur-md">
          {supportedLocales.map((l) => {
            const isSelected = l.code === locale;
            return (
              <button
                key={l.code}
                onClick={() => {
                  setLocale(l.code);
                  setOpen(false);
                }}
                className={cn(
                  "w-full text-left px-3 py-2 flex items-center justify-between hover:bg-slate-800 transition-colors",
                  isSelected ? "text-indigo-400 font-semibold" : "text-slate-300"
                )}
              >
                <div className="flex items-center space-x-2">
                  <span>{l.flag}</span>
                  <span>{l.nativeName}</span>
                </div>
                {isSelected && <Check className="h-3.5 w-3.5 text-indigo-400" />}
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
}

export function ThemeSwitcher({ className }: { className?: string }) {
  const { theme, toggleTheme } = useTheme();

  return (
    <button
      onClick={toggleTheme}
      className={cn(
        "inline-flex items-center justify-center p-2 rounded-lg border border-slate-700 bg-slate-900/80 hover:bg-slate-800 text-slate-300 hover:text-white transition-colors focus:outline-none focus:ring-2 focus:ring-indigo-500",
        className
      )}
      aria-label="Toggle Theme Mode"
      title={`Switch to ${theme === "dark" ? "light" : "dark"} mode`}
    >
      {theme === "dark" ? (
        <Sun className="h-4 w-4 text-amber-400" />
      ) : (
        <Moon className="h-4 w-4 text-indigo-400" />
      )}
    </button>
  );
}
