"use client";

import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { useI18n } from "@/lib/i18n";
import { ListTodo, PlusCircle, CheckCircle2 } from "lucide-react";

export default function TestCasesPage() {
  const { t } = useI18n();

  const standardTestTemplates = [
    { title: "Homepage Navigation & Title Check", category: "FUNCTIONAL", priority: "HIGH", desc: "Verify initial 200 OK HTTP response and correct DOM title tag." },
    { title: "Public Route & Navigation Integrity", category: "FUNCTIONAL", priority: "MEDIUM", desc: "Crawl all public navigation links and verify no 404 broken routes." },
    { title: "Interactive Forms & Input Validation", category: "UI_UX", priority: "HIGH", desc: "Inspect all interactive forms and submit buttons for proper validation states." },
    { title: "Mobile & Tablet Responsive Layout", category: "RESPONSIVE", priority: "HIGH", desc: "Audit viewports 375px and 768px for zero horizontal overflow." },
    { title: "WCAG 2.2 AA Accessibility Audit", category: "ACCESSIBILITY", priority: "MEDIUM", desc: "Check image alt tags, form label associations, and color contrast." },
    { title: "Security Headers & TLS Transport", category: "SECURITY", priority: "MEDIUM", desc: "Validate HSTS, X-Content-Type-Options, CSP, and secure cookie flags." },
  ];

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-white">{t.nav.testCases}</h1>
          <p className="text-sm text-slate-400 mt-1">
            Universal AI test case library across functional, responsive, accessibility, and security scopes.
          </p>
        </div>

        <Link href="/dashboard/tests/new">
          <Button className="bg-indigo-600 hover:bg-indigo-500 text-white">
            <PlusCircle className="mr-2 h-4 w-4" />
            {t.nav.newTest}
          </Button>
        </Link>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {standardTestTemplates.map((tc, idx) => (
          <Card key={idx} className="p-4 space-y-3">
            <div className="flex items-start justify-between">
              <h3 className="text-sm font-semibold text-white">{tc.title}</h3>
              <Badge variant="outline" className="text-[10px]">
                {tc.category}
              </Badge>
            </div>
            <p className="text-xs text-slate-400">{tc.desc}</p>
            <div className="pt-2 border-t border-slate-800 flex items-center justify-between text-[11px] text-slate-500">
              <span>Priority: <strong className="text-slate-300">{tc.priority}</strong></span>
              <span className="text-emerald-400 flex items-center gap-1">
                <CheckCircle2 className="h-3 w-3" />
                Active
              </span>
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
}
