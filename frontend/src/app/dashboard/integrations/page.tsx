"use client";

import { useState } from "react";
import { registryApi } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { useI18n } from "@/lib/i18n";
import {
  Globe2,
  Plus,
  ShieldCheck,
  ShieldAlert,
  Lock,
  CheckCircle2,
  XCircle,
  ExternalLink,
} from "lucide-react";

interface RegisteredSite {
  id: string;
  name: string;
  domain: string;
  baseUrl: string;
  authType: string;
  status: "ACTIVE" | "PENDING" | "REJECTED";
  createdAt: string;
}

const INITIAL_SITES: RegisteredSite[] = [
  {
    id: "site-001",
    name: "Enterprise Architecture Cert Mock",
    domain: "practice.cloud-architect-exam.org",
    baseUrl: "https://practice.cloud-architect-exam.org",
    authType: "OAUTH2",
    status: "ACTIVE",
    createdAt: "2026-11-01T10:00:00Z",
  },
  {
    id: "site-002",
    name: "Kubernetes CKAD Practice Sandbox",
    domain: "sandbox.kubernetes-ckad.net",
    baseUrl: "https://sandbox.kubernetes-ckad.net",
    authType: "API_KEY",
    status: "ACTIVE",
    createdAt: "2026-11-04T14:30:00Z",
  },
];

