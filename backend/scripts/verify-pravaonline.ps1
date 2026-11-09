# Verify testing against https://web.pravaonline.uz/tickets
$ErrorActionPreference = "Stop"
$BaseUrl = "http://localhost:8085"

$uniqueSuffix = [System.Guid]::NewGuid().ToString().Substring(0, 6)
$regBody = @{
    tenantName = "Prava QA Workspace $uniqueSuffix"
    tenantSlug = "prava-tenant-$uniqueSuffix"
    adminFullName = "Prava Auditor"
    adminEmail = "prava-$uniqueSuffix@example.com"
    adminPassword = "AuditorSecurePassword2026!"
} | ConvertTo-Json

Write-Host "Registering auditor account..."
$regResp = Invoke-RestMethod -Uri "$BaseUrl/api/v1/auth/register" -Method Post -Body $regBody -ContentType "application/json"
$token = $regResp.accessToken

$headers = @{
    "Authorization" = "Bearer $token"
}

Write-Host "Registering Project for web.pravaonline.uz..."
$projBody = @{
    name = "Prava Online Tickets"
    targetUrl = "https://web.pravaonline.uz/tickets"
    environment = "PRODUCTION"
    description = "Driver ticket examination practice portal"
    instructions = "Verify accessibility, responsive, and functional stability"
    isAuthorized = $true
} | ConvertTo-Json

$projResp = Invoke-RestMethod -Uri "$BaseUrl/api/v1/web-projects" -Method Post -Body $projBody -ContentType "application/json" -Headers $headers
$projectId = $projResp.id
Write-Host "Project ID: $projectId"

Write-Host "Generating AI Test Plan..."
$planBody = @{
    projectId = $projectId
    naturalLanguageGoal = "Saytga kirib sarlavha, navigatsiya, shakllar, mobil moslashuvchanlik va qulaylikni (A11y) tekshir."
    language = "uz-Latn"
    scopes = @("FUNCTIONAL", "UI_UX", "RESPONSIVE", "ACCESSIBILITY", "SECURITY")
} | ConvertTo-Json

$planResp = Invoke-RestMethod -Uri "$BaseUrl/api/v1/web-tests/plans" -Method Post -Body $planBody -ContentType "application/json" -Headers $headers
$planId = $planResp.id
Write-Host "Generated Plan ID: $planId with $($planResp.testCases.Count) cases"

Write-Host "Approving Plan..."
Invoke-RestMethod -Uri "$BaseUrl/api/v1/web-tests/plans/$planId/approve" -Method Put -Headers $headers | Out-Null

Write-Host "Executing Test Run via Playwright Chromium..."
$runBody = @{
    planId = $planId
    browserType = "CHROMIUM"
    viewportWidth = 1280
    viewportHeight = 800
} | ConvertTo-Json

$runData = Invoke-RestMethod -Uri "$BaseUrl/api/v1/web-tests/runs" -Method Post -Body $runBody -ContentType "application/json" -Headers $headers -TimeoutSec 120
Write-Host "`nTest Run Results:"
Write-Host "Run ID: $($runData.id)"
Write-Host "Status: $($runData.status)"
Write-Host "Total: $($runData.totalCases) | Passed: $($runData.passedCases) | Failed: $($runData.failedCases)"
Write-Host "`nExecution Logs:`n$($runData.executionLogs)"

Write-Host "`nFetching Defect Details..."
$defects = Invoke-RestMethod -Uri "$BaseUrl/api/v1/web-tests/runs/$($runData.id)/defects" -Method Get -Headers $headers
if ($defects) {
    foreach ($d in $defects) {
        Write-Host "Defect [$($d.severity)] $($d.defectCode): $($d.title)"
        Write-Host "  Actual: $($d.actualResult)"
        Write-Host "  AI Root Cause: $($d.aiRootCause)"
    }
} else {
    Write-Host "No defects recorded!"
}
