package com.platform.common.application.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public record PagedResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean isFirst,
        boolean isLast
) implements Serializable {

    public static <T> PagedResult<T> empty(int page, int size) {
        return new PagedResult<>(Collections.emptyList(), page, size, 0L, 0, true, true);
    }

    public static <T> PagedResult<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = size == 0 ? 1 : (int) Math.ceil((double) totalElements / (double) size);
        boolean isFirst = page <= 0;
        boolean isLast = page >= totalPages - 1;
        return new PagedResult<>(content, page, size, totalElements, totalPages, isFirst, isLast);
    }
}
