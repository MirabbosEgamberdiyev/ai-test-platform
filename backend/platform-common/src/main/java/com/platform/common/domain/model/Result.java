package com.platform.common.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Functional Result monad representing either Success with a value or Failure with an error.
 */
public final class Result<T, E> implements Serializable {

    private final T value;
    private final E error;
    private final boolean isSuccess;

    private Result(T value, E error, boolean isSuccess) {
        this.value = value;
        this.error = error;
        this.isSuccess = isSuccess;
    }

    public static <T, E> Result<T, E> success(T value) {
        return new Result<>(Objects.requireNonNull(value, "Success value must not be null"), null, true);
    }

    public static <T, E> Result<T, E> failure(E error) {
        return new Result<>(null, Objects.requireNonNull(error, "Error value must not be null"), false);
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public boolean isFailure() {
        return !isSuccess;
    }

    public Optional<T> getValue() {
        return Optional.ofNullable(value);
    }

    public Optional<E> getError() {
        return Optional.ofNullable(error);
    }

    public T orElseThrow(Function<E, ? extends RuntimeException> exceptionSupplier) {
        if (isSuccess) {
            return value;
        }
        throw exceptionSupplier.apply(error);
    }

    public <U> Result<U, E> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (isSuccess) {
            return Result.success(mapper.apply(value));
        }
        return Result.failure(error);
    }

    public <U> Result<U, E> flatMap(Function<? super T, Result<U, E>> mapper) {
        Objects.requireNonNull(mapper);
        if (isSuccess) {
            return mapper.apply(value);
        }
        return Result.failure(error);
    }

    public Result<T, E> ifSuccess(Consumer<? super T> consumer) {
        if (isSuccess) {
            consumer.accept(value);
        }
        return this;
    }

    public Result<T, E> ifFailure(Consumer<? super E> consumer) {
        if (isFailure()) {
            consumer.accept(error);
        }
        return this;
    }
}
