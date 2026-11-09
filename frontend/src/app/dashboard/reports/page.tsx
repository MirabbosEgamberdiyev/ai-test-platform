"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { useI18n } from "@/lib/i18n";
import { webTestsApi, WebTestRun } from "@/lib/api";
import { FileBarChart2, ArrowRight, Download, CheckCircle2, AlertTriangle } from "lucide-react";

export default function ReportsListPage() {
  const { t } = useI18n();
  const [runs, setRuns] = useState<WebTestRun[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadReports() {
      try {
        const data = await webTestsApi.listRuns();
        setRuns(data.filter((r) => r.status === "COMPLETED" || r.status === "FAILED"));
      } catch (err) {
        console.error("Failed to load reports:", err);
      } finally {
        setLoading(false);
      }
    }
    loadReports();
  }, []);

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div>
        <h1 className="text-2xl font-bold tracking-tight text-white">{t.nav.aiReports}</h1>
        <p className="text-sm text-slate-400 mt-1">
          Historical AI quality reports, defect telemetry, and downloadable audit evidence.
        </p>
      </div>

      <Card>
        <CardContent className="p-0">
          {loading ? (
            <div className="py-12 text-center text-sm text-slate-500">{t.common.loading}</div>
          ) : runs.length === 0 ? (
            <div className="py-12 text-center space-y-3">
              <FileBarChart2 className="h-10 w-10 text-slate-600 mx-auto" />
              <p className="text-sm text-slate-400">No reports generated yet. Run a test to generate reports.</p>
              <Link href="/dashboard/tests/new">
                <Button size="sm" variant="outline">
                  Create New Test
                </Button>
              </Link>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs text-slate-300">
                <thead className="bg-slate-900/60 text-slate-400 uppercase text-[10px] tracking-wider border-b border-slate-800">
                  <tr>
                    <th className="py-3.5 px-4">Report ID</th>
                    <th className="py-3.5 px-4">Browser Viewport</th>
                    <th className="py-3.5 px-4">Cases</th>
                    <th className="py-3.5 px-4">Pass Rate</th>
                    <th className="py-3.5 px-4">Defects</th>
                    <th className="py-3.5 px-4">Date</th>
                    <th className="py-3.5 px-4 text-right">Export</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/60 font-mono">
                  {runs.map((r) => {
                    const passPct = r.totalCases > 0 ? Math.round((r.passedCases / r.totalCases) * 100) : 100;
                    return (
                      <tr key={r.id} className="hover:bg-slate-900/40 transition-colors">
                        <td className="py-3.5 px-4 text-slate-400">{r.id.substring(0, 8)}...</td>
                        <td className="py-3.5 px-4 font-sans text-slate-200">
                          {r.browserType} ({r.viewportWidth}x{r.viewportHeight})
                        </td>
                        <td className="py-3.5 px-4">{r.totalCases}</td>
                        <td className="py-3.5 px-4 text-emerald-400 font-bold">{passPct}%</td>
                        <td className="py-3.5 px-4 text-rose-400 font-bold">{r.failedCases}</td>
                        <td className="py-3.5 px-4 font-sans text-slate-400">
                          {new Date(r.createdAt).toLocaleDateString()}
                        </td>
                        <td className="py-3.5 px-4 text-right font-sans">
                          <div className="flex items-center justify-end gap-2">
                            <Link href={`/dashboard/reports/${r.id}`}>
                              <Button size="sm" variant="ghost" className="h-7 text-xs text-indigo-400 hover:text-indigo-300">
                                Open Report
                              </Button>
                            </Link>
                            <a href={webTestsApi.getExportUrl(r.id, "html")} download>
                              <Button size="sm" variant="outline" className="h-7 text-xs">
                                <Download className="h-3 w-3" />
                              </Button>
                            </a>
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
