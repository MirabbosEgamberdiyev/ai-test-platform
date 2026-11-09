package com.platform.testops.domain.model;

public enum WebTestRunStatus {
    CREATED,
    DISCOVERING,
    GENERATING_PLAN,
    WAITING_APPROVAL,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED
}
