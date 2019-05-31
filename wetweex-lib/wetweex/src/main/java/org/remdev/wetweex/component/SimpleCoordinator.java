package org.remdev.wetweex.component;

import androidx.annotation.NonNull;
import org.remdev.wetweex.execution.ExecutableTask;
import org.remdev.wetweex.execution.TaskRegistry;
import org.remdev.wetweex.execution.Tasks;
import org.remdev.wetweex.viewmodel.TweexViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleCoordinator<T extends TweexViewModel> implements Coordinator<T> {

    private TaskRegistry taskRegistry = new SimpleTaskRegistry();
    private final Lock tasksLock = new ReentrantLock();
    private final List<TasksCompletedListener> listeners = new ArrayList<>();

    private final @NonNull T viewModel;

    public SimpleCoordinator(@NonNull T viewModel) {
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public T getViewModel() {
        return viewModel;
    }

    @Override
    public boolean tryCancelAll() {
        final AtomicBoolean resHolder = new AtomicBoolean(true);
        executeWithLock(() -> {
            for (ExecutableTask executableTask : taskRegistry.getTasks()) {
                boolean cancelled = executableTask.cancel();
                resHolder.set(resHolder.get() && cancelled);
            }
        });
        return resHolder.get();
    }

    @Override
    public boolean tryCancelTask(@NonNull String taskId) {
        AtomicBoolean res = new AtomicBoolean();
        executeWithLock(() -> {
            ExecutableTask task = taskRegistry.findById(taskId);
            boolean cancelled = task == null || task.cancel();
            res.set(cancelled);
        });
        return res.get();
    }

    @Override
    public boolean isTopTaskCrucial() {
        AtomicBoolean res = new AtomicBoolean();
        executeWithLock(() -> {
            ExecutableTask topTask = taskRegistry.getTopTask();
            boolean crucial = topTask != null && topTask.getImportance() == ExecutableTask.IMPORTANCE_CRUCIAL;
            res.set(crucial);
        });
        return res.get();
    }

    @Override
    public boolean isTopTaskCancellable() {
        AtomicBoolean res = new AtomicBoolean();
        executeWithLock(() -> {
            ExecutableTask topTask = taskRegistry.getTopTask();
            boolean cancelable = topTask == null || topTask.getImportance() == ExecutableTask.IMPORTANCE_CANCELLABLE;
            res.set(cancelable);
        });
        return res.get();
    }

    @Override
    public void cancelTopTask() {
        executeWithLock(() -> {
            ExecutableTask topTask = taskRegistry.getTopTask();
            if (topTask != null) {
                topTask.cancel();
            }
        });
    }

    @Override
    public boolean hasTasks() {
        return !taskRegistry.isEmpty();
    }

    @Override
    public void addTasksCompletedListener(TasksCompletedListener listener) {
        executeWithLock(() -> listeners.add(listener));
    }

    protected <R> ExecutableTask<R> submitTask(String tag, @NonNull final ExecutableTask<R> task) {
        Tasks.wrap(task, null, null, null, () -> completeTask(task));
        executeWithLock(() -> taskRegistry.register(task));
        onTaskRegistered(task, tag);
        task.start();
        return task;
    }

    protected <R> void onTaskRegistered(ExecutableTask<R> task, String tag) {

    }

    protected <R> void onTaskUnregistered(ExecutableTask<R> task) {

    }

    private <R> void completeTask(final ExecutableTask<R> task) {
        executeWithLock(() -> {
            taskRegistry.unregister(task);
            if (taskRegistry.isEmpty()) {
                notifyTasksCompleted();
            }
        });
        onTaskUnregistered(task);
    }

    private void notifyTasksCompleted() {
        for (TasksCompletedListener listener : listeners) {
            listener.onTasksCompleted();
        }
    }

    private void executeWithLock(Runnable action) {
        try {
            tasksLock.lock();
            action.run();
        } finally {
            tasksLock.unlock();
        }
    }

}
