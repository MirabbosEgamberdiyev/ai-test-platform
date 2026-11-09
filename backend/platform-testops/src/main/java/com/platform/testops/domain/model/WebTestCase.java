package com.platform.testops.domain.model;

import com.platform.common.domain.model.TenantId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class WebTestCase {
    private final UUID id;
    private final UUID planId;
    private final TenantId tenantId;
    private String title;
    private WebTestCaseCategory category;
    private String priority;
    private String stepsJson;
    private String expectedResult;
    private int orderIndex;
    private long version;
    private final Instant createdAt;
    private Instant updatedAt;

    public WebTestCase(UUID id, UUID planId, TenantId tenantId, String title, WebTestCaseCategory category,
                       String priority, String stepsJson, String expectedResult, int orderIndex,
                       long version, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.planId = Objects.requireNonNull(planId, "planId must not be null");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId must not be null");
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.category = Objects.requireNonNull(category, "category must not be null");
        this.priority = priority != null ? priority : "MEDIUM";
        this.stepsJson = stepsJson != null ? stepsJson : "[]";
        this.expectedResult = Objects.requireNonNull(expectedResult, "expectedResult must not be null");
        this.orderIndex = orderIndex;
        this.version = version;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
    }

    public static WebTestCase create(UUID planId, TenantId tenantId, String title, WebTestCaseCategory category,
                                     String priority, String stepsJson, String expectedResult, int orderIndex) {
        Instant now = Instant.now();
        return new WebTestCase(UUID.randomUUID(), planId, tenantId, title, category, priority, stepsJson, expectedResult, orderIndex, 0L, now, now);
    }

    public UUID getId() { return id; }
    public UUID getPlanId() { return planId; }
    public TenantId getTenantId() { return tenantId; }
    public String getTitle() { return title; }
    public WebTestCaseCategory getCategory() { return category; }
    public String getPriority() { return priority; }
    public String getStepsJson() { return stepsJson; }
    public String getExpectedResult() { return expectedResult; }
    public int getOrderIndex() { return orderIndex; }
    public long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void update(String title, WebTestCaseCategory category, String priority, String stepsJson, String expectedResult, int orderIndex) {
        this.title = title;
        this.category = category;
        this.priority = priority;
        this.stepsJson = stepsJson;
        this.expectedResult = expectedResult;
        this.orderIndex = orderIndex;
        this.updatedAt = Instant.now();
    }
}
