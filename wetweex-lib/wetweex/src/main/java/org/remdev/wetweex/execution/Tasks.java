package org.remdev.wetweex.execution;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.remdev.wetweex.live.AnyThreadMutableLiveData;
import org.remdev.wetweex.live.TweexLiveData;
import org.remdev.wetweex.live.TweexMutableLiveData;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class Tasks {

    /**
     * Runs a simple task
     */
    public static <R> TweexLiveData<Result<R>> execute(Callable<R> action) {
        TweexMutableLiveData<Result<R>> resLive = new AnyThreadMutableLiveData<>();
        ExecutableTask<R> task = ExecutableTask.cancellable(action)
                .onSuccess(res -> resLive.setOrPost(Result.success(res)))
                .onError(error -> resLive.setOrPost(Result.error(error)));
        task.start();
        return resLive;
    }

    /**
     * Joins tasks to one task and completes once all of them completed
     * @param tasks set of tasks to be joined
     * @return a new task which provides map of results for each task
     */
    public static ExecutableTask<Map<ExecutableTask, Result>> merge(@NonNull ExecutableTask ... tasks) {

        int importance = ExecutableTask.IMPORTANCE_CANCELLABLE;
        for (ExecutableTask task : tasks) {
            importance = Math.max(importance, task.getImportance());
        }
        final Callable<Map<ExecutableTask, Result>> action = () -> {
            Map<ExecutableTask, Result> results = new ConcurrentHashMap<>();
            CountDownLatch latch = new CountDownLatch(tasks.length);

            for (ExecutableTask task : tasks) {
                ExecutableTask wrappedTask = wrap(task, result -> {
                    results.put(task, Result.success(result));
                    latch.countDown();
                }, error -> {
                    results.put(task, Result.error(error));
                    latch.countDown();
                });
                wrappedTask.start();
            }
            latch.await();
            return results;
        };

        return new ExecutableTask<Map<ExecutableTask, Result>> (action, importance) {
            @Override
            public boolean cancel() {
                for (ExecutableTask task : tasks) {
                    task.cancel();
                }
                return super.cancel();
            }
        };
    }

    public static ExecutableTask<Map<ExecutableTask, Result>> join(@NonNull ExecutableTask ... tasks) {

        int importance = ExecutableTask.IMPORTANCE_CANCELLABLE;
        for (ExecutableTask task : tasks) {
            importance = Math.max(importance, task.getImportance());
        }
        final Callable<Map<ExecutableTask, Result>> action = () -> {
            Map<ExecutableTask, Result> results = new ConcurrentHashMap<>();
            CountDownLatch latch = new CountDownLatch(tasks.length);

            for (ExecutableTask task : tasks) {
                ExecutableTask wrappedTask = wrap(task, result -> {
                    results.put(task, Result.success(result));
                    latch.countDown();
                }, error -> {
                    results.put(task, Result.error(error));
                    latch.countDown();
                });
                wrappedTask.start();
            }
            latch.await();
            return results;
        };

        return new ExecutableTask<Map<ExecutableTask, Result>> (action, importance) {
            @Override
            public boolean cancel() {
                for (ExecutableTask task : tasks) {
                    task.cancel();
                }
                return super.cancel();
            }
        };
    }

    /**
     * Adds extra callbacks to the task.
     * @return updated task (same instance)
     */
    public static <R> ExecutableTask<R> wrap(@NonNull ExecutableTask<R> task,
                                             @Nullable ExecutableTask.Callback<R> success,
                                             @Nullable ExecutableTask.Callback<Throwable> error) {
        return wrap(task, success, error, null, null);
    }

    /**
     * Adds extra callbacks to the task.
     * @return updated task (same instance)
     */
    public static <R> ExecutableTask<R> wrap(@NonNull ExecutableTask<R> task,
                                             @Nullable ExecutableTask.Callback<R> success,
                                             @Nullable ExecutableTask.Callback<Throwable> error,
                                             @Nullable Runnable cancel,
                                             @Nullable Runnable complete) {
        final ExecutableTask.Callback<R> ownSuccessLambda = task.getSuccessCallback();
        final ExecutableTask.Callback<Throwable> ownErrorLambda = task.getErrorCallback();
        final Runnable ownCancelLambda = task.getCancelCallback();
        final Runnable ownCompleteLambda = task.getCompleteCallback();
        task.onSuccess(result -> {
            if (success != null) {
                success.invoke(result);
            }
            if (ownSuccessLambda != null) {
                ownSuccessLambda.invoke(result);
            }
        }).onError(throwable -> {
            if (error != null) {
                error.invoke(throwable);
            }
            if (ownErrorLambda != null) {
                ownErrorLambda.invoke(throwable);
            }
        }).onCancel(() -> {
            if (cancel != null) {
                cancel.run();
            }
            if (ownCancelLambda != null) {
                ownCancelLambda.run();
            }
        }).onComplete(() -> {
            if (complete != null) {
                complete.run();
            }
            if (ownCompleteLambda != null) {
                ownCompleteLambda.run();
            }
        });
        return task;
    }
}
