package org.remdev.wetweex.component;

import androidx.annotation.NonNull;
import org.remdev.wetweex.viewmodel.TweexViewModel;

/**
 * This interface declares the common behavior of coordinator
 * Basically it should be able to provide view model object
 * and also it is responsible for submitting tasks and updating
 * related view model according to tasks state
 * @param <T>
 */
public interface Coordinator<T extends TweexViewModel> {

    /**
     * Called when user presses back (or any other button)
     * trying to stop data loading or long-running operation
     * @return if it possible to stop loading or long-running operations
     */
    boolean tryCancelAll();

    boolean tryCancelTask(@NonNull String taskId);

    @NonNull
    T getViewModel();

    boolean isTopTaskCrucial();

    boolean isTopTaskCancellable();

    void cancelTopTask();

    boolean hasTasks();

    void addTasksCompletedListener(TasksCompletedListener listener);

    interface TasksCompletedListener {
        void onTasksCompleted();
    }
}
