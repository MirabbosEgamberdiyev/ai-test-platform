"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { authApi, setStoredToken } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { Sparkles, ArrowLeft, AlertCircle, CheckCircle2 } from "lucide-react";
import { useI18n } from "@/lib/i18n";
import { LanguageSwitcher, ThemeSwitcher } from "@/components/ui/controls";

export default function RegisterPage() {
  const router = useRouter();
  const { t } = useI18n();
  const [tenantName, setTenantName] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [googleLoading, setGoogleLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const resp = await authApi.register({
        tenantName,
        fullName: `${firstName} ${lastName}`.trim(),
        email,
        password,
      });

      setStoredToken(resp.accessToken, resp.refreshToken);
      if (typeof window !== "undefined") {
        localStorage.setItem("currentTenantId", resp.tenantId);
        localStorage.setItem("currentUserId", resp.userId);
      }

      router.push("/dashboard");
    } catch (err: any) {
      setError(err.message || t.common.error);
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleSignUp = async () => {
    setGoogleLoading(true);
    setError(null);
    const clientId = process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID;
    if (!clientId || clientId.trim() === "" || clientId === "mock-client-id" || clientId.startsWith("your-google")) {
      setError(
        "Google OAuth 2.0 Client ID is not configured in environment. [Status: BLOCKED_BY_EXTERNAL_CONFIGURATION]. Please configure GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET in deployment configuration."
      );
      setGoogleLoading(false);
      return;
    }
    const redirectUri = window.location.origin + "/login";
    const googleAuthUrl = `https://accounts.google.com/o/oauth2/v2/auth?client_id=${encodeURIComponent(
      clientId
    )}&redirect_uri=${encodeURIComponent(
      redirectUri
    )}&response_type=id_token&scope=openid%20profile%20email&nonce=${Math.random().toString(36).substring(2)}`;
    window.location.href = googleAuthUrl;
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
          {t.auth.registerTitle}
        </h2>
        <p className="mt-2 text-center text-sm text-slate-400">
          <Link href="/login" className="font-medium text-indigo-400 hover:text-indigo-300">
            {t.auth.registerSubtitle}
          </Link>
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <Card>
          <CardContent className="pt-6 space-y-4">
            {error && (
              <div className="rounded-lg bg-red-500/10 border border-red-500/30 p-3 text-xs text-red-300 flex items-center space-x-2">
                <AlertCircle className="h-4 w-4 flex-shrink-0" />
                <span>{error}</span>
              </div>
            )}

            {/* Google OAuth Button */}
            <button
              type="button"
              onClick={handleGoogleSignUp}
              disabled={googleLoading}
              className="w-full flex items-center justify-center gap-3 py-2.5 px-4 rounded-lg border border-slate-700 bg-slate-900 hover:bg-slate-800 text-slate-100 text-sm font-medium transition-colors disabled:opacity-50 shadow-sm"
            >
              <svg className="h-4 w-4" viewBox="0 0 24 24">
                <path
                  fill="#4285F4"
                  d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                />
                <path
                  fill="#34A853"
                  d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                />
                <path
                  fill="#FBBC05"
                  d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"
                />
                <path
                  fill="#EA4335"
                  d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"
                />
              </svg>
              <span>{googleLoading ? t.common.loading : t.auth.continueWithGoogle}</span>
            </button>

            {/* OR Divider */}
            <div className="relative flex items-center justify-center my-4">
              <div className="border-t border-slate-800 w-full" />
              <span className="bg-slate-900 px-3 text-[11px] font-semibold text-slate-400 uppercase tracking-wider absolute">
                {t.auth.orDivider}
              </span>
            </div>

            <form onSubmit={handleSubmit} className="space-y-4">
              <Input
                label={t.auth.orgLabel}
                type="text"
                required
                value={tenantName}
                onChange={(e) => setTenantName(e.target.value)}
                placeholder="Acme Test Engineering"
              />

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <Input
                  label={t.auth.firstNameLabel}
                  type="text"
                  required
                  value={firstName}
                  onChange={(e) => setFirstName(e.target.value)}
                  placeholder="Alex"
                />
                <Input
                  label={t.auth.lastNameLabel}
                  type="text"
                  required
                  value={lastName}
                  onChange={(e) => setLastName(e.target.value)}
                  placeholder="Mercer"
                />
              </div>

              <Input
                label={t.auth.emailLabel}
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="lead@company.com"
              />

              <Input
                label={t.auth.passwordLabel}
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••••••"
              />

              <div className="rounded-lg bg-slate-950 p-3 border border-slate-800 text-xs text-slate-400 space-y-1">
                <div className="flex items-center space-x-1.5 text-slate-300 font-medium">
                  <CheckCircle2 className="h-3.5 w-3.5 text-indigo-400" />
                  <span>{t.auth.tokenPerk}</span>
                </div>
                <div className="flex items-center space-x-1.5 text-slate-300 font-medium">
                  <CheckCircle2 className="h-3.5 w-3.5 text-indigo-400" />
                  <span>{t.auth.sandboxPerk}</span>
                </div>
              </div>

              <Button type="submit" className="w-full mt-4" isLoading={loading}>
                {t.auth.registerSubmit}
              </Button>
            </form>
          </CardContent>

          <CardFooter className="flex justify-between text-xs text-slate-400">
            <Link href="/" className="flex items-center hover:text-slate-200">
              <ArrowLeft className="mr-1 h-3.5 w-3.5" />
              {t.auth.backHome}
            </Link>
            <span>Tenant Row-Level Isolation</span>
          </CardFooter>
        </Card>
      </div>
    </div>
  );
}
