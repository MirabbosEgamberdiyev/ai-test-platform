"use client";

import { useState } from "react";
import { practiceApi } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { useI18n } from "@/lib/i18n";
import {
  Sparkles,
  CheckCircle2,
  XCircle,
  HelpCircle,
  Cpu,
  RefreshCw,
  Send,
  Flag,
} from "lucide-react";

interface OptionItem {
  optionKey: string;
  text: string;
}

export default function PracticeStudioPage() {
  const { t } = useI18n();

  // Session State
  const [activeSessionId, setActiveSessionId] = useState<string | null>("sess-demo-active");
  const [targetUrl, setTargetUrl] = useState<string>("https://practice.enterprise-exam.org/sample-test");
  const [sessionStarting, setSessionStarting] = useState(false);

  // Question & Options State
  const [questionText, setQuestionText] = useState<string>(
    "Which isolation level in ANSI SQL prevents Dirty Reads and Non-Repeatable Reads, but may still permit Phantom Reads depending on the database engine?"
  );
  const [options, setOptions] = useState<OptionItem[]>([
    { optionKey: "A", text: "Read Uncommitted" },
    { optionKey: "B", text: "Read Committed" },
    { optionKey: "C", text: "Repeatable Read" },
    { optionKey: "D", text: "Serializable" },
  ]);

  // AI Analysis State
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [aiResult, setAiResult] = useState<{
    predictedAnswer: string;
    reasoning: string;
    confidenceScore: number;
    recommendedAction: string;
  } | null>(null);

  // User Submission State
  const [selectedOption, setSelectedOption] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitFeedback, setSubmitFeedback] = useState<{
    correct: boolean;
    score: number;
    correctAnswer: string;
  } | null>(null);

  // Session summary
  const [sessionCompleted, setSessionCompleted] = useState(false);
  const [finalSummary, setFinalSummary] = useState<any>(null);

  const handleStartSession = async () => {
    setSessionStarting(true);
    try {
      const res = await practiceApi.startSession({
        registeredWebsiteId: "web-001",
        targetUrl: targetUrl,
      });
      setActiveSessionId(res.sessionId);
    } catch {
      setActiveSessionId("sess-" + Math.random().toString(36).substring(2, 8));
    } finally {
      setSessionStarting(false);
    }
  };

  const handleRunAiAnalysis = async () => {
    setIsAnalyzing(true);
    setSubmitFeedback(null);
    try {
      const res = await practiceApi.analyzeQuestion(activeSessionId || "sess-demo", {
        questionText,
        options,
        category: "Databases & Concurrency",
        difficulty: "MEDIUM",
      });
      setAiResult({
        predictedAnswer: res.predictedAnswer,
        reasoning: res.reasoning,
        confidenceScore: res.confidenceScore,
        recommendedAction: res.recommendedAction,
      });
      setSelectedOption(res.predictedAnswer);
    } catch {
      // Local fallback simulation if offline
      setAiResult({
        predictedAnswer: "C",
        reasoning:
          "Repeatable Read guarantees that any data read cannot change, preventing dirty reads and non-repeatable reads. Phantom reads (where new rows inserted by another concurrent transaction match the query range) can still occur unless table-level or range locks (Serializable) are enforced.",
        confidenceScore: 0.94,
        recommendedAction: "CONFIDENT_SELECT",
      });
      setSelectedOption("C");
    } finally {
      setIsAnalyzing(false);
    }
  };

  const handleSubmitAnswer = async () => {
    if (!selectedOption) return;
    setIsSubmitting(true);
    try {
      const res = await practiceApi.submitAnswer(activeSessionId || "sess-demo", {
        questionId: "q-101",
        selectedAnswer: selectedOption,
        correctAnswer: "C",
        timeSpentSeconds: 12,
      });
      setSubmitFeedback({
        correct: res.correct,
        score: res.score,
        correctAnswer: res.correctAnswer,
      });
    } catch {
      setSubmitFeedback({
        correct: selectedOption === "C",
        score: selectedOption === "C" ? 1.0 : 0.0,
        correctAnswer: "C",
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleFinishSession = async () => {
    try {
      const res = await practiceApi.finishSession(activeSessionId || "sess-demo");
      setFinalSummary(res);
      setSessionCompleted(true);
    } catch {
      setFinalSummary({
        sessionId: activeSessionId,
        totalQuestions: 1,
        correctAnswers: 1,
        scorePercent: 100.0,
        averageConfidence: 0.94,
        status: "COMPLETED",
      });
      setSessionCompleted(true);
    }
  };

  return (
    <div className="space-y-6 max-w-5xl mx-auto">
      {/* TITLE & SESSION BAR */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-white flex items-center space-x-2">
            <Cpu className="h-6 w-6 text-indigo-400" />
            <span>{t.practice.title}</span>
          </h1>
          <p className="text-sm text-slate-400 mt-1">
            {t.practice.subtitle}
          </p>
        </div>

        {activeSessionId && !sessionCompleted && (
          <Button variant="danger" size="sm" onClick={handleFinishSession}>
            <Flag className="mr-1.5 h-3.5 w-3.5" />
            {t.practice.completeSession}
          </Button>
        )}
      </div>

      {/* SESSION SETUP BAR */}
      <Card className="p-4 bg-slate-900/60">
        <div className="flex flex-col md:flex-row items-center gap-3">
          <div className="flex-1 w-full">
            <Input
              label={t.practice.targetUrlLabel}
              value={targetUrl}
              onChange={(e) => setTargetUrl(e.target.value)}
              placeholder="https://practice.example.com/exam/1"
            />
          </div>
          <div className="pt-5 w-full md:w-auto">
            <Button
              onClick={handleStartSession}
              isLoading={sessionStarting}
              className="w-full md:w-auto"
            >
              <RefreshCw className="mr-2 h-4 w-4" />
              {activeSessionId ? t.practice.resetSession : t.practice.launchSession}
            </Button>
          </div>
        </div>
      </Card>

      {sessionCompleted ? (
        /* SESSION COMPLETION SUMMARY CARD */
        <Card className="border-emerald-500/30 bg-emerald-950/20 p-8 text-center space-y-6">
          <div className="h-16 w-16 rounded-full bg-emerald-500/20 text-emerald-400 mx-auto flex items-center justify-center">
            <CheckCircle2 className="h-10 w-10" />
          </div>
          <div className="space-y-2">
            <h2 className="text-2xl font-bold text-white">{t.practice.finishedTitle}</h2>
            <p className="text-xs text-slate-300">
              {t.practice.finishedDesc}
            </p>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 max-w-2xl mx-auto">
            <div className="p-3 bg-slate-900 rounded-lg border border-slate-800">
              <div className="text-xs text-slate-400">{t.practice.statTotal}</div>
              <div className="text-xl font-bold text-white mt-1">{finalSummary?.totalQuestions || 1}</div>
            </div>
            <div className="p-3 bg-slate-900 rounded-lg border border-slate-800">
              <div className="text-xs text-slate-400">{t.practice.statCorrect}</div>
              <div className="text-xl font-bold text-emerald-400 mt-1">{finalSummary?.correctAnswers || 1}</div>
            </div>
            <div className="p-3 bg-slate-900 rounded-lg border border-slate-800">
              <div className="text-xs text-slate-400">{t.practice.statScore}</div>
              <div className="text-xl font-bold text-indigo-400 mt-1">{finalSummary?.scorePercent?.toFixed(1) || 100}%</div>
            </div>
            <div className="p-3 bg-slate-900 rounded-lg border border-slate-800">
              <div className="text-xs text-slate-400">{t.practice.statConfidence}</div>
              <div className="text-xl font-bold text-amber-400 mt-1">
                {((finalSummary?.averageConfidence || 0.94) * 100).toFixed(1)}%
              </div>
            </div>
          </div>

          <Button onClick={() => setSessionCompleted(false)}>
            {t.practice.startAnother}
          </Button>
        </Card>
      ) : (
        /* LIVE QUESTION PRACTICE AREA */
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
          {/* LEFT: QUESTION & CHOICES */}
          <div className="lg:col-span-7 space-y-6">
            <Card>
              <CardHeader>
                <div className="flex items-center justify-between">
                  <span className="text-xs font-semibold text-indigo-400 uppercase tracking-wider">
                    Question 01 • Database Concurrency & ACID
                  </span>
                  <Badge variant="outline">{t.practice.singleChoice}</Badge>
                </div>
                <CardTitle className="text-base font-normal text-white mt-2 leading-relaxed">
                  {questionText}
                </CardTitle>
              </CardHeader>

              <CardContent className="space-y-3 pt-2">
                {options.map((opt) => {
                  const isSelected = selectedOption === opt.optionKey;
                  const isAiRecommended = aiResult?.predictedAnswer === opt.optionKey;

                  return (
                    <button
                      key={opt.optionKey}
                      onClick={() => setSelectedOption(opt.optionKey)}
                      className={`w-full text-left p-4 rounded-xl border transition-all flex items-start justify-between ${
                        isSelected
                          ? "border-indigo-500 bg-indigo-500/10 text-white shadow-sm shadow-indigo-500/10"
                          : "border-slate-800 bg-slate-900/60 text-slate-300 hover:border-slate-700 hover:bg-slate-900"
                      }`}
                    >
                      <div className="flex items-start space-x-3">
                        <span
                          className={`flex-shrink-0 w-6 h-6 rounded-md text-xs font-bold flex items-center justify-center ${
                            isSelected
                              ? "bg-indigo-600 text-white"
                              : "bg-slate-800 text-slate-400"
                          }`}
                        >
                          {opt.optionKey}
                        </span>
                        <span className="text-sm font-medium">{opt.text}</span>
                      </div>

                      {isAiRecommended && (
                        <span className="inline-flex items-center text-[10px] uppercase font-bold text-indigo-400 bg-indigo-950/60 border border-indigo-700/50 px-2 py-0.5 rounded-full">
                          <Sparkles className="h-3 w-3 mr-1" /> {t.practice.aiPick}
                        </span>
                      )}
                    </button>
                  );
                })}
              </CardContent>

              <CardFooter className="flex flex-col sm:flex-row items-center justify-between gap-3 bg-slate-900/40">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={handleRunAiAnalysis}
                  isLoading={isAnalyzing}
                  className="w-full sm:w-auto"
                >
                  <Sparkles className="mr-2 h-4 w-4 text-indigo-400" />
                  {t.practice.askAi}
                </Button>

                <Button
                  size="sm"
                  onClick={handleSubmitAnswer}
                  disabled={!selectedOption}
                  isLoading={isSubmitting}
                  className="w-full sm:w-auto"
                >
                  <Send className="mr-2 h-4 w-4" />
                  {t.practice.submitAnswer}
                </Button>
              </CardFooter>
            </Card>

            {/* SUBMIT FEEDBACK NOTIFICATION */}
            {submitFeedback && (
              <div
                className={`p-4 rounded-xl border flex items-start space-x-3 ${
                  submitFeedback.correct
                    ? "bg-emerald-950/30 border-emerald-500/30 text-emerald-300"
                    : "bg-red-950/30 border-red-500/30 text-red-300"
                }`}
              >
                {submitFeedback.correct ? (
                  <CheckCircle2 className="h-5 w-5 flex-shrink-0 text-emerald-400 mt-0.5" />
                ) : (
                  <XCircle className="h-5 w-5 flex-shrink-0 text-red-400 mt-0.5" />
                )}
                <div>
                  <h4 className="font-semibold text-sm">
                    {submitFeedback.correct ? t.practice.correctTitle : t.practice.incorrectTitle}
                  </h4>
                  <p className="text-xs text-slate-300 mt-1">
                    {t.practice.feedbackDesc}
                  </p>
                </div>
              </div>
            )}
          </div>

          {/* RIGHT: AI EXPLANATION & CONFIDENCE RADAR */}
          <div className="lg:col-span-5 space-y-4">
            <Card className="border-indigo-500/20 bg-slate-900/90 h-full flex flex-col justify-between">
              <div>
                <CardHeader className="pb-3">
                  <div className="flex items-center justify-between">
                    <CardTitle className="text-sm flex items-center space-x-2 text-indigo-400">
                      <Sparkles className="h-4 w-4" />
                      <span>{t.practice.aiRationaleTitle}</span>
                    </CardTitle>
                    {aiResult && (
                      <Badge
                        variant={
                          aiResult.confidenceScore >= 0.9
                            ? "success"
                            : aiResult.confidenceScore >= 0.7
                            ? "warning"
                            : "danger"
                        }
                      >
                        {(aiResult.confidenceScore * 100).toFixed(1)}% Confidence
                      </Badge>
                    )}
                  </div>
                </CardHeader>

                <CardContent className="space-y-4 text-xs">
                  {aiResult ? (
                    <>
                      <div className="bg-slate-950/70 p-3 rounded-lg border border-slate-800 space-y-1">
                        <div className="text-slate-400 text-[10px] uppercase font-bold tracking-wider">
                          {t.practice.predictedAns}
                        </div>
                        <div className="text-sm font-semibold text-white">
                          Option {aiResult.predictedAnswer}
                        </div>
                      </div>

                      <div className="space-y-1.5">
                        <div className="text-slate-400 text-[10px] uppercase font-bold tracking-wider">
                          {t.practice.domainExp}
                        </div>
                        <p className="text-slate-300 leading-relaxed bg-slate-950/50 p-3 rounded-lg border border-slate-800/80">
                          {aiResult.reasoning}
                        </p>
                      </div>

                      <div className="bg-slate-950/70 p-3 rounded-lg border border-slate-800 flex items-center justify-between">
                        <span className="text-slate-400">{t.practice.policyRec}</span>
                        <span className="font-mono text-indigo-300 font-semibold">
                          {aiResult.recommendedAction}
                        </span>
                      </div>
                    </>
                  ) : (
                    <div className="py-12 text-center text-slate-500 space-y-3">
                      <HelpCircle className="h-8 w-8 mx-auto text-slate-600" />
                      <p>
                        {t.practice.emptyPrompt}
                      </p>
                    </div>
                  )}
                </CardContent>
              </div>

              <div className="p-4 border-t border-slate-800 bg-slate-950/40 text-[11px] text-slate-400 rounded-b-xl">
                {t.practice.securityNotice}
              </div>
            </Card>
          </div>
        </div>
      )}
    </div>
  );
}
