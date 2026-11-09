"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { useI18n } from "@/lib/i18n";
import { webProjectsApi, WebProject, WebsiteDiscovery } from "@/lib/api";
import {
  Globe2,
  PlusCircle,
  PlayCircle,
  Compass,
  Trash2,
  ExternalLink,
  ShieldCheck,
  CheckCircle2,
  AlertCircle,
  FileCode,
  Layers,
} from "lucide-react";

export default function WebsitesPage() {
  const { t } = useI18n();
  const [projects, setProjects] = useState<WebProject[]>([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [discoveringId, setDiscoveringId] = useState<string | null>(null);
  const [discoveryResult, setDiscoveryResult] = useState<WebsiteDiscovery | null>(null);

  // Form states
  const [name, setName] = useState("");
  const [targetUrl, setTargetUrl] = useState("");
  const [environment, setEnvironment] = useState("PRODUCTION");
  const [description, setDescription] = useState("");
  const [instructions, setInstructions] = useState("");
  const [isAuthorized, setIsAuthorized] = useState(true);
  const [formError, setFormError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    loadWebsites();
  }, []);

  async function loadWebsites() {
    setLoading(true);
    try {
      const data = await webProjectsApi.list();
      setProjects(data);
    } catch (err) {
      console.error("Failed to load websites:", err);
    } finally {
      setLoading(false);
    }
  }

  async function handleCreateWebsite(e: React.FormEvent) {
    e.preventDefault();
    if (!name || !targetUrl) {
      setFormError("Name and Target URL are required");
      return;
    }
    if (!isAuthorized) {
      setFormError("You must confirm authorization to test this target");
      return;
    }

    setSubmitting(true);
    setFormError(null);
    try {
      await webProjectsApi.create({
        name,
        targetUrl,
        environment,
        description,
        instructions,
        isAuthorized,
      });
      setIsModalOpen(false);
      setName("");
      setTargetUrl("");
      setDescription("");
      setInstructions("");
      loadWebsites();
    } catch (err: any) {
      setFormError(err.message || "Failed to register website");
    } finally {
      setSubmitting(false);
    }
  }

  async function handleDiscover(project: WebProject) {
    setDiscoveringId(project.id);
    try {
      const res = await webProjectsApi.discover(project.id);
      setDiscoveryResult(res);
    } catch (err) {
      console.error("Discovery error:", err);
    } finally {
      setDiscoveringId(null);
    }
  }

  async function handleDelete(id: string) {
    if (confirm("Are you sure you want to remove this website registration?")) {
      try {
        await webProjectsApi.delete(id);
        loadWebsites();
      } catch (err) {
        console.error("Delete error:", err);
      }
    }
  }

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      {/* HEADER */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-white">{t.webTesting.websitesTitle}</h1>
          <p className="text-sm text-slate-400 mt-1">
            {t.webTesting.websitesSubtitle}
          </p>
        </div>

        <Button onClick={() => setIsModalOpen(true)} className="bg-indigo-600 hover:bg-indigo-500 text-white">
          <PlusCircle className="mr-2 h-4 w-4" />
          {t.webTesting.registerWebsiteBtn}
        </Button>
      </div>

      {/* WEBSITES LIST */}
      {loading ? (
        <div className="py-12 text-center text-sm text-slate-500">{t.common.loading}</div>
      ) : projects.length === 0 ? (
        <Card className="p-12 text-center space-y-4">
          <Globe2 className="h-12 w-12 text-slate-600 mx-auto" />
          <h3 className="text-lg font-semibold text-white">No websites registered yet</h3>
          <p className="text-sm text-slate-400 max-w-md mx-auto">
            Register your first web application to enable autonomous Playwright discovery, AI test generation, and defect tracking.
          </p>
          <Button onClick={() => setIsModalOpen(true)} className="bg-indigo-600 hover:bg-indigo-500 text-white">
            <PlusCircle className="mr-2 h-4 w-4" />
            {t.webTesting.registerWebsiteBtn}
          </Button>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {projects.map((project) => (
            <Card key={project.id} className="flex flex-col justify-between hover:border-slate-700 transition-colors">
              <CardHeader className="pb-3">
                <div className="flex items-start justify-between">
                  <div className="space-y-1">
                    <CardTitle className="text-base flex items-center gap-2">
                      {project.name}
                    </CardTitle>
                    <a
                      href={project.targetUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-xs text-indigo-400 hover:underline flex items-center gap-1 font-mono truncate max-w-[220px]"
                    >
                      {project.targetUrl}
                      <ExternalLink className="h-3 w-3 flex-shrink-0" />
                    </a>
                  </div>
                  <Badge variant={project.environment === "PRODUCTION" ? "default" : "outline"}>
                    {project.environment}
                  </Badge>
                </div>
                {project.description && (
                  <CardDescription className="text-xs mt-2 line-clamp-2">
                    {project.description}
                  </CardDescription>
                )}
              </CardHeader>

              <CardContent className="pt-2 border-t border-slate-800/80 space-y-3">
                <div className="flex items-center justify-between text-xs text-slate-400">
                  <span className="flex items-center gap-1.5 text-emerald-400">
                    <ShieldCheck className="h-3.5 w-3.5" />
                    Authorized Target
                  </span>
                  <span>{new Date(project.createdAt).toLocaleDateString()}</span>
                </div>

                <div className="flex items-center gap-2 pt-1">
                  <Button
                    size="sm"
                    variant="outline"
                    className="flex-1 text-xs"
                    disabled={discoveringId === project.id}
                    onClick={() => handleDiscover(project)}
                  >
                    <Compass className="mr-1.5 h-3.5 w-3.5 text-cyan-400" />
                    {discoveringId === project.id ? "Analyzing..." : t.webTesting.discoverBtn}
                  </Button>
                  <Link href={`/dashboard/tests/new?projectId=${project.id}`} className="flex-1">
                    <Button size="sm" className="w-full text-xs bg-indigo-600 hover:bg-indigo-500 text-white">
                      <PlayCircle className="mr-1.5 h-3.5 w-3.5" />
                      {t.webTesting.startTestBtn}
                    </Button>
                  </Link>
                  <Button
                    size="sm"
                    variant="ghost"
                    className="text-slate-500 hover:text-rose-400 h-8 w-8 p-0"
                    onClick={() => handleDelete(project.id)}
                  >
                    <Trash2 className="h-3.5 w-3.5" />
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}

      {/* DISCOVERY RESULT MODAL */}
      {discoveryResult && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 p-4 overflow-y-auto">
          <div className="bg-slate-900 border border-slate-800 rounded-xl max-w-2xl w-full p-6 space-y-5 my-8">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <div>
                <h3 className="text-lg font-bold text-white flex items-center gap-2">
                  <Compass className="h-5 w-5 text-indigo-400" />
                  Universal Discovery Analysis
                </h3>
                <p className="text-xs text-slate-400 font-mono mt-0.5">{discoveryResult.targetUrl}</p>
              </div>
              <Button size="sm" variant="ghost" onClick={() => setDiscoveryResult(null)}>
                ✕
              </Button>
            </div>

            <div className="grid grid-cols-2 gap-3 text-xs">
              <div className="p-3 bg-slate-950 rounded-lg border border-slate-800">
                <span className="text-slate-500">Page Title</span>
                <p className="font-semibold text-white mt-1 truncate">{discoveryResult.pageTitle || "N/A"}</p>
              </div>
              <div className="p-3 bg-slate-950 rounded-lg border border-slate-800">
                <span className="text-slate-500">{t.webTesting.detectedFramework}</span>
                <p className="font-semibold text-emerald-400 mt-1 truncate">{discoveryResult.detectedFramework}</p>
              </div>
            </div>

            {/* Routes */}
            <div className="space-y-1.5">
              <h4 className="text-xs font-semibold text-slate-300 uppercase tracking-wider">
                {t.webTesting.discoveredRoutes} ({discoveryResult.routes.length})
              </h4>
              <div className="max-h-32 overflow-y-auto space-y-1 bg-slate-950 p-2.5 rounded-lg border border-slate-800 text-xs font-mono">
                {discoveryResult.routes.map((r, i) => (
                  <div key={i} className="flex items-center justify-between text-slate-400 hover:text-slate-200">
                    <span className="truncate">{r.path}</span>
                    <span className="text-[10px] text-slate-600">{r.linkText || "link"}</span>
                  </div>
                ))}
              </div>
            </div>

            {/* Forms */}
            <div className="space-y-1.5">
              <h4 className="text-xs font-semibold text-slate-300 uppercase tracking-wider">
                {t.webTesting.discoveredForms} ({discoveryResult.forms.length})
              </h4>
              <div className="max-h-32 overflow-y-auto space-y-1.5 bg-slate-950 p-2.5 rounded-lg border border-slate-800 text-xs">
                {discoveryResult.forms.map((f, i) => (
                  <div key={i} className="flex items-center justify-between p-1.5 rounded bg-slate-900/60 border border-slate-800/40">
                    <span className="font-semibold text-slate-300">{f.formType} Form</span>
                    <Badge variant="outline" className="text-[10px]">{f.fields.length} Inputs</Badge>
                  </div>
                ))}
              </div>
            </div>

            <div className="flex justify-end space-x-2 pt-2 border-t border-slate-800">
              <Button size="sm" variant="outline" onClick={() => setDiscoveryResult(null)}>
                {t.common.close}
              </Button>
            </div>
          </div>
        </div>
      )}

      {/* REGISTER WEBSITE MODAL */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 p-4 overflow-y-auto">
          <div className="bg-slate-900 border border-slate-800 rounded-xl max-w-lg w-full p-6 space-y-5 my-8">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="text-lg font-bold text-white flex items-center gap-2">
                <Globe2 className="h-5 w-5 text-indigo-400" />
                {t.webTesting.registerWebsiteBtn}
              </h3>
              <Button size="sm" variant="ghost" onClick={() => setIsModalOpen(false)}>
                ✕
              </Button>
            </div>

            <form onSubmit={handleCreateWebsite} className="space-y-4">
              {formError && (
                <div className="p-3 rounded-lg bg-rose-500/10 border border-rose-500/20 text-rose-400 text-xs flex items-center gap-2">
                  <AlertCircle className="h-4 w-4 flex-shrink-0" />
                  <span>{formError}</span>
                </div>
              )}

              <div className="space-y-1.5">
                <label className="text-xs font-medium text-slate-300">
                  {t.webTesting.websiteName} *
                </label>
                <Input
                  placeholder="e.g. My E-Commerce Store"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  required
                />
              </div>

              <div className="space-y-1.5">
                <label className="text-xs font-medium text-slate-300">
                  {t.webTesting.targetUrl} *
                </label>
                <Input
                  type="url"
                  placeholder="https://example.com"
                  value={targetUrl}
                  onChange={(e) => setTargetUrl(e.target.value)}
                  required
                />
                <p className="text-[11px] text-slate-500">
                  HTTPS is enforced. Local / private targets require explicit authorization.
                </p>
              </div>

              <div className="space-y-1.5">
                <label className="text-xs font-medium text-slate-300">
                  {t.webTesting.environment}
                </label>
                <select
                  value={environment}
                  onChange={(e) => setEnvironment(e.target.value)}
                  className="w-full h-10 px-3 rounded-lg bg-slate-950 border border-slate-800 text-xs text-white focus:outline-none focus:ring-1 focus:ring-indigo-500"
                >
                  <option value="PRODUCTION">Production</option>
                  <option value="STAGING">Staging</option>
                  <option value="DEVELOPMENT">Development</option>
                </select>
              </div>

              <div className="space-y-1.5">
                <label className="text-xs font-medium text-slate-300">
                  {t.webTesting.description}
                </label>
                <Input
                  placeholder="Short description of the web application..."
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                />
              </div>

              <div className="space-y-1.5">
                <label className="text-xs font-medium text-slate-300">
                  {t.webTesting.instructions}
                </label>
                <textarea
                  rows={2}
                  className="w-full p-2.5 rounded-lg bg-slate-950 border border-slate-800 text-xs text-white focus:outline-none focus:ring-1 focus:ring-indigo-500"
                  placeholder="Any guidelines, focus areas, or known behaviors for the AI..."
                  value={instructions}
                  onChange={(e) => setInstructions(e.target.value)}
                />
              </div>

              <div className="pt-2 border-t border-slate-800 flex items-start space-x-2.5">
                <input
                  type="checkbox"
                  id="authConfirm"
                  checked={isAuthorized}
                  onChange={(e) => setIsAuthorized(e.target.checked)}
                  className="mt-0.5 rounded border-slate-700 bg-slate-950 text-indigo-600 focus:ring-0"
                />
                <label htmlFor="authConfirm" className="text-xs text-slate-400 select-none">
                  {t.webTesting.authorizeConfirm}
                </label>
              </div>

              <div className="flex justify-end space-x-2 pt-3 border-t border-slate-800">
                <Button type="button" variant="outline" onClick={() => setIsModalOpen(false)}>
                  {t.common.cancel}
                </Button>
                <Button type="submit" disabled={submitting} className="bg-indigo-600 hover:bg-indigo-500 text-white">
                  {submitting ? "Registering..." : t.common.save}
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
