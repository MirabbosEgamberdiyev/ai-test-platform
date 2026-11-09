const API_BASE = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8085";

export interface ApiError {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  instance?: string;
}

export class ApiException extends Error {
  status: number;
  data: ApiError;

  constructor(status: number, data: ApiError) {
    super(data.detail || data.title || "An error occurred during API request");
    this.name = "ApiException";
    this.status = status;
    this.data = data;
  }
}

function getStoredToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem("accessToken");
}

export function setStoredToken(token: string, refreshToken?: string) {
  if (typeof window !== "undefined") {
    localStorage.setItem("accessToken", token);
    if (refreshToken) {
      localStorage.setItem("refreshToken", refreshToken);
    }
  }
}

export function clearStoredTokens() {
  if (typeof window !== "undefined") {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("currentUser");
  }
}

export async function apiRequest<T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<T> {
  const token = getStoredToken();
  const correlationId = "fe-" + Math.random().toString(36).substring(2, 10);

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    "x-correlation-id": correlationId,
    ...(options.headers as Record<string, string>),
  };

  if (token && !headers["Authorization"]) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const url = `${API_BASE}${endpoint}`;

  const response = await fetch(url, {
    ...options,
    headers,
  });

  if (!response.ok) {
    let errorData: ApiError = {
      status: response.status,
      title: response.statusText,
    };
    try {
      errorData = await response.json();
    } catch {
      // Body not JSON
    }
    throw new ApiException(response.status, errorData);
  }

  if (response.status === 204) {
    return {} as T;
  }

  return response.json();
}

// -------------------------------------------------------------
// IAM AUTH API
// -------------------------------------------------------------
export const authApi = {
  register: (data: { tenantName: string; tenantSlug?: string; email: string; password: string; fullName: string }) => {
    const slug = data.tenantSlug || data.tenantName.toLowerCase().replace(/[^a-z0-9]/g, "-").replace(/-+/g, "-").substring(0, 40);
    return apiRequest<{
      tenantId: string;
      userId: string;
      accessToken: string;
      refreshToken: string;
      tokenType: string;
      expiresIn: number;
    }>("/api/v1/auth/register", {
      method: "POST",
      body: JSON.stringify({
        tenantName: data.tenantName,
        tenantSlug: slug || "tenant-" + Math.random().toString(36).substring(2, 6),
        adminFullName: data.fullName,
        adminEmail: data.email,
        adminPassword: data.password,
      }),
    });
  },

  login: (data: { email: string; password: string }) =>
    apiRequest<{
      tenantId: string;
      userId: string;
      accessToken: string;
      refreshToken: string;
      tokenType: string;
      expiresIn: number;
    }>("/api/v1/auth/login", {
      method: "POST",
      body: JSON.stringify({
        email: data.email,
        password: data.password,
      }),
    }),
};

