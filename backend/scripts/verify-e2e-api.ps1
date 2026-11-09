$ErrorActionPreference = "Stop"

$BASE_URL = "http://localhost:8085"
$testReport = @()

function Log-Step($stepName, $passed, $details) {
    $script:testReport += [PSCustomObject]@{
        Step = $stepName
        Passed = $passed
        Details = $details
    }
    $status = if ($passed) { "[PASS]" } else { "[FAIL]" }
    Write-Host "$status $stepName : $details"
}

Write-Host "=== STARTING FULL END-TO-END API AUDIT SUITE ===" -ForegroundColor Cyan

# 1. Registration
$uniqueSuffix = [System.Guid]::NewGuid().ToString().Substring(0, 8)
$regPayload = @{
    tenantName = "Audit Enterprise $uniqueSuffix"
    tenantSlug = "audit-tenant-$uniqueSuffix"
    adminFullName = "Audit Director"
    adminEmail = "director_$uniqueSuffix@audit-enterprise.io"
    adminPassword = "AuditSecurePass2026!"
} | ConvertTo-Json

try {
    $regResp = Invoke-RestMethod -Uri "$BASE_URL/api/v1/auth/register" -Method Post -Body $regPayload -ContentType "application/json"
    $tenantId = $regResp.user.tenantId
    $accessToken = $regResp.accessToken
    $refreshToken = $regResp.refreshToken
    Log-Step "1. Tenant & Admin Registration" ($null -ne $accessToken -and $null -ne $tenantId) "TenantId=$tenantId, AccessToken generated"
} catch {
    Log-Step "1. Tenant & Admin Registration" $false $_.Exception.Message
    exit 1
}

# 2. Login
$loginPayload = @{
    email = "director_$uniqueSuffix@audit-enterprise.io"
    password = "AuditSecurePass2026!"
} | ConvertTo-Json

try {
    $loginResp = Invoke-RestMethod -Uri "$BASE_URL/api/v1/auth/login" -Method Post -Body $loginPayload -ContentType "application/json"
    $token = $loginResp.accessToken
    Log-Step "2. User Authentication (Login)" ($null -ne $token) "JWT successfully issued and verified"
} catch {
    Log-Step "2. User Authentication (Login)" $false $_.Exception.Message
}

# 3. RBAC Unauthorized Protection Check
try {
    Invoke-RestMethod -Uri "$BASE_URL/api/v1/registry/websites" -Method Get
    Log-Step "3. Unauthenticated Access Protection" $false "Failed: Endpoint was accessible without JWT"
} catch {
    $is401or403 = $_.Exception.Response.StatusCode.value__ -in @(401, 403)
    Log-Step "3. Unauthenticated Access Protection" $is401or403 "Blocked as expected with HTTP $($_.Exception.Response.StatusCode.value__)"
}

$authHeaders = @{
    Authorization = "Bearer $token"
    "x-correlation-id" = "e2e-audit-$uniqueSuffix"
}

# 4. Target Website Registration (Legitimate)
try {
    $existingSites = Invoke-RestMethod -Uri "$BASE_URL/api/v1/registry/websites" -Method Get -Headers $authHeaders
    if ($existingSites -and $existingSites.Count -gt 0) {
        $siteId = $existingSites[0].id
        Log-Step "4. Safe Target Website Registration" $true "Reused Verified SiteId=$siteId, Domain=$($existingSites[0].domainName)"
    } else {
        $sitePayload = @{
            domainName = "example.com"
            displayName = "Example Practice Portal"
            baseUrl = "https://example.com"
            platformType = "CUSTOM_PORTAL"
        } | ConvertTo-Json
        $siteResp = Invoke-RestMethod -Uri "$BASE_URL/api/v1/registry/websites" -Method Post -Body $sitePayload -ContentType "application/json" -Headers $authHeaders
        $siteId = $siteResp.id
        Log-Step "4. Safe Target Website Registration" ($null -ne $siteId) "Registered SiteId=$siteId, Domain=$($siteResp.domainName)"
    }
} catch {
    Log-Step "4. Safe Target Website Registration" $false $_.Exception.Message
}

# 5. SSRF Guard - Private IP Block Test
$ssrfPrivatePayload = @{
    domainName = "192.168.1.100"
    displayName = "Internal Host Attack"
    baseUrl = "http://192.168.1.100/admin"
    platformType = "CUSTOM_PORTAL"
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "$BASE_URL/api/v1/registry/websites" -Method Post -Body $ssrfPrivatePayload -ContentType "application/json" -Headers $authHeaders
    Log-Step "5. SSRF Guard: Private IP RFC 1918 Block" $false "Failed: Private IP was not blocked!"
} catch {
    Log-Step "5. SSRF Guard: Private IP RFC 1918 Block" $true "Successfully blocked private IP"
}

