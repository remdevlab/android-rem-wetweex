package org.remdev.wetweex.execution;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;

import org.remdev.wetweex.utils.SignalLatch;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutableTask<R> {

    public static final int STATE_NEW = 0;
    public static final int STATE_RUNNING = 1;
    public static final int STATE_COMPLETED_SUCCESSFULLY = 2;
    public static final int STATE_FAILED = 3;
    public static final int STATE_CANCELLED = 4;

    /**
     * Default status which means the task can be cancelled
     */
    public static final int IMPORTANCE_CANCELLABLE = 0;
    /**
     * This priority means that task can not be cancelled
     * {@link ExecutableTask#cancel()} method will not try to stop the task and
     * will return false
     */
    public static final int IMPORTANCE_NON_CANCELLABLE = 1;
    /**
     * The priority means the task is extremely important to
     * complete otherwise the related component does not
     * bring any sense until the task completes
     * For instance, the activity shows nothing and waits for data
     * so back press should not cancel the task, but finish the activity
     */
    public static final int IMPORTANCE_CRUCIAL = 2;

    private final String id = UUID.randomUUID().toString();
    private final Callable<R> action;
    private @Nullable Callback<R> successLambda;
    private @Nullable Callback<Throwable> errorLambda;
    private @Nullable Runnable cancelLambda;
    private @Nullable Runnable completeLambda;
    private Future<R> future = null;
    private final AtomicInteger state = new AtomicInteger(STATE_NEW);
    private final int importance;
    private final Set<CountDownLatch> waiters = new HashSet<>();

    protected ExecutableTask(Callable<R> action, int importance) {
        this.action = action;
        this.importance = importance;
    }

    public ExecutableTask<R> onSuccess(@Nullable Callback<R> action) {
        if (state.get() != STATE_NEW) {
            throw new IllegalStateException("Task is already being processed and listeners can not be changed");
        }
        successLambda = action;
        return this;
    }

    public ExecutableTask<R> onError(@Nullable Callback<Throwable> action) {
        if (state.get() != STATE_NEW) {
            throw new IllegalStateException("Task is already being processed and listeners can not be changed");
        }
        errorLambda = action;
        return this;
    }

    public ExecutableTask<R> onCancel(@Nullable Runnable action) {
        if (state.get() != STATE_NEW) {
            throw new IllegalStateException("Task is already being processed and listeners can not be changed");
        }
        cancelLambda = action;
        return this;
    }

    public ExecutableTask<R> onComplete(@Nullable Runnable action) {
        if (state.get() != STATE_NEW) {
            throw new IllegalStateException("Task is already being processed and listeners can not be changed");
        }
        completeLambda = action;
        return this;
    }

    public boolean cancel() {
        if (importance == IMPORTANCE_NON_CANCELLABLE) {
            return false;
        }
        return future == null || future.cancel(true);
    }

    public @NonNull String getId() {
        return id;
    }

    public @Nullable Callback<R> getSuccessCallback() {
        return successLambda;
    }

    public @Nullable Runnable getCancelCallback() {
        return cancelLambda;
    }

    public @Nullable Runnable getCompleteCallback() {
        return completeLambda;
    }

    public @Nullable Callback<Throwable> getErrorCallback() {
        return errorLambda;
    }

    public synchronized Future<R> start() {
        if (state.getAndSet(STATE_RUNNING) != STATE_NEW) {
            throw new IllegalStateException("Task is already started");
        }
        new Thread(() -> {
            future = Executors.newSingleThreadExecutor().submit(() -> {
                R res = action.call();
                state.set(STATE_COMPLETED_SUCCESSFULLY);
                if (successLambda != null) {
                    successLambda.invoke(res);
                }
                return res;
            });
            try {
                future.get();
            } catch (Throwable e) {
                if (e instanceof CancellationException) {
                    if (cancelLambda != null) {
                        cancelLambda.run();
                    }
                    return;
                }
                if (e instanceof ExecutionException) {
                    e = e.getCause();
                }
                if (errorLambda != null) {
                    errorLambda.invoke(e);
                } else {
                    throw new RuntimeException(e);
                }
            } finally {
                if (completeLambda != null) {
                    completeLambda.run();
                }
                for (CountDownLatch waiter : waiters) {
                    waiter.countDown();
                }
            }
        }).start();
        return future;
    }

    public boolean isRunning() {
        return state.get() == STATE_RUNNING;
    }

    public boolean isCancelled() {
        return state.get() == STATE_CANCELLED;
    }

    public boolean isFinished() {
        return state.get() == STATE_COMPLETED_SUCCESSFULLY || state.get() == STATE_FAILED;
    }

    public boolean isCompletedSuccessfully() {
        return state.get() == STATE_COMPLETED_SUCCESSFULLY;
    }

    public boolean isFailed() {
        return state.get() == STATE_FAILED;
    }

    /**
     * Returns one of the state code
     * <ul>
     *     <li>{@link ExecutableTask#STATE_NEW}</li>
     *     <li>{@link ExecutableTask#STATE_RUNNING}</li>
     *     <li>{@link ExecutableTask#STATE_CANCELLED}</li>
     *     <li>{@link ExecutableTask#STATE_COMPLETED_SUCCESSFULLY}</li>
     *     <li>{@link ExecutableTask#STATE_FAILED}</li>
     * </ul>
     * @return state code
     */
    public int getState() {
        return state.get();
    }

    /**
     * Returns one of the importance code
     * <ul>
     *     <li>{@link ExecutableTask#IMPORTANCE_CANCELLABLE}</li>
     *     <li>{@link ExecutableTask#IMPORTANCE_NON_CANCELLABLE}</li>
     *     <li>{@link ExecutableTask#IMPORTANCE_CRUCIAL}</li>
     * </ul>
     * @return importance code
     */
    public int getImportance() {
        return importance;
    }

    public void await() throws InterruptedException {
        CountDownLatch latch = new SignalLatch();
        waiters.add(latch);
        latch.await();
    }

    public <V> ExecutableTask<V> join(Function<R, V> action) {
        return new ExecutableTask<>(() -> action.apply(ExecutableTask.this.action.call()), getImportance());
    }

    public static <R> ExecutableTask<R> cancellable(Callable<R> action) {
        return new ExecutableTask<R>(action, IMPORTANCE_CANCELLABLE);
    }

    public static <R> ExecutableTask<R> nonCancellable(Callable<R> action) {
        return new ExecutableTask<R>(action, IMPORTANCE_NON_CANCELLABLE);
    }

    public static <R> ExecutableTask<R> crucial(Callable<R> action) {
        return new ExecutableTask<R>(action, IMPORTANCE_CRUCIAL);
    }

    public static <R> ExecutableTask<R> withImportance(Callable<R> action, int importance) {
        return new ExecutableTask<R>(action, importance);
    }

    public interface Callback<R> {
        void invoke(R result);
    }
}