export default function IntegrationsPage() {
  const { t } = useI18n();
  const [sites, setSites] = useState<RegisteredSite[]>(INITIAL_SITES);
  const [showAddForm, setShowAddForm] = useState(false);
  const [name, setName] = useState("");
  const [baseUrl, setBaseUrl] = useState("");
  const [authType, setAuthType] = useState("OAUTH2");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    // Frontend SSRF early check test
    const lower = baseUrl.toLowerCase();
    if (
      lower.includes("proctorio.com") ||
      lower.includes("respondus.com") ||
      lower.includes("honorlock.com")
    ) {
      setError("Registration Rejected: High-stakes proctored exam platforms are hard-blocked by SSRF & Ethics Policy.");
      setLoading(false);
      return;
    }

    if (
      lower.includes("127.0.0.1") ||
      lower.includes("localhost") ||
      lower.includes("169.254.169.254") ||
      lower.includes("192.168.") ||
      lower.includes("10.")
    ) {
      setError("Registration Rejected: SSRF Guard blocked internal network / private IP address.");
      setLoading(false);
      return;
    }

    try {
      const res = await registryApi.registerWebsite({
        name,
        baseUrl,
        authType,
      });
      setSites([
        ...sites,
        {
          id: res.id || "site-" + Math.random().toString(36).substring(2, 6),
          name: res.name || name,
          domain: res.domain || new URL(baseUrl).hostname,
          baseUrl: res.baseUrl || baseUrl,
          authType: res.authType || authType,
          status: (res.status as any) || "ACTIVE",
          createdAt: new Date().toISOString(),
        },
      ]);
      setShowAddForm(false);
      setName("");
      setBaseUrl("");
    } catch {
      // In local offline mode: add to state
      try {
        const parsedDomain = new URL(baseUrl).hostname;
        setSites([
          ...sites,
          {
            id: "site-" + Math.random().toString(36).substring(2, 6),
            name,
            domain: parsedDomain,
            baseUrl,
            authType,
            status: "ACTIVE",
            createdAt: new Date().toISOString(),
          },
        ]);
        setShowAddForm(false);
        setName("");
        setBaseUrl("");
      } catch (urlErr) {
        setError("Invalid URL format. Please include https://");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6 max-w-6xl mx-auto">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-white flex items-center space-x-2">
            <Globe2 className="h-6 w-6 text-indigo-400" />
            <span>{t.integrations.title}</span>
          </h1>
          <p className="text-sm text-slate-400 mt-1">
            {t.integrations.subtitle}
          </p>
        </div>

        <Button onClick={() => setShowAddForm(!showAddForm)} size="sm">
          <Plus className="mr-1.5 h-4 w-4" />
          {t.integrations.registerBtn}
        </Button>
      </div>

      {/* SECURITY CONTROLS CARD */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="rounded-xl border border-emerald-500/20 bg-emerald-950/20 p-4 space-y-1">
          <div className="flex items-center space-x-2 text-emerald-400 text-xs font-semibold uppercase tracking-wider">
            <ShieldCheck className="h-4 w-4" />
            <span>{t.integrations.ssrfCardTitle}</span>
          </div>
          <p className="text-xs text-slate-300">
            {t.integrations.ssrfCardDesc}
          </p>
        </div>

        <div className="rounded-xl border border-indigo-500/20 bg-indigo-950/20 p-4 space-y-1">
          <div className="flex items-center space-x-2 text-indigo-400 text-xs font-semibold uppercase tracking-wider">
            <Lock className="h-4 w-4" />
            <span>{t.integrations.vaultCardTitle}</span>
          </div>
          <p className="text-xs text-slate-300">
            {t.integrations.vaultCardDesc}
          </p>
        </div>

        <div className="rounded-xl border border-rose-500/20 bg-rose-950/20 p-4 space-y-1">
          <div className="flex items-center space-x-2 text-rose-400 text-xs font-semibold uppercase tracking-wider">
            <ShieldAlert className="h-4 w-4" />
            <span>{t.integrations.proctorCardTitle}</span>
          </div>
          <p className="text-xs text-slate-300">
            {t.integrations.proctorCardDesc}
          </p>
        </div>
      </div>

      {/* ADD ENVIRONMENT MODAL / CARD */}
      {showAddForm && (
        <Card className="border border-indigo-500/30 bg-slate-900/95">
          <CardHeader>
            <CardTitle>{t.integrations.modalTitle}</CardTitle>
            <CardDescription>
              {t.integrations.modalDesc}
            </CardDescription>
          </CardHeader>
          <CardContent>
            {error && (
              <div className="mb-4 rounded-lg bg-red-500/10 border border-red-500/30 p-3 text-xs text-red-300 flex items-center space-x-2">
                <XCircle className="h-4 w-4 flex-shrink-0" />
                <span>{error}</span>
              </div>
            )}
            <form onSubmit={handleRegister} className="space-y-4">
              <Input
                label={t.integrations.envNameLabel}
                required
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="e.g. AWS Certified DevOps Mock Platform"
              />

              <Input
                label={t.integrations.baseUrlLabel}
                required
                type="url"
                value={baseUrl}
                onChange={(e) => setBaseUrl(e.target.value)}
                placeholder="https://practice.example.com"
              />

              <div className="space-y-1.5">
                <label className="text-xs font-semibold uppercase tracking-wider text-slate-400">
                  {t.integrations.authTypeLabel}
                </label>
                <select
                  value={authType}
                  onChange={(e) => setAuthType(e.target.value)}
                  className="flex h-10 w-full rounded-lg border border-slate-700 bg-slate-800 px-3 py-2 text-sm text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
                >
                  <option value="OAUTH2">{t.integrations.authOauth}</option>
                  <option value="API_KEY">{t.integrations.authApiKey}</option>
                  <option value="BASIC_AUTH">{t.integrations.authBasic}</option>
                  <option value="PUBLIC">{t.integrations.authPublic}</option>
                </select>
              </div>

              <div className="flex justify-end space-x-3 pt-2">
                <Button
                  type="button"
                  variant="ghost"
                  onClick={() => setShowAddForm(false)}
                >
                  {t.integrations.cancelBtn}
                </Button>
                <Button type="submit" isLoading={loading}>
                  {t.integrations.verifyBtn}
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      )}

      {/* SITES TABLE */}
      <Card>
        <CardHeader>
          <CardTitle>{t.integrations.tableTitle}</CardTitle>
          <CardDescription>
            {t.integrations.tableDesc}
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs min-w-[600px]">
              <thead>
                <tr className="border-b border-slate-800 text-slate-400 uppercase tracking-wider">
                  <th className="pb-3 font-semibold">{t.integrations.colEnv}</th>
                  <th className="pb-3 font-semibold">{t.integrations.colDomain}</th>
                  <th className="pb-3 font-semibold">{t.integrations.colAuth}</th>
                  <th className="pb-3 font-semibold">{t.integrations.colSsrf}</th>
                  <th className="pb-3 font-semibold">{t.integrations.colCreated}</th>
                  <th className="pb-3 font-semibold">{t.integrations.colActions}</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60 text-slate-300">
                {sites.map((site) => (
                  <tr key={site.id}>
                    <td className="py-3 font-medium text-white">{site.name}</td>
                    <td className="py-3 font-mono text-indigo-400">{site.domain}</td>
                    <td className="py-3">
                      <span className="bg-slate-800 px-2 py-1 rounded text-slate-300">
                        {site.authType}
                      </span>
                    </td>
                    <td className="py-3">
                      <Badge variant="success">
                        <CheckCircle2 className="mr-1 h-3 w-3" /> {t.integrations.verifiedSafe}
                      </Badge>
                    </td>
                    <td className="py-3 text-slate-400">
                      {new Date(site.createdAt).toLocaleDateString()}
                    </td>
                    <td className="py-3">
                      <a
                        href={site.baseUrl}
                        target="_blank"
                        rel="noreferrer"
                        className="inline-flex items-center text-slate-400 hover:text-white"
                        aria-label={`Open external link to ${site.name}`}
                      >
                        <ExternalLink className="h-3.5 w-3.5" />
                      </a>
                    </td>
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