# 6. SSRF Guard - Cloud Metadata 169.254.169.254 Block Test
$ssrfMetaPayload = @{
    domainName = "169.254.169.254"
    displayName = "Cloud Metadata Exploit"
    baseUrl = "http://169.254.169.254/latest/meta-data"
    platformType = "CUSTOM_PORTAL"
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "$BASE_URL/api/v1/registry/websites" -Method Post -Body $ssrfMetaPayload -ContentType "application/json" -Headers $authHeaders
    Log-Step "6. SSRF Guard: Cloud Metadata Block" $false "Failed: Metadata endpoint was not blocked!"
} catch {
    Log-Step "6. SSRF Guard: Cloud Metadata Block" $true "Successfully blocked metadata endpoint"
}

# 7. SSRF Guard - Proctoring Platform Blacklist Test
$proctorPayload = @{
    domainName = "proctorio.com"
    displayName = "Proctorio Cheating Bypass"
    baseUrl = "https://proctorio.com/exam/secure"
    platformType = "CUSTOM_PORTAL"
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "$BASE_URL/api/v1/registry/websites" -Method Post -Body $proctorPayload -ContentType "application/json" -Headers $authHeaders
    Log-Step "7. SSRF Guard: High-Stakes Proctoring Blacklist" $false "Failed: Proctoring URL was not blocked!"
} catch {
    Log-Step "7. SSRF Guard: High-Stakes Proctoring Blacklist" $true "Successfully blocked high-stakes proctoring platform"
}

# 8. Start Practice Session
$sessionPayload = @{
    targetUrl = "https://example.com/practice-scenario"
    practiceMode = "STUDY_ASSISTED"
} | ConvertTo-Json

try {
    $sessResp = Invoke-RestMethod -Uri "$BASE_URL/api/v1/practice/sessions" -Method Post -Body $sessionPayload -ContentType "application/json" -Headers $authHeaders
    $sessionId = $sessResp.id
    Log-Step "8. Start Practice Session" ($null -ne $sessionId) "SessionId=$sessionId, State=$($sessResp.state)"
} catch {
    Log-Step "8. Start Practice Session" $false $_.Exception.Message
}

# 9. AI Question Multimodal Analysis
try {
    $aiResp = Invoke-RestMethod -Uri "$BASE_URL/api/v1/practice/sessions/$sessionId/analyze" -Method Post -Headers $authHeaders
    $hasPrediction = $null -ne $aiResp.predictedAnswer -and $aiResp.confidenceScore -gt 0.0
    $questionId = $aiResp.questionId
    Log-Step "9. AI Multimodal Question Analysis" $hasPrediction "QuestionId=$questionId, Predicted=$($aiResp.predictedAnswer), Confidence=$($aiResp.confidenceScore), Rec=$($aiResp.recommendedAction)"
} catch {
    Log-Step "9. AI Multimodal Question Analysis" $false $_.Exception.Message
}

# 10. Submit Practice Answer
$submitPayload = @{
    sessionId = $sessionId
    questionId = $questionId
    selectedOption = "B"
} | ConvertTo-Json

try {
    $submitResp = Invoke-RestMethod -Uri "$BASE_URL/api/v1/practice/sessions/$sessionId/submit" -Method Post -Body $submitPayload -ContentType "application/json" -Headers $authHeaders
    $hasResult = $submitResp.answeredQuestions -ge 1
    Log-Step "10. Submit Answer & Score Evaluation" $hasResult "AnsweredQuestions=$($submitResp.answeredQuestions), Total=$($submitResp.totalQuestions)"
} catch {
    Log-Step "10. Submit Answer & Score Evaluation" $false $_.Exception.Message
}

# 11. Finish Practice Session
try {
    $finishResp = Invoke-RestMethod -Uri "$BASE_URL/api/v1/practice/sessions/$sessionId/finish" -Method Post -Headers $authHeaders
    $isFinished = $null -ne $finishResp.id
    Log-Step "11. Finish Practice Session" $isFinished "ResultId=$($finishResp.id), AccuracyPercentage=$($finishResp.accuracyPercentage)%"
} catch {
    Log-Step "11. Finish Practice Session" $false $_.Exception.Message
}

# 12. Retrieve Analytics Results
try {
    $analyticsResp = Invoke-RestMethod -Uri "$BASE_URL/api/v1/analytics/results/$sessionId" -Method Get -Headers $authHeaders
    $validAnalytics = $analyticsResp.sessionId -eq $sessionId
    Log-Step "12. Persisted Practice Analytics Retrieval" $validAnalytics "SessionId=$($analyticsResp.sessionId), AccuracyPercentage=$($analyticsResp.accuracyPercentage)%, Total=$($analyticsResp.totalQuestions)"
} catch {
    Log-Step "12. Persisted Practice Analytics Retrieval" $false $_.Exception.Message
}

Write-Host "`n=== API AUDIT SUMMARY ===" -ForegroundColor Cyan
$passedCount = ($testReport | Where-Object { $_.Passed }).Count
$totalCount = $testReport.Count
Write-Host "Passed: $passedCount / $totalCount" -ForegroundColor Green

$jsonReport = $testReport | ConvertTo-Json -Depth 5
Set-Content -Path "d:\My-project\ai-test-platform\artifacts\api-audit-evidence.json" -Value $jsonReport
Write-Host "Evidence written to artifacts/api-audit-evidence.json"
