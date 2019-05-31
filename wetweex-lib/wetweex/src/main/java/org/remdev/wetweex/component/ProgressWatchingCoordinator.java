package org.remdev.wetweex.component;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import org.remdev.wetweex.execution.ExecutableTask;
import org.remdev.wetweex.live.TweexMutableLiveData;
import org.remdev.wetweex.model.LoadProgressInfo;
import org.remdev.wetweex.viewmodel.TweexProgressViewModel;

public class ProgressWatchingCoordinator<T extends TweexProgressViewModel> extends SimpleCoordinator<T> {

    public ProgressWatchingCoordinator(@NonNull T viewModel) {
        super(viewModel);
    }

    private void updateProgress(Consumer<LoadProgressInfo> action) {
        TweexMutableLiveData<LoadProgressInfo> loadingInfoData = getViewModel().getLoadingInfo();
        LoadProgressInfo loadingInfo = loadingInfoData.getValue();
        if (loadingInfo == null) {
            loadingInfo = new LoadProgressInfo();
        }
        action.accept(loadingInfo);
        loadingInfoData.setOrPost(loadingInfo);
    }

    @Override
    protected <R> void onTaskRegistered(ExecutableTask<R> task, String tag) {
        updateProgress(loadProgressInfo -> loadProgressInfo.addOperation(task.getId(), tag));
    }

    @Override
    protected <R> void onTaskUnregistered(ExecutableTask<R> task) {
        updateProgress(loadProgressInfo -> loadProgressInfo.completeOperation(task.getId()));
    }
}
