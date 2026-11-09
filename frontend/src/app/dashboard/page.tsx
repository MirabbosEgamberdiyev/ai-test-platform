"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { useI18n } from "@/lib/i18n";
import { webTestsApi, DashboardStats, WebTestRun } from "@/lib/api";
import {
  Globe2,
  PlusCircle,
  PlayCircle,
  ShieldCheck,
  AlertTriangle,
  CheckCircle2,
  Clock,
  Sparkles,
  ArrowRight,
  TrendingUp,
  Cpu,
} from "lucide-react";

export default function DashboardOverviewPage() {
  const { t } = useI18n();
  const [stats, setStats] = useState<DashboardStats>({
    totalProjects: 0,
    totalWebsites: 0,
    totalTestRuns: 0,
    passedTests: 0,
    failedTests: 0,
    activeExecutions: 0,
    criticalDefects: 0,
    successRatePercentage: 100,
  });
  const [runs, setRuns] = useState<WebTestRun[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      try {
        const [statsData, runsData] = await Promise.allSettled([
          webTestsApi.getDashboardStats(),
          webTestsApi.listRuns(),
        ]);
        if (statsData.status === "fulfilled") {
          setStats(statsData.value);
        }
        if (runsData.status === "fulfilled") {
          setRuns(runsData.value);
        }
      } catch (err) {
        console.error("Dashboard stats error:", err);
      } finally {
        setLoading(false);
      }
    }
    loadData();
    const interval = setInterval(loadData, 10000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="space-y-8 max-w-7xl mx-auto">
      {/* HEADER SECTION */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-white">{t.dashboard.overviewTitle}</h1>
          <p className="text-sm text-slate-400 mt-1">
            {t.dashboard.overviewSubtitle}
          </p>
        </div>

        <div className="flex items-center space-x-3">
          <Link href="/dashboard/websites">
            <Button variant="outline" size="sm">
              <Globe2 className="mr-2 h-4 w-4" />
              {t.nav.websites}
            </Button>
          </Link>
          <Link href="/dashboard/tests/new">
            <Button size="sm" className="bg-indigo-600 hover:bg-indigo-500 text-white">
              <PlusCircle className="mr-2 h-4 w-4" />
              {t.nav.newTest}
            </Button>
          </Link>
        </div>
      </div>

      {/* METRIC TILES (REAL BACKEND METRICS) */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <Card className="p-5">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-slate-400 uppercase tracking-wider">
              {t.dashboard.tokenBudgetTitle}
            </span>
            <div className="p-2 rounded-lg bg-indigo-500/10 text-indigo-400">
              <Globe2 className="h-4 w-4" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline space-x-2">
            <span className="text-2xl font-bold text-white">{stats.totalWebsites}</span>
            <span className="text-xs text-slate-400">{t.dashboard.tokenSoftCap}</span>
          </div>
          <p className="text-xs text-slate-500 mt-1">{t.dashboard.tokenBudgetSub}</p>
        </Card>

        <Card className="p-5">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-slate-400 uppercase tracking-wider">
              {t.dashboard.accuracyTitle}
            </span>
            <div className="p-2 rounded-lg bg-emerald-500/10 text-emerald-400">
              <TrendingUp className="h-4 w-4" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline space-x-2">
            <span className="text-2xl font-bold text-emerald-400">{stats.successRatePercentage}%</span>
            <span className="text-xs text-emerald-500/80">({stats.passedTests}/{stats.passedTests + stats.failedTests})</span>
          </div>
          <p className="text-xs text-slate-500 mt-1">{t.dashboard.accuracySub}</p>
        </Card>

        <Card className="p-5">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-slate-400 uppercase tracking-wider">
              {t.dashboard.workersTitle}
            </span>
            <div className="p-2 rounded-lg bg-cyan-500/10 text-cyan-400">
              <Cpu className="h-4 w-4" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline space-x-2">
            <span className="text-2xl font-bold text-white">{stats.activeExecutions}</span>
            <span className="text-xs text-cyan-400 font-medium">Playwright Workers</span>
          </div>
          <p className="text-xs text-slate-500 mt-1">{t.dashboard.workersSub}</p>
        </Card>

        <Card className="p-5">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-slate-400 uppercase tracking-wider">
              {t.dashboard.outboxTitle}
            </span>
            <div className="p-2 rounded-lg bg-rose-500/10 text-rose-400">
              <AlertTriangle className="h-4 w-4" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline space-x-2">
            <span className="text-2xl font-bold text-rose-400">{stats.criticalDefects}</span>
            <span className="text-xs text-slate-400">Total detected</span>
          </div>
          <p className="text-xs text-slate-500 mt-1">{t.dashboard.outboxSub}</p>
        </Card>
      </div>

      {/* ACTION BANNER */}
      <div className="relative overflow-hidden rounded-xl border border-indigo-500/30 bg-gradient-to-r from-indigo-950/40 via-slate-900 to-indigo-950/20 p-6">
        <div className="relative z-10 flex flex-col md:flex-row md:items-center md:justify-between gap-6">
          <div className="space-y-2 max-w-2xl">
            <div className="flex items-center space-x-2">
              <span className="flex h-2 w-2 rounded-full bg-emerald-400 animate-pulse" />
              <span className="text-xs font-semibold text-emerald-400 uppercase tracking-wider">
                Universal AI Automation Ready
              </span>
            </div>
            <h2 className="text-xl font-bold text-white">
              {t.dashboard.bannerTitle}
            </h2>
            <p className="text-sm text-slate-300">
              {t.dashboard.bannerSubtitle}
            </p>
          </div>

          <div className="flex-shrink-0">
            <Link href="/dashboard/tests/new">
              <Button size="lg" className="bg-indigo-600 hover:bg-indigo-500 text-white shadow-lg shadow-indigo-600/30">
                <Sparkles className="mr-2 h-4 w-4" />
                {t.dashboard.bannerBtn}
              </Button>
            </Link>
          </div>
        </div>
      </div>

      {/* RECENT RUNS TABLE */}
      <Card>
        <CardHeader className="flex flex-row items-center justify-between pb-3">
          <div>
            <CardTitle>{t.dashboard.recentRunsTitle}</CardTitle>
            <CardDescription className="mt-1">
              {t.dashboard.recentRunsSubtitle}
            </CardDescription>
          </div>
          <Link href="/dashboard/tests">
            <Button variant="ghost" size="sm" className="text-indigo-400 hover:text-indigo-300">
              {t.dashboard.viewHistoryBtn}
              <ArrowRight className="ml-1.5 h-3.5 w-3.5" />
            </Button>
          </Link>
        </CardHeader>
        <CardContent>
          {loading ? (
            <div className="py-8 text-center text-sm text-slate-500">{t.common.loading}</div>
          ) : runs.length === 0 ? (
            <div className="py-10 text-center space-y-3">
              <Globe2 className="h-8 w-8 text-slate-600 mx-auto" />
              <p className="text-sm text-slate-400">No test runs executed yet.</p>
              <Link href="/dashboard/tests/new">
                <Button size="sm" variant="outline">
                  <PlusCircle className="mr-2 h-4 w-4" />
                  {t.webTesting.startTestBtn}
                </Button>
              </Link>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs text-slate-300">
                <thead className="bg-slate-900/60 text-slate-400 uppercase text-[10px] tracking-wider border-b border-slate-800">
                  <tr>
                    <th className="py-3 px-4">{t.dashboard.colSessionId}</th>
                    <th className="py-3 px-4">{t.dashboard.colDomain}</th>
                    <th className="py-3 px-4">{t.dashboard.colQuestions}</th>
                    <th className="py-3 px-4">{t.dashboard.colScore}</th>
                    <th className="py-3 px-4">{t.dashboard.colStatus}</th>
                    <th className="py-3 px-4 text-right">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/60 font-mono">
                  {runs.slice(0, 5).map((run) => (
                    <tr key={run.id} className="hover:bg-slate-900/40 transition-colors">
                      <td className="py-3.5 px-4 text-slate-400">
                        {run.id.substring(0, 8)}...
                      </td>
                      <td className="py-3.5 px-4 font-sans font-medium text-slate-200">
                        {run.browserType} ({run.viewportWidth}x{run.viewportHeight})
                      </td>
                      <td className="py-3.5 px-4">{run.totalCases} cases</td>
                      <td className="py-3.5 px-4">
                        <span className="text-emerald-400">{run.passedCases}</span> /{" "}
                        <span className="text-rose-400">{run.failedCases}</span>
                      </td>
                      <td className="py-3.5 px-4 font-sans">
                        <Badge
                          variant={
                            run.status === "COMPLETED"
                              ? "success"
                              : run.status === "RUNNING"
                              ? "warning"
                              : "danger"
                          }
                        >
                          {run.status}
                        </Badge>
                      </td>
                      <td className="py-3.5 px-4 text-right font-sans">
                        <Link href={`/dashboard/tests/${run.id}`}>
                          <Button variant="ghost" size="sm" className="h-7 text-xs text-indigo-400 hover:text-indigo-300">
                            View Execution
                          </Button>
                        </Link>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
