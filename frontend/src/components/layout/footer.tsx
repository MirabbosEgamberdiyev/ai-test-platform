"use client";

import Link from "next/link";
import { Sparkles, Shield, Lock, CheckCircle2 } from "lucide-react";
import { useI18n } from "@/lib/i18n";

export function Footer() {
  const { t } = useI18n();

  return (
    <footer className="border-t border-slate-800 bg-slate-950 py-12 text-slate-400 text-sm">
      <div className="container mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 grid grid-cols-1 md:grid-cols-4 gap-8">
        <div className="space-y-3">
          <div className="flex items-center space-x-2 text-white font-bold text-lg">
            <Sparkles className="h-5 w-5 text-indigo-400" />
            <span>AiTestOps Platform</span>
          </div>
          <p className="text-xs text-slate-400 leading-relaxed">
            {t.footer.brandDesc}
          </p>
        </div>

        <div>
          <h4 className="font-semibold text-slate-200 text-xs uppercase tracking-wider mb-3">
            {t.footer.pillarsTitle}
          </h4>
          <ul className="space-y-2 text-xs">
            <li className="flex items-center space-x-1.5">
              <CheckCircle2 className="h-3.5 w-3.5 text-indigo-400" />
              <span>{t.footer.pillar1}</span>
            </li>
            <li className="flex items-center space-x-1.5">
              <CheckCircle2 className="h-3.5 w-3.5 text-indigo-400" />
              <span>{t.footer.pillar2}</span>
            </li>
            <li className="flex items-center space-x-1.5">
              <CheckCircle2 className="h-3.5 w-3.5 text-indigo-400" />
              <span>{t.footer.pillar3}</span>
            </li>
            <li className="flex items-center space-x-1.5">
              <CheckCircle2 className="h-3.5 w-3.5 text-indigo-400" />
              <span>{t.footer.pillar4}</span>
            </li>
          </ul>
        </div>

        <div>
          <h4 className="font-semibold text-slate-200 text-xs uppercase tracking-wider mb-3">
            {t.footer.complianceTitle}
          </h4>
          <ul className="space-y-2 text-xs">
            <li className="flex items-center space-x-1.5">
              <Shield className="h-3.5 w-3.5 text-emerald-400" />
              <span>{t.footer.comp1}</span>
            </li>
            <li className="flex items-center space-x-1.5">
              <Lock className="h-3.5 w-3.5 text-emerald-400" />
              <span>{t.footer.comp2}</span>
            </li>
            <li className="flex items-center space-x-1.5">
              <Lock className="h-3.5 w-3.5 text-emerald-400" />
              <span>{t.footer.comp3}</span>
            </li>
            <li className="flex items-center space-x-1.5">
              <Shield className="h-3.5 w-3.5 text-emerald-400" />
              <span>{t.footer.comp4}</span>
            </li>
          </ul>
        </div>

        <div>
          <h4 className="font-semibold text-slate-200 text-xs uppercase tracking-wider mb-3">
            {t.footer.docsTitle}
          </h4>
          <ul className="space-y-2 text-xs">
            <li>
              <a href="http://localhost:8085/swagger-ui/index.html" target="_blank" rel="noreferrer" className="hover:text-indigo-400 transition-colors">
                {t.footer.docOpenApi}
              </a>
            </li>
            <li>
              <a href="http://localhost:8085/actuator/health" target="_blank" rel="noreferrer" className="hover:text-indigo-400 transition-colors">
                {t.footer.docActuator}
              </a>
            </li>
            <li>
              <a href="http://localhost:9090" target="_blank" rel="noreferrer" className="hover:text-indigo-400 transition-colors">
                {t.footer.docPrometheus}
              </a>
            </li>
            <li>
              <a href="http://localhost:3001" target="_blank" rel="noreferrer" className="hover:text-indigo-400 transition-colors">
                {t.footer.docGrafana}
              </a>
            </li>
          </ul>
        </div>
      </div>

      <div className="container mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 mt-8 pt-6 border-t border-slate-900 text-center text-xs text-slate-500">
        &copy; {new Date().getFullYear()} {t.footer.rights}
      </div>
    </footer>
  );
}
