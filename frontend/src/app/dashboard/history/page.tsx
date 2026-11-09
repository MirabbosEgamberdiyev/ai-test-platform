"use client";

import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import { useI18n } from "@/lib/i18n";
import { History, ArrowRight } from "lucide-react";

export default function HistoryPage() {
  const { t } = useI18n();

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div>
        <h1 className="text-2xl font-bold tracking-tight text-white">{t.nav.testHistory}</h1>
        <p className="text-sm text-slate-400 mt-1">
          Long-term regression analytics, historical run trends, and artifact retention management.
        </p>
      </div>

      <Card className="p-8 text-center space-y-3">
        <History className="h-10 w-10 text-indigo-400 mx-auto" />
        <h3 className="text-base font-semibold text-white">Immutable Run History</h3>
        <p className="text-xs text-slate-400 max-w-md mx-auto">
          All test executions, logs, and screenshots are permanently logged to immutable audit records with configurable 90-day retention policies.
        </p>
        <Link href="/dashboard/tests">
          <Button size="sm" variant="outline">
            View Recent Test Runs
            <ArrowRight className="ml-1.5 h-3.5 w-3.5" />
          </Button>
        </Link>
      </Card>
    </div>
  );
}
