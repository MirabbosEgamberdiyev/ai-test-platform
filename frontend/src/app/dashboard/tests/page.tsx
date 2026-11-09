"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { useI18n } from "@/lib/i18n";
import { webTestsApi, WebTestRun } from "@/lib/api";
import {
  PlayCircle,
  PlusCircle,
  Clock,
  ArrowRight,
  RefreshCw,
  Globe2,
} from "lucide-react";

export default function TestRunsPage() {
  const { t } = useI18n();
  const [runs, setRuns] = useState<WebTestRun[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadRuns() {
      try {
        const data = await webTestsApi.listRuns();
        setRuns(data);
      } catch (err) {
        console.error("Failed to load test runs:", err);
      } finally {
        setLoading(false);
      }
    }
    loadRuns();
  }, []);

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-white">{t.nav.testRuns}</h1>
          <p className="text-sm text-slate-400 mt-1">
            Complete audit log of Playwright browser test executions across your target websites.
          </p>
        </div>

        <Link href="/dashboard/tests/new">
          <Button className="bg-indigo-600 hover:bg-indigo-500 text-white">
            <PlusCircle className="mr-2 h-4 w-4" />
            {t.nav.newTest}
          </Button>
        </Link>
      </div>

      <Card>
        <CardContent className="p-0">
          {loading ? (
            <div className="py-12 text-center text-sm text-slate-500">{t.common.loading}</div>
          ) : runs.length === 0 ? (
            <div className="py-12 text-center space-y-3">
              <PlayCircle className="h-10 w-10 text-slate-600 mx-auto" />
              <p className="text-sm text-slate-400">No test runs recorded yet.</p>
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
                    <th className="py-3.5 px-4">Run ID</th>
                    <th className="py-3.5 px-4">Browser & Viewport</th>
                    <th className="py-3.5 px-4">Test Cases</th>
                    <th className="py-3.5 px-4">Passed / Failed</th>
                    <th className="py-3.5 px-4">Status</th>
                    <th className="py-3.5 px-4">Date</th>
                    <th className="py-3.5 px-4 text-right">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/60 font-mono">
                  {runs.map((run) => (
                    <tr key={run.id} className="hover:bg-slate-900/40 transition-colors">
                      <td className="py-3.5 px-4 text-slate-400">
                        {run.id.substring(0, 8)}...
                      </td>
                      <td className="py-3.5 px-4 font-sans font-medium text-slate-200">
                        {run.browserType} ({run.viewportWidth}x{run.viewportHeight})
                      </td>
                      <td className="py-3.5 px-4">{run.totalCases} cases</td>
                      <td className="py-3.5 px-4">
                        <span className="text-emerald-400 font-semibold">{run.passedCases}</span> /{" "}
                        <span className="text-rose-400 font-semibold">{run.failedCases}</span>
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
                      <td className="py-3.5 px-4 font-sans text-slate-400">
                        {new Date(run.createdAt).toLocaleString()}
                      </td>
                      <td className="py-3.5 px-4 text-right font-sans">
                        <div className="flex items-center justify-end gap-1.5">
                          <Link href={`/dashboard/tests/${run.id}`}>
                            <Button variant="ghost" size="sm" className="h-7 text-xs text-indigo-400 hover:text-indigo-300">
                              Monitor
                            </Button>
                          </Link>
                          <Link href={`/dashboard/reports/${run.id}`}>
                            <Button variant="ghost" size="sm" className="h-7 text-xs text-emerald-400 hover:text-emerald-300">
                              Report
                            </Button>
                          </Link>
                        </div>
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
