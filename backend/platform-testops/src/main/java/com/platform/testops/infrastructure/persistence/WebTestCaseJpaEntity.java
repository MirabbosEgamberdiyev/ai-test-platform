package com.platform.testops.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.testops.domain.model.WebTestCase;
import com.platform.testops.domain.model.WebTestCaseCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "web_test_cases")
public class WebTestCaseJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "plan_id", nullable = false)
    private UUID planId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private WebTestCaseCategory category;

    @Column(name = "priority", nullable = false, length = 20)
    private String priority;

    @Column(name = "steps_json", nullable = false, columnDefinition = "TEXT")
    private String stepsJson;

    @Column(name = "expected_result", nullable = false, columnDefinition = "TEXT")
    private String expectedResult;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public WebTestCaseJpaEntity() {}

    public static WebTestCaseJpaEntity fromDomain(WebTestCase domain) {
        WebTestCaseJpaEntity entity = new WebTestCaseJpaEntity();
        entity.id = domain.getId();
        entity.planId = domain.getPlanId();
        entity.tenantId = domain.getTenantId().value();
        entity.title = domain.getTitle();
        entity.category = domain.getCategory();
        entity.priority = domain.getPriority();
        entity.stepsJson = domain.getStepsJson();
        entity.expectedResult = domain.getExpectedResult();
        entity.orderIndex = domain.getOrderIndex();
        entity.version = domain.getVersion();
        entity.createdAt = domain.getCreatedAt();
        entity.updatedAt = domain.getUpdatedAt();
        return entity;
    }

    public WebTestCase toDomain() {
        return new WebTestCase(
                this.id,
                this.planId,
                TenantId.of(this.tenantId),
                this.title,
                this.category,
                this.priority,
                this.stepsJson,
                this.expectedResult,
                this.orderIndex,
                this.version,
                this.createdAt,
                this.updatedAt
        );
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPlanId() { return planId; }
    public void setPlanId(UUID planId) { this.planId = planId; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public WebTestCaseCategory getCategory() { return category; }
    public void setCategory(WebTestCaseCategory category) { this.category = category; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getStepsJson() { return stepsJson; }
    public void setStepsJson(String stepsJson) { this.stepsJson = stepsJson; }
    public String getExpectedResult() { return expectedResult; }
    public void setExpectedResult(String expectedResult) { this.expectedResult = expectedResult; }
    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }
    public long getVersion() { return version; }
    public void setVersion(long version) { this.version = version; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
