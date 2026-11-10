"use client";

import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { useState, useEffect, Suspense } from "react";
import { authApi } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { Sparkles, ArrowLeft, CheckCircle2, AlertCircle, Loader2 } from "lucide-react";
import { useI18n } from "@/lib/i18n";
import { LanguageSwitcher, ThemeSwitcher } from "@/components/ui/controls";

function VerifyEmailContent() {
  const searchParams = useSearchParams();
  const { t } = useI18n();
  const [loading, setLoading] = useState(true);
  const [verified, setVerified] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const token = searchParams.get("token");
    if (!token) {
      setLoading(false);
      setError("Email verification token is missing. Please check your verification link.");
      return;
    }

    authApi
      .verifyEmail(token)
      .then(() => {
        setVerified(true);
      })
      .catch((err) => {
        setError(err.message || "Failed to verify email address.");
      })
      .finally(() => {
        setLoading(false);
      });
  }, [searchParams]);

  return (
    <Card>
      <CardContent className="pt-6">
        {loading && (
          <div className="space-y-4 text-center py-8">
            <Loader2 className="mx-auto h-8 w-8 animate-spin text-indigo-500" />
            <p className="text-xs text-slate-400">Verifying security token...</p>
          </div>
        )}

        {!loading && verified && (
          <div className="space-y-4 text-center py-4">
            <div className="mx-auto h-12 w-12 rounded-full bg-emerald-500/20 text-emerald-400 flex items-center justify-center">
              <CheckCircle2 className="h-6 w-6" />
            </div>
            <h3 className="text-base font-semibold text-white">Email Verified!</h3>
            <p className="text-xs text-slate-400 leading-relaxed">
              {t.auth.verifyEmailSuccess}
            </p>
            <div className="pt-2">
              <Link href="/login">
                <Button className="w-full bg-indigo-600 hover:bg-indigo-500">
                  {t.auth.backToLogin}
                </Button>
              </Link>
            </div>
          </div>
        )}

        {!loading && error && (
          <div className="space-y-4 text-center py-4">
            <div className="mx-auto h-12 w-12 rounded-full bg-red-500/20 text-red-400 flex items-center justify-center">
              <AlertCircle className="h-6 w-6" />
            </div>
            <h3 className="text-base font-semibold text-white">Verification Failed</h3>
            <p className="text-xs text-red-300 leading-relaxed">
              {error}
            </p>
            <div className="pt-2">
              <Link href="/login">
                <Button variant="outline" className="w-full">
                  {t.auth.backToLogin}
                </Button>
              </Link>
            </div>
          </div>
        )}
      </CardContent>

      <CardFooter className="flex justify-between text-xs text-slate-400">
        <Link href="/login" className="flex items-center hover:text-slate-200">
          <ArrowLeft className="mr-1 h-3.5 w-3.5" />
          {t.auth.backToLogin}
        </Link>
        <span>Anti-Phishing Token Hash</span>
      </CardFooter>
    </Card>
  );
}

export default function VerifyEmailPage() {
  const { t } = useI18n();

  return (
    <div className="min-h-screen bg-slate-950 flex flex-col justify-center py-12 px-4 sm:px-6 lg:px-8 relative">
      <div className="absolute top-6 right-6 flex items-center space-x-2">
        <LanguageSwitcher />
        <ThemeSwitcher />
      </div>

      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <Link href="/" className="flex items-center justify-center space-x-2 mb-6">
          <div className="h-10 w-10 rounded-xl bg-indigo-600 flex items-center justify-center shadow-lg shadow-indigo-600/30">
            <Sparkles className="h-5 w-5 text-white" />
          </div>
          <span className="text-2xl font-bold text-white">AiTestOps</span>
        </Link>
        <h2 className="text-center text-2xl font-bold tracking-tight text-white">
          {t.auth.verifyEmailTitle}
        </h2>
        <p className="mt-2 text-center text-sm text-slate-400">
          {t.auth.verifyEmailSubtitle}
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <Suspense
          fallback={
            <div className="flex justify-center py-12">
              <Loader2 className="h-8 w-8 animate-spin text-indigo-500" />
            </div>
          }
        >
          <VerifyEmailContent />
        </Suspense>
      </div>
    </div>
  );
}
