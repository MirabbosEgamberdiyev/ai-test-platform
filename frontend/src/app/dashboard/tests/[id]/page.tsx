"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { useI18n } from "@/lib/i18n";
import { webTestsApi, WebTestRun, WebDefect } from "@/lib/api";
import {
  PlayCircle,
  CheckCircle2,
  AlertTriangle,
  Clock,
  Terminal,
  ImageIcon,
  ArrowRight,
  RefreshCw,
  Cpu,
  ShieldCheck,
  Maximize2,
} from "lucide-react";

export default function LiveExecutionPage() {
  const params = useParams();
  const router = useRouter();
  const { t } = useI18n();
  const runId = params.id as string;

  const [run, setRun] = useState<WebTestRun | null>(null);
  const [defects, setDefects] = useState<WebDefect[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedScreenshot, setSelectedScreenshot] = useState<string | null>(null);

  useEffect(() => {
    let interval: NodeJS.Timeout;

    async function fetchExecution() {
      try {
        const runData = await webTestsApi.getRun(runId);
        setRun(runData);

        if (runData.failedCases > 0 || runData.status === "COMPLETED" || runData.status === "FAILED") {
          const defectsData = await webTestsApi.getDefects(runId);
          setDefects(defectsData);
        }
      } catch (err) {
        console.error("Failed to fetch test run:", err);
      } finally {
        setLoading(false);
      }
    }

    fetchExecution();

    // Poll if still active
    interval = setInterval(() => {
      fetchExecution();
    }, 2500);

    return () => clearInterval(interval);
  }, [runId]);

  if (loading && !run) {
    return (
      <div className="py-20 text-center space-y-3">
        <RefreshCw className="h-8 w-8 text-indigo-400 animate-spin mx-auto" />
        <p className="text-sm text-slate-400">{t.common.loading}</p>
      </div>
    );
  }

  if (!run) {
    return (
      <div className="py-20 text-center space-y-4">
        <AlertTriangle className="h-10 w-10 text-rose-400 mx-auto" />
        <h2 className="text-lg font-bold text-white">Test Execution Not Found</h2>
        <Link href="/dashboard">
          <Button variant="outline">Back to Dashboard</Button>
        </Link>
      </div>
    );
  }

  const isFinished = run.status === "COMPLETED" || run.status === "FAILED";
  const progressPct =
    run.totalCases > 0
      ? Math.round(((run.passedCases + run.failedCases) / run.totalCases) * 100)
      : isFinished
      ? 100
      : 25;

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      {/* HEADER */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 border-b border-slate-800 pb-4">
        <div className="space-y-1">
          <div className="flex items-center space-x-3">
            <h1 className="text-2xl font-bold tracking-tight text-white">{t.webTesting.liveExecutionTitle}</h1>
            <Badge
              variant={
                run.status === "COMPLETED"
                  ? "success"
                  : run.status === "RUNNING"
                  ? "warning"
                  : "danger"
              }
              className="text-xs px-2.5 py-0.5"
            >
              {run.status === "RUNNING" && <span className="mr-1.5 h-2 w-2 rounded-full bg-amber-400 animate-ping" />}
              {run.status}
            </Badge>
          </div>
          <p className="text-xs text-slate-400 font-mono">
            Run ID: {run.id} • Sandbox: {run.browserType} ({run.viewportWidth}x{run.viewportHeight})
          </p>
        </div>

        {isFinished && (
          <Link href={`/dashboard/reports/${run.id}`}>
            <Button className="bg-indigo-600 hover:bg-indigo-500 text-white shadow-lg shadow-indigo-600/30">
              <CheckCircle2 className="mr-2 h-4 w-4" />
              {t.webTesting.viewReportBtn}
              <ArrowRight className="ml-2 h-4 w-4" />
            </Button>
          </Link>
        )}
      </div>

      {/* METRIC TILES */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        <Card className="p-4">
          <span className="text-xs text-slate-400">Total Cases</span>
          <p className="text-2xl font-bold text-white mt-1">{run.totalCases}</p>
        </Card>
        <Card className="p-4">
          <span className="text-xs text-emerald-400">Passed</span>
          <p className="text-2xl font-bold text-emerald-400 mt-1">{run.passedCases}</p>
        </Card>
        <Card className="p-4">
          <span className="text-xs text-rose-400">Discovered Defects</span>
          <p className="text-2xl font-bold text-rose-400 mt-1">{run.failedCases}</p>
        </Card>
        <Card className="p-4">
          <span className="text-xs text-slate-400">{t.webTesting.progress}</span>
          <p className="text-2xl font-bold text-indigo-400 mt-1">{progressPct}%</p>
        </Card>
      </div>

      {/* PROGRESS BAR */}
      <div className="w-full bg-slate-900 h-2.5 rounded-full overflow-hidden border border-slate-800">
        <div
          className={`h-full transition-all duration-500 ${
            run.status === "COMPLETED"
              ? "bg-emerald-500"
              : run.status === "FAILED"
              ? "bg-rose-500"
              : "bg-indigo-600"
          }`}
          style={{ width: `${progressPct}%` }}
        />
      </div>

      {/* MAIN TWO-COLUMN SPLIT: LOGS & DEFECT EVIDENCE */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* CONSOLE LOG STREAM */}
        <div className="lg:col-span-2 space-y-3">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-semibold text-white flex items-center gap-2">
              <Terminal className="h-4 w-4 text-indigo-400" />
              {t.webTesting.logs}
            </h3>
            <span className="text-[11px] text-slate-500 font-mono">Stream: Active</span>
          </div>

          <div className="bg-slate-950 border border-slate-800 rounded-xl p-4 font-mono text-xs text-slate-300 h-[360px] overflow-y-auto space-y-1.5 leading-relaxed">
            {run.executionLogs ? (
              run.executionLogs.split("\n").map((line, idx) => (
                <div
                  key={idx}
                  className={`${
                    line.includes("PASSED")
                      ? "text-emerald-400"
                      : line.includes("FAILED") || line.includes("error")
                      ? "text-rose-400 font-bold"
                      : line.includes("[TEST CASE]")
                      ? "text-cyan-400 font-bold pt-1"
                      : "text-slate-400"
                  }`}
                >
                  {line}
                </div>
              ))
            ) : (
              <div className="text-slate-600">Initializing Playwright browser context...</div>
            )}
          </div>
        </div>

        {/* EVIDENCE & DEFECTS PANEL */}
        <div className="space-y-3">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-semibold text-white flex items-center gap-2">
              <ImageIcon className="h-4 w-4 text-rose-400" />
              Evidence & Defects ({defects.length})
            </h3>
          </div>

          <div className="bg-slate-950 border border-slate-800 rounded-xl p-3 h-[360px] overflow-y-auto space-y-3">
            {defects.length === 0 ? (
              <div className="h-full flex flex-col items-center justify-center text-center p-4 text-slate-500 text-xs">
                <CheckCircle2 className="h-8 w-8 text-emerald-500/40 mb-2" />
                <span>No defects detected so far. All browser assertions are passing.</span>
              </div>
            ) : (
              defects.map((d) => (
                <div key={d.id} className="p-3 bg-slate-900/80 rounded-lg border border-rose-500/20 space-y-2">
                  <div className="flex items-start justify-between">
                    <span className="text-xs font-semibold text-white">{d.title}</span>
                    <Badge variant="danger" className="text-[10px]">
                      {d.severity}
                    </Badge>
                  </div>
                  <p className="text-[11px] text-slate-400 line-clamp-2">{d.actualResult}</p>

                  {d.screenshotPath && (
                    <button
                      onClick={() => setSelectedScreenshot(d.screenshotPath || null)}
                      className="w-full flex items-center justify-center gap-1.5 py-1.5 px-2 bg-slate-950 hover:bg-slate-800 rounded border border-slate-800 text-[11px] text-indigo-400 transition-colors"
                    >
                      <Maximize2 className="h-3 w-3" />
                      View Failure Screenshot
                    </button>
                  )}
                </div>
              ))
            )}
          </div>
        </div>
      </div>

      {/* SCREENSHOT LIGHTBOX MODAL */}
      {selectedScreenshot && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-xl max-w-3xl w-full p-4 space-y-3">
            <div className="flex items-center justify-between pb-2 border-b border-slate-800">
              <span className="text-xs font-mono text-slate-400">{selectedScreenshot}</span>
              <Button size="sm" variant="ghost" onClick={() => setSelectedScreenshot(null)}>
                ✕
              </Button>
            </div>
            <div className="rounded overflow-hidden bg-slate-950 p-2 flex items-center justify-center max-h-[70vh]">
              {/* Fallback canvas / indicator for screenshot */}
              <div className="text-center p-8 space-y-2">
                <ImageIcon className="h-12 w-12 text-slate-600 mx-auto" />
                <p className="text-xs text-slate-400 font-mono">Screenshot captured: {selectedScreenshot}</p>
                <p className="text-[11px] text-slate-500">Stored in server artifacts storage</p>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
