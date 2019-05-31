package org.remdev.wetweex.activity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import org.remdev.wetweex.ProgressView;
import org.remdev.wetweex.component.Coordinator;
import org.remdev.wetweex.model.LoadProgressInfo;
import org.remdev.wetweex.observer.LoadProgressInfoObserver;
import org.remdev.wetweex.viewmodel.TweexProgressViewModel;

import java.util.List;

public abstract class TweexProgressObservingActivity<T extends TweexProgressViewModel, C extends Coordinator<T>>
                extends TweexActivity<T, C> {

    @NonNull
    protected abstract ProgressView getProgressView();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getViewModel().getLoadingInfo() instanceof LiveData) {
            ((LiveData<LoadProgressInfo>) getViewModel().getLoadingInfo()).observe(this, new ProgressObserver());
        }
    }

    protected void showProgress(boolean indeterminate, List<String> operationsTags) {
        getProgressView().show(indeterminate, operationsTags);
    }

    protected void hideProgress() {
        getProgressView().hide();
    }

    private class ProgressObserver extends LoadProgressInfoObserver {
        @Override
        protected void showProgress(boolean indeterminate, List<String> operationsTag) {
            TweexProgressObservingActivity.this.showProgress(indeterminate, operationsTag);
        }

        @Override
        protected void hideProgress() {
            TweexProgressObservingActivity.this.hideProgress();
        }
    }
}