// -------------------------------------------------------------
// WEB PROJECTS API (UNIVERSAL WEB TESTING TARGETS)
// -------------------------------------------------------------
export interface WebProject {
  id: string;
  tenantId: string;
  websiteId?: string;
  name: string;
  targetUrl: string;
  environment: string; // DEVELOPMENT, STAGING, PRODUCTION
  description?: string;
  instructions?: string;
  isAuthorized: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface WebsiteDiscovery {
  targetUrl: string;
  pageTitle: string;
  metaDescription: string;
  detectedFramework: string;
  hasLoginForm: boolean;
  hasRegistrationForm: boolean;
  routes: Array<{ url: string; path: string; linkText: string; isInternal: boolean }>;
  forms: Array<{ formId: string; action: string; method: string; formType: string; fields: any[]; submitButtonText: string }>;
  interactiveElements: Array<{ tagName: string; text: string; selector: string; role: string; ariaLabel: string }>;
  screenshotPath?: string;
  accessibilitySummary?: string;
  discoveredAt: string;
}

export const webProjectsApi = {
  list: () => apiRequest<WebProject[]>("/api/v1/web-projects"),

  get: (id: string) => apiRequest<WebProject>(`/api/v1/web-projects/${id}`),

  create: (data: {
    name: string;
    targetUrl: string;
    environment?: string;
    description?: string;
    instructions?: string;
    isAuthorized?: boolean;
  }) =>
    apiRequest<WebProject>("/api/v1/web-projects", {
      method: "POST",
      body: JSON.stringify(data),
    }),

  update: (
    id: string,
    data: {
      name: string;
      targetUrl: string;
      environment?: string;
      description?: string;
      instructions?: string;
      isAuthorized?: boolean;
    }
  ) =>
    apiRequest<WebProject>(`/api/v1/web-projects/${id}`, {
      method: "PUT",
      body: JSON.stringify(data),
    }),

  delete: (id: string) =>
    apiRequest<void>(`/api/v1/web-projects/${id}`, {
      method: "DELETE",
    }),

  discover: (id: string) =>
    apiRequest<WebsiteDiscovery>(`/api/v1/web-projects/${id}/discover`, {
      method: "POST",
    }),
};

// -------------------------------------------------------------
// WEB TESTS, RUNS & DEFECTS API
// -------------------------------------------------------------
export interface TestCase {
  id: string;
  planId: string;
  title: string;
  category: string;
  priority: string;
  stepsJson: string;
  expectedResult: string;
  orderIndex: number;
}

export interface WebTestPlan {
  id: string;
  projectId: string;
  tenantId: string;
  naturalLanguageGoal: string;
  detectedScope: string;
  language: string;
  status: string; // DRAFT, APPROVED, REJECTED
  discoverySummary?: string;
  testCases: TestCase[];
  createdAt: string;
}

export interface WebTestRun {
  id: string;
  projectId: string;
  planId: string;
  tenantId: string;
  status: "CREATED" | "DISCOVERING" | "GENERATING_PLAN" | "WAITING_APPROVAL" | "RUNNING" | "COMPLETED" | "FAILED" | "CANCELLED";
  browserType: string;
  viewportWidth: number;
  viewportHeight: number;
  startedAt?: string;
  completedAt?: string;
  totalCases: number;
  passedCases: number;
  failedCases: number;
  blockedCases: number;
  skippedCases: number;
  errorSummary?: string;
  executionLogs?: string;
  createdAt: string;
}

export interface WebDefect {
  id: string;
  runId: string;
  caseId?: string;
  defectCode: string;
  title: string;
  severity: "CRITICAL" | "HIGH" | "MEDIUM" | "LOW" | "INFORMATIONAL";
  reproductionSteps: string;
  expectedResult: string;
  actualResult: string;
  screenshotPath?: string;
  consoleLogs?: string;
  networkEvidence?: string;
  aiRootCause?: string;
  confidenceScore: number;
  status: string;
  createdAt: string;
}

export interface WebTestReport {
  id: string;
  runId: string;
  tenantId: string;
  websiteName: string;
  targetUrl: string;
  executionDurationMs: number;
  passPercentage: number;
  criticalDefectsCount: number;
  highDefectsCount: number;
  mediumDefectsCount: number;
  lowDefectsCount: number;
  aiExecutiveSummary: string;
  aiRecommendations: string;
  jsonReportPath?: string;
  htmlReportPath?: string;
  defects: WebDefect[];
  createdAt: string;
}

export interface DashboardStats {
  totalProjects: number;
  totalWebsites: number;
  totalTestRuns: number;
  passedTests: number;
  failedTests: number;
  activeExecutions: number;
  criticalDefects: number;
  successRatePercentage: number;
}

export const webTestsApi = {
  generatePlan: (data: {
    projectId: string;
    naturalLanguageGoal: string;
    language?: string;
    scopes?: string[];
  }) =>
    apiRequest<WebTestPlan>("/api/v1/web-tests/plans", {
      method: "POST",
      body: JSON.stringify(data),
    }),

  approvePlan: (planId: string) =>
    apiRequest<WebTestPlan>(`/api/v1/web-tests/plans/${planId}/approve`, {
      method: "PUT",
    }),

  startRun: (data: {
    planId: string;
    browserType?: string;
    viewportWidth?: number;
    viewportHeight?: number;
  }) =>
    apiRequest<WebTestRun>("/api/v1/web-tests/runs", {
      method: "POST",
      body: JSON.stringify(data),
    }),

  listRuns: () => apiRequest<WebTestRun[]>("/api/v1/web-tests/runs"),

  getRun: (id: string) => apiRequest<WebTestRun>(`/api/v1/web-tests/runs/${id}`),

  getDefects: (runId: string) => apiRequest<WebDefect[]>(`/api/v1/web-tests/runs/${runId}/defects`),

  getReport: (runId: string) => apiRequest<WebTestReport>(`/api/v1/web-tests/runs/${runId}/report`),

  getExportUrl: (runId: string, format: "html" | "json" | "csv" = "html") =>
    `${API_BASE}/api/v1/web-tests/runs/${runId}/export?format=${format}`,

  getDashboardStats: () => apiRequest<DashboardStats>("/api/v1/web-dashboard/stats"),
};

// -------------------------------------------------------------
// LEGACY COMPATIBILITY EXPORTS
// -------------------------------------------------------------
export const registryApi = {
  listWebsites: () => apiRequest<any[]>("/api/v1/registry/websites"),
  registerWebsite: (data: any) =>
    apiRequest<any>("/api/v1/registry/websites", { method: "POST", body: JSON.stringify(data) }),
};

export const practiceApi = {
  startSession: (data: any) =>
    apiRequest<any>("/api/v1/practice/sessions", { method: "POST", body: JSON.stringify(data) }),
  analyzeQuestion: (sessionId: string, data: any) =>
    apiRequest<any>(`/api/v1/practice/sessions/${sessionId}/analyze`, { method: "POST", body: JSON.stringify(data) }),
  submitAnswer: (sessionId: string, data: any) =>
    apiRequest<any>(`/api/v1/practice/sessions/${sessionId}/submit`, { method: "POST", body: JSON.stringify(data) }),
  finishSession: (sessionId: string) =>
    apiRequest<any>(`/api/v1/practice/sessions/${sessionId}/finish`, { method: "POST" }),
};

export const analyticsApi = {
  getSessionResult: (sessionId: string) =>
    apiRequest<any>(`/api/v1/analytics/results/${sessionId}`),
};
