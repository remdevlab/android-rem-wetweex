package org.remdev.wetweex.component;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.remdev.wetweex.execution.ExecutableTask;
import org.remdev.wetweex.execution.TaskRegistry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

final class SimpleTaskRegistry implements TaskRegistry {

    private Deque<ExecutableTask> taskStack = new ArrayDeque<>();
    private Map<String, ExecutableTask> idTaskMap = new ConcurrentHashMap<>();

    @Override
    public <R> void register(@NonNull ExecutableTask<R> task) {
        idTaskMap.put(task.getId(), task);
        taskStack.push(task);
    }

    @Override
    public <R> boolean unregister(@NonNull ExecutableTask<R> task) {
        idTaskMap.remove(task.getId());
        return taskStack.remove(task);
    }

    @Nullable
    @Override
    public ExecutableTask findById(@NonNull String id) {
        return idTaskMap.get(id);
    }

    @Nullable
    @Override
    public ExecutableTask getTopTask() {
        return taskStack.peek();
    }

    @NonNull
    @Override
    public List<ExecutableTask> getTasks() {
        return new ArrayList<>(taskStack);
    }

    @Override
    public boolean isEmpty() {
        return taskStack.isEmpty();
    }
}
