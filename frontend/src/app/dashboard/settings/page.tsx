"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { useI18n } from "@/lib/i18n";
import { Settings, ShieldCheck, Key, Database, Bell } from "lucide-react";

export default function SettingsPage() {
  const { t } = useI18n();
  const [retentionDays, setRetentionDays] = useState("90");
  const [maxConcurrency, setMaxConcurrency] = useState("5");

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      <div>
        <h1 className="text-2xl font-bold tracking-tight text-white">{t.nav.settings}</h1>
        <p className="text-sm text-slate-400 mt-1">
          Configure organization settings, browser worker concurrency, and security policies.
        </p>
      </div>

      <div className="space-y-5">
        <Card className="p-6 space-y-4">
          <div className="flex items-center space-x-3 border-b border-slate-800 pb-3">
            <ShieldCheck className="h-5 w-5 text-emerald-400" />
            <div>
              <h3 className="text-sm font-semibold text-white">Security & SSRF Policies</h3>
              <p className="text-xs text-slate-400">Strict egress boundaries and target authorization enforcement</p>
            </div>
          </div>

          <div className="space-y-3 text-xs text-slate-300">
            <div className="flex items-center justify-between p-3 rounded-lg bg-slate-950 border border-slate-800">
              <div>
                <p className="font-semibold text-white">SSRF RFC 1918 Blocking</p>
                <p className="text-slate-500">Blocks local, loopback, and private subnet exploration</p>
              </div>
              <span className="text-emerald-400 font-bold">ENFORCED</span>
            </div>

            <div className="flex items-center justify-between p-3 rounded-lg bg-slate-950 border border-slate-800">
              <div>
                <p className="font-semibold text-white">Hardware AES-256-GCM Vault</p>
                <p className="text-slate-500">Encrypts test credentials and tokens at rest</p>
              </div>
              <span className="text-emerald-400 font-bold">ACTIVE</span>
            </div>
          </div>
        </Card>

        <Card className="p-6 space-y-4">
          <div className="flex items-center space-x-3 border-b border-slate-800 pb-3">
            <Database className="h-5 w-5 text-indigo-400" />
            <div>
              <h3 className="text-sm font-semibold text-white">Artifact Retention & Concurrency</h3>
              <p className="text-xs text-slate-400">Configure storage cleanup and browser worker allocation limits</p>
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="space-y-1.5">
              <label className="text-xs font-medium text-slate-300">Screenshot & Trace Retention (Days)</label>
              <Input
                type="number"
                value={retentionDays}
                onChange={(e) => setRetentionDays(e.target.value)}
              />
            </div>
            <div className="space-y-1.5">
              <label className="text-xs font-medium text-slate-300">Max Concurrent Playwright Workers</label>
              <Input
                type="number"
                value={maxConcurrency}
                onChange={(e) => setMaxConcurrency(e.target.value)}
              />
            </div>
          </div>

          <div className="flex justify-end pt-2">
            <Button size="sm" className="bg-indigo-600 hover:bg-indigo-500 text-white">
              {t.common.save}
            </Button>
          </div>
        </Card>
      </div>
    </div>
  );
}
