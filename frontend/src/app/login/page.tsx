"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { authApi, setStoredToken } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter } from "@/components/ui/card";
import { Sparkles, ArrowLeft, AlertCircle } from "lucide-react";
import { useI18n } from "@/lib/i18n";
import { LanguageSwitcher, ThemeSwitcher } from "@/components/ui/controls";

export default function LoginPage() {
  const router = useRouter();
  const { t } = useI18n();
  const [email, setEmail] = useState("admin@enterprise-demo.com");
  const [password, setPassword] = useState("AdminSecure2026!");
  const [tenantId, setTenantId] = useState("tenant-demo-001");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const resp = await authApi.login({ email, password });
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
          {t.auth.loginTitle}
        </h2>
        <p className="mt-2 text-center text-sm text-slate-400">
          <Link href="/register" className="font-medium text-indigo-400 hover:text-indigo-300">
            {t.auth.loginSubtitle}
          </Link>
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <Card>
          <CardContent className="pt-6">
            {error && (
              <div className="mb-4 rounded-lg bg-red-500/10 border border-red-500/30 p-3 text-xs text-red-300 flex items-center space-x-2">
                <AlertCircle className="h-4 w-4 flex-shrink-0" />
                <span>{error}</span>
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-4">
              <Input
                label={t.auth.tenantLabel}
                type="text"
                required
                value={tenantId}
                onChange={(e) => setTenantId(e.target.value)}
                placeholder="tenant-demo-001"
              />

              <Input
                label={t.auth.emailLabel}
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="you@company.com"
              />

              <Input
                label={t.auth.passwordLabel}
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••••••"
              />

              <Button type="submit" className="w-full mt-4" isLoading={loading}>
                {t.auth.loginSubmit}
              </Button>
            </form>
          </CardContent>

          <CardFooter className="flex justify-between text-xs text-slate-400">
            <Link href="/" className="flex items-center hover:text-slate-200">
              <ArrowLeft className="mr-1 h-3.5 w-3.5" />
              {t.auth.backHome}
            </Link>
            <span>Clean Architecture RBAC</span>
          </CardFooter>
        </Card>
      </div>
    </div>
  );
}
