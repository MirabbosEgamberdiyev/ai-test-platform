# Verification Script for Google / Telegram SSO Authenticated Testing on web.pravaonline.uz/me
$ErrorActionPreference = "Stop"
$BaseUrl = "http://localhost:8085"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " VERIFYING GOOGLE / TELEGRAM SSO AUTHENTICATION TESTING" -ForegroundColor Cyan
Write-Host " Target: https://web.pravaonline.uz/me" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# Step 1: Register/Authenticate Tenant
$regBody = @{
    tenantName = "SSO QA Audit Tenant"
    tenantSlug = "sso-audit-" + [System.Guid]::NewGuid().ToString().Substring(0, 6)
    adminFullName = "Principal QA Engineer"
    adminEmail = "sso-auditor-" + [System.Guid]::NewGuid().ToString().Substring(0, 6) + "@testplatform.uz"
    adminPassword = "AuditorPassword2026!"
} | ConvertTo-Json

$regResp = Invoke-RestMethod -Uri "$BaseUrl/api/v1/auth/register" -Method Post -Body $regBody -ContentType "application/json"
$token = $regResp.accessToken
$tenantId = $regResp.user.tenantId
Write-Host "`n[STEP 1] Tenant & Auditor Authenticated: $($regResp.user.email) (Tenant: $tenantId)" -ForegroundColor Green

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type"  = "application/json"
}

# Step 2: Create Web Project
$projBody = @{
    name = "Prava Online - SSO Authenticated Profile (/me)"
    targetUrl = "https://web.pravaonline.uz/me"
    environment = "PRODUCTION"
    description = "Tests authenticated profile and private routes for Google/Telegram logged-in users"
    instructions = "Verify authenticated profile access, navigation, responsiveness, and WCAG accessibility"
    isAuthorized = $true
} | ConvertTo-Json

$projResp = Invoke-RestMethod -Uri "$BaseUrl/api/v1/web-projects" -Method Post -Body $projBody -Headers $headers
$projectId = $projResp.id
Write-Host "`n[STEP 2] Web Project created: $projectId ($($projResp.targetUrl))" -ForegroundColor Green

# Step 3: Generate AI Test Plan with Google / Telegram SSO Auth Mode
$planBody = @{
    projectId = $projectId
    naturalLanguageGoal = "Saytga Google yoki Telegram orqali kirgan foydalanuvchi sifatida shaxsiy profil (/me), testlar, mobil moslashuvchanlik va qulaylikni tekshir."
    language = "uz-Latn"
    scopes = @("FUNCTIONAL", "RESPONSIVE", "ACCESSIBILITY", "SECURITY")
    authType = "GOOGLE_TELEGRAM_SSO"
    sessionCookie = "accessToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.e30.mock_sso_session; userData=%7B%22id%22%3A1%2C%22name%22%3A%22Mirabbos%22%7D"
    authUsername = "mirabboscgamberdiyev3@gmail.com"
} | ConvertTo-Json

$planResp = Invoke-RestMethod -Uri "$BaseUrl/api/v1/web-tests/plans" -Method Post -Body $planBody -Headers $headers
$planId = $planResp.id
Write-Host "`n[STEP 3] AI Test Plan Generated with SSO Case:" -ForegroundColor Green
Write-Host "  Plan ID: $planId"
Write-Host "  Total Generated Test Cases: $($planResp.testCases.Count)"
foreach ($tc in $planResp.testCases) {
    Write-Host "    - [$($tc.category)] $($tc.title)" -ForegroundColor Yellow
}

# Step 4: Approve Plan
$approveResp = Invoke-RestMethod -Uri "$BaseUrl/api/v1/web-tests/plans/$planId/approve" -Method Put -Headers $headers
Write-Host "`n[STEP 4] Plan Approved (Status: $($approveResp.status))" -ForegroundColor Green

# Step 5: Start Test Run with Google/Telegram Session Injection
Write-Host "`n[STEP 5] Executing Isolated Playwright Chromium with Session Injection..." -ForegroundColor Cyan
$runBody = @{
    planId = $planId
    browserType = "CHROMIUM"
    viewportWidth = 1280
    viewportHeight = 800
    authType = "GOOGLE_TELEGRAM_SSO"
    sessionCookie = "accessToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.e30.mock_sso_session; userData=%7B%22id%22%3A1%2C%22name%22%3A%22Mirabbos%22%7D"
    authUsername = "mirabboscgamberdiyev3@gmail.com"
} | ConvertTo-Json

$runResp = Invoke-RestMethod -Uri "$BaseUrl/api/v1/web-tests/runs" -Method Post -Body $runBody -Headers $headers
$runId = $runResp.id
Write-Host "`n[RESULT] Test Run Completed!" -ForegroundColor Green
Write-Host "  Run ID: $runId"
Write-Host "  Status: $($runResp.status)"
Write-Host "  Passed: $($runResp.passedCases)"
Write-Host "  Failed: $($runResp.failedCases)"
Write-Host "  Total:  $($runResp.totalCases)"

# Output Execution Logs
Write-Host "`n[PLAYWRIGHT EXECUTION LOGS]:" -ForegroundColor DarkCyan
Write-Host $runResp.executionLogs

# Step 6: Fetch Executive Report
$report = Invoke-RestMethod -Uri "$BaseUrl/api/v1/web-tests/runs/$runId/report" -Method Get -Headers $headers
Write-Host "`n[EXECUTIVE REPORT]:" -ForegroundColor Green
Write-Host "  Pass Rate: $($report.passPercentage)%"
Write-Host "  Total Defects: $($report.criticalDefectsCount + $report.highDefectsCount + $report.mediumDefectsCount + $report.lowDefectsCount)"
Write-Host "  HTML Report: $($report.htmlReportPath)"
Write-Host "  JSON Report: $($report.jsonReportPath)"
