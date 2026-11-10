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
    const msg = data.detail || (data as any).message || data.title || (data as any).error || "An error occurred during API request";
    super(msg);
    this.name = "ApiException";
    this.status = status;
    this.data = data;
  }
}

function isJwtExpired(token: string | null): boolean {
  if (!token || token === "undefined" || token === "null") return true;
  try {
    const parts = token.split(".");
    if (parts.length !== 3) return true;
    const base64Url = parts[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join("")
    );
    const parsed = JSON.parse(jsonPayload);
    if (!parsed.exp) return false;
    // Expired if current time exceeds exp minus 30 seconds buffer
    return Date.now() >= (parsed.exp * 1000 - 30000);
  } catch {
    return false;
  }
}

function getStoredToken(): string | null {
  if (typeof window === "undefined") return null;
  const token = localStorage.getItem("accessToken");
  if (!token || token === "undefined" || token === "null") return null;
  if (isJwtExpired(token)) {
    localStorage.removeItem("accessToken");
    return null;
  }
  return token;
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

export async function ensureDemoSession(forceRefresh: boolean = false): Promise<string> {
  if (typeof window === "undefined") return "";
  let token = localStorage.getItem("accessToken");
  if (!forceRefresh && token && token !== "undefined" && token !== "null" && !isJwtExpired(token)) {
    return token;
  }

  // 1. Try standard Admin login
  try {
    let res = await fetch(`${API_BASE}/api/v1/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        email: "Admin",
        password: "Admin.123$",
      }),
    });

    // 2. If not yet updated, try legacy demo passwords
    if (!res.ok) {
      res = await fetch(`${API_BASE}/api/v1/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email: "admin@enterprise-demo.com",
          password: "AdminSecure2026!",
        }),
      });
    }

    // 3. If account does not exist or login failed, provision a fresh workspace session
    if (!res.ok) {
      const stamp = Date.now().toString(36) + Math.random().toString(36).substring(2, 6);
      res = await fetch(`${API_BASE}/api/v1/auth/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          tenantName: "Enterprise Test Workspace",
          tenantSlug: "ws-" + stamp,
          adminFullName: "Enterprise Administrator",
          adminEmail: `admin-${stamp}@aitestops.local`,
          adminPassword: "AdminSecure2026!",
        }),
      });
    }

    if (res.ok) {
      const data = await res.json();
      if (data.accessToken) {
        setStoredToken(data.accessToken, data.refreshToken);
        const tId = data.user?.tenantId || data.tenantId || "tenant-demo-001";
        localStorage.setItem("currentTenantId", tId);
        if (data.user?.id) localStorage.setItem("currentUserId", data.user.id);
        return data.accessToken;
      }
    }
  } catch (e) {
    console.warn("Failed to auto-authenticate demo session:", e);
  }
  return "";
}

export async function apiRequest<T>(
  endpoint: string,
  options: RequestInit & { _retry?: boolean } = {}
): Promise<T> {
  let token = getStoredToken();
  if ((!token || token === "undefined" || token === "null") &&
      endpoint !== "/api/v1/auth/login" &&
      endpoint !== "/api/v1/auth/register" &&
      !endpoint.startsWith("/api/v1/auth/oauth/") &&
      !endpoint.startsWith("/api/v1/auth/forgot-password") &&
      !endpoint.startsWith("/api/v1/auth/reset-password") &&
      !endpoint.startsWith("/api/v1/auth/verify-email")) {
    token = await ensureDemoSession();
  }
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

  let response: Response;
  try {
    response = await fetch(url, {
      ...options,
      headers,
    });
  } catch (netErr: any) {
    throw new ApiException(0, {
      status: 0,
      title: "Network Connection Error",
      detail: netErr.message || "Could not reach backend API at " + API_BASE,
    });
  }

  // Handle 401 Unauthorized or 403 Forbidden by attempting single transparent re-auth & retry
  if ((response.status === 401 || response.status === 403) && !options._retry &&
      endpoint !== "/api/v1/auth/login" &&
      endpoint !== "/api/v1/auth/register") {
    clearStoredTokens();
    const freshToken = await ensureDemoSession(true);
    if (freshToken) {
      headers["Authorization"] = `Bearer ${freshToken}`;
      return apiRequest<T>(endpoint, {
        ...options,
        headers,
        _retry: true,
      });
    }
  }

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

  googleLogin: (idToken: string) =>
    apiRequest<{
      tenantId: string;
      userId: string;
      accessToken: string;
      refreshToken: string;
      tokenType: string;
      expiresIn: number;
      user: any;
    }>("/api/v1/auth/oauth/google", {
      method: "POST",
      body: JSON.stringify({ credential: idToken }),
    }),

  forgotPassword: (email: string) =>
    apiRequest<{ message: string; success: boolean }>("/api/v1/auth/forgot-password", {
      method: "POST",
      body: JSON.stringify({ email }),
    }),

  resetPassword: (token: string, newPassword: string) =>
    apiRequest<{ message: string; success: boolean }>("/api/v1/auth/reset-password", {
      method: "POST",
      body: JSON.stringify({ token, newPassword }),
    }),

  changePassword: (oldPassword: string, newPassword: string) =>
    apiRequest<{ message: string; success: boolean }>("/api/v1/auth/change-password", {
      method: "POST",
      body: JSON.stringify({ oldPassword, newPassword }),
    }),

  verifyEmail: (token: string) =>
    apiRequest<{ message: string; success: boolean }>("/api/v1/auth/verify-email", {
      method: "POST",
      body: JSON.stringify({ token }),
    }),

  logoutAll: () =>
    apiRequest<{ message: string; success: boolean }>("/api/v1/auth/logout-all", {
      method: "POST",
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
    authType?: string;
    authUsername?: string;
    authPassword?: string;
    sessionCookie?: string;
    bearerToken?: string;
    storageStateJson?: string;
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
    authType?: string;
    authUsername?: string;
    authPassword?: string;
    sessionCookie?: string;
    bearerToken?: string;
    storageStateJson?: string;
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
// URL TEST SOLVING ENGINE API
// -------------------------------------------------------------
export interface DiscoveredTicket {
  id: string;
  ticketNumber: number;
  title: string;
  ticketUrl: string;
  questionCount: number;
  category?: string;
  isAvailable: boolean;
}

export interface UrlDiscoveryResponse {
  id: string;
  targetUrl: string;
  platformType: string;
  discoveredTitle: string;
  ticketCount: number;
  status: string;
  errorMessage?: string;
  tickets: DiscoveredTicket[];
  confirmationMessage: string;
  createdAt: string;
}

export interface SolvedQuestion {
  id: string;
  ticketNumber: number;
  ticketTitle: string;
  questionIndex: number;
  questionText: string;
  imageUrl?: string;
  options: string[];
  selectedAnswerIndex: number;
  selectedAnswerText: string;
  aiConfidenceScore: number;
  verificationStatus: "SOLVED" | "VERIFIED" | "NEEDS_REVIEW" | "AMBIGUOUS";
  reasoningExplanation: string;
  ruleReference?: string;
  ambiguityNote?: string;
  arithmeticVerified: boolean;
}

export interface UrlSolvingSession {
  id: string;
  targetUrl: string;
  selectedTicketNumbers: string;
  language: string;
  status: "PENDING" | "SOLVING" | "COMPLETED" | "FAILED";
  executionMode?: "ANSWER_MODE" | "AUTHORIZED_AUTOMATION_MODE" | "GUIDED_MODE";
  submissionStatus?: "NOT_STARTED" | "WAITING_FOR_APPROVAL" | "SUBMITTING" | "VERIFIED" | "FAILED" | "PROHIBITED";
  screenshotPath?: string;
  executionEvidenceJson?: string;
  browserLogs?: string;
  totalQuestions: number;
  solvedQuestionsCount: number;
  verifiedQuestionsCount: number;
  reviewNeededCount: number;
  executionTimeMs: number;
  createdAt: string;
  completedAt?: string;
  questions?: SolvedQuestion[];
}

export const urlTestsApi = {
  discover: (data: { targetUrl: string }, language: string = "uz-Latn") =>
    apiRequest<UrlDiscoveryResponse>(`/api/v1/url-tests/discover?language=${language}`, {
      method: "POST",
      body: JSON.stringify(data),
    }),

  getDiscovery: (id: string, language: string = "uz-Latn") =>
    apiRequest<UrlDiscoveryResponse>(`/api/v1/url-tests/discovery/${id}?language=${language}`),

  solve: (data: {
    discoverySessionId?: string;
    targetUrl: string;
    selectedTicketNumbers: string;
    language?: string;
    executionMode?: "ANSWER_MODE" | "AUTHORIZED_AUTOMATION_MODE" | "GUIDED_MODE";
    userConfirmedSubmission?: boolean;
  }) =>
    apiRequest<UrlSolvingSession>("/api/v1/url-tests/solve", {
      method: "POST",
      body: JSON.stringify(data),
    }),

  getSession: (id: string) =>
    apiRequest<UrlSolvingSession>(`/api/v1/url-tests/sessions/${id}`),

  listSessions: () =>
    apiRequest<UrlSolvingSession[]>("/api/v1/url-tests/sessions"),

  getExportUrl: (sessionId: string, format: "json" | "csv" = "json") =>
    `${API_BASE}/api/v1/url-tests/sessions/${sessionId}/export?format=${format}`,

  exportText: async (sessionId: string): Promise<string> => {
    const res = await fetch(`${API_BASE}/api/v1/url-tests/sessions/${sessionId}/export?format=copy`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("accessToken") || ""}`,
      },
    });
    return res.text();
  },
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
