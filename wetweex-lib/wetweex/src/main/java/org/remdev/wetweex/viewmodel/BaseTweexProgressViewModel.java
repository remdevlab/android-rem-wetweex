package org.remdev.wetweex.viewmodel;

import org.remdev.wetweex.live.AnyThreadMutableLiveData;
import org.remdev.wetweex.live.TweexMutableLiveData;
import org.remdev.wetweex.model.LoadProgressInfo;

public class BaseTweexProgressViewModel implements TweexProgressViewModel {

    private TweexMutableLiveData<LoadProgressInfo> loadingInfo = new AnyThreadMutableLiveData<>();

    @Override
    public TweexMutableLiveData<LoadProgressInfo> getLoadingInfo() {
        return loadingInfo;
    }
}
