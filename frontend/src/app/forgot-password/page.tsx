"use client";

import Link from "next/link";
import { useState } from "react";
import { authApi } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { Sparkles, ArrowLeft, CheckCircle2, AlertCircle } from "lucide-react";
import { useI18n } from "@/lib/i18n";
import { LanguageSwitcher, ThemeSwitcher } from "@/components/ui/controls";

export default function ForgotPasswordPage() {
  const { t } = useI18n();
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await authApi.forgotPassword(email);
      setSubmitted(true);
    } catch (err: any) {
      setError(err.message || t.common.error);
    } finally {
      setLoading(false);
    }
  };

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
          {t.auth.forgotPasswordTitle}
        </h2>
        <p className="mt-2 text-center text-sm text-slate-400">
          {t.auth.forgotPasswordSubtitle}
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <Card>
          <CardContent className="pt-6">
            {submitted ? (
              <div className="space-y-4 text-center py-4">
                <div className="mx-auto h-12 w-12 rounded-full bg-emerald-500/20 text-emerald-400 flex items-center justify-center">
                  <CheckCircle2 className="h-6 w-6" />
                </div>
                <h3 className="text-base font-semibold text-white">Check your email</h3>
                <p className="text-xs text-slate-400 leading-relaxed">
                  {t.auth.forgotPasswordSuccess}
                </p>
                <div className="pt-2">
                  <Link href="/login">
                    <Button variant="outline" className="w-full">
                      {t.auth.backToLogin}
                    </Button>
                  </Link>
                </div>
              </div>
            ) : (
              <form onSubmit={handleSubmit} className="space-y-4">
                {error && (
                  <div className="rounded-lg bg-red-500/10 border border-red-500/30 p-3 text-xs text-red-300 flex items-center space-x-2">
                    <AlertCircle className="h-4 w-4 flex-shrink-0" />
                    <span>{error}</span>
                  </div>
                )}

                <Input
                  label={t.auth.emailLabel}
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="you@company.com"
                />

                <Button type="submit" className="w-full mt-2" isLoading={loading}>
                  {t.auth.forgotPasswordSubmit}
                </Button>
              </form>
            )}
          </CardContent>

          <CardFooter className="flex justify-between text-xs text-slate-400">
            <Link href="/login" className="flex items-center hover:text-slate-200">
              <ArrowLeft className="mr-1 h-3.5 w-3.5" />
              {t.auth.backToLogin}
            </Link>
            <span>SHA-256 Timed Tokens</span>
          </CardFooter>
        </Card>
      </div>
    </div>
  );
}
