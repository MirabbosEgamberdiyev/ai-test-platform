# Universal AI Web Test for https://web.pravaonline.uz/me
$ErrorActionPreference = "Stop"
$BaseUrl = "http://localhost:8085"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " STARTING AI TEST AUTOMATION RUN: https://web.pravaonline.uz/me" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. Onboard / Authenticate
$uniqueSuffix = [System.Guid]::NewGuid().ToString().Substring(0, 6)
$regBody = @{
    tenantName = "Prava Profile Audit $uniqueSuffix"
    tenantSlug = "prava-me-$uniqueSuffix"
    adminFullName = "Senior QA Automation Engineer"
    adminEmail = "auditor-$uniqueSuffix@prava-test.uz"
    adminPassword = "AuditorSecurePassword2026!"
} | ConvertTo-Json

Write-Host "`n[STEP 1] Authenticating Auditor Account..." -ForegroundColor Yellow
$regResp = Invoke-RestMethod -Uri "$BaseUrl/api/v1/auth/register" -Method Post -Body $regBody -ContentType "application/json"
$token = $regResp.accessToken
Write-Host "  Auditor authenticated: $($regResp.email) (Tenant: $($regResp.tenantId))" -ForegroundColor Green

$headers = @{
    "Authorization" = "Bearer $token"
}

# 2. Register Target Project
Write-Host "`n[STEP 2] Registering Target Project: https://web.pravaonline.uz/me..." -ForegroundColor Yellow
$projBody = @{
    name = "Prava Online - Shaxsiy Kabinet (/me)"
    targetUrl = "https://web.pravaonline.uz/me"
    environment = "PRODUCTION"
    description = "Prava Online haydovchilik portali shaxsiy kabinet / profil sahifasi"
    instructions = "Profil ma'lumotlari, autentifikatsiya holati, mobil moslashuvchanlik va qulaylikni tekshir"
    isAuthorized = $true
} | ConvertTo-Json

$projResp = Invoke-RestMethod -Uri "$BaseUrl/api/v1/web-projects" -Method Post -Body $projBody -ContentType "application/json" -Headers $headers
$projectId = $projResp.id
Write-Host "  Project ID created: $projectId" -ForegroundColor Green
Write-Host "  Target URL: $($projResp.targetUrl)" -ForegroundColor Green

# 3. Discovery via Playwright
Write-Host "`n[STEP 3] Running Automated Discovery via Playwright Chromium..." -ForegroundColor Yellow
$discovery = Invoke-RestMethod -Uri "$BaseUrl/api/v1/web-projects/$projectId/discover" -Method Post -Headers $headers
Write-Host "  Discovered Page Title: $($discovery.pageTitle)" -ForegroundColor Green
Write-Host "  Detected Framework: $($discovery.detectedFramework)" -ForegroundColor Green
Write-Host "  Discovered Routes Count: $($discovery.routes.Count)" -ForegroundColor Green
Write-Host "  Discovered Forms Count: $($discovery.forms.Count)" -ForegroundColor Green
Write-Host "  Interactive Elements Count: $($discovery.elements.Count)" -ForegroundColor Green
Write-Host "  Accessibility Baseline: $($discovery.accessibilitySummary)" -ForegroundColor Green

# 4. Generate AI Test Plan in Uzbek
Write-Host "`n[STEP 4] Generating Natural Language AI Test Plan (uz-Latn)..." -ForegroundColor Yellow
$planBody = @{
    projectId = $projectId
    naturalLanguageGoal = "Shaxsiy kabinet (profile) sahifasiga kirib, sahifa yuklanishi, foydalanuvchi ma'lumotlari va navigatsiya havolalarini, autentifikatsiya/sessiya xavfsizligini, mobil moslashuvchanlikni (375px) va WCAG 2.2 AA qulaylik standartlarini tekshir."
    language = "uz-Latn"
    scopes = @("FUNCTIONAL", "UI_UX", "RESPONSIVE", "ACCESSIBILITY", "SECURITY")
} | ConvertTo-Json

$planResp = Invoke-RestMethod -Uri "$BaseUrl/api/v1/web-tests/plans" -Method Post -Body $planBody -ContentType "application/json" -Headers $headers
$planId = $planResp.id
Write-Host "  Plan ID: $planId" -ForegroundColor Green
Write-Host "  Plan Status: $($planResp.status)" -ForegroundColor Green
Write-Host "  Generated Test Cases ($($planResp.testCases.Count)): " -ForegroundColor Green
foreach ($tc in $planResp.testCases) {
    Write-Host "    - [$($tc.category)] $($tc.title) (Priority: $($tc.priority))" -ForegroundColor Cyan
}

# 5. Approve Plan
Write-Host "`n[STEP 5] Human-in-the-Loop Plan Approval..." -ForegroundColor Yellow
$approvedPlan = Invoke-RestMethod -Uri "$BaseUrl/api/v1/web-tests/plans/$planId/approve" -Method Put -Headers $headers
Write-Host "  Plan Status: $($approvedPlan.status)" -ForegroundColor Green

