package com.platform.testops.domain.model;

public enum SubmissionStatus {
    NOT_STARTED,
    PREPARING,
    WAITING_FOR_APPROVAL,
    EXECUTING,
    SUBMISSION_PENDING,
    SUBMITTED,
    VERIFIED,
    FAILED,
    UNKNOWN_RESULT,
    MANUAL_VERIFICATION_REQUIRED,
    POLICY_BLOCKED
}
