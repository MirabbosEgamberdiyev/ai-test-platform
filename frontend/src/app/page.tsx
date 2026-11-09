"use client";

import Link from "next/link";
import { useState } from "react";
import { Navbar } from "@/components/layout/navbar";
import { Footer } from "@/components/layout/footer";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { useI18n } from "@/lib/i18n";
import {
  Sparkles,
  ShieldAlert,
  Layers,
  ArrowRight,
  Cpu,
  Workflow,
  Activity,
  Lock,
} from "lucide-react";

export default function HomePage() {
  const { t } = useI18n();
  const [selectedDemoOption, setSelectedDemoOption] = useState<string>("B");

  return (
    <div className="min-h-screen flex flex-col bg-slate-950 text-slate-100">
      <Navbar />

      <main className="flex-1">
        {/* HERO SECTION */}
        <section className="relative overflow-hidden pt-20 pb-28 px-4 sm:px-6 lg:px-8 border-b border-slate-900 bg-[radial-gradient(ellipse_80%_80%_at_50%_-20%,rgba(120,119,198,0.25),rgba(255,255,255,0))]">
          <div className="container mx-auto max-w-6xl text-center">
            <div className="inline-flex items-center space-x-2 px-3.5 py-1.5 rounded-full border border-indigo-500/30 bg-indigo-500/10 text-indigo-300 text-xs font-semibold mb-6 animate-pulse">
              <Sparkles className="h-3.5 w-3.5" />
              <span>{t.hero.badge}</span>
            </div>

            <h1 className="text-4xl sm:text-6xl font-extrabold tracking-tight text-white max-w-4xl mx-auto leading-tight sm:leading-tight">
              {t.hero.title} <br className="hidden sm:inline" />
              <span className="text-transparent bg-clip-text bg-gradient-to-r from-indigo-400 via-purple-300 to-indigo-500">
                {t.hero.titleHighlight}
              </span>
            </h1>

            <p className="mt-6 text-lg sm:text-xl text-slate-300 max-w-3xl mx-auto leading-relaxed">
              {t.hero.subtitle}
            </p>

            <div className="mt-10 flex flex-col sm:flex-row items-center justify-center gap-4">
              <Link href="/register">
                <Button size="lg" className="w-full sm:w-auto text-base px-8 h-12 shadow-lg shadow-indigo-600/30">
                  {t.hero.launchBtn}
                  <ArrowRight className="ml-2 h-4 w-4" />
                </Button>
              </Link>
              <Link href="/dashboard/practice">
                <Button variant="outline" size="lg" className="w-full sm:w-auto text-base px-8 h-12">
                  {t.hero.demoBtn}
                </Button>
              </Link>
            </div>

            {/* QUICK STATS */}
            <div className="mt-16 grid grid-cols-2 md:grid-cols-4 gap-6 max-w-4xl mx-auto text-left">
              <div className="rounded-xl border border-slate-800 bg-slate-900/50 p-4">
                <div className="text-2xl font-bold text-white">{t.hero.statPrinciples}</div>
                <div className="text-xs text-slate-400 mt-1">{t.hero.statPrinciplesLabel}</div>
              </div>
              <div className="rounded-xl border border-slate-800 bg-slate-900/50 p-4">
                <div className="text-2xl font-bold text-indigo-400">{t.hero.statBackend}</div>
                <div className="text-xs text-slate-400 mt-1">{t.hero.statBackendLabel}</div>
              </div>
              <div className="rounded-xl border border-slate-800 bg-slate-900/50 p-4">
                <div className="text-2xl font-bold text-emerald-400">{t.hero.statLatency}</div>
                <div className="text-xs text-slate-400 mt-1">{t.hero.statLatencyLabel}</div>
              </div>
              <div className="rounded-xl border border-slate-800 bg-slate-900/50 p-4">
                <div className="text-2xl font-bold text-purple-400">{t.hero.statSandbox}</div>
                <div className="text-xs text-slate-400 mt-1">{t.hero.statSandboxLabel}</div>
              </div>
            </div>
          </div>
        </section>

        {/* INTERACTIVE LIVE PRACTICE SIMULATION */}
        <section className="py-20 px-4 sm:px-6 lg:px-8 bg-slate-950 border-b border-slate-900">
          <div className="container mx-auto max-w-5xl">
            <div className="text-center mb-12">
              <Badge variant="default" className="mb-3">{t.simulator.badge}</Badge>
              <h2 className="text-3xl font-bold text-white">{t.simulator.title}</h2>
              <p className="text-slate-400 text-sm mt-2">
                {t.simulator.subtitle}
              </p>
            </div>

            <Card className="border border-slate-800 bg-slate-900 shadow-2xl overflow-hidden">
              <div className="bg-slate-950 px-6 py-3 border-b border-slate-800 flex items-center justify-between text-xs text-slate-400">
                <div className="flex items-center space-x-2">
                  <span className="h-2 w-2 rounded-full bg-emerald-500 animate-ping" />
                  <span className="font-mono text-slate-300">{t.simulator.sessionBar}</span>
                </div>
                <Badge variant="success">{t.simulator.confidenceBadge}</Badge>
              </div>

              <CardContent className="p-6 sm:p-8 space-y-6">
                <div>
                  <span className="text-xs font-semibold text-indigo-400 uppercase tracking-wider">
                    {t.simulator.questionCategory}
                  </span>
                  <h3 className="text-lg font-medium text-white mt-1">
                    {t.simulator.questionText}
                  </h3>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  {[
                    { key: "A", text: t.simulator.optA },
                    { key: "B", text: t.simulator.optB },
                    { key: "C", text: t.simulator.optC },
                    { key: "D", text: t.simulator.optD },
                  ].map((opt) => (
                    <button
                      key={opt.key}
                      onClick={() => setSelectedDemoOption(opt.key)}
                      className={`text-left p-4 rounded-xl border transition-all ${
                        selectedDemoOption === opt.key
                          ? "border-indigo-500 bg-indigo-500/10 text-white"
                          : "border-slate-800 bg-slate-950/60 text-slate-300 hover:border-slate-700"
                      }`}
                    >
                      <span className={`inline-block w-6 h-6 rounded-md text-xs font-bold text-center leading-6 mr-3 ${
                        selectedDemoOption === opt.key ? "bg-indigo-600 text-white" : "bg-slate-800 text-slate-400"
                      }`}>
                        {opt.key}
                      </span>
                      {opt.text}
                    </button>
                  ))}
                </div>

                {/* AI RATIONALE PANEL */}
                <div className="rounded-xl border border-indigo-500/20 bg-indigo-950/20 p-5 space-y-2">
                  <div className="flex items-center space-x-2 text-indigo-400 font-semibold text-xs uppercase tracking-wider">
                    <Sparkles className="h-4 w-4" />
                    <span>{t.simulator.aiPanelTitle}</span>
                  </div>
                  <p className="text-xs text-slate-300 leading-relaxed">
                    {t.simulator.aiRationale}
                  </p>
                </div>
              </CardContent>
            </Card>
          </div>
        </section>

        {/* 55 PRINCIPLES SHOWCASE */}
        <section id="architecture" className="py-20 px-4 sm:px-6 lg:px-8 border-b border-slate-900 bg-slate-900/30">
          <div className="container mx-auto max-w-6xl">
            <div className="text-center max-w-3xl mx-auto mb-16">
              <Badge variant="outline" className="mb-3">{t.principles.badge}</Badge>
              <h2 className="text-3xl sm:text-4xl font-bold text-white">
                {t.principles.title}
              </h2>
              <p className="mt-3 text-slate-400 text-sm">
                {t.principles.subtitle}
              </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <Card>
                <CardHeader>
                  <div className="h-10 w-10 rounded-lg bg-indigo-600/20 text-indigo-400 flex items-center justify-center mb-2">
                    <Layers className="h-5 w-5" />
                  </div>
                  <CardTitle>{t.principles.card1Title}</CardTitle>
                  <CardDescription>{t.principles.card1Sub}</CardDescription>
                </CardHeader>
                <CardContent className="text-xs text-slate-400 space-y-2">
                  <p>{t.principles.card1Desc}</p>
                  <p className="text-indigo-400 font-mono">{t.principles.card1Foot}</p>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <div className="h-10 w-10 rounded-lg bg-emerald-600/20 text-emerald-400 flex items-center justify-center mb-2">
                    <ShieldAlert className="h-5 w-5" />
                  </div>
                  <CardTitle>{t.principles.card2Title}</CardTitle>
                  <CardDescription>{t.principles.card2Sub}</CardDescription>
                </CardHeader>
                <CardContent className="text-xs text-slate-400 space-y-2">
                  <p>{t.principles.card2Desc}</p>
                  <p className="text-emerald-400 font-mono">{t.principles.card2Foot}</p>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <div className="h-10 w-10 rounded-lg bg-purple-600/20 text-purple-400 flex items-center justify-center mb-2">
                    <Cpu className="h-5 w-5" />
                  </div>
                  <CardTitle>{t.principles.card3Title}</CardTitle>
                  <CardDescription>{t.principles.card3Sub}</CardDescription>
                </CardHeader>
                <CardContent className="text-xs text-slate-400 space-y-2">
                  <p>{t.principles.card3Desc}</p>
                  <p className="text-purple-400 font-mono">{t.principles.card3Foot}</p>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <div className="h-10 w-10 rounded-lg bg-amber-600/20 text-amber-400 flex items-center justify-center mb-2">
                    <Workflow className="h-5 w-5" />
                  </div>
                  <CardTitle>{t.principles.card4Title}</CardTitle>
                  <CardDescription>{t.principles.card4Sub}</CardDescription>
                </CardHeader>
                <CardContent className="text-xs text-slate-400 space-y-2">
                  <p>{t.principles.card4Desc}</p>
                  <p className="text-amber-400 font-mono">{t.principles.card4Foot}</p>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <div className="h-10 w-10 rounded-lg bg-sky-600/20 text-sky-400 flex items-center justify-center mb-2">
                    <Activity className="h-5 w-5" />
                  </div>
                  <CardTitle>{t.principles.card5Title}</CardTitle>
                  <CardDescription>{t.principles.card5Sub}</CardDescription>
                </CardHeader>
                <CardContent className="text-xs text-slate-400 space-y-2">
                  <p>{t.principles.card5Desc}</p>
                  <p className="text-sky-400 font-mono">{t.principles.card5Foot}</p>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <div className="h-10 w-10 rounded-lg bg-rose-600/20 text-rose-400 flex items-center justify-center mb-2">
                    <Lock className="h-5 w-5" />
                  </div>
                  <CardTitle>{t.principles.card6Title}</CardTitle>
                  <CardDescription>{t.principles.card6Sub}</CardDescription>
                </CardHeader>
                <CardContent className="text-xs text-slate-400 space-y-2">
                  <p>{t.principles.card6Desc}</p>
                  <p className="text-rose-400 font-mono">{t.principles.card6Foot}</p>
                </CardContent>
              </Card>
            </div>
          </div>
        </section>

        {/* CALL TO ACTION */}
        <section className="py-20 px-4 sm:px-6 lg:px-8 text-center bg-gradient-to-b from-slate-900 to-slate-950">
          <div className="container mx-auto max-w-4xl space-y-6">
            <h2 className="text-3xl sm:text-4xl font-bold text-white">
              {t.cta.title}
            </h2>
            <p className="text-slate-400 text-sm sm:text-base max-w-2xl mx-auto">
              {t.cta.subtitle}
            </p>
            <div className="pt-4 flex justify-center space-x-4">
              <Link href="/register">
                <Button size="lg" className="px-8">
                  {t.cta.registerBtn}
                </Button>
              </Link>
              <Link href="/login">
                <Button variant="outline" size="lg" className="px-8">
                  {t.cta.loginBtn}
                </Button>
              </Link>
            </div>
          </div>
        </section>
      </main>

      <Footer />
    </div>
  );
}
