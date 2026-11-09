"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { useI18n } from "@/lib/i18n";
import { webTestsApi, WebTestReport, WebDefect } from "@/lib/api";
import {
  FileBarChart2,
  Download,
  Printer,
  Sparkles,
  ShieldCheck,
  CheckCircle2,
  AlertTriangle,
  Clock,
  ExternalLink,
  Code,
  FileSpreadsheet,
  FileText,
  ArrowLeft,
  RefreshCw,
} from "lucide-react";

export default function ReportDetailPage() {
  const params = useParams();
  const { t } = useI18n();
  const runId = params.id as string;

  const [report, setReport] = useState<WebTestReport | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadReport() {
      try {
        const data = await webTestsApi.getReport(runId);
        setReport(data);
      } catch (err) {
        console.error("Failed to load report:", err);
      } finally {
        setLoading(false);
      }
    }
    loadReport();
  }, [runId]);

  if (loading) {
    return (
      <div className="py-20 text-center space-y-3">
        <RefreshCw className="h-8 w-8 text-indigo-400 animate-spin mx-auto" />
        <p className="text-sm text-slate-400">{t.common.loading}</p>
      </div>
    );
  }

  if (!report) {
    return (
      <div className="py-20 text-center space-y-4">
        <AlertTriangle className="h-10 w-10 text-rose-400 mx-auto" />
        <h2 className="text-lg font-bold text-white">Report Not Found</h2>
        <Link href="/dashboard">
          <Button variant="outline">Back to Dashboard</Button>
        </Link>
      </div>
    );
  }

  return (
    <div className="space-y-8 max-w-5xl mx-auto pb-12">
      {/* HEADER & EXPORT ACTIONS */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-4 border-b border-slate-800 pb-5">
        <div className="space-y-1">
          <Link href="/dashboard" className="text-xs text-slate-500 hover:text-slate-300 flex items-center gap-1 mb-2">
            <ArrowLeft className="h-3 w-3" />
            Back to Dashboard
          </Link>
          <h1 className="text-2xl font-bold tracking-tight text-white flex items-center gap-2">
            <FileBarChart2 className="h-6 w-6 text-indigo-400" />
            {t.webTesting.reportTitle}
          </h1>
          <p className="text-sm text-slate-400">
            {report.websiteName} •{" "}
            <a
              href={report.targetUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="text-indigo-400 hover:underline font-mono"
            >
              {report.targetUrl}
            </a>
          </p>
        </div>

        {/* EXPORT BUTTONS */}
        <div className="flex flex-wrap items-center gap-2">
          <a href={webTestsApi.getExportUrl(runId, "html")} download>
            <Button size="sm" variant="outline" className="text-xs">
              <FileText className="mr-1.5 h-3.5 w-3.5 text-indigo-400" />
              HTML
            </Button>
          </a>
          <a href={webTestsApi.getExportUrl(runId, "json")} download>
            <Button size="sm" variant="outline" className="text-xs">
              <Code className="mr-1.5 h-3.5 w-3.5 text-cyan-400" />
              JSON
            </Button>
          </a>
          <a href={webTestsApi.getExportUrl(runId, "csv")} download>
            <Button size="sm" variant="outline" className="text-xs">
              <FileSpreadsheet className="mr-1.5 h-3.5 w-3.5 text-emerald-400" />
              CSV
            </Button>
          </a>
          <Button size="sm" onClick={() => window.print()} className="bg-slate-800 hover:bg-slate-700 text-xs">
            <Printer className="mr-1.5 h-3.5 w-3.5" />
            Print / PDF
          </Button>
        </div>
      </div>

      {/* KPI METRIC CARDS */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        <Card className="p-4">
          <span className="text-xs text-slate-400">{t.webTesting.kpiPassRate}</span>
          <p className="text-2xl font-bold text-emerald-400 mt-1">{report.passPercentage}%</p>
        </Card>
        <Card className="p-4">
          <span className="text-xs text-slate-400">{t.webTesting.kpiDuration}</span>
          <p className="text-2xl font-bold text-white mt-1">{(report.executionDurationMs / 1000).toFixed(1)}s</p>
        </Card>
        <Card className="p-4">
          <span className="text-xs text-slate-400">{t.webTesting.kpiTotalDefects}</span>
          <p className="text-2xl font-bold text-rose-400 mt-1">{report.defects.length}</p>
        </Card>
        <Card className="p-4">
          <span className="text-xs text-slate-400">{t.webTesting.kpiCritical}</span>
          <p className="text-2xl font-bold text-rose-500 mt-1">{report.criticalDefectsCount}</p>
        </Card>
      </div>

      {/* AI EXECUTIVE SUMMARY & RECOMMENDATIONS */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
        <Card className="p-5 space-y-3 bg-gradient-to-br from-indigo-950/30 to-slate-900 border-indigo-500/20">
          <div className="flex items-center space-x-2 text-indigo-400">
            <Sparkles className="h-4 w-4" />
            <h3 className="text-sm font-bold uppercase tracking-wider">{t.webTesting.aiSummaryTitle}</h3>
          </div>
          <p className="text-xs text-slate-300 leading-relaxed">{report.aiExecutiveSummary}</p>
        </Card>

        <Card className="p-5 space-y-3 bg-gradient-to-br from-emerald-950/20 to-slate-900 border-emerald-500/20">
          <div className="flex items-center space-x-2 text-emerald-400">
            <ShieldCheck className="h-4 w-4" />
            <h3 className="text-sm font-bold uppercase tracking-wider">{t.webTesting.aiRecommendationsTitle}</h3>
          </div>
          <p className="text-xs text-slate-300 leading-relaxed">{report.aiRecommendations}</p>
        </Card>
      </div>

      {/* DEFECTS & BUGS LIST */}
      <div className="space-y-4">
        <h2 className="text-lg font-bold text-white flex items-center gap-2">
          <AlertTriangle className="h-5 w-5 text-rose-400" />
          {t.webTesting.defectsListTitle} ({report.defects.length})
        </h2>

        {report.defects.length === 0 ? (
          <Card className="p-8 text-center space-y-2">
            <CheckCircle2 className="h-10 w-10 text-emerald-400 mx-auto" />
            <h3 className="text-sm font-semibold text-white">Zero Defects Discovered</h3>
            <p className="text-xs text-slate-400">
              All automated browser workflows, responsive viewports, and accessibility criteria passed successfully.
            </p>
          </Card>
        ) : (
          <div className="space-y-4">
            {report.defects.map((defect) => (
              <Card key={defect.id} className="p-5 border-l-4 border-l-rose-500 space-y-3">
                <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2 border-b border-slate-800/80 pb-3">
                  <div className="flex items-center space-x-2.5">
                    <span className="font-mono text-xs text-slate-400 font-bold">{defect.defectCode}</span>
                    <h3 className="text-sm font-bold text-white">{defect.title}</h3>
                  </div>
                  <Badge
                    variant={
                      defect.severity === "CRITICAL"
                        ? "danger"
                        : defect.severity === "HIGH"
                        ? "warning"
                        : "outline"
                    }
                    className="text-[10px] w-fit"
                  >
                    {defect.severity}
                  </Badge>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs">
                  <div className="space-y-1">
                    <span className="text-slate-500 font-semibold">{t.webTesting.expected}:</span>
                    <p className="text-slate-300">{defect.expectedResult}</p>
                  </div>
                  <div className="space-y-1">
                    <span className="text-rose-400 font-semibold">{t.webTesting.actual}:</span>
                    <p className="text-slate-300">{defect.actualResult}</p>
                  </div>
                </div>

                {defect.reproductionSteps && (
                  <div className="space-y-1 pt-1 text-xs">
                    <span className="text-slate-500 font-semibold">Reproduction Steps:</span>
                    <pre className="p-2.5 bg-slate-950 rounded border border-slate-800 text-[11px] text-slate-400 font-mono whitespace-pre-wrap">
                      {defect.reproductionSteps}
                    </pre>
                  </div>
                )}

                {defect.aiRootCause && (
                  <div className="p-3 bg-indigo-950/20 border border-indigo-500/20 rounded-lg text-xs space-y-1">
                    <span className="font-bold text-indigo-400 flex items-center gap-1.5">
                      <Sparkles className="h-3.5 w-3.5" />
                      {t.webTesting.aiRootCause} ({(defect.confidenceScore * 100).toFixed(0)}% confidence):
                    </span>
                    <p className="text-slate-300 leading-relaxed">{defect.aiRootCause}</p>
                  </div>
                )}
              </Card>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
