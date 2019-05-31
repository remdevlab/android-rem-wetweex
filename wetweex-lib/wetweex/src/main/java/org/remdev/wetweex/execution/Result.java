package org.remdev.wetweex.execution;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Result<R> {

    private boolean success;

    @Nullable
    private R res;

    @Nullable
    private Throwable error;

    private Result(boolean success, @Nullable R res, @Nullable Throwable error) {
        this.success = success;
        this.res = res;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    @Nullable
    public R getResult() {
        return res;
    }

    @Nullable
    public Throwable getError() {
        return error;
    }

    public static <R> Result<R> error(@NonNull Throwable error) {
        return new Result<>(false, null, error);
    }

    public static <R> Result<R> success(@Nullable R res) {
        return new Result<>(true, res, null);
    }
}