# 6. Execute Test Run in Browser Sandbox
Write-Host "`n[STEP 6] Executing Test Run in Isolated Playwright Sandbox..." -ForegroundColor Yellow
$runBody = @{
    planId = $planId
    browserType = "CHROMIUM"
    viewportWidth = 1280
    viewportHeight = 800
} | ConvertTo-Json

$runData = Invoke-RestMethod -Uri "$BaseUrl/api/v1/web-tests/runs" -Method Post -Body $runBody -ContentType "application/json" -Headers $headers -TimeoutSec 120

Write-Host "  Execution Run ID: $($runData.id)" -ForegroundColor Green
Write-Host "  Status: $($runData.status)" -ForegroundColor $(if ($runData.status -eq "COMPLETED") { "Green" } else { "Magenta" })
Write-Host "  Total Cases: $($runData.totalCases) | Passed: $($runData.passedCases) | Failed: $($runData.failedCases)" -ForegroundColor Green
Write-Host "`n--- Playwright Sandbox Execution Log ---" -ForegroundColor DarkGray
Write-Host $runData.executionLogs -ForegroundColor Gray
Write-Host "----------------------------------------" -ForegroundColor DarkGray

# 7. Discovered Defects & AI Analysis
Write-Host "`n[STEP 7] Inspecting Discovered Defects & AI Root Cause..." -ForegroundColor Yellow
$defects = Invoke-RestMethod -Uri "$BaseUrl/api/v1/web-tests/runs/$($runData.id)/defects" -Method Get -Headers $headers
if ($defects -and $defects.Count -gt 0) {
    Write-Host "  Found $($defects.Count) defect(s):" -ForegroundColor Magenta
    foreach ($d in $defects) {
        Write-Host "  * [$($d.severity)] $($d.defectCode) - $($d.title)" -ForegroundColor Red
        Write-Host "    Expected: $($d.expectedResult)"
        Write-Host "    Actual: $($d.actualResult)"
        Write-Host "    AI Hypothesis: $($d.aiRootCause)"
        if ($d.screenshotPath) {
            Write-Host "    Screenshot: $($d.screenshotPath)"
        }
    }
} else {
    Write-Host "  No defects discovered! All automated checks passed cleanly." -ForegroundColor Green
}

# 8. Report & Exports
Write-Host "`n[STEP 8] Report Generation & Multi-Format Exports..." -ForegroundColor Yellow
$report = Invoke-RestMethod -Uri "$BaseUrl/api/v1/web-tests/runs/$($runData.id)/report" -Method Get -Headers $headers
Write-Host "  Pass Percentage: $($report.passPercentage)%" -ForegroundColor Green
Write-Host "  Duration: $($report.executionDurationMs) ms" -ForegroundColor Green
Write-Host "  AI Executive Summary: $($report.aiExecutiveSummary)" -ForegroundColor Cyan

$htmlExport = Invoke-RestMethod -Uri "$BaseUrl/api/v1/web-tests/runs/$($runData.id)/export?format=html" -Method Get -Headers $headers
$jsonExport = Invoke-RestMethod -Uri "$BaseUrl/api/v1/web-tests/runs/$($runData.id)/export?format=json" -Method Get -Headers $headers
$csvExport = Invoke-RestMethod -Uri "$BaseUrl/api/v1/web-tests/runs/$($runData.id)/export?format=csv" -Method Get -Headers $headers

Set-Content -Path "D:\My-project\ai-test-platform\artifacts\pravaonline-me-report.html" -Value $htmlExport -Encoding UTF8
Set-Content -Path "D:\My-project\ai-test-platform\artifacts\pravaonline-me-report.csv" -Value $csvExport -Encoding UTF8

Write-Host "  HTML Export saved: artifacts/pravaonline-me-report.html ($($htmlExport.Length) chars)" -ForegroundColor Green
Write-Host "  CSV Export saved: artifacts/pravaonline-me-report.csv ($($csvExport.Length) chars)" -ForegroundColor Green

# Save evidence to artifacts
$evidenceObj = @{
    targetUrl = "https://web.pravaonline.uz/me"
    projectId = $projectId
    planId = $planId
    runId = $runData.id
    status = $runData.status
    totalCases = $runData.totalCases
    passedCases = $runData.passedCases
    failedCases = $runData.failedCases
    durationMs = $report.executionDurationMs
    passPercentage = $report.passPercentage
    aiSummary = $report.aiExecutiveSummary
    defects = $defects
    executionLogs = $runData.executionLogs
}
$evidencePath = "D:\My-project\ai-test-platform\artifacts\pravaonline-me-evidence.json"
$evidenceObj | ConvertTo-Json -Depth 10 | Set-Content -Path $evidencePath -Encoding UTF8
Write-Host "`n==========================================================" -ForegroundColor Green
Write-Host " AUDIT COMPLETE - Evidence saved to: $evidencePath" -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Green
