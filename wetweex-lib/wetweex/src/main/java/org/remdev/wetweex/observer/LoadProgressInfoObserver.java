package org.remdev.wetweex.observer;

import androidx.lifecycle.Observer;
import org.remdev.wetweex.model.LoadProgressInfo;

import java.util.List;

public abstract class LoadProgressInfoObserver implements Observer<LoadProgressInfo> {

    @Override
    public void onChanged(LoadProgressInfo loadProgressInfo) {
        if (loadProgressInfo.isLoading()) {
            showProgress(loadProgressInfo.isIndeterminate(), loadProgressInfo.getRunningOperationMessages());
        } else if (loadProgressInfo.isLoading() == false) {
            hideProgress();
        }
    }

    protected abstract void showProgress(boolean indeterminate, List<String> operationMessages);

    protected abstract void hideProgress();

}
