"use client";

import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useI18n } from "@/lib/i18n";
import {
  BarChart2,
  TrendingUp,
  Award,
  Clock,
  Sparkles,
  Download,
  Filter,
} from "lucide-react";

export default function AnalyticsPage() {
  const { t } = useI18n();

  const categoryStats = [
    { name: "Distributed Systems & Saga", accuracy: 96, questions: 48, status: t.analytics.mastered },
    { name: "Cloud Architecture & Kubernetes", accuracy: 92, questions: 35, status: t.analytics.proficient },
    { name: "Database Concurrency & Isolation", accuracy: 88, questions: 42, status: t.analytics.proficient },
    { name: "OWASP & Hexagonal Security", accuracy: 98, questions: 23, status: t.analytics.mastered },
  ];

  const sessionHistory = [
    {
      id: "sess-7a8f9c",
      topic: "Cloud Solutions Architect Mock",
      completedAt: "2026-11-08 16:45",
      questions: 20,
      score: 95,
      confidence: 94.8,
      duration: "14m 20s",
    },
    {
      id: "sess-3d2e1b",
      topic: "System Design & Distributed Saga",
      completedAt: "2026-11-07 11:20",
      questions: 15,
      score: 86.6,
      confidence: 89.1,
      duration: "10m 05s",
    },
    {
      id: "sess-9f4c8a",
      topic: "Kubernetes CKAD Practice Sandbox",
      completedAt: "2026-11-05 19:10",
      questions: 10,
      score: 90,
      confidence: 93.5,
      duration: "08m 15s",
    },
  ];

  return (
    <div className="space-y-6 max-w-6xl mx-auto">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-white flex items-center space-x-2">
            <BarChart2 className="h-6 w-6 text-indigo-400" />
            <span>{t.analytics.title}</span>
          </h1>
          <p className="text-sm text-slate-400 mt-1">
            {t.analytics.subtitle}
          </p>
        </div>

        <Button variant="outline" size="sm">
          <Download className="mr-1.5 h-4 w-4" />
          {t.analytics.exportBtn}
        </Button>
      </div>

      {/* AGGREGATE SUMMARY TILES */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <Card className="p-5">
          <div className="flex items-center justify-between text-slate-400 text-xs font-semibold uppercase tracking-wider">
            <span>{t.analytics.overallScoreTitle}</span>
            <Award className="h-4 w-4 text-emerald-400" />
          </div>
          <div className="mt-3 text-3xl font-bold text-emerald-400">92.4%</div>
          <p className="text-xs text-slate-400 mt-2">{t.analytics.overallScoreSub}</p>
        </Card>

        <Card className="p-5">
          <div className="flex items-center justify-between text-slate-400 text-xs font-semibold uppercase tracking-wider">
            <span>{t.analytics.avgConfidenceTitle}</span>
            <Sparkles className="h-4 w-4 text-indigo-400" />
          </div>
          <div className="mt-3 text-3xl font-bold text-indigo-400">92.5%</div>
          <p className="text-xs text-slate-400 mt-2">{t.analytics.avgConfidenceSub}</p>
        </Card>

        <Card className="p-5">
          <div className="flex items-center justify-between text-slate-400 text-xs font-semibold uppercase tracking-wider">
            <span>{t.analytics.avgLatencyTitle}</span>
            <Clock className="h-4 w-4 text-amber-400" />
          </div>
          <div className="mt-3 text-3xl font-bold text-white">1.34s</div>
          <p className="text-xs text-slate-400 mt-2">{t.analytics.avgLatencySub}</p>
        </Card>

        <Card className="p-5">
          <div className="flex items-center justify-between text-slate-400 text-xs font-semibold uppercase tracking-wider">
            <span>{t.analytics.questionsPracticedTitle}</span>
            <TrendingUp className="h-4 w-4 text-purple-400" />
          </div>
          <div className="mt-3 text-3xl font-bold text-white">148</div>
          <p className="text-xs text-slate-400 mt-2">{t.analytics.questionsPracticedSub}</p>
        </Card>
      </div>

      {/* CATEGORY BREAKDOWN */}
      <Card>
        <CardHeader>
          <CardTitle>{t.analytics.subjectTitle}</CardTitle>
          <CardDescription>
            {t.analytics.subjectDesc}
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          {categoryStats.map((cat) => (
            <div key={cat.name} className="space-y-1.5">
              <div className="flex items-center justify-between text-xs">
                <span className="font-medium text-white">{cat.name}</span>
                <div className="flex items-center space-x-3">
                  <span className="text-slate-400">{cat.questions} questions</span>
                  <span className="font-semibold text-indigo-400">{cat.accuracy}%</span>
                  <Badge variant="success">{cat.status}</Badge>
                </div>
              </div>
              <div className="w-full bg-slate-800 h-2 rounded-full overflow-hidden">
                <div
                  className="bg-indigo-500 h-full rounded-full transition-all"
                  style={{ width: `${cat.accuracy}%` }}
                />
              </div>
            </div>
          ))}
        </CardContent>
      </Card>

      {/* FULL SESSION HISTORY */}
      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <div>
              <CardTitle>{t.analytics.historyTitle}</CardTitle>
              <CardDescription>{t.analytics.historyDesc}</CardDescription>
            </div>
            <Button variant="ghost" size="sm">
              <Filter className="mr-1.5 h-3.5 w-3.5" />
              {t.analytics.filterBtn}
            </Button>
          </div>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs min-w-[600px]">
              <thead>
                <tr className="border-b border-slate-800 text-slate-400 uppercase tracking-wider">
                  <th className="pb-3 font-semibold">{t.analytics.colSession}</th>
                  <th className="pb-3 font-semibold">{t.analytics.colTopic}</th>
                  <th className="pb-3 font-semibold">{t.analytics.colCompleted}</th>
                  <th className="pb-3 font-semibold">{t.analytics.colDuration}</th>
                  <th className="pb-3 font-semibold">{t.analytics.colScore}</th>
                  <th className="pb-3 font-semibold">{t.analytics.colConfidence}</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60 text-slate-300">
                {sessionHistory.map((s) => (
                  <tr key={s.id}>
                    <td className="py-3 font-mono text-indigo-400">{s.id}</td>
                    <td className="py-3 font-medium text-white">{s.topic}</td>
                    <td className="py-3 text-slate-400">{s.completedAt}</td>
                    <td className="py-3 text-slate-400">{s.duration}</td>
                    <td className="py-3 font-semibold text-emerald-400">{s.score}%</td>
                    <td className="py-3">{s.confidence}%</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
