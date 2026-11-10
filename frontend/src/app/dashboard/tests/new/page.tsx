"use client";

import { Suspense, useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { useI18n } from "@/lib/i18n";
import {
  webProjectsApi,
  webTestsApi,
  WebProject,
  WebTestPlan,
} from "@/lib/api";
import {
  Sparkles,
  PlayCircle,
  CheckCircle2,
  AlertCircle,
  ArrowRight,
  ArrowLeft,
  Globe2,
  Lock,
  Layers,
  Check,
  Key,
  ShieldCheck,
  Info,
  ExternalLink,
  HelpCircle,
  ChevronDown,
  ChevronUp,
} from "lucide-react";

function NewTestWizardContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { t, locale } = useI18n();

  const [step, setStep] = useState(1);
  const [projects, setProjects] = useState<WebProject[]>([]);
  const [selectedProjectId, setSelectedProjectId] = useState<string>("");
  const [targetUrl, setTargetUrl] = useState<string>("");

  // Natural language objective
  const [objective, setObjective] = useState<string>("");

  // Scopes
  const [scopes, setScopes] = useState<string[]>([
    "FUNCTIONAL",
    "RESPONSIVE",
    "ACCESSIBILITY",
    "SECURITY",
  ]);

  // Auth credentials & SSO session
  const [authEnabled, setAuthEnabled] = useState(true);
  const [authType, setAuthType] = useState<"GOOGLE_TELEGRAM_SSO" | "BEARER_TOKEN" | "PASSWORD" | "NONE">("GOOGLE_TELEGRAM_SSO");
  const [sessionCookie, setSessionCookie] = useState("");
  const [bearerToken, setBearerToken] = useState("");
  const [authUsername, setAuthUsername] = useState("");
  const [authPassword, setAuthPassword] = useState("");
  const [showDevToolsGuide, setShowDevToolsGuide] = useState(false);

  // AI Plan Generation & Approval
  const [generating, setGenerating] = useState(false);
  const [generatedPlan, setGeneratedPlan] = useState<WebTestPlan | null>(null);
  const [startingRun, setStartingRun] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Multi-lingual sample prompts
  const samplePrompts = {
    "uz-Latn": "Saytga kirib ro'yxatdan o'tishni tekshir. Login, parolni tiklash, profilni tahrirlash, mahsulot qidirish, savatga qo'shish va buyurtma berish jarayonlarini tekshir. Barcha xatolarni aniqlab hisobot tayyorla.",
    "uz-Cyrl": "Сайтга кириб рўйхатдан ўтишни текшир. Логин, паролни тиклаш, профилни таҳрирлаш, маҳсулот қидириш, саватга қўшиш ва буюртма бериш жараёнларини текшир. Барча хатоларни аниқлаб ҳисобот тайёрла.",
    "ru": "Проверь сайт полностью: главную страницу, навигацию, формы авторизации, поиск, адаптивность на мобильных устройствах и доступность WCAG 2.2.",
    "en": "Test all major website functionality: homepage navigation, registration and login workflows, responsive mobile layout at 375px, and WCAG 2.2 AA accessibility.",
  };

  useEffect(() => {
    async function loadProjects() {
      try {
        const list = await webProjectsApi.list();
        setProjects(list);
        const paramId = searchParams.get("projectId");
        if (paramId) {
          const found = list.find((p) => p.id === paramId);
          if (found) {
            setSelectedProjectId(found.id);
            setTargetUrl(found.targetUrl);
          }
        } else if (list.length > 0) {
          setSelectedProjectId(list[0].id);
          setTargetUrl(list[0].targetUrl);
        }
      } catch (err) {
        console.error("Failed to load projects:", err);
      }
    }
    loadProjects();
  }, [searchParams]);

  function handleSelectProject(id: string) {
    setSelectedProjectId(id);
    const p = projects.find((item) => item.id === id);
    if (p) setTargetUrl(p.targetUrl);
  }

  function toggleScope(scope: string) {
    setScopes((prev) =>
      prev.includes(scope) ? prev.filter((s) => s !== scope) : [...prev, scope]
    );
  }

  function applySamplePrompt() {
    const prompt = samplePrompts[locale as keyof typeof samplePrompts] || samplePrompts.en;
    setObjective(prompt);
  }

  async function handleGeneratePlan() {
    if (!targetUrl) {
      setError("Please select or enter a target website URL.");
      return;
    }
    if (!objective.trim()) {
      setError("Please describe what you want to test.");
      return;
    }

    let cleanUrl = targetUrl.trim();
    if (!cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://")) {
      cleanUrl = "https://" + cleanUrl;
      setTargetUrl(cleanUrl);
    }

    setGenerating(true);
    setError(null);

    try {
      let projectId = selectedProjectId;
      if (!projectId) {
        // Automatically reuse existing project if already registered for this URL
        const existing = projects.find(
          (p) =>
            p.targetUrl.toLowerCase() === cleanUrl.toLowerCase() ||
            p.targetUrl.toLowerCase() === (cleanUrl + "/").toLowerCase() ||
            (cleanUrl + "/").toLowerCase() === p.targetUrl.toLowerCase()
        );
        if (existing) {
          projectId = existing.id;
          setSelectedProjectId(existing.id);
        } else {
          // Automatically register website
          const newProj = await webProjectsApi.create({
            name: new URL(cleanUrl).hostname,
            targetUrl: cleanUrl,
            environment: "PRODUCTION",
            isAuthorized: true,
          });
          projectId = newProj.id;
          setSelectedProjectId(newProj.id);
        }
      }

      const effectiveAuthType = authEnabled ? authType : "NONE";

      const plan = await webTestsApi.generatePlan({
        projectId,
        naturalLanguageGoal: objective,
        language: locale,
        scopes,
        authType: effectiveAuthType,
        authUsername: authUsername.trim() || undefined,
        authPassword: authPassword.trim() || undefined,
        sessionCookie: sessionCookie.trim() || undefined,
        bearerToken: bearerToken.trim() || undefined,
      });

      setGeneratedPlan(plan);
      setStep(5);
    } catch (err: any) {
      const msg = err.data?.detail || err.data?.message || err.data?.title || err.message || "Failed to generate AI test plan";
      setError(msg);
    } finally {
      setGenerating(false);
    }
  }

  async function handleApproveAndExecute() {
    if (!generatedPlan) return;

    setStartingRun(true);
    setError(null);
    try {
      await webTestsApi.approvePlan(generatedPlan.id);

      const effectiveAuthType = authEnabled ? authType : "NONE";

      const run = await webTestsApi.startRun({
        planId: generatedPlan.id,
        browserType: "CHROMIUM",
        viewportWidth: 1280,
        viewportHeight: 800,
        authType: effectiveAuthType,
        authUsername: authUsername.trim() || undefined,
        authPassword: authPassword.trim() || undefined,
        sessionCookie: sessionCookie.trim() || undefined,
        bearerToken: bearerToken.trim() || undefined,
      });

      router.push(`/dashboard/tests/${run.id}`);
    } catch (err: any) {
      setError(err.message || "Failed to start test execution");
      setStartingRun(false);
    }
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* HEADER */}
      <div className="space-y-1">
        <h1 className="text-2xl font-bold tracking-tight text-white">{t.webTesting.wizardTitle}</h1>
        <p className="text-sm text-slate-400">
          {t.webTesting.wizardSubtitle}
        </p>
      </div>

      {/* STEP INDICATOR */}
      <div className="flex items-center justify-between border-b border-slate-800 pb-4">
        {[
          { num: 1, label: t.webTesting.step1 },
          { num: 2, label: t.webTesting.step2 },
          { num: 3, label: t.webTesting.step3 },
          { num: 4, label: t.webTesting.step4 },
          { num: 5, label: t.webTesting.step5 },
        ].map((s) => (
          <div
            key={s.num}
            onClick={() => s.num < step && setStep(s.num)}
            className={`flex items-center space-x-2 text-xs font-medium cursor-pointer transition-colors ${
              step === s.num
                ? "text-indigo-400 font-bold"
                : s.num < step
                ? "text-slate-400 hover:text-white"
                : "text-slate-600 cursor-not-allowed"
            }`}
          >
            <div
              className={`flex h-6 w-6 items-center justify-center rounded-full text-xs ${
                step === s.num
                  ? "bg-indigo-600 text-white"
                  : s.num < step
                  ? "bg-emerald-600/30 text-emerald-400 border border-emerald-500/40"
                  : "bg-slate-800 text-slate-500"
              }`}
            >
              {s.num < step ? <Check className="h-3 w-3" /> : s.num}
            </div>
            <span className="hidden sm:inline">{s.label}</span>
          </div>
        ))}
      </div>

      {error && (
        <div className="p-3.5 rounded-lg bg-rose-500/10 border border-rose-500/20 text-rose-400 text-xs flex items-center gap-2">
          <AlertCircle className="h-4 w-4 flex-shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* STEP 1: TARGET URL */}
      {step === 1 && (
        <Card className="p-6 space-y-5">
          <div className="space-y-1">
            <h3 className="text-base font-semibold text-white">Select or Enter Website</h3>
            <p className="text-xs text-slate-400">Choose from your registered websites or enter a new authorized URL.</p>
          </div>

          {projects.length > 0 && (
            <div className="space-y-1.5">
              <label className="text-xs font-medium text-slate-300">Registered Website</label>
              <select
                value={selectedProjectId}
                onChange={(e) => handleSelectProject(e.target.value)}
                className="w-full h-10 px-3 rounded-lg bg-slate-950 border border-slate-800 text-xs text-white focus:outline-none focus:ring-1 focus:ring-indigo-500"
              >
                {projects.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.name} ({p.targetUrl})
                  </option>
                ))}
              </select>
            </div>
          )}

          <div className="space-y-1.5">
            <label className="text-xs font-medium text-slate-300">Website URL (HTTPS)</label>
            <Input
              type="url"
              placeholder="https://example.com"
              value={targetUrl}
              onChange={(e) => setTargetUrl(e.target.value)}
            />
          </div>

          <div className="flex justify-end pt-2">
            <Button
              onClick={() => {
                if (!targetUrl) {
                  setError("Target URL is required");
                  return;
                }
                setError(null);
                setStep(2);
              }}
              className="bg-indigo-600 hover:bg-indigo-500 text-white"
            >
              Next: Goal & Instructions
              <ArrowRight className="ml-2 h-4 w-4" />
            </Button>
          </div>
        </Card>
      )}

      {/* STEP 2: OBJECTIVE */}
      {step === 2 && (
        <Card className="p-6 space-y-5">
          <div className="flex items-center justify-between">
            <div className="space-y-1">
              <h3 className="text-base font-semibold text-white">{t.webTesting.objectiveLabel}</h3>
              <p className="text-xs text-slate-400">{t.webTesting.objectiveHelp}</p>
            </div>
            <Button size="sm" variant="outline" onClick={applySamplePrompt} className="text-xs">
              <Sparkles className="mr-1.5 h-3.5 w-3.5 text-indigo-400" />
              {t.webTesting.applySample}
            </Button>
          </div>

          <textarea
            rows={5}
            className="w-full p-3.5 rounded-lg bg-slate-950 border border-slate-800 text-xs text-white leading-relaxed focus:outline-none focus:ring-1 focus:ring-indigo-500 font-sans"
            placeholder={samplePrompts[locale as keyof typeof samplePrompts] || samplePrompts.en}
            value={objective}
            onChange={(e) => setObjective(e.target.value)}
          />

          <div className="flex justify-between pt-2">
            <Button variant="outline" onClick={() => setStep(1)}>
              <ArrowLeft className="mr-2 h-4 w-4" />
              Back
            </Button>
            <Button
              onClick={() => {
                if (!objective.trim()) {
                  setError("Please write or apply a testing objective");
                  return;
                }
                setError(null);
                setStep(3);
              }}
              className="bg-indigo-600 hover:bg-indigo-500 text-white"
            >
              Next: Testing Scope
              <ArrowRight className="ml-2 h-4 w-4" />
            </Button>
          </div>
        </Card>
      )}

      {/* STEP 3: SCOPES */}
      {step === 3 && (
        <Card className="p-6 space-y-5">
          <div className="space-y-1">
            <h3 className="text-base font-semibold text-white">Select Testing Scopes</h3>
            <p className="text-xs text-slate-400">Choose which categories of automated quality checks the AI should execute.</p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            {[
              { id: "FUNCTIONAL", title: t.webTesting.scopeFunctional, desc: "User navigation, links, and forms" },
              { id: "RESPONSIVE", title: t.webTesting.scopeResponsive, desc: "Viewport overflows and layout shifts" },
              { id: "ACCESSIBILITY", title: t.webTesting.scopeA11y, desc: "WCAG 2.2 AA alt tags & labels" },
              { id: "SECURITY", title: t.webTesting.scopeSecurity, desc: "TLS encryption & security headers" },
              { id: "PERFORMANCE", title: t.webTesting.scopePerformance, desc: "LCP, navigation time, and assets" },
            ].map((s) => {
              const active = scopes.includes(s.id);
              return (
                <div
                  key={s.id}
                  onClick={() => toggleScope(s.id)}
                  className={`p-3.5 rounded-lg border cursor-pointer transition-colors ${
                    active
                      ? "bg-indigo-950/40 border-indigo-500/40 text-white"
                      : "bg-slate-950 border-slate-800 text-slate-400 hover:border-slate-700"
                  }`}
                >
                  <div className="flex items-center justify-between mb-1">
                    <span className="font-semibold text-xs">{s.title}</span>
                    <div
                      className={`h-4 w-4 rounded flex items-center justify-center text-[10px] ${
                        active ? "bg-indigo-600 text-white" : "border border-slate-700"
                      }`}
                    >
                      {active && <Check className="h-3 w-3" />}
                    </div>
                  </div>
                  <p className="text-[11px] text-slate-500">{s.desc}</p>
                </div>
              );
            })}
          </div>

          <div className="flex justify-between pt-2">
            <Button variant="outline" onClick={() => setStep(2)}>
              <ArrowLeft className="mr-2 h-4 w-4" />
              Back
            </Button>
            <Button onClick={() => setStep(4)} className="bg-indigo-600 hover:bg-indigo-500 text-white">
              Next: Authentication (Optional)
              <ArrowRight className="ml-2 h-4 w-4" />
            </Button>
          </div>
        </Card>
      )}

      {/* STEP 4: AUTH (SSO, GOOGLE, TELEGRAM, BEARER, PASSWORD) */}
      {step === 4 && (
        <Card className="p-6 space-y-5">
          <div className="space-y-1">
            <h3 className="text-base font-semibold text-white flex items-center gap-2">
              <Key className="h-4 w-4 text-indigo-400" />
              {t.webTesting.authTitle}
            </h3>
            <p className="text-xs text-slate-400">
              {t.webTesting.authSubtitle}
            </p>
          </div>

          {/* Toggle Auth Required */}
          <div className="flex items-center space-x-2.5 p-3 rounded-lg bg-slate-950 border border-slate-800">
            <input
              type="checkbox"
              id="enableAuth"
              checked={authEnabled}
              onChange={(e) => setAuthEnabled(e.target.checked)}
              className="rounded border-slate-700 bg-slate-900 text-indigo-600 focus:ring-indigo-500"
            />
            <label htmlFor="enableAuth" className="text-xs text-slate-200 font-medium cursor-pointer flex items-center gap-2">
              <ShieldCheck className="h-4 w-4 text-emerald-400" />
              {t.webTesting.authTitle} (Protected Dashboards / Profile / Tickets)
            </label>
          </div>

          {authEnabled && (
            <div className="space-y-4 pt-1">
              {/* Method Selection Tabs */}
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-2.5">
                {/* Method 1: Google / Telegram SSO */}
                <div
                  onClick={() => setAuthType("GOOGLE_TELEGRAM_SSO")}
                  className={`p-3 rounded-lg border cursor-pointer transition-all relative ${
                    authType === "GOOGLE_TELEGRAM_SSO"
                      ? "bg-indigo-950/50 border-indigo-500 text-white shadow-sm shadow-indigo-500/20"
                      : "bg-slate-950 border-slate-800 text-slate-400 hover:border-slate-700"
                  }`}
                >
                  <div className="flex items-center justify-between mb-1">
                    <span className="font-semibold text-xs flex items-center gap-1.5">
                      <Globe2 className="h-3.5 w-3.5 text-indigo-400" />
                      Google / Telegram SSO
                    </span>
                    <Badge variant="outline" className="text-[10px] bg-emerald-950/60 text-emerald-300 border-emerald-800/40">
                      Recommended
                    </Badge>
                  </div>
                  <p className="text-[11px] text-slate-400">Session Cookie / Auth Token</p>
                </div>

                {/* Method 2: Bearer Token */}
                <div
                  onClick={() => setAuthType("BEARER_TOKEN")}
                  className={`p-3 rounded-lg border cursor-pointer transition-all ${
                    authType === "BEARER_TOKEN"
                      ? "bg-indigo-950/50 border-indigo-500 text-white shadow-sm shadow-indigo-500/20"
                      : "bg-slate-950 border-slate-800 text-slate-400 hover:border-slate-700"
                  }`}
                >
                  <div className="flex items-center justify-between mb-1">
                    <span className="font-semibold text-xs flex items-center gap-1.5">
                      <Key className="h-3.5 w-3.5 text-indigo-400" />
                      Bearer Token
                    </span>
                  </div>
                  <p className="text-[11px] text-slate-400">JWT / API Authorization</p>
                </div>

                {/* Method 3: Password */}
                <div
                  onClick={() => setAuthType("PASSWORD")}
                  className={`p-3 rounded-lg border cursor-pointer transition-all ${
                    authType === "PASSWORD"
                      ? "bg-indigo-950/50 border-indigo-500 text-white shadow-sm shadow-indigo-500/20"
                      : "bg-slate-950 border-slate-800 text-slate-400 hover:border-slate-700"
                  }`}
                >
                  <div className="flex items-center justify-between mb-1">
                    <span className="font-semibold text-xs flex items-center gap-1.5">
                      <Lock className="h-3.5 w-3.5 text-indigo-400" />
                      Username & Password
                    </span>
                  </div>
                  <p className="text-[11px] text-slate-400">Traditional Login Form</p>
                </div>
              </div>

              {/* METHOD 1 CONTENT: GOOGLE / TELEGRAM SSO */}
              {authType === "GOOGLE_TELEGRAM_SSO" && (
                <div className="space-y-3 bg-slate-950/60 p-4 rounded-xl border border-slate-800/80">
                  <div className="flex items-start gap-2 text-xs text-indigo-300 bg-indigo-950/40 p-3 rounded-lg border border-indigo-800/40">
                    <Info className="h-4 w-4 shrink-0 mt-0.5" />
                    <div>
                      <p className="font-medium">{t.webTesting.authSsoDesc}</p>
                    </div>
                  </div>

                  <div className="space-y-1.5">
                    <label className="text-xs font-medium text-slate-200 block">
                      {t.webTesting.authSsoCookieLabel}
                    </label>
                    <textarea
                      rows={3}
                      value={sessionCookie}
                      onChange={(e) => setSessionCookie(e.target.value)}
                      placeholder={t.webTesting.authSsoCookiePlaceholder}
                      className="w-full text-xs font-mono rounded-lg bg-slate-900 border border-slate-800 p-2.5 text-slate-200 placeholder:text-slate-600 focus:outline-none focus:ring-1 focus:ring-indigo-500"
                    />
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
                    <div className="space-y-1">
                      <label className="text-xs font-medium text-slate-400">Test Account Email / Username (Optional)</label>
                      <Input
                        placeholder="tester@gmail.com"
                        value={authUsername}
                        onChange={(e) => setAuthUsername(e.target.value)}
                      />
                    </div>
                  </div>

                  {/* DevTools Quick Helper Accordion */}
                  <div className="border border-slate-800 rounded-lg overflow-hidden bg-slate-900/50">
                    <button
                      type="button"
                      onClick={() => setShowDevToolsGuide(!showDevToolsGuide)}
                      className="w-full px-3 py-2 text-left text-xs font-medium text-slate-300 flex items-center justify-between hover:bg-slate-800/40"
                    >
                      <span className="flex items-center gap-1.5 text-indigo-300">
                        <HelpCircle className="h-3.5 w-3.5" />
                        {t.webTesting.authSsoGuideTitle}
                      </span>
                      {showDevToolsGuide ? <ChevronUp className="h-4 w-4" /> : <ChevronDown className="h-4 w-4" />}
                    </button>
                    {showDevToolsGuide && (
                      <div className="p-3 text-xs text-slate-400 space-y-1.5 border-t border-slate-800/60 bg-slate-950/80 font-sans">
                        <p>{t.webTesting.authSsoStep1}</p>
                        <p>{t.webTesting.authSsoStep2}</p>
                        <p>{t.webTesting.authSsoStep3}</p>
                      </div>
                    )}
                  </div>
                </div>
              )}

              {/* METHOD 2 CONTENT: BEARER JWT */}
              {authType === "BEARER_TOKEN" && (
                <div className="space-y-3 bg-slate-950/60 p-4 rounded-xl border border-slate-800/80">
                  <div className="space-y-1.5">
                    <label className="text-xs font-medium text-slate-200 block">
                      {t.webTesting.authBearerLabel}
                    </label>
                    <textarea
                      rows={3}
                      value={bearerToken}
                      onChange={(e) => setBearerToken(e.target.value)}
                      placeholder={t.webTesting.authBearerPlaceholder}
                      className="w-full text-xs font-mono rounded-lg bg-slate-900 border border-slate-800 p-2.5 text-slate-200 placeholder:text-slate-600 focus:outline-none focus:ring-1 focus:ring-indigo-500"
                    />
                  </div>
                </div>
              )}

              {/* METHOD 3 CONTENT: TRADITIONAL USERNAME & PASSWORD */}
              {authType === "PASSWORD" && (
                <div className="space-y-3 bg-slate-950/60 p-4 rounded-xl border border-slate-800/80">
                  <div className="text-xs text-amber-300/90 bg-amber-950/30 p-2.5 rounded-lg border border-amber-800/30 flex items-center gap-2">
                    <Info className="h-4 w-4 shrink-0 text-amber-400" />
                    <span>{t.webTesting.authPasswordNotice}</span>
                  </div>
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <div className="space-y-1.5">
                      <label className="text-xs font-medium text-slate-300">
                        {t.webTesting.authUsernameLabel}
                      </label>
                      <Input
                        placeholder="tester@example.com"
                        value={authUsername}
                        onChange={(e) => setAuthUsername(e.target.value)}
                      />
                    </div>
                    <div className="space-y-1.5">
                      <label className="text-xs font-medium text-slate-300">
                        {t.webTesting.authPasswordLabel}
                      </label>
                      <Input
                        type="password"
                        placeholder="••••••••••••"
                        value={authPassword}
                        onChange={(e) => setAuthPassword(e.target.value)}
                      />
                    </div>
                  </div>
                </div>
              )}
            </div>
          )}

          <div className="flex justify-between pt-2">
            <Button variant="outline" onClick={() => setStep(3)}>
              <ArrowLeft className="mr-2 h-4 w-4" />
              Back
            </Button>
            <Button
              onClick={handleGeneratePlan}
              disabled={generating}
              className="bg-indigo-600 hover:bg-indigo-500 text-white"
            >
              <Sparkles className="mr-2 h-4 w-4" />
              {generating ? "AI is Analyzing Website..." : t.webTesting.generatePlanBtn}
            </Button>
          </div>
        </Card>
      )}

      {/* STEP 5: REVIEW & EXECUTE */}
      {step === 5 && generatedPlan && (
        <Card className="p-6 space-y-6">
          <div className="flex items-start justify-between border-b border-slate-800 pb-4">
            <div className="space-y-1">
              <h3 className="text-lg font-bold text-white flex items-center gap-2">
                <Sparkles className="h-5 w-5 text-indigo-400" />
                {t.webTesting.planGeneratedTitle}
              </h3>
              <p className="text-xs text-slate-400">
                Review the AI-generated structured test cases below before authorizing browser execution.
              </p>
            </div>
            <Badge variant="outline">{generatedPlan.testCases.length} Test Cases</Badge>
          </div>

          <div className="space-y-3">
            {generatedPlan.testCases.map((tc, idx) => (
              <div key={tc.id || idx} className="p-3.5 rounded-lg bg-slate-950 border border-slate-800 space-y-1.5">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-semibold text-white">
                    {idx + 1}. {tc.title}
                  </span>
                  <div className="flex items-center gap-2">
                    <Badge variant="default" className="text-[10px]">
                      {tc.category}
                    </Badge>
                    <Badge variant="outline" className="text-[10px]">
                      {tc.priority}
                    </Badge>
                  </div>
                </div>
                <p className="text-[11px] text-slate-400">
                  <strong className="text-slate-300">Expected:</strong> {tc.expectedResult}
                </p>
              </div>
            ))}
          </div>

          <div className="flex items-center justify-between pt-4 border-t border-slate-800">
            <Button variant="outline" onClick={() => setStep(2)}>
              <ArrowLeft className="mr-2 h-4 w-4" />
              Edit Goal
            </Button>
            <Button
              onClick={handleApproveAndExecute}
              disabled={startingRun}
              className="bg-emerald-600 hover:bg-emerald-500 text-white shadow-lg shadow-emerald-600/20"
            >
              <PlayCircle className="mr-2 h-4 w-4" />
              {startingRun ? "Launching Browser..." : t.webTesting.approveAndRunBtn}
            </Button>
          </div>
        </Card>
      )}
    </div>
  );
}

export default function NewTestWizardPage() {
  return (
    <Suspense fallback={<div className="py-20 text-center text-sm text-slate-500">Loading wizard...</div>}>
      <NewTestWizardContent />
    </Suspense>
  );
}
