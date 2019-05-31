package org.remdev.wetweex.execution;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public interface TaskRegistry {

    <R> void register(@NonNull ExecutableTask<R> task);

    /**
     * @return true if task was found and removed from registry
     */
    <R> boolean unregister(@NonNull ExecutableTask<R> task);

    @Nullable
    ExecutableTask findById(@NonNull String id);

    @Nullable
    ExecutableTask getTopTask();

    @NonNull
    List<ExecutableTask> getTasks();

    boolean isEmpty();

}
