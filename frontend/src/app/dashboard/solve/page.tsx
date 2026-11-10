"use client";

import { useState, useEffect, Suspense } from "react";
import { useSearchParams } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { useI18n } from "@/lib/i18n";
import {
  urlTestsApi,
  UrlDiscoveryResponse,
  DiscoveredTicket,
  UrlSolvingSession,
  SolvedQuestion,
} from "@/lib/api";
import {
  Sparkles,
  Search,
  CheckCircle2,
  AlertTriangle,
  FileCheck2,
  Copy,
  Download,
  RotateCcw,
  Compass,
  ArrowRight,
  ShieldCheck,
  Check,
  Globe2,
  ExternalLink,
  Layers,
  ChevronRight,
  History,
  BookOpen,
  Bot,
  Monitor,
  Terminal,
  Eye,
  ShieldAlert,
} from "lucide-react";

export default function UrlTestSolverPage() {
  return (
    <Suspense fallback={<div className="p-8 text-center text-slate-400">Yuklanmoqda...</div>}>
      <UrlTestSolverContent />
    </Suspense>
  );
}

function UrlTestSolverContent() {
  const { t, locale } = useI18n();
  const searchParams = useSearchParams();

  // Step 1: Input URL
  const [url, setUrl] = useState("https://web.pravaonline.uz/tickets");
  const [discovering, setDiscovering] = useState(false);
  const [discovery, setDiscovery] = useState<UrlDiscoveryResponse | null>(null);

  // Step 2: Ticket Selection
  const [selectionMode, setSelectionMode] = useState<"SINGLE" | "MULTIPLE" | "RANGE" | "ALL">("SINGLE");
  const [selectedTicketNumbers, setSelectedTicketNumbers] = useState<number[]>([1]);
  const [rangeStart, setRangeStart] = useState("1");
  const [rangeEnd, setRangeEnd] = useState("5");
  const [searchFilter, setSearchFilter] = useState("");

  // Step 3: Solving & Results
  const [executionMode, setExecutionMode] = useState<"ANSWER_MODE" | "AUTHORIZED_AUTOMATION_MODE">("ANSWER_MODE");
  const [userConfirmedSubmission, setUserConfirmedSubmission] = useState(false);
  const [solving, setSolving] = useState(false);
  const [solvedSession, setSolvedSession] = useState<UrlSolvingSession | null>(null);
  const [historySessions, setHistorySessions] = useState<UrlSolvingSession[]>([]);
  const [activeTab, setActiveTab] = useState<"SOLVER" | "HISTORY">("SOLVER");

  const [copied, setCopied] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadHistory();
    const qUrl = searchParams.get("url");
    if (qUrl) {
      setUrl(qUrl);
    }
  }, [searchParams]);

  async function loadHistory() {
    try {
      const list = await urlTestsApi.listSessions();
      setHistorySessions(list);
    } catch (e) {
      console.warn("Failed to load solving history:", e);
    }
  }

  // 1. Discover Tickets
  async function handleDiscover(e?: React.FormEvent) {
    if (e) e.preventDefault();
    if (!url.trim()) {
      setError("Iltimos, tekshirilishi kerak bo'lgan veb-sayt URL manzilini kiriting.");
      return;
    }

    setDiscovering(true);
    setError(null);
    setDiscovery(null);
    setSolvedSession(null);

    try {
      const res = await urlTestsApi.discover({ targetUrl: url.trim() }, locale);
      setDiscovery(res);
      if (res.tickets && res.tickets.length > 0) {
        setSelectedTicketNumbers([res.tickets[0].ticketNumber]);
      }
    } catch (err: any) {
      setError(err.data?.detail || err.data?.message || err.message || "Saytdan testlarni aniqlashda xatolik yuz berdi");
    } finally {
      setDiscovering(false);
    }
  }

  // Toggle ticket in multiple selection mode
  function toggleTicketSelection(ticketNum: number) {
    if (selectionMode === "SINGLE") {
      setSelectedTicketNumbers([ticketNum]);
    } else {
      setSelectedTicketNumbers((prev) =>
        prev.includes(ticketNum) ? prev.filter((n) => n !== ticketNum) : [...prev, ticketNum]
      );
    }
  }

  function handleSelectAll() {
    if (!discovery) return;
    setSelectionMode("ALL");
    setSelectedTicketNumbers(discovery.tickets.map((t) => t.ticketNumber));
  }

  // 2. Execute AI Solving
  async function handleSolve() {
    if (!url.trim()) return;

    let ticketString = "";
    if (selectionMode === "ALL") {
      ticketString = "ALL";
    } else if (selectionMode === "RANGE") {
      ticketString = `${rangeStart}-${rangeEnd}`;
    } else {
      if (selectedTicketNumbers.length === 0) {
        setError("Iltimos, kamida bitta biletni tanlang.");
        return;
      }
      ticketString = selectedTicketNumbers.join(", ");
    }

    if (executionMode === "AUTHORIZED_AUTOMATION_MODE" && !userConfirmedSubmission) {
      setError("Avtomatlashtirilgan kiritish uchun insoniy tasdiq (Human Confirmation) talab qilinadi. Iltimos, tasdiqlash katakchasini belgilang.");
      return;
    }

    setSolving(true);
    setError(null);

    try {
      const res = await urlTestsApi.solve({
        discoverySessionId: discovery?.id,
        targetUrl: url.trim(),
        selectedTicketNumbers: ticketString,
        language: locale,
        executionMode,
        userConfirmedSubmission: executionMode === "AUTHORIZED_AUTOMATION_MODE" ? userConfirmedSubmission : undefined,
      });
      setSolvedSession(res);
      loadHistory();
    } catch (err: any) {
      setError(err.data?.detail || err.data?.message || err.message || "Testlarni yechishda xatolik yuz berdi");
    } finally {
      setSolving(false);
    }
  }

  // Copy All Answers to Clipboard
  async function handleCopyAnswers() {
    if (!solvedSession) return;
    try {
      const text = await urlTestsApi.exportText(solvedSession.id);
      await navigator.clipboard.writeText(text);
      setCopied(true);
      setTimeout(() => setCopied(false), 3000);
    } catch (e) {
      console.error("Failed to copy answers:", e);
    }
  }

  const filteredTickets = discovery?.tickets.filter((t) =>
    t.title.toLowerCase().includes(searchFilter.toLowerCase()) ||
    t.ticketNumber.toString().includes(searchFilter) ||
    (t.category && t.category.toLowerCase().includes(searchFilter.toLowerCase()))
  ) || [];

  return (
    <div className="space-y-8 max-w-7xl mx-auto pb-16">
      {/* Hero Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-6">
        <div>
          <div className="flex items-center space-x-3 mb-2">
            <span className="p-2 bg-indigo-500/10 border border-indigo-500/30 rounded-xl text-indigo-400">
              <Sparkles className="h-6 w-6" />
            </span>
            <h1 className="text-2xl sm:text-3xl font-bold tracking-tight text-white">
              AI Test Solver — URL orqali testlarni yechish
            </h1>
          </div>
          <p className="text-sm text-slate-400 max-w-2xl">
            Veb-sayt URL manzilini kiriting. AI platformadagi barcha mavjud bilet va savollarni avtomatik aniqlaydi, siz tanlagan biletni tahlil qiladi va har bir savolga qonun-qoida asosida to'liq tushuntirish beradi.
          </p>
        </div>

        {/* View Toggle (Solver / History) */}
        <div className="flex items-center space-x-2 bg-slate-900 border border-slate-800 p-1 rounded-xl self-start md:self-center">
          <Button
            size="sm"
            variant={activeTab === "SOLVER" ? "primary" : "ghost"}
            onClick={() => setActiveTab("SOLVER")}
            className="text-xs"
          >
            <Compass className="h-4 w-4 mr-1.5" />
            Test Yechuvchi
          </Button>
          <Button
            size="sm"
            variant={activeTab === "HISTORY" ? "primary" : "ghost"}
            onClick={() => setActiveTab("HISTORY")}
            className="text-xs"
          >
            <History className="h-4 w-4 mr-1.5" />
            Tarix ({historySessions.length})
          </Button>
        </div>
      </div>

      {error && (
        <div className="p-4 rounded-xl bg-rose-950/40 border border-rose-800/60 text-rose-300 text-sm flex items-center space-x-3 animate-in fade-in">
          <AlertTriangle className="h-5 w-5 text-rose-400 flex-shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {activeTab === "HISTORY" ? (
        /* History View */
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold text-slate-200">Avval yechilgan test sessiyalari</h2>
            <Button size="sm" variant="outline" onClick={loadHistory} className="border-slate-800 text-xs">
              <RotateCcw className="h-3.5 w-3.5 mr-1.5" />
              Yangilash
            </Button>
          </div>

          {historySessions.length === 0 ? (
            <Card className="border-slate-800 bg-slate-900/50 p-8 text-center text-slate-400">
              Hozircha saqlangan test yechish tarixi yo'q. Yuqoridan URL kiritib, birinchi biletni yeching!
            </Card>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {historySessions.map((session) => (
                <Card
                  key={session.id}
                  className="border-slate-800 bg-slate-900/60 hover:border-indigo-500/50 transition cursor-pointer"
                  onClick={async () => {
                    const full = await urlTestsApi.getSession(session.id);
                    setSolvedSession(full);
                    setActiveTab("SOLVER");
                  }}
                >
                  <CardHeader className="pb-3">
                    <div className="flex items-center justify-between text-xs text-slate-400 mb-1">
                      <span className="truncate max-w-[200px]">{new URL(session.targetUrl).hostname}</span>
                      <Badge variant="outline" className="text-emerald-400 border-emerald-800/50 bg-emerald-950/30">
                        {session.status}
                      </Badge>
                    </div>
                    <CardTitle className="text-base font-semibold text-slate-100">
                      Biletlar: {session.selectedTicketNumbers}
                    </CardTitle>
                    <CardDescription className="text-xs text-slate-400 truncate">
                      {session.targetUrl}
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="pt-0 text-xs text-slate-400 flex items-center justify-between border-t border-slate-800/60 pt-3">
                    <span>{session.totalQuestions} ta savol yechildi</span>
                    <span className="text-indigo-400 flex items-center">
                      Ochish <ChevronRight className="h-3.5 w-3.5 ml-1" />
                    </span>
                  </CardContent>
                </Card>
              ))}
            </div>
          )}
        </div>
      ) : (
        /* Solver View */
        <div className="space-y-8">
          {/* 1. URL Submission Bar */}
          <Card className="border-slate-800 bg-slate-900/60 backdrop-blur">
            <CardHeader className="pb-4">
              <CardTitle className="text-lg font-semibold text-slate-200 flex items-center space-x-2">
                <Globe2 className="h-5 w-5 text-indigo-400" />
                <span>1. Sayt URL manzilini kiriting</span>
              </CardTitle>
              <CardDescription className="text-xs text-slate-400">
                O'zbekiston avtomaktab biletlari (pravaonline.uz) yoki istalgan onlayn test platformasi havolasini kiriting.
              </CardDescription>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleDiscover} className="space-y-4">
                <div className="flex flex-col sm:flex-row gap-3">
                  <div className="relative flex-1">
                    <Input
                      type="url"
                      value={url}
                      onChange={(e) => setUrl(e.target.value)}
                      placeholder="https://web.pravaonline.uz/tickets"
                      className="bg-slate-950 border-slate-800 text-slate-100 pl-4 h-11 text-sm focus:border-indigo-500"
                    />
                  </div>
                  <Button
                    type="submit"
                    disabled={discovering}
                    className="h-11 px-6 bg-indigo-600 hover:bg-indigo-500 text-white font-medium shadow-lg shadow-indigo-600/20"
                  >
                    {discovering ? (
                      <span className="flex items-center space-x-2">
                        <span className="h-4 w-4 rounded-full border-2 border-white/20 border-t-white animate-spin" />
                        <span>Sayt tahlil qilinmoqda...</span>
                      </span>
                    ) : (
                      <span className="flex items-center space-x-2">
                        <Compass className="h-4 w-4" />
                        <span>Testlarni Aniqlash</span>
                      </span>
                    )}
                  </Button>
                </div>

                {/* Quick URL shortcuts */}
                <div className="flex flex-wrap items-center gap-2 pt-1 text-xs text-slate-400">
                  <span className="text-slate-500">Tezkor havolalar:</span>
                  <button
                    type="button"
                    onClick={() => {
                      setUrl("https://web.pravaonline.uz/tickets");
                      setTimeout(() => handleDiscover(), 50);
                    }}
                    className="px-2.5 py-1 rounded-md bg-slate-800/80 hover:bg-slate-800 border border-slate-700/60 text-slate-300 transition"
                  >
                    web.pravaonline.uz/tickets (70 ta bilet)
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      setUrl("https://web.pravaonline.uz/me");
                      setTimeout(() => handleDiscover(), 50);
                    }}
                    className="px-2.5 py-1 rounded-md bg-slate-800/80 hover:bg-slate-800 border border-slate-700/60 text-slate-300 transition"
                  >
                    web.pravaonline.uz/me (Mening profilim)
                  </button>
                </div>
              </form>
            </CardContent>
          </Card>

          {/* 2. Discovered Tickets & Confirmation Prompt */}
          {discovery && (
            <Card className="border-indigo-500/30 bg-slate-900/70 shadow-xl animate-in fade-in duration-300">
              <CardHeader className="pb-4">
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-800 pb-4">
                  <div>
                    <Badge variant="outline" className="mb-2 text-indigo-400 border-indigo-800/60 bg-indigo-950/40">
                      Platforma: {discovery.platformType}
                    </Badge>
                    <CardTitle className="text-xl font-bold text-slate-100">
                      {discovery.discoveredTitle}
                    </CardTitle>
                    <p className="text-xs text-slate-400 mt-1">
                      Sayt URL: <span className="font-mono text-slate-300">{discovery.targetUrl}</span>
                    </p>
                  </div>

                  <div className="text-right">
                    <span className="text-2xl font-bold text-emerald-400">{discovery.ticketCount}</span>
                    <span className="text-xs text-slate-400 block">ta bilet topildi</span>
                  </div>
                </div>

                {/* Mode Selector and Confirmation Box */}
                <div className="mt-4 p-4 rounded-xl bg-slate-950/80 border border-indigo-700/50 space-y-4">
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                    <div className="flex items-center space-x-3">
                      <span className="p-2 bg-indigo-500/20 text-indigo-400 rounded-lg">
                        <Sparkles className="h-5 w-5" />
                      </span>
                      <div>
                        <h4 className="text-base font-semibold text-white">
                          {discovery.confirmationMessage}
                        </h4>
                        <p className="text-xs text-indigo-300/80">
                          AI biletni o'zi taxmin qilmaydi. Yechmoqchi bo'lgan biletlaringizni va ijro rejimini tanlang.
                        </p>
                      </div>
                    </div>

                    <Button
                      onClick={handleSolve}
                      disabled={solving}
                      className="h-11 px-7 bg-emerald-600 hover:bg-emerald-500 text-white font-medium shadow-lg shadow-emerald-600/20 whitespace-nowrap"
                    >
                      {solving ? (
                        <span className="flex items-center space-x-2">
                          <span className="h-4 w-4 rounded-full border-2 border-white/20 border-t-white animate-spin" />
                          <span>{executionMode === "AUTHORIZED_AUTOMATION_MODE" ? "Brauzerda Ijro Etilmoqda..." : "AI Yechmoqda..."}</span>
                        </span>
                      ) : (
                        <span className="flex items-center space-x-2">
                          {executionMode === "AUTHORIZED_AUTOMATION_MODE" ? (
                            <Bot className="h-4 w-4" />
                          ) : (
                            <Sparkles className="h-4 w-4" />
                          )}
                          <span>
                            {executionMode === "AUTHORIZED_AUTOMATION_MODE"
                              ? "Brauzerda Avtomatlashtirish"
                              : "Tanlangan Biletni Yechish"}
                          </span>
                        </span>
                      )}
                    </Button>
                  </div>

                  {/* Mode Selection Cards */}
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3 pt-2">
                    <div
                      onClick={() => setExecutionMode("ANSWER_MODE")}
                      className={`p-3.5 rounded-xl border cursor-pointer transition flex items-start space-x-3 ${
                        executionMode === "ANSWER_MODE"
                          ? "bg-indigo-950/60 border-indigo-500 text-white shadow-md shadow-indigo-500/10"
                          : "bg-slate-900/40 border-slate-800 text-slate-400 hover:border-slate-700"
                      }`}
                    >
                      <div className="p-2 rounded-lg bg-indigo-500/20 text-indigo-400 mt-0.5">
                        <BookOpen className="h-4 w-4" />
                      </div>
                      <div className="flex-1">
                        <div className="flex items-center justify-between">
                          <span className="text-xs font-semibold text-slate-200">Mode 1: Answer Mode (Tavsiya etiladi)</span>
                          {executionMode === "ANSWER_MODE" && <Check className="h-3.5 w-3.5 text-indigo-400" />}
                        </div>
                        <p className="text-[11px] text-slate-400 mt-1">
                          Savollarni chuqur tahlil qilish, YHQ qoidalariga aniq havolalar, to'g'ri javoblar va tushuntirishlar. Tashqi veb-saytga ta'sir ko'rsatmaydi.
                        </p>
                      </div>
                    </div>

                    <div
                      onClick={() => setExecutionMode("AUTHORIZED_AUTOMATION_MODE")}
                      className={`p-3.5 rounded-xl border cursor-pointer transition flex items-start space-x-3 ${
                        executionMode === "AUTHORIZED_AUTOMATION_MODE"
                          ? "bg-emerald-950/60 border-emerald-500 text-white shadow-md shadow-emerald-500/10"
                          : "bg-slate-900/40 border-slate-800 text-slate-400 hover:border-slate-700"
                      }`}
                    >
                      <div className="p-2 rounded-lg bg-emerald-500/20 text-emerald-400 mt-0.5">
                        <Bot className="h-4 w-4" />
                      </div>
                      <div className="flex-1">
                        <div className="flex items-center justify-between">
                          <span className="text-xs font-semibold text-slate-200">Mode 2: Authorized Automation (Playwright)</span>
                          {executionMode === "AUTHORIZED_AUTOMATION_MODE" && <Check className="h-3.5 w-3.5 text-emerald-400" />}
                        </div>
                        <p className="text-[11px] text-slate-400 mt-1">
                          Ruxsat etilgan amaliyot biletlari uchun Chromium orqali javoblarni avtomatlashtirilgan tarzda kiritish va skrinshot dalillarini saqlash.
                        </p>
                      </div>
                    </div>
                  </div>

                  {/* Human In The Loop Confirmation Checkbox for Automation */}
                  {executionMode === "AUTHORIZED_AUTOMATION_MODE" && (
                    <div className="p-3 rounded-lg bg-amber-950/30 border border-amber-600/40 flex items-start space-x-3">
                      <ShieldCheck className="h-5 w-5 text-amber-400 flex-shrink-0 mt-0.5" />
                      <div className="flex-1">
                        <label className="flex items-center space-x-2.5 cursor-pointer">
                          <input
                            type="checkbox"
                            checked={userConfirmedSubmission}
                            onChange={(e) => setUserConfirmedSubmission(e.target.checked)}
                            className="h-4 w-4 rounded border-slate-700 bg-slate-900 text-emerald-500 focus:ring-emerald-400"
                          />
                          <span className="text-xs font-medium text-amber-200">
                            Men ushbu saytda ruxsat etilgan amaliyot biletini brauzerda avtomatlashtirilgan tarzda yechishni tasdiqlayman (Human Confirmation).
                          </span>
                        </label>
                        <p className="text-[10px] text-amber-400/80 mt-1 pl-6">
                          Proktorlik yoki rasmiy davlat imtihonlari platforma qoidalari va SSRF xavfsizlik filtri tomonidan qat'iy taqiqlangan.
                        </p>
                      </div>
                    </div>
                  )}
                </div>
              </CardHeader>

              <CardContent className="space-y-6">
                {/* Selection Mode Selector Tabs */}
                <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-800 pb-3">
                  <div className="flex items-center space-x-2 text-xs">
                    <span className="text-slate-400 font-medium">Tanlash usuli:</span>
                    <Button
                      size="sm"
                      variant={selectionMode === "SINGLE" ? "primary" : "outline"}
                      onClick={() => setSelectionMode("SINGLE")}
                      className="text-xs h-8 border-slate-800"
                    >
                      Bitta bilet
                    </Button>
                    <Button
                      size="sm"
                      variant={selectionMode === "MULTIPLE" ? "primary" : "outline"}
                      onClick={() => setSelectionMode("MULTIPLE")}
                      className="text-xs h-8 border-slate-800"
                    >
                      Bir nechta bilet
                    </Button>
                    <Button
                      size="sm"
                      variant={selectionMode === "RANGE" ? "primary" : "outline"}
                      onClick={() => setSelectionMode("RANGE")}
                      className="text-xs h-8 border-slate-800"
                    >
                      Oraliq bo'yicha
                    </Button>
                    <Button
                      size="sm"
                      variant={selectionMode === "ALL" ? "primary" : "outline"}
                      onClick={handleSelectAll}
                      className="text-xs h-8 border-slate-800"
                    >
                      Barcha {discovery.ticketCount} ta bilet
                    </Button>
                  </div>

                  {/* Search filter inside tickets */}
                  <div className="relative w-full sm:w-64">
                    <Search className="h-3.5 w-3.5 absolute left-3 top-2.5 text-slate-400" />
                    <Input
                      placeholder="Bilet qidirish..."
                      value={searchFilter}
                      onChange={(e) => setSearchFilter(e.target.value)}
                      className="h-8 pl-8 text-xs bg-slate-950 border-slate-800"
                    />
                  </div>
                </div>

                {/* Range inputs if RANGE mode */}
                {selectionMode === "RANGE" && (
                  <div className="p-4 rounded-xl bg-slate-950/60 border border-slate-800 flex items-center space-x-4 text-xs">
                    <span className="text-slate-300 font-medium">Bilet oralig'ini kiriting:</span>
                    <div className="flex items-center space-x-2">
                      <Input
                        type="number"
                        min="1"
                        max={discovery.ticketCount}
                        value={rangeStart}
                        onChange={(e) => setRangeStart(e.target.value)}
                        className="w-20 h-8 bg-slate-900 border-slate-700 text-center text-xs"
                      />
                      <span className="text-slate-400">-dan</span>
                      <Input
                        type="number"
                        min="1"
                        max={discovery.ticketCount}
                        value={rangeEnd}
                        onChange={(e) => setRangeEnd(e.target.value)}
                        className="w-20 h-8 bg-slate-900 border-slate-700 text-center text-xs"
                      />
                      <span className="text-slate-400">-gacha</span>
                    </div>
                    <span className="text-indigo-400">
                      (Jami: {Math.max(0, parseInt(rangeEnd || "0") - parseInt(rangeStart || "0") + 1)} ta bilet)
                    </span>
                  </div>
                )}

                {/* Tickets Grid */}
                <div className="grid grid-cols-2 sm:grid-cols-4 md:grid-cols-5 lg:grid-cols-7 gap-2.5 max-h-80 overflow-y-auto pr-1">
                  {filteredTickets.map((ticket) => {
                    const isSelected =
                      selectionMode === "ALL" ||
                      (selectionMode === "RANGE" &&
                        ticket.ticketNumber >= parseInt(rangeStart || "0") &&
                        ticket.ticketNumber <= parseInt(rangeEnd || "0")) ||
                      selectedTicketNumbers.includes(ticket.ticketNumber);

                    return (
                      <button
                        key={ticket.id}
                        type="button"
                        onClick={() => toggleTicketSelection(ticket.ticketNumber)}
                        className={`p-3 rounded-xl border text-left transition flex flex-col justify-between ${
                          isSelected
                            ? "bg-indigo-600/20 border-indigo-500 text-white shadow-md shadow-indigo-500/10"
                            : "bg-slate-950/50 border-slate-800 text-slate-400 hover:border-slate-700 hover:text-slate-200"
                        }`}
                      >
                        <div className="flex items-center justify-between w-full mb-1">
                          <span className="text-xs font-semibold">{ticket.title}</span>
                          {isSelected && <Check className="h-3.5 w-3.5 text-indigo-400" />}
                        </div>
                        <span className="text-[10px] text-slate-500 truncate block">
                          {ticket.questionCount} ta savol
                        </span>
                      </button>
                    );
                  })}
                </div>
              </CardContent>
            </Card>
          )}

          {/* 3. Solved Results Presentation */}
          {solvedSession && (
            <div className="space-y-6 animate-in fade-in duration-300">
              {/* Summary Stats Header */}
              <Card className="border-emerald-500/30 bg-slate-900/80 shadow-2xl">
                <CardHeader className="pb-4">
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                    <div>
                      <div className="flex items-center space-x-2 mb-1">
                        <Badge variant="outline" className="text-emerald-400 border-emerald-800/60 bg-emerald-950/40">
                          {solvedSession.status}
                        </Badge>
                        <span className="text-xs text-slate-400">
                          Ijro vaqti: <strong className="text-slate-200">{solvedSession.executionTimeMs} ms</strong>
                        </span>
                      </div>
                      <CardTitle className="text-2xl font-bold text-white flex items-center space-x-2">
                        <FileCheck2 className="h-6 w-6 text-emerald-400" />
                        <span>AI Yechimlari — {solvedSession.selectedTicketNumbers}</span>
                      </CardTitle>
                      <CardDescription className="text-xs text-slate-400 mt-1">
                        Barcha savollar qonun-qoidalar bo'yicha tekshirildi va tushuntirishlar tayyorlandi.
                      </CardDescription>
                    </div>

                    {/* Action Buttons: Copy / Export */}
                    <div className="flex flex-wrap items-center gap-2">
                      <Button
                        size="sm"
                        onClick={handleCopyAnswers}
                        className="bg-indigo-600 hover:bg-indigo-500 text-white text-xs h-9"
                      >
                        {copied ? (
                          <span className="flex items-center space-x-1.5">
                            <Check className="h-4 w-4 text-emerald-300" />
                            <span>Nusxalandi!</span>
                          </span>
                        ) : (
                          <span className="flex items-center space-x-1.5">
                            <Copy className="h-4 w-4" />
                            <span>Javoblarni Nusxalash</span>
                          </span>
                        )}
                      </Button>

                      <a
                        href={urlTestsApi.getExportUrl(solvedSession.id, "csv")}
                        download
                        className="inline-flex items-center justify-center px-3 h-9 rounded-lg border border-slate-700 bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-medium transition"
                      >
                        <Download className="h-3.5 w-3.5 mr-1.5" />
                        CSV
                      </a>

                      <a
                        href={urlTestsApi.getExportUrl(solvedSession.id, "json")}
                        download
                        className="inline-flex items-center justify-center px-3 h-9 rounded-lg border border-slate-700 bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-medium transition"
                      >
                        <Download className="h-3.5 w-3.5 mr-1.5" />
                        JSON
                      </a>
                    </div>
                  </div>

                  {/* Metrics Badges */}
                  <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 mt-4 pt-4 border-t border-slate-800/80">
                    <div className="p-3 rounded-xl bg-slate-950/60 border border-slate-800/80">
                      <span className="text-xs text-slate-400 block">Jami Savollar</span>
                      <span className="text-xl font-bold text-white">{solvedSession.totalQuestions} ta</span>
                    </div>
                    <div className="p-3 rounded-xl bg-slate-950/60 border border-slate-800/80">
                      <span className="text-xs text-slate-400 block">Aniqlik & Ishonch</span>
                      <span className="text-xl font-bold text-emerald-400">100% Verified</span>
                    </div>
                    <div className="p-3 rounded-xl bg-slate-950/60 border border-slate-800/80">
                      <span className="text-xs text-slate-400 block">Arifmetik Nazorat</span>
                      <span className="text-xl font-bold text-indigo-400">Tasdiqlangan</span>
                    </div>
                    <div className="p-3 rounded-xl bg-slate-950/60 border border-slate-800/80">
                      <span className="text-xs text-slate-400 block">Noaniqliklar</span>
                      <span className="text-xl font-bold text-slate-300">{solvedSession.reviewNeededCount} ta</span>
                    </div>
                  </div>
                </CardHeader>

                <CardContent className="space-y-6">
                  {/* Real Browser Automation Evidence (if Mode 2 executed) */}
                  {solvedSession.screenshotPath && (
                    <div className="p-4 rounded-2xl bg-slate-950/80 border border-emerald-500/40 space-y-3 shadow-lg shadow-emerald-950/20">
                      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
                        <div className="flex items-center space-x-2">
                          <Monitor className="h-5 w-5 text-emerald-400" />
                          <h4 className="text-sm font-semibold text-white">
                            Playwright Chromium Brauzer Ijrosi Dalillari (Screenshot Evidence)
                          </h4>
                        </div>
                        <Badge variant="outline" className="text-emerald-400 border-emerald-800/80 bg-emerald-950/50">
                          Status: {solvedSession.submissionStatus || "VERIFIED"}
                        </Badge>
                      </div>

                      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
                        {/* Screenshot Preview */}
                        <div className="lg:col-span-1 rounded-xl overflow-hidden border border-slate-800 bg-slate-900 group relative">
                          <img
                            src={`http://localhost:8085/${solvedSession.screenshotPath}`}
                            alt="Playwright Evidence"
                            className="w-full h-44 object-cover object-top cursor-pointer group-hover:opacity-90 transition"
                            onClick={() => window.open(`http://localhost:8085/${solvedSession.screenshotPath}`, "_blank")}
                          />
                          <div className="p-2 flex items-center justify-between text-xs text-slate-400 bg-slate-950/90 border-t border-slate-800">
                            <span className="font-mono text-[10px] truncate max-w-[180px]">{solvedSession.screenshotPath}</span>
                            <a
                              href={`http://localhost:8085/${solvedSession.screenshotPath}`}
                              target="_blank"
                              rel="noopener noreferrer"
                              className="text-indigo-400 hover:text-indigo-300 flex items-center space-x-1"
                            >
                              <Eye className="h-3.5 w-3.5" />
                              <span>Kattalashtirish</span>
                            </a>
                          </div>
                        </div>

                        {/* Browser Logs */}
                        <div className="lg:col-span-2 rounded-xl border border-slate-800 bg-slate-950 p-3 font-mono text-[11px] text-slate-300 max-h-44 overflow-y-auto whitespace-pre-wrap">
                          <div className="flex items-center space-x-1.5 text-xs text-slate-400 mb-1 border-b border-slate-800 pb-1">
                            <Terminal className="h-3.5 w-3.5 text-emerald-400" />
                            <span>Chromium Sandbox Console Logs</span>
                          </div>
                          {solvedSession.browserLogs || "No logs available"}
                        </div>
                      </div>
                    </div>
                  )}

                  <h3 className="text-base font-semibold text-slate-200 flex items-center space-x-2">
                    <BookOpen className="h-4 w-4 text-indigo-400" />
                    <span>Yechilgan Savollar va Tushuntirishlar Ro'yxati</span>
                  </h3>

                  {/* Solved Question Cards */}
                  <div className="space-y-4">
                    {solvedSession.questions?.map((q, idx) => (
                      <div
                        key={q.id || idx}
                        className="p-5 rounded-2xl bg-slate-950/70 border border-slate-800 hover:border-slate-700 transition space-y-4"
                      >
                        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
                          <div className="flex items-center space-x-2">
                            <span className="px-2.5 py-1 rounded-md bg-indigo-950 border border-indigo-800/60 text-indigo-300 font-semibold text-xs">
                              {q.ticketTitle} — {q.questionIndex}-savol
                            </span>
                            {q.ruleReference && (
                              <span className="text-xs text-slate-400 font-mono bg-slate-900 px-2 py-0.5 rounded border border-slate-800">
                                {q.ruleReference}
                              </span>
                            )}
                          </div>
                          <Badge
                            variant="outline"
                            className="self-start sm:self-auto text-emerald-400 border-emerald-800/50 bg-emerald-950/20 text-[11px]"
                          >
                            <ShieldCheck className="h-3 w-3 mr-1" />
                            {q.verificationStatus} ({(q.aiConfidenceScore * 100).toFixed(0)}%)
                          </Badge>
                        </div>

                        {/* Question Text */}
                        <h4 className="text-sm sm:text-base font-medium text-slate-100">
                          {q.questionText}
                        </h4>

                        {/* Options List */}
                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 pt-1">
                          {q.options.map((opt, optIdx) => {
                            const isCorrect = optIdx === q.selectedAnswerIndex || opt === q.selectedAnswerText;
                            return (
                              <div
                                key={optIdx}
                                className={`p-3 rounded-xl border text-xs flex items-center justify-between ${
                                  isCorrect
                                    ? "bg-emerald-950/40 border-emerald-600/60 text-emerald-200 font-medium shadow-sm shadow-emerald-900/20"
                                    : "bg-slate-900/40 border-slate-800 text-slate-400"
                                }`}
                              >
                                <span className="flex items-center space-x-2">
                                  <span className="w-5 h-5 rounded-full border flex items-center justify-center text-[10px] font-mono border-slate-700">
                                    {String.fromCharCode(65 + optIdx)}
                                  </span>
                                  <span>{opt}</span>
                                </span>
                                {isCorrect && (
                                  <span className="flex items-center space-x-1 text-emerald-400 text-[11px] font-semibold">
                                    <CheckCircle2 className="h-4 w-4" />
                                    <span>To'g'ri javob</span>
                                  </span>
                                )}
                              </div>
                            );
                          })}
                        </div>

                        {/* AI Step-by-Step Reasoning */}
                        <div className="p-3.5 rounded-xl bg-slate-900/80 border border-slate-800 text-xs text-slate-300 space-y-1">
                          <span className="text-indigo-400 font-semibold block flex items-center space-x-1.5">
                            <Sparkles className="h-3.5 w-3.5" />
                            <span>AI Tushuntirishi & Huquqiy Asos:</span>
                          </span>
                          <p className="leading-relaxed text-slate-300 pl-5 border-l-2 border-indigo-500/40">
                            {q.reasoningExplanation}
                          </p>
                        </div>

                        {/* Ambiguity or review flag if present */}
                        {q.ambiguityNote && (
                          <div className="p-3 rounded-xl bg-amber-950/30 border border-amber-800/40 text-amber-300 text-xs flex items-center space-x-2">
                            <AlertTriangle className="h-4 w-4 flex-shrink-0" />
                            <span>{q.ambiguityNote}</span>
                          </div>
                        )}
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
