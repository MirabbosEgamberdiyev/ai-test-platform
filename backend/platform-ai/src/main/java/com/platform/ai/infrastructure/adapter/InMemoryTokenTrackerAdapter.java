package com.platform.ai.infrastructure.adapter;

import com.platform.ai.application.port.TokenTrackerPort;
import com.platform.ai.domain.model.TokenSpendRecord;
import com.platform.common.domain.model.TenantId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryTokenTrackerAdapter implements TokenTrackerPort {

    private final List<TokenSpendRecord> records = new CopyOnWriteArrayList<>();
    private final Map<TenantId, AtomicLong> tenantTokenTotals = new ConcurrentHashMap<>();

    @Override
    public void recordSpend(TokenSpendRecord record) {
        records.add(record);
        long totalTokens = (long) record.inputTokens() + record.outputTokens();
        tenantTokenTotals.computeIfAbsent(record.tenantId(), k -> new AtomicLong(0))
                .addAndGet(totalTokens);
    }

    @Override
    public long getTenantMonthlyUsage(TenantId tenantId) {
        AtomicLong usage = tenantTokenTotals.get(tenantId);
        return usage != null ? usage.get() : 0L;
    }

    public List<TokenSpendRecord> getAllRecords() {
        return List.copyOf(records);
    }
}
